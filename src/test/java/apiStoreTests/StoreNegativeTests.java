package apiStoreTests;

import apiConfig.BaseTest;
import apiUtils.ApiRequests;
import dto.store.OrderDto;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static apiUtils.EntityUtils.buildOrderDto;
import static org.assertj.core.api.Assertions.assertThat;

public class StoreNegativeTests extends ApiRequests {
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

        OrderDto orderBody = buildOrderDto(id, quantity, status, complete);

        Response response = sendPostRequest("/v2/store/order", orderBody);

        assertThat(response.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(message).isNotBlank();

    }

    @ParameterizedTest
    @CsvSource({
            "999999, 404, Не существует такого заказа",   // ID несуществующего заказа
            "null, 404, Некорректный ID заказа",          // null в ID
            "abc, 404, Некорректный формат ID"            // Некорректный ID (строка вместо числа)
    })
    void deleteInvalidOrderTest(String id, int expectedStatus, String message) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Response deleteResponse = sendDeleteRequest("/v2/store/order/" + id);

        assertThat(deleteResponse.getStatusCode()).isEqualTo(expectedStatus);
        assertThat(message).isNotBlank();
    }
}
