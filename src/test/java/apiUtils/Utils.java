package apiUtils;

import apiConfig.BaseTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Utils {

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

    public static Response DeleteRequest(String endpoint) {
        return RestAssured
                .given()
                .spec(BaseTest.requestSpec(BaseTest.get("base.url")))
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }
}
