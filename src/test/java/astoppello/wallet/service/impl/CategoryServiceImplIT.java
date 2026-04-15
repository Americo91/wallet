package astoppello.wallet.service.impl;

import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.CategoryMapperImpl;
import astoppello.wallet.mapper.DateMapper;
import astoppello.wallet.mapper.TrackingMapperImpl;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({CategoryServiceImpl.class, CategoryMapperImpl.class, TrackingMapperImpl.class, DateMapper.class})
public class CategoryServiceImplIT {

    private static final String CATEGORY_NAME = "CategoryName";
    @Autowired
    private CategoryService service;

    private CategoryDto parentCategory;

    @BeforeEach
    void setUp() {
        parentCategory = CategoryDto.builder()
                .type(CategoryType.INCOME)
                .name(CATEGORY_NAME)
                .build();
    }

    private CategoryDto buildDto(String name) {
        return CategoryDto.builder()
                .name(name)
                .type(CategoryType.EXPENSE)
                .build();
    }

    @Test
    void save() {
        String checking = "Checking";
        CategoryDto saved = service.save(buildDto(checking));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo(checking);
        assertThat(saved.getType()).isEqualTo(CategoryType.EXPENSE);
        assertThat(saved.getTrackingDate()).isNotNull();
        assertThat(saved.getTrackingDate().getCreatedAt()).isNotNull();
        assertThat(saved.getTrackingDate().getUpdatedAt()).isNotNull();
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void save_parent() {
        String name = "name";
        CategoryDto categoryDto = buildDto(name);
        CategoryDto parent = service.save(parentCategory);
        categoryDto.setParentId(parent.getId());

        CategoryDto saved = service.save(categoryDto);
        assertThat(saved).isNotNull();
        assertThat(saved.getParentId()).isEqualTo(parent.getId());
        assertThat(saved.getType()).isEqualTo(parent.getType());
        assertThat(saved.getName()).isEqualTo(name);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTrackingDate()).isNotNull();

        CategoryDto updatedParent = service.getByID(parent.getId());
        assertThat(updatedParent.getSubcategories()).hasSize(1);
        assertThat(updatedParent.getSubcategories().getFirst().getId()).isEqualTo(saved.getId());
    }

    @Test
    void save_parentNotFound() {
        CategoryDto categoryDto = buildDto("missingParent");
        categoryDto.setParentId(UUID.randomUUID());
        assertThatThrownBy(() -> service.save(categoryDto))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getAll() {
        String health = "health";
        service.save(buildDto(health));
        String games = "games";
        service.save(buildDto(games));

        List<CategoryDto> all = service.getAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(CategoryDto::getName).containsExactlyInAnyOrder(health, games);
    }

    @Test
    void getByID() {
        CategoryDto saved = service.save(buildDto("name"));
        CategoryDto byID = service.getByID(saved.getId());
        assertThat(byID.getId()).isEqualTo(saved.getId());
        assertThat(byID.getName()).isEqualTo("name");
    }

    @Test
    void getByID_notFound() {
        assertThatThrownBy(() -> service.getByID(UUID.randomUUID())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByName() {
        String health = "health";
        service.save(buildDto(health));
        String games = "games";
        service.save(buildDto(games));

        List<CategoryDto> byName = service.getByName(games);
        assertThat(byName).hasSize(1);
        assertThat(byName).extracting(CategoryDto::getName).containsExactly(games);
    }

    @Test
    void getByName_notFound() {
        assertThat(service.getByName("missing")).hasSize(0);
    }

    @Test
    void update() {
        String checking = "Checking";
        CategoryDto saved = service.save(buildDto(checking));

        CategoryDto updated = service.update(saved.getId(), buildDto("updated"));
        assertThat(updated.getName()).isEqualTo("updated");
    }

    @Test
    void delete() {
        String checking = "Checking";
        CategoryDto saved = service.save(buildDto(checking));

        service.delete(saved.getId());
        assertThat(service.getAll()).hasSize(0);
    }


}
