package apiStoreTests;

import apiConfig.BaseTest;
import apiUtils.ApiRequests;
import dto.store.OrderDto;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static apiUtils.EntityUtils.buildOrderDto;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreAffirmativeTests extends ApiRequests {

    @ParameterizedTest
    @CsvSource({
            "7, 10, placed, true",
            "8, 5, approved, false",
            "9, 20, delivered, true"
    })
    @Order(1)
    void createOrderTest(Long id, int quantity, String status, boolean complete) {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        OrderDto orderBody = buildOrderDto(id, quantity, status, complete);
        long petId = orderBody.getPetId();

        Response response = sendPostRequest("/v2/store/order", orderBody);
        OrderDto actualResponse = response.as(OrderDto.class);

        assertThat(response.getStatusCode()).isEqualTo(200);

        assertThat(actualResponse)
                .isNotNull()
                .extracting(OrderDto::getId, OrderDto::getPetId, OrderDto::getQuantity, OrderDto::getStatus, OrderDto::getComplete)
                .containsExactly(id, petId, quantity, status, complete);
    }

    /**
     * Тест запускать с задержкой потому что сайт не сразу отрабатывает запросы
     */

    @ParameterizedTest
    @ValueSource(ints = {7, 8, 9})
    @Order(2)
    void deleteSingleOrderTest(int orderId) throws InterruptedException {
        BaseTest.installSpec(BaseTest.requestSpec(BaseTest.get("base.url")), BaseTest.responseSpec());

        Thread.sleep(3000);
        Response deleteResponse = sendDeleteRequest("/v2/store/order/" + orderId);
        Response getResponse = sendGetRequest("/v2/store/order/" + orderId);

        Assertions.assertAll(
                () -> Assertions.assertEquals(200, deleteResponse.getStatusCode()),
                () -> Assertions.assertEquals(404, getResponse.getStatusCode())
        );
    }
}
