package astoppello.wallet.service.impl;

import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.dto.*;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.repository.StandingOrderRepository;
import astoppello.wallet.repository.TransactionRepository;
import astoppello.wallet.service.*;
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
        InstitutionDto institution = institutionService.save(InstitutionDto.builder().name("Bank").build());
        accountDto = accountService.save(institution.getId(), AccountDto.builder()
                .name("Checking")
                .balance(BigDecimal.valueOf(1000))
                .currency(Currency.EUR)
                .build());
        expenseCategory = categoryService.save(CategoryDto.builder()
                .name("Subscriptions")
                .type(CategoryType.EXPENSE)
                .build());
    }

    @Test
    void executeStandingOrders_noDueOrders_noTransactionCreated() {
        standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Spotify")
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.now().plusDays(5))
                .category(expenseCategory.getId())
                .enabled(true)
                .build());

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).isEmpty();
    }

    @Test
    void executeStandingOrders_expenseOrder_createsTransactionAndDeductsBalance() {
        standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Spotify")
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true)
                .build());

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(1);
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(990));
    }

    @Test
    void executeStandingOrders_incomeOrder_addsToBalance() {
        CategoryDto incomeCategory = categoryService.save(CategoryDto.builder()
                .name("Salary")
                .type(CategoryType.INCOME)
                .build());

        standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Monthly Salary")
                .amount(BigDecimal.valueOf(3000))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.INCOME)
                .nextOccurrence(LocalDate.now())
                .category(incomeCategory.getId())
                .enabled(true)
                .build());

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(1);
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(4000));
    }

    @Test
    void executeStandingOrders_monthlyOrder_advancesNextOccurrenceByOneMonth() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Rent")
                .amount(BigDecimal.valueOf(800))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true)
                .build());

        executor.executeStandingOrders();

        StandingOrderDto updated = standingOrderService.getByID(saved.getId());
        assertThat(updated.getNextOccurrence()).isEqualTo(LocalDate.now().plusMonths(1));
    }

    @Test
    void executeStandingOrders_weeklyOrder_advancesNextOccurrenceByOneWeek() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Weekly Sub")
                .amount(BigDecimal.valueOf(5))
                .currency(Currency.EUR)
                .frequency(Frequency.WEEKLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true)
                .build());

        executor.executeStandingOrders();

        StandingOrderDto updated = standingOrderService.getByID(saved.getId());
        assertThat(updated.getNextOccurrence()).isEqualTo(LocalDate.now().plusWeeks(1));
    }

    @Test
    void executeStandingOrders_unaTantumOrder_disablesAfterExecution() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("One-time fee")
                .amount(BigDecimal.valueOf(50))
                .currency(Currency.EUR)
                .frequency(Frequency.UNA_TANTUM)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true)
                .build());

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(1);
        StandingOrder domain = standingOrderRepository.findById(saved.getId()).orElseThrow();
        assertThat(domain.isEnabled()).isFalse();
    }

    @Test
    void executeStandingOrders_disabledOrder_isSkipped() {
        StandingOrderDto saved = standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Disabled sub")
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true)
                .build());
        standingOrderService.update(saved.getId(), StandingOrderDto.builder().enabled(false).build());

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).isEmpty();
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    void executeStandingOrders_multipleOrders_allProcessed() {
        CategoryDto incomeCategory = categoryService.save(CategoryDto.builder()
                .name("Income")
                .type(CategoryType.INCOME)
                .build());

        standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Subscription")
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.now())
                .category(expenseCategory.getId())
                .enabled(true)
                .build());

        standingOrderService.save(accountDto.getId(), StandingOrderDto.builder()
                .name("Salary")
                .amount(BigDecimal.valueOf(2000))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.INCOME)
                .nextOccurrence(LocalDate.now())
                .category(incomeCategory.getId())
                .enabled(true)
                .build());

        executor.executeStandingOrders();

        assertThat(transactionRepository.findAll()).hasSize(2);
        AccountDto updated = accountService.getByID(accountDto.getId());
        assertThat(updated.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(2990));
    }
}
