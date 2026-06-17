// OrderClient.java
package client;

import io.restassured.response.Response;
import model.Order;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDER_PATH = "/orders";       // Путь для заказов
    private static final String GET_ORDERS_PATH = "/orders";  // Путь для получения списка

    @Step("Создание заказа: {order.firstName} {order.lastName}")
    public Response createOrder(Order order) {
        // Отправляем POST запрос на создание заказа
        return given()
                .body(order)                // Передаём объект заказа
                .when()
                .post(ORDER_PATH);          // Выполняем POST запрос
    }

    @Step("Получение списка заказов")
    public Response getOrdersList() {
        // Отправляем GET запрос на получение всех заказов
        return given()
                .when()
                .get(GET_ORDERS_PATH);      // Выполняем GET запрос
    }
}