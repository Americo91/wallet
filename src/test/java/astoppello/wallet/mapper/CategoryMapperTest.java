package astoppello.wallet.mapper;

import astoppello.wallet.domain.Category;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CategoryMapperImpl.class, DateMapper.class})
class CategoryMapperTest {

    @Autowired
    private CategoryMapper mapper;

    @Test
    void toDto_parentCategory() {
        Category parent = new Category();
        parent.setId(UUID.randomUUID());
        parent.setName("Housing");
        parent.setType(CategoryType.EXPENSE);
        parent.setTrackingDate(TestTrackingData.trackingDate);

        CategoryDto dto = mapper.toDto(parent);

        assertThat(dto.getId()).isEqualTo(parent.getId());
        assertThat(dto.getName()).isEqualTo("Housing");
        assertThat(dto.getType().name()).isEqualTo(CategoryType.EXPENSE.name());
        assertThat(dto.getParentId()).isNull();
        assertThat(dto.getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDto_subcategory() {
        Category parent = new Category();
        parent.setId(UUID.randomUUID());
        parent.setName("Housing");
        parent.setType(CategoryType.EXPENSE);

        Category sub = new Category();
        sub.setId(UUID.randomUUID());
        sub.setName("Rent & Mortgage");
        sub.setParent(parent);

        CategoryDto dto = mapper.toDto(sub);

        assertThat(dto.getId()).isEqualTo(sub.getId());
        assertThat(dto.getName()).isEqualTo("Rent & Mortgage");
        assertThat(dto.getType().name()).isEqualTo(parent.getType().name());
        assertThat(dto.getParentId()).isEqualTo(parent.getId());
        assertThat(dto.getCreatedAt()).isNull();
    }

    @Test
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        CategoryDto dto = new CategoryDto("Electricity")
                .id(id)
                .parentId(parentId);

        Category domain = mapper.toDomain(dto);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("Electricity");
        assertThat(domain.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(domain.getParent()).isNull();
        assertThat(domain.getSubcategories()).isNull();
    }

    @Test
    void toDomain_parentCategory() {
        CategoryDto dto = new CategoryDto("Income")
                .id(UUID.randomUUID())
                .type(CategoryDto.TypeEnum.INCOME);

        Category domain = mapper.toDomain(dto);

        assertThat(domain.getName()).isEqualTo("Income");
        assertThat(domain.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(domain.getParent()).isNull();
    }
}
