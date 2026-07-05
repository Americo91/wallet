package astoppello.wallet.mapper;

import astoppello.wallet.domain.Category;
import astoppello.wallet.dto.CategoryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class})
public interface CategoryMapper {

    @Mapping(target = "parentId", source = "parent.id")
    @Mapping(target = "createdAt", source = "trackingDate.createdAt")
    @Mapping(target = "updatedAt", source = "trackingDate.updatedAt")
    CategoryDto toDto(Category domain);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "trackingDate", ignore = true)
    Category toDomain(CategoryDto dto);
}
