package apiUserTests;

import apiConfig.BaseTest;
import dto.ResponseDto;
import dto.user.UserDto;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static apiUtils.ApiRequests.sendGetRequest;
import static apiUtils.ApiRequests.sendPostRequest;
import static org.assertj.core.api.Assertions.assertThat;

public class UserAffirmativeTests {

    @ParameterizedTest
    @MethodSource("data.UserTestDataProvider#provideValidUsers")
    void createUsersWithListTest(List<UserDto> users) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendPostRequest("/v2/user/createWithList", users);
        ResponseDto apiResponse = response.as(ResponseDto.class);

        List<UserDto> createdUsers = users.stream()
                .map(user -> sendGetRequest("/v2/user/" + user.getUsername()).as(UserDto.class))
                .toList();

        assertThat(apiResponse.getCode()).isEqualTo(200);
        assertThat(apiResponse.getMessage()).isEqualTo("ok");
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.jsonPath().getString("message")).isNotBlank();

        assertThat(createdUsers)
                .as("Список созданных пользователей не должен быть пустым")
                .isNotEmpty()
                .allSatisfy(user -> {
                    assertThat(user)
                            .as("Проверка пользователя " + user.getUsername())
                            .isNotNull()
                            .extracting(UserDto::getUsername, u -> u.getEmail()) // Используем лямбду вместо `getEmail()`
                            .containsExactly(user.getUsername(), user.getEmail());
                });
    }

    @ParameterizedTest
    @MethodSource("data.UserTestDataProvider#provideValidUsers")
    void getUserByUsernameTest(List<UserDto> users) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        List<UserDto> receivedUsers = users.stream()
                .map(user -> sendGetRequest("/v2/user/" + user.getUsername()).as(UserDto.class))
                .toList();

        assertThat(receivedUsers)
                .as("Список полученных пользователей не должен быть пустым")
                .isNotEmpty()
                .allSatisfy(user -> assertThat(user)
                        .as("Проверка пользователя " + user.getUsername())
                        .isNotNull()
                        .extracting(UserDto::getUsername, UserDto::getFirstName, UserDto::getLastName, UserDto::getEmail, UserDto::getPhone, UserDto::getUserStatus)
                        .containsExactly(user.getUsername(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhone(), user.getUserStatus()));
    }
}
