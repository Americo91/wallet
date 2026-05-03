package astoppello.wallet.repository;

import astoppello.wallet.domain.Category;
import astoppello.wallet.model.CategoryType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;
    private String NAME = "categoryName";

    @Test
    void findByName() {
        categoryRepository.save(Category.builder().type(CategoryType.INCOME).name(NAME).build());

        assertThat(categoryRepository.findByName(NAME)).isNotEmpty();
    }

    @Test
    void findByNameAndType() {
        categoryRepository.save(Category.builder().type(CategoryType.INCOME).name(NAME).build());
        assertThat(categoryRepository.findByNameAndType(NAME, CategoryType.INCOME)).isNotEmpty();
    }
}