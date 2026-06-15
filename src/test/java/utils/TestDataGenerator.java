package utils;

import model.Courier;
import model.CourierCredentials;
import model.Order;
import java.util.List;
import java.util.UUID;

public class TestDataGenerator {

    // Генерация  курьера
    public static Courier getRandomCourier() {
        String uniqueLogin = "courier_" + UUID.randomUUID().toString().substring(0, 8);
        return new Courier(uniqueLogin, "password123", "TestFirstName");
    }

    // Заказ с указанными цветами
    public static Order getOrderWithColors(List<String> colors) {
        return new Order(
                "Тест",                      // firstName
                "Тестов",                    // lastName
                "Тестовая улица, 1",         // address
                "Сокольники",                // metroStation
                "+79998887766",              // phone
                3,                           // rentTime (дней)
                "2025-12-31",                // deliveryDate
                "Тестовый комментарий",      // comment
                colors                       // список цветов - теперь List<String>
        );
    }

    // создание учётных данных для курьера
    public static CourierCredentials getCredentials(Courier courier) {
        return new CourierCredentials(courier.getLogin(), courier.getPassword());
    }
}