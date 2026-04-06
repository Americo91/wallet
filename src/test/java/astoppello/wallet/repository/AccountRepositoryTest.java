package astoppello.wallet.repository;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Institution;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import org.junit.jupiter.api.BeforeEach;
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

    @Autowired
    private InstitutionRepository institutionRepository;

    private Institution institution;

    @BeforeEach
    void setUp() {
        institution = institutionRepository.save(Institution.builder().name("institution").build());
    }

    private Account buildAccount(String name) {
        return Account.builder().institution(institution).name(name).accountType(AccountTypeEnum.LIQUIDITY).balance(BigDecimal.valueOf(100)).currency(Currency.EUR)
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

    @Test
    void cascadeDelete() {
        accountRepository.save(buildAccount("to-delete"));
        assertThat(accountRepository.findAll().size()).isOne();
        assertThat(institutionRepository.findAll().size()).isOne();

        institutionRepository.delete(institution);

        assertThat(institutionRepository.findAll().size()).isZero();
        assertThat(accountRepository.findAll().size()).isZero();
    }
}