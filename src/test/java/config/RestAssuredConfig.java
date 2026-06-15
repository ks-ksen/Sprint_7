package config;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RestAssuredConfig {

    public static void setUp() {
        RestAssured.baseURI = "https://qa-scooter.education-services.ru";

        // Базовая спецификацию для всех запросов
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)  // Все запросы в формате JSON
                .setBasePath("/api/v1")             // Базовый путь API
                .build();

        RestAssured.requestSpecification = requestSpec;
    }
}