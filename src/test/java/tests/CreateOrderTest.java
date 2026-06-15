package tests;

import client.OrderClient;
import config.RestAssuredConfig;
import io.qameta.allure.Step;
import io.qameta.allure.junit5.AllureJunit5;
import io.restassured.response.Response;
import model.Order;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import utils.TestDataGenerator;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AllureJunit5.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateOrderTest {

    private OrderClient orderClient;

    @BeforeAll
    public void setUp() {
        RestAssuredConfig.setUp();
        orderClient = new OrderClient();
    }

    // Метод-провайдер данных для параметризованных тестов ДОЛЖЕН быть статическим
    static Stream<Arguments> provideColorCombinations() {
        return Stream.of(
                Arguments.of(Collections.singletonList("BLACK"), "только BLACK"),
                Arguments.of(Collections.singletonList("GREY"), "только GREY"),
                Arguments.of(Arrays.asList("BLACK", "GREY"), "оба цвета"),
                Arguments.of(Collections.emptyList(), "без указания цвета")
        );
    }

    @ParameterizedTest(name = "Создание заказа с цветами: {1}")
    @MethodSource("provideColorCombinations")  // Ссылаемся на статический метод
    @Step("Создание заказа с цветами: {colors}")
    public void testCreateOrderWithDifferentColors(List<String> colors, String testName) {
        // Создаём заказ с заданными цветами
        Order order = TestDataGenerator.getOrderWithColors(colors);

        // Отправляем запрос на создание
        Response createResponse = orderClient.createOrder(order);

        // Проверяем код ответа и наличие track
        createResponse.then()
                .statusCode(201)
                .body("track", notNullValue());

        // Получаем track из ответа
        int track = createResponse.path("track");
        assertTrue(track > 0, "Track должен быть положительным числом");
    }

    @Test
    @Step("Проверка наличия поля track в ответе")
    public void testCreateOrderReturnsTrack() {
        // Создаём заказ со стандартными параметрами
        Order order = TestDataGenerator.getOrderWithColors(Collections.singletonList("BLACK"));

        Response response = orderClient.createOrder(order);

        // Проверяем структуру ответа
        response.then()
                .statusCode(201)
                .body("track", is(notNullValue()))
                .body("track", greaterThan(0))
                .body("$", hasKey("track"));

        // Извлекаем и проверяем track
        int track = response.path("track");
        assertTrue(track > 0, "Track должен быть положительным");
    }
}