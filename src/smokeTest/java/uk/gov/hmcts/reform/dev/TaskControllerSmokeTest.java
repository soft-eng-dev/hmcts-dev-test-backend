package uk.gov.hmcts.reform.dev;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerSmokeTest {

    @Value("${TEST_URL:http://localhost:4000}")
    private String testUrl;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = testUrl;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    void smokeTest_tasksEndpointAvailable() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/tasks?page=0&size=1")
            .then()
            .extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    void smokeTest_summaryEndpointAvailable() {
        Response response = given()
            .contentType(ContentType.JSON)
            .when()
            .get("/summary")
            .then()
            .extract().response();

        Assertions.assertEquals(200, response.statusCode());
    }
}
