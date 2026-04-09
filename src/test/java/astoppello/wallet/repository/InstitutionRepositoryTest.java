package astoppello.wallet.repository;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Institution;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.model.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InstitutionRepositoryTest {

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private Institution buildInstitution(String name) {
        return Institution.builder()
                .name(name)
                .color("red")
                .trackingDate(TrackingDate.builder()
                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                        .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                        .build())
                .build();
    }

    @Test
    void findByName() {
        String name = "institution";
        Institution institution = institutionRepository.save(buildInstitution(name));

        assertThat(institution).isNotNull();
        assertThat(institution.getId()).isNotNull();
        assertThat(institution.getName()).isEqualTo(name);
        assertThat(institution.getColor()).isNotNull();
        assertThat(institution.getTrackingDate()).isNotNull();
        assertThat(institution.getTrackingDate().getCreatedAt()).isNotNull();
        assertThat(institution.getTrackingDate().getUpdatedAt()).isNotNull();

        assertThat(institutionRepository.findByName(name).isPresent()).isTrue();
    }

    @Test
    void cascadeDelete() {
        Institution institution = institutionRepository.save(Institution.builder().name("institution").build());
        Account account = Account.builder().institution(institution).name("account").balance(BigDecimal.ZERO).currency(Currency.EUR).build();

        accountRepository.save(account);
        assertThat(accountRepository.findAll().size()).isOne();
        assertThat(institutionRepository.findAll().size()).isOne();

        institutionRepository.delete(institution);

        assertThat(institutionRepository.findAll().size()).isZero();
        assertThat(accountRepository.findAll().size()).isZero();
    }
}