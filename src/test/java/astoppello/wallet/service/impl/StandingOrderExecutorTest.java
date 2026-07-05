package astoppello.wallet.service.impl;

import astoppello.wallet.domain.*;
import astoppello.wallet.mapper.StandingOrderMapper;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.StandingOrderRepository;
import astoppello.wallet.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandingOrderExecutorTest {

    @Mock
    private StandingOrderRepository standingOrderRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private StandingOrderMapper standingOrderMapper;

    @InjectMocks
    private StandingOrderExecutor executor;

    @Test
    void executeStandingOrders_noDueOrders() {
        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of());

        executor.executeStandingOrders();

        verifyNoInteractions(transactionRepository, accountRepository, standingOrderMapper);
    }

    @Test
    void executeStandingOrders_createsTransactionAndUpdatesBalance_expense() {
        Account account = Account.builder().id(UUID.randomUUID()).name("Revolut")
                .balance(BigDecimal.valueOf(1000)).build();
        Category category = Category.builder().id(UUID.randomUUID()).name("Subscriptions").build();
        Timestamp occurrence = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusHours(10));

        StandingOrder order = StandingOrder.builder()
                .id(UUID.randomUUID())
                .name("Spotify")
                .amount(BigDecimal.valueOf(10))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(occurrence)
                .account(account)
                .category(category)
                .labels(new HashSet<>())
                .enabled(true)
                .build();

        Transaction mappedTransaction = Transaction.builder()
                .account(account)
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.valueOf(10))
                .date(occurrence)
                .category(category)
                .labels(new HashSet<>())
                .build();

        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order));
        when(standingOrderMapper.toTransaction(order)).thenReturn(mappedTransaction);

        executor.executeStandingOrders();

        verify(transactionRepository).save(mappedTransaction);
        assertThat(mappedTransaction.getTrackingDate()).isNotNull();

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(990));
        verify(accountRepository).save(account);

        // nextOccurrence should be advanced by 1 month
        LocalDateTime expectedNext = occurrence.toLocalDateTime().plusMonths(1);
        assertThat(order.getNextOccurrence()).isEqualTo(Timestamp.valueOf(expectedNext));
        verify(standingOrderRepository).save(order);
    }

    @Test
    void executeStandingOrders_createsTransactionAndUpdatesBalance_income() {
        Account account = Account.builder().id(UUID.randomUUID()).name("Checking")
                .balance(BigDecimal.valueOf(500)).build();
        Category category = Category.builder().id(UUID.randomUUID()).name("Salary").build();
        Timestamp occurrence = Timestamp.valueOf(LocalDate.now().atStartOfDay().plusHours(10));

        StandingOrder order = StandingOrder.builder()
                .id(UUID.randomUUID())
                .name("Monthly Salary")
                .amount(BigDecimal.valueOf(3000))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.INCOME)
                .nextOccurrence(occurrence)
                .account(account)
                .category(category)
                .labels(new HashSet<>())
                .enabled(true)
                .build();

        Transaction mappedTransaction = Transaction.builder()
                .account(account)
                .type(TransactionType.INCOME)
                .amount(BigDecimal.valueOf(3000))
                .date(occurrence)
                .category(category)
                .labels(new HashSet<>())
                .build();

        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order));
        when(standingOrderMapper.toTransaction(order)).thenReturn(mappedTransaction);

        executor.executeStandingOrders();

        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(3500));
    }

    @Test
    void executeStandingOrders_dailyFrequency() {
        StandingOrder order = buildOrder(Frequency.DAILY);
        Timestamp original = order.getNextOccurrence();
        Transaction tx = buildTransaction(order);

        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order));
        when(standingOrderMapper.toTransaction(order)).thenReturn(tx);

        executor.executeStandingOrders();

        LocalDateTime expectedNext = original.toLocalDateTime().plusDays(1);
        assertThat(order.getNextOccurrence()).isEqualTo(Timestamp.valueOf(expectedNext));
    }

    @Test
    void executeStandingOrders_weeklyFrequency() {
        StandingOrder order = buildOrder(Frequency.WEEKLY);
        Timestamp original = order.getNextOccurrence();
        Transaction tx = buildTransaction(order);

        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order));
        when(standingOrderMapper.toTransaction(order)).thenReturn(tx);

        executor.executeStandingOrders();

        LocalDateTime expectedNext = original.toLocalDateTime().plusWeeks(1);
        assertThat(order.getNextOccurrence()).isEqualTo(Timestamp.valueOf(expectedNext));
    }

    @Test
    void executeStandingOrders_yearlyFrequency() {
        StandingOrder order = buildOrder(Frequency.YEARLY);
        Timestamp original = order.getNextOccurrence();
        Transaction tx = buildTransaction(order);

        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order));
        when(standingOrderMapper.toTransaction(order)).thenReturn(tx);

        executor.executeStandingOrders();

        LocalDateTime expectedNext = original.toLocalDateTime().plusYears(1);
        assertThat(order.getNextOccurrence()).isEqualTo(Timestamp.valueOf(expectedNext));
    }

    @Test
    void executeStandingOrders_unaTantumDisablesOrder() {
        StandingOrder order = buildOrder(Frequency.UNA_TANTUM);
        Timestamp original = order.getNextOccurrence();
        Transaction tx = buildTransaction(order);

        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order));
        when(standingOrderMapper.toTransaction(order)).thenReturn(tx);

        executor.executeStandingOrders();

        assertThat(order.isEnabled()).isFalse();
        assertThat(order.getNextOccurrence()).isEqualTo(original);
    }

    @Test
    void executeStandingOrders_failureOnOneDoesNotBlockOthers() {
        StandingOrder order1 = buildOrder(Frequency.MONTHLY);
        StandingOrder order2 = buildOrder(Frequency.MONTHLY);
        Transaction tx2 = buildTransaction(order2);

        when(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order1, order2));
        when(standingOrderMapper.toTransaction(order1)).thenThrow(new RuntimeException("mapping error"));
        when(standingOrderMapper.toTransaction(order2)).thenReturn(tx2);

        executor.executeStandingOrders();

        // order2 should still be processed
        verify(transactionRepository).save(tx2);
        verify(standingOrderRepository).save(order2);
    }

    private StandingOrder buildOrder(Frequency frequency) {
        Account account = Account.builder().id(UUID.randomUUID()).name("Test")
                .balance(BigDecimal.valueOf(1000)).build();
        Category category = Category.builder().id(UUID.randomUUID()).name("Test").build();

        return StandingOrder.builder()
                .id(UUID.randomUUID())
                .name("Test Order")
                .amount(BigDecimal.TEN)
                .currency(Currency.EUR)
                .frequency(frequency)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(Timestamp.valueOf(LocalDate.now().atStartOfDay().plusHours(10)))
                .account(account)
                .category(category)
                .labels(new HashSet<>())
                .enabled(true)
                .build();
    }

    private Transaction buildTransaction(StandingOrder order) {
        return Transaction.builder()
                .account(order.getAccount())
                .type(order.getType())
                .amount(order.getAmount())
                .date(order.getNextOccurrence())
                .category(order.getCategory())
                .labels(new HashSet<>())
                .build();
    }
}
