package astoppello.wallet.mapper;

import astoppello.wallet.domain.Category;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.dto.CategoryDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {CategoryMapperImpl.class, TrackingMapperImpl.class, DateMapper.class})
class CategoryMapperTest {

    @Autowired
    private CategoryMapper mapper;

    @Test
    void toDto_parentCategory() {
        Category parent = new Category();
        parent.setId(UUID.randomUUID());
        parent.setName("Housing");
        parent.setType(CategoryType.EXPENSE);
        parent.setTrackingDate(TrackingMapperTest.trackingDate);

        CategoryDto dto = mapper.toDto(parent);

        assertThat(dto.getId()).isEqualTo(parent.getId());
        assertThat(dto.getName()).isEqualTo("Housing");
        assertThat(dto.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(dto.getParentId()).isNull();
        assertThat(dto.getSubcategories()).isEmpty();
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
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
        assertThat(dto.getType()).isNull();
        assertThat(dto.getParentId()).isEqualTo(parent.getId());
        assertThat(dto.getSubcategories()).isEmpty();
        assertThat(dto.getTrackingDate()).isNull();
    }

    @Test
    void toDto_withSubcategories() {
        Category parent = new Category();
        parent.setId(UUID.randomUUID());
        parent.setName("Food & Dining");
        parent.setType(CategoryType.EXPENSE);

        Category sub1 = new Category();
        sub1.setId(UUID.randomUUID());
        sub1.setName("Groceries");
        sub1.setParent(parent);

        Category sub2 = new Category();
        sub2.setId(UUID.randomUUID());
        sub2.setName("Restaurants");
        sub2.setParent(parent);

        parent.setSubcategories(List.of(sub1, sub2));

        CategoryDto dto = mapper.toDto(parent);

        assertThat(dto.getSubcategories()).hasSize(2);
        assertThat(dto.getSubcategories()).extracting(CategoryDto::getName)
                .containsExactly("Groceries", "Restaurants");
        assertThat(dto.getSubcategories()).extracting(CategoryDto::getParentId)
                .containsOnly(parent.getId());
    }

    @Test
    void toDomain() {
        UUID id = UUID.randomUUID();
        UUID parentId = UUID.randomUUID();

        CategoryDto dto = CategoryDto.builder()
                .id(id)
                .name("Electricity")
                .parentId(parentId)
                .build();

        Category domain = mapper.toDomain(dto);

        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getName()).isEqualTo("Electricity");
        assertThat(domain.getType()).isNull();
        assertThat(domain.getParent()).isNull();
        assertThat(domain.getSubcategories()).isNull();
    }

    @Test
    void toDomain_parentCategory() {
        CategoryDto dto = CategoryDto.builder()
                .id(UUID.randomUUID())
                .name("Income")
                .type(CategoryType.INCOME)
                .build();

        Category domain = mapper.toDomain(dto);

        assertThat(domain.getName()).isEqualTo("Income");
        assertThat(domain.getType()).isEqualTo(CategoryType.INCOME);
        assertThat(domain.getParent()).isNull();
    }
}