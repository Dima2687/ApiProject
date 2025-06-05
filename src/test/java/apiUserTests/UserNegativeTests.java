package apiUserTests;

import apiConfig.BaseTest;
import dto.user.UserDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static apiUtils.ApiRequests.sendGetRequest;
import static apiUtils.ApiRequests.sendPostRequest;
import static org.assertj.core.api.Assertions.assertThat;

public class UserNegativeTests {
    @ParameterizedTest
    @MethodSource("data.UserTestDataProvider#provideInvalidUsernames")
    void getUserByInvalidUsernameTest(String invalidUsername) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendGetRequest("/v2/user/" + invalidUsername);

        String errorMessage = response.jsonPath().getString("message");

        assertThat(response.getStatusCode()).isIn(404, 405);
        assertThat(errorMessage).isIn(null, "User not found", "unknown", "Invalid request");
        assertThat(response.getContentType()).isEqualTo("application/json");
    }

    @ParameterizedTest
    @MethodSource("data.UserTestDataProvider#provideInvalidUsers")
    void createUsersWithInvalidDataTest(List<UserDto> invalidUsers) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendPostRequest("/v2/user/createWithList", invalidUsers);
        String errorMessage = response.jsonPath().getString("message");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(errorMessage).isIn(null, "User list cannot be empty", "Invalid request", "ok");
        assertThat(response.getContentType()).isEqualTo("application/json");
    }

    @ParameterizedTest
    @MethodSource("data.UserTestDataProvider#provideDuplicateUsers")
    void createUsersWithDuplicateUsernamesTest(List<UserDto> duplicateUsers) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendPostRequest("/v2/user/createWithList", duplicateUsers);

        String errorMessage = response.jsonPath().getString("message");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(errorMessage).isIn(null, "User list cannot be empty", "Invalid request", "ok");
        assertThat(response.getContentType()).isEqualTo("application/json");
    }

    @Test
    void createUsersWithEmptyListTest() {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendPostRequest("/v2/user/createWithList", List.of());  //пустой список

        String errorMessage = response.jsonPath().getString("message");

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(errorMessage).isIn(null, "User list cannot be empty", "Invalid request", "ok");
        assertThat(response.getContentType()).isEqualTo("application/json");
    }
}

