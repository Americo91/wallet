package astoppello.wallet.repository;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.event.TransactionEntityListener;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
@Import(TransactionEntityListener.class)
class TransactionRepositoryTest {

    @Autowired AccountRepository accountRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired TransactionRepository transactionRepository;

    @Test
    void findByAccount() {
        Account account = accountRepository.save(
                Account.builder().balance(BigDecimal.ZERO).currency(Currency.EUR).name("name").build());

        assertThat(transactionRepository.findByAccount(account)).isEmpty();

        Category category = categoryRepository.save(
                Category.builder().name("category").type(CategoryType.EXPENSE).build());
        Transaction transaction = transactionRepository.save(
                Transaction.builder()
                        .amount(BigDecimal.TEN)
                        .category(category)
                        .type(TransactionType.EXPENSE)
                        .date(Timestamp.valueOf(LocalDateTime.now()))
                        .account(account)
                        .build());

        assertThat(transactionRepository.findByAccount(account)).hasSize(1).contains(transaction);
        assertThat(accountRepository.findById(account.getId()))
                .isPresent()
                .hasValueSatisfying(a -> assertThat(a.getBalance()).isEqualByComparingTo(BigDecimal.TEN.negate()));
    }
}