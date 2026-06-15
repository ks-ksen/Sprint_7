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
public class CreateCourierTest {

    private CourierClient courierClient;
    private Courier testCourier;
    private int createdCourierId;

    @BeforeAll
    public void setUp() {
        RestAssuredConfig.setUp();
        courierClient = new CourierClient();
    }

    @AfterEach
    @Step("Очистка тестовых данных - удаление созданного курьера")
    public void cleanUp() {
        if (createdCourierId > 0) {
            Response deleteResponse = courierClient.deleteCourier(createdCourierId);
            deleteResponse.then().statusCode(200);
            createdCourierId = 0;  // Сбрасываем id
        }
    }

    @Test
    @Step("Создание нового курьера")
    public void testCreateCourierSuccess() {
        // Генерируем уникального курьера
        testCourier = TestDataGenerator.getRandomCourier();

        //запрос на создание
        Response createResponse = courierClient.createCourier(testCourier);

        //код ответа
        createResponse.then()
                .statusCode(201)
                .body("ok", is(true));

        //id созданного курьера для удаления
        CourierCredentials credentials = TestDataGenerator.getCredentials(testCourier);
        createdCourierId = courierClient.getCourierId(credentials);
        assertTrue(createdCourierId > 0, "ID курьера должен быть положительным числом");
    }

    @Test
    @Step("Создание курьера без пароля")
    public void testCreateCourierWithoutPassword() {
        Courier courierWithoutPassword = new Courier(
                "courier_" + System.currentTimeMillis(),
                null,  // Пароль отсутствует
                "TestName"
        );

        Response response = courierClient.createCourier(courierWithoutPassword);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Step("Создание курьера без логина")
    public void testCreateCourierWithoutLogin() {
        Courier courierWithoutLogin = new Courier(
                null,  // Логин отсутствует
                "password123",
                "TestName"
        );

        Response response = courierClient.createCourier(courierWithoutLogin);

        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @Step("Проверка поля ok в ответе")
    public void testCreateCourierReturnsOkTrue() {
        testCourier = TestDataGenerator.getRandomCourier();
        Response response = courierClient.createCourier(testCourier);

        response.then()
                .statusCode(201)
                .body("ok", is(true))
                .body("$", hasKey("ok"));

        CourierCredentials credentials = TestDataGenerator.getCredentials(testCourier);
        createdCourierId = courierClient.getCourierId(credentials);
    }

    @Test
    @Step("Попытка создать курьера с уже существующим логином")
    public void testCreateCourierWithExistingLogin() {
        Courier firstCourier = TestDataGenerator.getRandomCourier();
        Response firstResponse = courierClient.createCourier(firstCourier);
        firstResponse.then().statusCode(201);

        CourierCredentials credentials = TestDataGenerator.getCredentials(firstCourier);
        int firstCourierId = courierClient.getCourierId(credentials);

        Courier duplicateLoginCourier = new Courier(
                firstCourier.getLogin(),  // Тот же логин
                "different_password",
                "DifferentName"
        );

        Response duplicateResponse = courierClient.createCourier(duplicateLoginCourier);

        // Проверяем ошибку
        duplicateResponse.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        // Сохраняем id для удаления в afterEach
        createdCourierId = firstCourierId;
    }
}