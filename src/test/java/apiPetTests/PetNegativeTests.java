package apiPetTests;

import apiConfig.BaseTest;
import apiUtils.EntityUtils;
import dto.ResponseDto;
import dto.pet.PetDto;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static apiUtils.ApiRequests.sendGetRequest;
import static apiUtils.ApiRequests.sendPostRequest;
import static org.assertj.core.api.Assertions.assertThat;

public class PetNegativeTests {
    public static final String NUMBER_EXCEPTION = "'java.lang.NumberFormatException: For input string: \"abc\"'";

    @ParameterizedTest
    @CsvSource({
            "0, error, Pet not found",
            "-1, error, Pet not found",
            "abc, unknown, " + NUMBER_EXCEPTION
    })
    void getInvalidPetTest(String petId, String expectedType, String expectedMessage) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendGetRequest("/v2/pet/" + petId);
        ResponseDto actualResponse = response.as(ResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(404);

        assertThat(actualResponse)
                .isNotNull()
                .extracting(ResponseDto::getType, ResponseDto::getMessage)
                .containsExactly(expectedType, expectedMessage);
    }

    @ParameterizedTest
    @CsvSource({
            "'0', 'Dog', 'Rex', 'available', 'Friendly'",
            "'-100', 'Cat', 'Bella', 'pending', 'Playful'",
            "'abc', 'Rabbit', 'Max', 'sold', 'Curious'"
    })
    void postInvalidPetTest(String petId) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        try {
            long parsedPetId = Long.parseLong(petId);
            PetDto petBody = EntityUtils.buildPetDto(parsedPetId, 1L, "ErrorName", "unknown", "unknown", "ErrorTag");

            Response postResponse = sendPostRequest("/v2/pet/", petBody);
            Response getResponse = sendGetRequest("/v2/pet/" + petId);
            ResponseDto actualResponseE = getResponse.as(ResponseDto.class);

            assertThat(postResponse.getStatusCode()).isEqualTo(200);
            assertThat(getResponse.getStatusCode()).isEqualTo(404);

            assertThat(actualResponseE)
                    .isNotNull()
                    .extracting(ResponseDto::getType, ResponseDto::getMessage)
                    .containsExactly("error", "Pet not found");

        } catch (NumberFormatException e) {
            System.out.println("Ошибка преобразования petId: " + petId);
        }
    }
}
