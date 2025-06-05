package apiStoreTests;

import apiConfig.BaseTest;
import apiUtils.Utils;
import dto.store.OrderDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class StoreAffirmativeTests extends Utils {

    @ParameterizedTest
    @CsvSource({
            "7, 10, placed, true",
            "8, 5, approved, false",
            "9, 20, delivered, true"
    })
    void createOrderTest(Long id, int quantity, String status, boolean complete) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        //long petId = ThreadLocalRandom.current().nextLong(500, 9999);

        OrderDto orderBody = OrderDto.builder()
                .id(id)
                .petId(ThreadLocalRandom.current().nextLong(500, 9999))
                .quantity(quantity)
                .shipDate(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) // Генерируем текущее UTC-время
                .status(status)
                .complete(complete)
                .build();

        Response response = sendPostRequest("/v2/store/order", orderBody);
        OrderDto actualResponse = response.as(OrderDto.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(200, response.getStatusCode()),                     // Ожидался статус 200
                () -> Assertions.assertEquals(orderBody.getId(), actualResponse.getId()),                 //ID заказа не совпадает
                () -> Assertions.assertEquals(orderBody.getPetId(), actualResponse.getPetId()),           //ID питомца не совпадает
                () -> Assertions.assertEquals(orderBody.getQuantity(), actualResponse.getQuantity()),     //Количество не совпадает
                () -> Assertions.assertEquals(orderBody.getStatus(), actualResponse.getStatus()),        //Статус заказа не совпадает
                () -> Assertions.assertEquals(orderBody.getComplete(), actualResponse.getComplete())        //Значение `complete` не совпадает
        );
    }

    /**
     *Тест запускать секунд 10 потому что сайт не сразу отрабатывает запросы
     */

    @Test
    void deleteSingleOrderTest() {
        int id = 7; // Можно изменить на 8 или 9 при необходимости

        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response deleteResponse = DeleteRequest("/v2/store/order/" + id);
        Assertions.assertEquals(200, deleteResponse.getStatusCode());

        Response getResponse = sendGetRequest("/v2/store/order/" + id);
        Assertions.assertEquals(404, getResponse.getStatusCode());
    }
}
