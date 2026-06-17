package tests;

import client.OrderClient;
import config.RestAssuredConfig;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import static org.hamcrest.Matchers.*;

@ExtendWith(AllureJunit5.class)
public class GetOrdersListTest extends RestAssuredConfig {

    private OrderClient orderClient;

    @BeforeAll
    @Step("Настройка конфигурации RestAssured")
    public static void setUp() {
        RestAssuredConfig.setUp();
    }

    @BeforeEach
    public void init() {
        orderClient = new OrderClient();
    }

    @Test
    @Step("Получение списка заказов и проверка структуры")
    public void testGetOrdersListReturnsList() {
        Response response = orderClient.getOrdersList();

        response.then()
                .statusCode(200)
                .body("orders", notNullValue())  // Поле orders существует
                .body("orders", instanceOf(List.class));  // orders - это список

        List<Object> orders = response.jsonPath().getList("orders");

        if (orders.size() > 0) {
            response.then()
                    .body("orders[0].id", notNullValue())
                    .body("orders[0].firstName", notNullValue())
                    .body("orders[0].lastName", notNullValue())
                    .body("orders[0].address", notNullValue())
                    .body("orders[0].metroStation", notNullValue())
                    .body("orders[0].phone", notNullValue())
                    .body("orders[0].rentTime", notNullValue())
                    .body("orders[0].deliveryDate", notNullValue());
        }
    }


}