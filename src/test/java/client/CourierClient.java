package client;

import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String COURIER_PATH = "/courier";
    private static final String LOGIN_PATH = "/courier/login";
    private static final String DELETE_PATH = "/courier/";

    @Step("Создание курьера с логином: {courier.login}")
    public Response createCourier(Courier courier) {
        return given()
                .body(courier)
                .when()
                .post(COURIER_PATH);
    }

    @Step("Логин курьера с логином: {credentials.login}")
    public Response loginCourier(CourierCredentials credentials) {
        return given()
                .body(credentials)
                .when()
                .post(LOGIN_PATH);
    }

    @Step("Удаление курьера с id: {courierId}")
    public Response deleteCourier(int courierId) {
        return given()
                .when()
                .delete(DELETE_PATH + courierId);
    }

    @Step("Получение id курьера по логину и паролю")
    public int getCourierId(CourierCredentials credentials) {
        Response response = loginCourier(credentials);

        // Проверяем, что авторизация прошла успешно
        response.then().statusCode(200);

        // Извлекаем id из ответа
        Integer id = response.path("id");
        if (id == null) {
            throw new RuntimeException("Не удалось получить id курьера. Ответ: " + response.getBody().asString());
        }
        return id;
    }
}