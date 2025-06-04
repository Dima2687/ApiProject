package apiUtils;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class PetUtils {

    public static <T> Response sendPostRequest(String endpoint, T body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(body)
                .post(endpoint)
                .then()
                .log().all()
                .extract().response();
    }

    public static Response sendGetRequest(String endpoint) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(endpoint)
                .then()
                .log().all()
                .extract().response();
    }
}
