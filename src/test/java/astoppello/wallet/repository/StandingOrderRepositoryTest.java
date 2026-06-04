package astoppello.wallet.repository;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
class StandingOrderRepositoryTest {

    @Autowired AccountRepository accountRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired StandingOrderRepository standingOrderRepository;

    @Test
    void findByEnabledTrueAndNextOccurrenceBetween() {
        Account account = accountRepository.save(
                Account.builder().balance(BigDecimal.ZERO).currency(Currency.EUR).name("Revolut").build());
        Category category = categoryRepository.save(
                Category.builder().name("Subscriptions").type(CategoryType.EXPENSE).build());

        LocalDateTime now = LocalDateTime.now();
        StandingOrder order = standingOrderRepository.save(
                StandingOrder.builder()
                        .name("Spotify")
                        .amount(BigDecimal.valueOf(3))
                        .currency(Currency.EUR)
                        .frequency(Frequency.MONTHLY)
                        .type(TransactionType.EXPENSE)
                        .nextOccurrence(Timestamp.valueOf(now.plusDays(2)))
                        .account(account)
                        .category(category)
                        .enabled(true)
                        .build());

        Timestamp from = Timestamp.valueOf(now);
        Timestamp to = Timestamp.valueOf(now.plusDays(3));

        assertThat(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(from, to))
                .hasSize(1).contains(order);
    }

    @Test
    void findByEnabledTrueAndNextOccurrenceBetween_disabledExcluded() {
        Account account = accountRepository.save(
                Account.builder().balance(BigDecimal.ZERO).currency(Currency.EUR).name("Revolut2").build());
        Category category = categoryRepository.save(
                Category.builder().name("Bills").type(CategoryType.EXPENSE).build());

        LocalDateTime now = LocalDateTime.now();
        standingOrderRepository.save(
                StandingOrder.builder()
                        .name("Old subscription")
                        .amount(BigDecimal.TEN)
                        .currency(Currency.EUR)
                        .frequency(Frequency.MONTHLY)
                        .type(TransactionType.EXPENSE)
                        .nextOccurrence(Timestamp.valueOf(now.plusDays(1)))
                        .account(account)
                        .category(category)
                        .enabled(false)
                        .build());

        Timestamp from = Timestamp.valueOf(now);
        Timestamp to = Timestamp.valueOf(now.plusDays(3));

        assertThat(standingOrderRepository.findByEnabledTrueAndNextOccurrenceBetween(from, to)).isEmpty();
    }

    @Test
    void findByEnabledTrue() {
        Account account = accountRepository.save(
                Account.builder().balance(BigDecimal.ZERO).currency(Currency.EUR).name("Boursorama").build());
        Category category = categoryRepository.save(
                Category.builder().name("Housing").type(CategoryType.EXPENSE).build());

        standingOrderRepository.save(
                StandingOrder.builder()
                        .name("Rent")
                        .amount(BigDecimal.valueOf(1310.92))
                        .currency(Currency.EUR)
                        .frequency(Frequency.MONTHLY)
                        .type(TransactionType.EXPENSE)
                        .nextOccurrence(Timestamp.valueOf(LocalDateTime.now().plusDays(5)))
                        .account(account)
                        .category(category)
                        .enabled(true)
                        .build());

        assertThat(standingOrderRepository.findByEnabledTrue()).hasSize(1);
    }
}
