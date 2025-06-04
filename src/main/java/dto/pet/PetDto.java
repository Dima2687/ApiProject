package dto.pet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetDto {
    private Long id;
    private PetCategoryDto category;
    private String name;
    private List<String> photoUrls;
    private List<PetTagsDto> tags;
    private String status;
}
