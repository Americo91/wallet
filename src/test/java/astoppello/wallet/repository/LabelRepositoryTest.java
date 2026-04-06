package astoppello.wallet.repository;

import astoppello.wallet.domain.Label;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LabelRepositoryTest {

    @Autowired
    private LabelRepository labelRepository;

    @Test
    void findByName() {
        String name = "labelName";
        labelRepository.save(Label.builder().name(name).build());
        assertThat(labelRepository.findByName(name).isPresent()).isTrue();
    }

    @Test
    void testManyToManyCascadeDelete() {
        //TODO
    }
}