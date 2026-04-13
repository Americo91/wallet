package astoppello.wallet.repository;

import astoppello.wallet.domain.Category;
import astoppello.wallet.model.CategoryType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByName() {
        String name = "categoryName";
        categoryRepository.save(Category.builder().type(CategoryType.INCOME).name(name).build());

        assertThat(categoryRepository.findByName(name)).isNotEmpty();
    }
}