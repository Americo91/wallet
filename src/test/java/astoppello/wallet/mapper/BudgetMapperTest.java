package astoppello.wallet.mapper;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.BudgetDto;
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
@ContextConfiguration(classes = {BudgetMapperImpl.class, DateMapper.class})
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
                .trackingDate(TestTrackingData.trackingDate)
                .build();


        BudgetDto dto = budgetMapper.toDto(budget);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(budget.getId());
        assertThat(dto.getName()).isEqualTo(budget.getName());
        assertThat(dto.getPeriod().name()).isEqualTo(budget.getPeriod().name());
        assertThat(dto.getBudgetLimit()).isEqualTo(budget.getBudgetLimit());
        assertThat(dto.getCategoryIds()).isEqualTo(budget.getCategories().stream().map(Category::getId).collect(Collectors.toSet()));
        assertThat(dto.getAccountIds()).isEqualTo(budget.getAccounts().stream().map(Account::getId).collect(Collectors.toSet()));
        assertThat(dto.getLabelIds()).isEqualTo(budget.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
        assertThat(dto.getClosed()).isEqualTo(budget.isClosed());
        assertThat(dto.getOverLimit()).hasSize(1);
        astoppello.wallet.dto.Overlimit dtoOverlimit = dto.getOverLimit().iterator().next();
        assertThat(dtoOverlimit.getLimit()).isEqualByComparingTo(overlimit.getLimit());
        assertThat(dtoOverlimit.getPeriodAmount()).isEqualByComparingTo(overlimit.getPeriodAmount());
        assertThat(dtoOverlimit.getPeriodStart()).isEqualTo(overlimit.getPeriodStart());
        assertThat(dtoOverlimit.getPeriodEnd()).isEqualTo(overlimit.getPeriodEnd());
        assertThat(dto.getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDomain() {
        astoppello.wallet.dto.Overlimit dtoOverlimit = new astoppello.wallet.dto.Overlimit()
                .limit(BigDecimal.TEN)
                .periodAmount(new BigDecimal("11.00"))
                .periodStart(LocalDate.of(2026, 1, 1))
                .periodEnd(LocalDate.of(2026, 1, 31));

        BudgetDto dto = new BudgetDto("budget name", BudgetDto.PeriodEnum.MONTHLY, BigDecimal.TEN,
                Set.of(UUID.randomUUID()), Set.of(UUID.randomUUID()), Set.of(UUID.randomUUID()))
                .id(UUID.randomUUID())
                .closed(false)
                .overLimit(Set.of(dtoOverlimit));

        Budget budget = budgetMapper.toDomain(dto);
        assertThat(budget).isNotNull();
        assertThat(budget.getId()).isEqualTo(dto.getId());
        assertThat(budget.getName()).isEqualTo(dto.getName());
        assertThat(budget.getPeriod().name()).isEqualTo(dto.getPeriod().name());
        assertThat(budget.getBudgetLimit()).isEqualTo(dto.getBudgetLimit());
        assertThat(budget.isClosed()).isEqualTo(dto.getClosed());
        assertThat(budget.getCategories()).isEmpty();
        assertThat(budget.getAccounts()).isEmpty();
        assertThat(budget.getLabels()).isEmpty();
        assertThat(budget.getOverLimit()).hasSize(1);
        Overlimit domainOverlimit = budget.getOverLimit().iterator().next();
        assertThat(domainOverlimit.getLimit()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(domainOverlimit.getPeriodAmount()).isEqualByComparingTo(new BigDecimal("11.00"));
        assertThat(budget.getTrackingDate()).isNull();
    }
}
