package apiPetTests;

import apiConfig.BaseTest;
import apiUtils.Utils;
import dto.pet.PetCategoryDto;
import dto.pet.PetDto;
import dto.pet.PetTagsDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PetAffirmativeTests extends Utils {

    @ParameterizedTest
    @CsvSource({
            "54, 1, Dog, Rex, available, Friendly",
            "2000, 2, Cat, Bella, pending, Playful",
            "696, 3, Rabbit, Max, sold, Curious"
    })
    @Order(1)
    void createPetTest(long petId, long categoryId, String categoryName, String petName, String status, String tagName) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        // Генерируем случайные ID
        //long tagId = ThreadLocalRandom.current().nextLong(500, 999);

        PetDto petBody = PetDto.builder()
                .id(petId)
                .category(new PetCategoryDto(categoryId, categoryName))
                .name(petName)
                .photoUrls(List.of("none"))
                .tags(List.of(new PetTagsDto(ThreadLocalRandom.current().nextLong(500, 999), tagName)))
                .status(status)
                .build();

        Response response = Utils.sendPostRequest("/v2/pet", petBody);
        PetDto actualResponse = response.as(PetDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(200, response.getStatusCode()),                                   //Проверка статус-кода ответа (200)
                () -> Assertions.assertEquals(petBody.getId(), actualResponse.getId()),                                 //Проверка ID питомца
                () -> Assertions.assertEquals(petBody.getName(), actualResponse.getName()),                             //Проверка имени питомца
                () -> Assertions.assertEquals(petBody.getStatus(), actualResponse.getStatus()),                         //Проверка статуса питомца
                () -> Assertions.assertEquals(petBody.getCategory().getName(), actualResponse.getCategory().getName()), //Проверка категории питомца
                () -> assertThat(actualResponse.getTags()).isNotNull().hasSizeGreaterThanOrEqualTo(1),         //Проверка, что в tags есть хотя бы один элемент.
                () -> Assertions.assertEquals(tagName, actualResponse.getTags().get(0).getName())                       //Проверка имени первого тега
        );
    }

    /**
     Тест запускать секунд 10 потому что сайт не сразу отрабатывает запросы(если запуск производится по 1 тесту)
     **/

    @ParameterizedTest
    @ValueSource(ints = {54, 2000, 696})
    @Order(2)
    public void getPetIdTest(long petId) throws InterruptedException {
        Thread.sleep(700);
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response response = sendGetRequest("/v2/pet/" + petId);
        PetDto actualResponse = response.as(PetDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(200, response.getStatusCode()),                                       //Проверка статус-кода (200)
                () -> Assertions.assertNotNull(actualResponse),                                                             //Проверка: ответ API не `null`
                () -> Assertions.assertEquals(petId, actualResponse.getId()),                                               //Проверка: ID питомца совпадает
                () -> Assertions.assertFalse(actualResponse.getName().isEmpty()),                                           //Проверка: имя питомца не пустое
                () -> Assertions.assertNotNull(actualResponse.getCategory()),                                               //Проверка: категория питомца существует
                () -> Assertions.assertTrue(List.of("available", "pending", "sold").contains(actualResponse.getStatus())),  //Проверка: статус питомца корректный
                () -> assertThat(actualResponse.getTags()).isNotNull().hasSizeGreaterThanOrEqualTo(1)              //Проверка: у питомца есть хотя бы один тег
        );
    }
}
