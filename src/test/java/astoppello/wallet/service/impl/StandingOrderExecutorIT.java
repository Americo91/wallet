package astoppello.wallet.service.impl;

import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.repository.StandingOrderRepository;
import astoppello.wallet.repository.TransactionRepository;
import astoppello.wallet.service.AccountService;
import astoppello.wallet.service.CategoryService;
import astoppello.wallet.service.InstitutionService;
import astoppello.wallet.service.StandingOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ComponentScan(
        basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.mapper"},
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = StandingOrderNotificationJob.class)
)
class StandingOrderExecutorIT {

    @Autowired
    private StandingOrderExecutor executor;

    @Autowired
    private StandingOrderService standingOrderService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private StandingOrderRepository standingOrderRepository;

    private AccountDto accountDto;
    private CategoryDto expenseCategory;

    @BeforeEach
    void setUp() {
        InstitutionDto institution = institutionService.save(new InstitutionDto("Bank"));
        accountDto = accountService.save(institution.getId(), new AccountDto("Checking", AccountDto.AccountTypeEnum.LIQUIDITY, BigDecimal.valueOf(1000), AccountDto.CurrencyEnum.EUR));
        expenseCategory = categoryService.save(new CategoryDto("Subscriptions"));
    }

    @Test
    void executeStandingOrders_noDueOrders_noTransactionCreated() {
        standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Spotify")
                .amount(BigDecimal.TEN)
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .type(StandingOrderDto.TypeEnum.EXPENSE)
                .nextOccurrence(LocalDate.now().plusDays(5))
                .category(expenseCategory.getId())
                .enabled(true));

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).isEmpty();
    }

    @Test
    void executeStandingOrders_expenseOrder_createsTransactionAndDeductsBalance() {
        standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Spotify")
                .amount(BigDecimal.TEN)
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .type(StandingOrderDto.TypeEnum.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true));

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(1);
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(990));
    }

    @Test
    void executeStandingOrders_incomeOrder_addsToBalance() {
        CategoryDto incomeCategory = categoryService.save(new CategoryDto("Salary"));

        standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Monthly Salary")
                .amount(BigDecimal.valueOf(3000))
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .type(StandingOrderDto.TypeEnum.INCOME)
                .nextOccurrence(LocalDate.now())
                .category(incomeCategory.getId())
                .enabled(true));

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(1);
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(4000));
    }

    @Test
    void executeStandingOrders_monthlyOrder_advancesNextOccurrenceByOneMonth() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Rent")
                .amount(BigDecimal.valueOf(800))
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .type(StandingOrderDto.TypeEnum.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true));

        executor.executeStandingOrders();

        StandingOrderDto updated = standingOrderService.getByID(saved.getId());
        assertThat(updated.getNextOccurrence()).isEqualTo(LocalDate.now().plusMonths(1));
    }

    @Test
    void executeStandingOrders_weeklyOrder_advancesNextOccurrenceByOneWeek() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Weekly Sub")
                .amount(BigDecimal.valueOf(5))
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.WEEKLY)
                .type(StandingOrderDto.TypeEnum.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true));

        executor.executeStandingOrders();

        StandingOrderDto updated = standingOrderService.getByID(saved.getId());
        assertThat(updated.getNextOccurrence()).isEqualTo(LocalDate.now().plusWeeks(1));
    }

    @Test
    void executeStandingOrders_unaTantumOrder_disablesAfterExecution() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("One-time fee")
                .amount(BigDecimal.valueOf(50))
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.UNA_TANTUM)
                .type(StandingOrderDto.TypeEnum.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true));

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(1);
        StandingOrder domain = standingOrderRepository.findById(saved.getId()).orElseThrow();
        assertThat(domain.isEnabled()).isFalse();
    }

    @Test
    void executeStandingOrders_disabledOrder_isSkipped() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Disabled sub")
                .amount(BigDecimal.valueOf(10))
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .type(StandingOrderDto.TypeEnum.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true));
        standingOrderService.update(saved.getId(), new StandingOrderDto().enabled(false));

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).isEmpty();
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void executeStandingOrders_multipleOrders_allProcessed() {
        CategoryDto incomeCategory = categoryService.save(new CategoryDto()
                .name("Income")
                .type(CategoryDto.TypeEnum.INCOME));

        standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Subscription")
                .amount(BigDecimal.TEN)
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .type(StandingOrderDto.TypeEnum.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true));

        standingOrderService.save(accountDto.getId(), new StandingOrderDto()
                .name("Salary")
                .amount(BigDecimal.valueOf(2000))
                .currency(StandingOrderDto.CurrencyEnum.EUR)
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .type(StandingOrderDto.TypeEnum.INCOME)
                .nextOccurrence(LocalDate.now())
                .category(incomeCategory.getId())
                .enabled(true));

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(2);
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(2990));
    }
}
