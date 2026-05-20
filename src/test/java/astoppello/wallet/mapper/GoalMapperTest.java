package astoppello.wallet.mapper;

import astoppello.wallet.domain.Goal;
import astoppello.wallet.dto.GoalDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {GoalMapperImpl.class, DateMapper.class, TrackingMapperImpl.class})
class GoalMapperTest {

    @Autowired
    private GoalMapper mapper;

    @Test
    void toDto() {
        Goal goal = Goal.builder()
                .id(UUID.randomUUID())
                .name("Emergency Fund")
                .amount(new BigDecimal("10000.00"))
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();

        GoalDto dto = mapper.toDto(goal);

        assertThat(dto.getId()).isEqualTo(goal.getId());
        assertThat(dto.getName()).isEqualTo("Emergency Fund");
        assertThat(dto.getAmount()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDto_nullTrackingDate() {
        Goal goal = Goal.builder()
                .id(UUID.randomUUID())
                .name("Vacation")
                .amount(new BigDecimal("5000.00"))
                .build();

        GoalDto dto = mapper.toDto(goal);

        assertThat(dto.getName()).isEqualTo("Vacation");
        assertThat(dto.getAmount()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(dto.getTrackingDate()).isNull();
    }

    @Test
    void toDomain() {
        GoalDto dto = GoalDto.builder()
                .id(UUID.randomUUID())
                .name("New Car")
                .amount(new BigDecimal("25000.00"))
                .build();

        Goal domain = mapper.toDomain(dto);

        assertThat(domain.getId()).isEqualTo(dto.getId());
        assertThat(domain.getName()).isEqualTo("New Car");
        assertThat(domain.getAmount()).isEqualByComparingTo(new BigDecimal("25000.00"));
        assertThat(domain.getTrackingDate()).isNull();
    }
}