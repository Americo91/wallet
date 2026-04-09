package astoppello.wallet.repository;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    private Account buildAccount(String name) {
        return Account.builder().name(name).accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.valueOf(100)).currency(Currency.EUR)
                .trackingDate(TrackingDate.builder()
                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                        .updatedAt(Timestamp.valueOf(LocalDateTime.now())).build()).build();
    }

    @Test
    void findByName() {
        String name = "name";
        Account save = accountRepository.save(buildAccount(name));
        Optional<Account> byName = accountRepository.findByName(name);
        assertThat(byName.isPresent()).isTrue();
        assertThat(byName.get()).isEqualTo(save);
    }
}