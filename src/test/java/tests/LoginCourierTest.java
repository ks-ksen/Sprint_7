package tests;

import client.CourierClient;
import config.RestAssuredConfig;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import utils.TestDataGenerator;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginCourierTest {

    private CourierClient courierClient;
    private Courier testCourier;
    private int createdCourierId;

    @BeforeAll
    public void setUp() {
        RestAssuredConfig.setUp();
        courierClient = new CourierClient();
    }

    @BeforeEach
    @Step("Создание тестового курьера перед каждым тестом")
    public void createTestCourier() {
        testCourier = TestDataGenerator.getRandomCourier();
        Response createResponse = courierClient.createCourier(testCourier);
        createResponse.then().statusCode(201);

        // Получаем id созданного курьера
        CourierCredentials credentials = TestDataGenerator.getCredentials(testCourier);
        createdCourierId = courierClient.getCourierId(credentials);
    }

    @AfterEach
    @Step("Удаление тестового курьера после теста")
    public void deleteTestCourier() {
        if (createdCourierId > 0) {
            Response deleteResponse = courierClient.deleteCourier(createdCourierId);
            deleteResponse.then().statusCode(200);
            createdCourierId = 0;
        }
    }

    @Test
    @Step("Авторизация существующего курьера")
    public void testCourierCanLogin() {
        CourierCredentials credentials = TestDataGenerator.getCredentials(testCourier);

        Response loginResponse = courierClient.loginCourier(credentials);

        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", greaterThan(0));
    }

    @Test
    @Step("Авторизация с неправильным логином")
    public void testLoginWithWrongLogin() {
        CourierCredentials wrongCredentials = new CourierCredentials(
                "non_existent_login_" + System.currentTimeMillis(),
                testCourier.getPassword()
        );

        Response loginResponse = courierClient.loginCourier(wrongCredentials);

        loginResponse.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Авторизация с неправильным паролем")
    public void testLoginWithWrongPassword() {
        CourierCredentials wrongCredentials = new CourierCredentials(
                testCourier.getLogin(),
                "wrong_password_123"
        );

        Response loginResponse = courierClient.loginCourier(wrongCredentials);

        loginResponse.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Авторизация без логина")
    public void testLoginWithoutLogin() {
        CourierCredentials credentialsWithoutLogin = new CourierCredentials(
                null,  // Логин отсутствует
                testCourier.getPassword()
        );

        Response loginResponse = courierClient.loginCourier(credentialsWithoutLogin);

        loginResponse.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @Step("Авторизация с несуществующими учётными данными")
    public void testLoginWithNonExistentUser() {
        CourierCredentials nonExistentCredentials = new CourierCredentials(
                "ghost_user_" + System.currentTimeMillis(),
                "ghost_password"
        );

        Response loginResponse = courierClient.loginCourier(nonExistentCredentials);

        loginResponse.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Проверка, что в ответе приходит id курьера")
    public void testLoginReturnsId() {
        CourierCredentials credentials = TestDataGenerator.getCredentials(testCourier);

        Response loginResponse = courierClient.loginCourier(credentials);

        // Получаем id из ответа и проверяем его
        Integer courierId = loginResponse.then()
                .statusCode(200)
                .extract()
                .path("id");

        assertNotNull(courierId, "ID курьера не должен быть null");
        assertTrue(courierId > 0, "ID курьера должен быть больше 0");
        assertEquals(createdCourierId, courierId, "ID должен соответствовать созданному");
    }
}