package astoppello.wallet.mapper;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.BudgetDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.model.Frequency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {BudgetMapperImpl.class, DateMapper.class, TrackingMapperImpl.class})
class BudgetMapperTest {

    @Autowired
    private BudgetMapper budgetMapper;
    private Overlimit overlimit;

    @BeforeEach
    void setUp() {
        overlimit = new Overlimit(BigDecimal.TEN, new BigDecimal("11.00"), LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
    }

    @Test
    void toDto() {
        Budget budget = Budget.builder()
                .id(UUID.randomUUID())
                .name("budget name")
                .period(Frequency.MONTHLY)
                .budgetLimit(BigDecimal.TEN)
                .categories(Set.of(Category.builder().id(UUID.randomUUID()).build()))
                .accounts(Set.of(Account.builder().id(UUID.randomUUID()).build()))
                .labels(Set.of(Label.builder().id(UUID.randomUUID()).build()))
                .overLimit(Set.of(overlimit))
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();


        BudgetDto dto = budgetMapper.toDto(budget);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(budget.getId());
        assertThat(dto.getName()).isEqualTo(budget.getName());
        assertThat(dto.getPeriod()).isEqualTo(budget.getPeriod());
        assertThat(dto.getBudgetLimit()).isEqualTo(budget.getBudgetLimit());
        assertThat(dto.getCategoryIds()).isEqualTo(budget.getCategories().stream().map(Category::getId).collect(Collectors.toSet()));
        assertThat(dto.getAccountIds()).isEqualTo(budget.getAccounts().stream().map(Account::getId).collect(Collectors.toSet()));
        assertThat(dto.getLabelIds()).isEqualTo(budget.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
        assertThat(dto.isClosed()).isEqualTo(budget.isClosed());
        assertThat(dto.getOverLimit()).isEqualTo(budget.getOverLimit());
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDomain() {
        BudgetDto dto = BudgetDto.builder()
                .id(UUID.randomUUID())
                .name("budget name")
                .period(Frequency.MONTHLY)
                .budgetLimit(BigDecimal.TEN)
                .categoryIds(Set.of(UUID.randomUUID()))
                .accountIds(Set.of(UUID.randomUUID()))
                .labelIds(Set.of(UUID.randomUUID()))
                .closed(false)
                .overLimit(Set.of(overlimit))
                .trackingDate(TrackingDateDto.builder().updatedAt(TrackingMapperTest.UPDATED_AT_OFFSET).createdAt(TrackingMapperTest.CREATED_AT_OFFSET).build())
                .build();

        Budget budget = budgetMapper.toDomain(dto);
        assertThat(budget).isNotNull();
        assertThat(budget.getId()).isEqualTo(dto.getId());
        assertThat(budget.getName()).isEqualTo(dto.getName());
        assertThat(budget.getPeriod()).isEqualTo(dto.getPeriod());
        assertThat(budget.getBudgetLimit()).isEqualTo(dto.getBudgetLimit());
        assertThat(budget.isClosed()).isEqualTo(dto.isClosed());
        assertThat(budget.getCategories()).isEmpty();
        assertThat(budget.getAccounts()).isEmpty();
        assertThat(budget.getLabels()).isEmpty();
        assertThat(budget.getOverLimit()).isEqualTo(dto.getOverLimit());
        assertThat(budget.getTrackingDate()).isNotNull();
        assertThat(budget.getTrackingDate().getUpdatedAt()).isEqualTo(TrackingMapperTest.UPDATED_AT);
        assertThat(budget.getTrackingDate().getCreatedAt()).isEqualTo(TrackingMapperTest.CREATED_AT);
    }
}