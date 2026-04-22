package astoppello.wallet.repository;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.model.CategoryType;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TransactionRepositoryTest {
    @Autowired AccountRepository accountRepository;
    @Autowired CategoryRepository categoryRepository;
    @Autowired TransactionRepository transactionRepository;


    @Test
    void findByAccount() {
        Account account = Account.builder().id(UUID.randomUUID()).build();
        assertThat(transactionRepository.findByAccount(account)).hasSize(0);

        Category category = categoryRepository.save(Category.builder().name("category").type(CategoryType.EXPENSE).build());
        Account save = accountRepository.save(Account.builder().balance(BigDecimal.ZERO).currency(Currency.EUR).name("name").build());
        Transaction transaction = transactionRepository.save(Transaction.builder().amount(BigDecimal.TEN).category(category).type(TransactionType.EXPENSE).date(Timestamp.valueOf(LocalDateTime.now())).account(save).build());

        assertThat(transactionRepository.findByAccount(save)).hasSize(1).contains(transaction);
    }
}