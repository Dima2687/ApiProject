package apiPetTests;

import apiConfig.BaseTest;

import constants.Constants;
import dto.pet.PetCategoryDto;
import dto.pet.PetDto;
import dto.pet.PetErrorDto;
import dto.pet.PetTagsDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static apiUtils.Utils.sendGetRequest;
import static apiUtils.Utils.sendPostRequest;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetNegativeTests extends BaseTest {

    @ParameterizedTest
    @CsvSource({
            "0, 'error', 'Pet not found'",
            "-1, 'error', 'Pet not found'",
            "abc, 'unknown'," + Constants.NUMBEREXCEPTION
    })
    @Order(1)
    void getInvalidPetTest(String petId, String expectedType, String expectedMessage) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        PetErrorDto actualResponse = sendGetRequest("/v2/pet/" + petId).as(PetErrorDto.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actualResponse),                                     //Проверка: ответ API не `null`
                () -> Assertions.assertEquals(expectedType, actualResponse.getType()),              //Проверка: тип ошибки совпадает
                () -> Assertions.assertEquals(expectedMessage, actualResponse.getMessage())         //Проверка: сообщение ошибки корректное
        );
    }

    @ParameterizedTest
    @CsvSource({
            "'0', 'Dog', 'Rex', 'available', 'Friendly'",
            "'-100', 'Cat', 'Bella', 'pending', 'Playful'",
            "'abc', 'Rabbit', 'Max', 'sold', 'Curious'" // может удалить потому что ошибка не логируется и тест можно будет упростить
    })

    @Order(2)
    void postInvalidPetTest(String petId) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());
        try {
            //long parsedPetId = Long.parseLong(petId); // Преобразуем строку в число

            PetDto petBody = PetDto.builder()
                    .id(Long.parseLong(petId))
                    .category(new PetCategoryDto(1L, "ErrorName"))
                    .name("unknown")
                    .photoUrls(List.of("none"))
                    .tags(List.of(new PetTagsDto(1L, "ErrorTag")))
                    .status("unknown")
                    .build();

            Response postResponse = sendPostRequest("/v2/pet/", petBody);
            PetDto actualResponse = postResponse.as(PetDto.class);

            Response getResponse = sendGetRequest("/v2/pet/" + petId);
            PetErrorDto actualResponseE = getResponse.as(PetErrorDto.class);

            Assertions.assertAll(
                    () -> Assertions.assertEquals(200, postResponse.getStatusCode()),                     //Проверка: статус-код POST-запроса (200)
                    () -> Assertions.assertEquals(404, getResponse.getStatusCode()),                      //Проверка: статус-код POST-запроса (404)
                    () -> Assertions.assertEquals("error", actualResponseE.getType()),                    //Проверка: тип ошибки совпадает ('error')
                    () -> Assertions.assertEquals("Pet not found", actualResponseE.getMessage()),         //Проверка: сообщение ошибки корректное ('Pet not found')
                    () -> Assertions.assertFalse(actualResponse.getTags().isEmpty())                               //Проверка: питомец содержит хотя бы один тег
            );

        } catch (NumberFormatException e) {
            Assertions.assertThrows(NumberFormatException.class, () -> Long.parseLong(petId));
        }
    }
}
