package astoppello.wallet.service.impl;

import astoppello.wallet.dto.GoalDto;
import astoppello.wallet.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ComponentScan(basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.mapper"})
class GoalServiceImplIT {

    public static final String GOAL_NAME = "Emergency Fund";
    public static final BigDecimal GOAL_AMOUNT = new BigDecimal("10000.00");

    @Autowired
    private GoalService service;

    private GoalDto goalDto;
    private GoalDto saved;

    @BeforeEach
    void setUp() {
        goalDto = GoalDto.builder()
                .name(GOAL_NAME)
                .amount(GOAL_AMOUNT)
                .build();
        saved = service.save(goalDto);
    }

    @Test
    void getAll() {
        service.save(GoalDto.builder().name("Vacation").amount(new BigDecimal("5000.00")).build());

        List<GoalDto> all = service.getAll();
        assertThat(all).hasSize(2);
        assertThat(all).extracting(GoalDto::getName).containsExactly(GOAL_NAME, "Vacation");
    }

    @Test
    void getByID() {
        GoalDto byID = service.getByID(saved.getId());
        assertThat(byID).isNotNull();
        assertThat(byID.getName()).isEqualTo(GOAL_NAME);
        assertThat(byID.getAmount()).isEqualByComparingTo(GOAL_AMOUNT);
        assertThat(byID.getId()).isNotNull();
        assertThat(byID.getTrackingDate()).isNotNull();
        assertThat(byID.getTrackingDate().getCreatedAt()).isNotNull();
        assertThat(byID.getTrackingDate().getUpdatedAt()).isNotNull();
    }

    @Test
    void getByName() {
        assertThat(service.getByName(GOAL_NAME)).isNotNull();
    }

    @Test
    void save() {
        GoalDto saved1 = service.save(goalDto);
        assertThat(saved1.getId()).isNotNull();
    }

    @Test
    void update() {
        String name = "New Car";
        BigDecimal newAmount = new BigDecimal("25000.00");
        GoalDto updated = service.update(saved.getId(), GoalDto.builder().name(name).amount(newAmount).build());
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo(name);
        assertThat(updated.getAmount()).isEqualByComparingTo(newAmount);
        assertThat(updated.getTrackingDate().getUpdatedAt()).isNotEqualTo(saved.getTrackingDate().getUpdatedAt());
    }

    @Test
    void delete() {
        service.delete(saved.getId());
        assertThat(service.getAll()).hasSize(0);
    }
}