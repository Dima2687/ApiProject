package apiUtils;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class ApiRequests {

    public static <T> Response sendPostRequest(String endpoint, T body) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response sendGetRequest(String endpoint) {
        return given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response sendDeleteRequest(String endpoint) {
        return given()
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }
}
