package apiUtils;

import dto.pet.PetCategoryDto;
import dto.pet.PetDto;
import dto.pet.PetTagsDto;
import dto.store.OrderDto;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class EntityUtils {
    private static final Random RANDOM = new Random();

    public static long generateRandomPetId() {
        return RANDOM.nextLong(10, 999);
    }

    public static PetDto buildPetDto(long petId, long categoryId, String categoryName, String petName, String status, String tagName) {
        return PetDto.builder()
                .id(petId)
                .category(new PetCategoryDto(categoryId, categoryName))
                .name(petName)
                .photoUrls(List.of("none"))
                .tags(List.of(new PetTagsDto(generateRandomPetId(), tagName)))
                .status(status)
                .build();
    }

    public static OrderDto buildOrderDto(Long id, int quantity, String status, boolean complete) {
        return OrderDto.builder()
                .id(id)
                .petId(generateRandomPetId())
                .quantity(quantity)
                .shipDate(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .status(status)
                .complete(complete)
                .build();
    }
}
