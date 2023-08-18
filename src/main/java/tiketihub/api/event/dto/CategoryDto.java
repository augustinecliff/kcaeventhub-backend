package tiketihub.api.event.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import tiketihub.api.event.model.Category;

@Getter
@NoArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;

    public CategoryDto(Long id,String name) {
        this.id = id;
        this.name = name;
    }
    public static CategoryDto CategoryToCategoryDtoConversion(Category category) {
        return new CategoryDto(category.getId(),
                category.getName());
    }
    public static Category categoryDtoToCategoryConversion(CategoryDto category) {
        return new Category(category.getId(),
                category.getName());
    }
}
