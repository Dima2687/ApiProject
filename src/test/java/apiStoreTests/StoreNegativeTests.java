package apiStoreTests;

import apiConfig.BaseTest;
import dto.store.OrderDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import static apiUtils.Utils.DeleteRequest;
import static apiUtils.Utils.sendPostRequest;

public class StoreNegativeTests {
    /**
     * В этом тесте на сервере бага, они все проходят и приходит 200, вместо того, что бы выкинуть 400, сервер принимает некорректные данные.
     */

    @ParameterizedTest
    @CsvSource({
            "0, 10, placed, true, 400, Некорректный ID",                   // `null` в ID
            "4, -5, approved, false, 400, Отрицательное количество",       // Отрицательное значение в `quantity`
            "5, 10, unknown, true, 400, Некорректный статус",              // Некорректный статус заказа (бага)
            "6, 10, placed, , 400, Null в complete",                       // `null` в поле `complete`
            "7, 10, placed, true, 500, Ошибка сервера"                     // Форсированная серверная ошибка
    })
    void createInvalidOrderTest(Long id, int quantity, String status, Boolean complete, int expectedStatus, String message) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        OrderDto orderBody = OrderDto.builder()
                .id(id)
                .petId(ThreadLocalRandom.current().nextLong(500, 9999))
                .quantity(quantity)
                .shipDate(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .status(status)
                .complete(complete)
                .build();

        Response response = sendPostRequest("/v2/store/order", orderBody);

        Assertions.assertEquals(expectedStatus, response.getStatusCode(), message);
    }

    @ParameterizedTest
    @CsvSource({
            "999999, 404, Не существует такого заказа",   // ID несуществующего заказа
            "null, 404, Некорректный ID заказа",          // null в ID
            "abc, 404, Некорректный формат ID"            // Некорректный ID (строка вместо числа)
    })
    void deleteInvalidOrderTest(String id, int expectedStatus, String message) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response deleteResponse = DeleteRequest("/v2/store/order/" + id);
        Assertions.assertEquals(expectedStatus, deleteResponse.getStatusCode(), message);
    }
}
