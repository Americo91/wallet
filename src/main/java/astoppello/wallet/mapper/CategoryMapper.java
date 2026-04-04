package astoppello.wallet.mapper;

import astoppello.wallet.domain.Category;
import astoppello.wallet.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    CategoryDto toDto(Category domain);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    Category toDomain(CategoryDto dto);
}