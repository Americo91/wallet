package astoppello.wallet.repository;

import astoppello.wallet.domain.Goal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class GoalRepositoryTest {

    @Autowired
    private GoalRepository goalRepository;

    @Test
    void findByName() {
        String name = "Emergency Fund";
        goalRepository.save(Goal.builder().name(name).amount(new BigDecimal("10000.00")).build());
        assertThat(goalRepository.findByName(name).isPresent()).isTrue();
    }
}