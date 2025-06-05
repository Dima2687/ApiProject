package apiPetTests;

import apiConfig.BaseTest;
import apiUtils.ApiRequests;
import dto.pet.PetCategoryDto;
import dto.pet.PetDto;
import dto.pet.PetTagsDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static apiUtils.EntityUtils.buildPetDto;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetAffirmativeTests extends ApiRequests {

    @ParameterizedTest
    @CsvSource({
            "54, 1, Dog, Rex, available, Friendly",
            "2000, 2, Cat, Bella, pending, Playful",
            "696, 3, Rabbit, Max, sold, Curious"
    })
    @Order(1)
    void createPetTest(long petId, long categoryId, String categoryName, String petName, String status, String tagName) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());
        PetDto petBody = buildPetDto(petId, categoryId, categoryName, petName, status, tagName);

        Response response = ApiRequests.sendPostRequest("/v2/pet", petBody);
        PetDto actualResponse = response.as(PetDto.class);

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(actualResponse)
                .isNotNull()
                .extracting(PetDto::getId, PetDto::getName, PetDto::getStatus)
                .containsExactly(petBody.getId(), petBody.getName(), petBody.getStatus());

        assertThat(actualResponse.getCategory())
                .isNotNull()
                .extracting(PetCategoryDto::getName)
                .isEqualTo(petBody.getCategory().getName());

        assertThat(actualResponse.getTags())
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(1)
                .extracting(PetTagsDto::getName)
                .contains(tagName);
    }

    /**
     * В этом тесте захардкожены id потому что на сервере постоянно меняются данные в id  и что б запрос успешно прошёл
     */

    @ParameterizedTest
    @ValueSource(longs = {54, 2000, 696})
    @Order(2)
    void getPetIdTest(long petId) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendGetRequest("/v2/pet/" + petId);
        PetDto actualResponse = response.as(PetDto.class);

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(actualResponse)
                .isNotNull()
                .extracting(PetDto::getId, PetDto::getName, PetDto::getStatus)
                .containsExactly(petId, actualResponse.getName(), actualResponse.getStatus());

        assertThat(actualResponse.getCategory()
                .getName())
                .isNotNull()
                .isNotEmpty();

        assertThat(actualResponse.getTags())
                .isNotNull()
                .hasSizeGreaterThanOrEqualTo(1);

    }
}
