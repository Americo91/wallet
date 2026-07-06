package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.domain.Institution;
import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AccountMapperImpl.class, DateMapper.class})
class AccountMapperTest {

    @Autowired
    private AccountMapper mapper;

    @Test
    void toDto() {
        Institution institution = Institution.builder()
                .id(UUID.randomUUID())
                .name("Test Bank")
                .build();

        Account account = Account.builder()
                .id(UUID.randomUUID())
                .name("Checking")
                .institution(institution)
                .accountType(AccountTypeEnum.LIQUIDITY)
                .balance(new BigDecimal("1500.00"))
                .currency(Currency.EUR)
                .trackingDate(TestTrackingData.trackingDate)
                .build();

        AccountDto dto = mapper.toDto(account);

        assertThat(dto.getId()).isEqualTo(account.getId());
        assertThat(dto.getName()).isEqualTo(account.getName());
        assertThat(dto.getInstitution()).isEqualTo(institution.getId());
        assertThat(dto.getAccountType().name()).isEqualTo(account.getAccountType().name());
        assertThat(dto.getBalance()).isEqualTo(account.getBalance());
        assertThat(dto.getCurrency().name()).isEqualTo(account.getCurrency().name());
        assertThat(dto.getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDto_noInstitution() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .name("Cash")
                .institution(null)
                .accountType(AccountTypeEnum.LIQUIDITY)
                .balance(new BigDecimal("200.00"))
                .currency(Currency.EUR)
                .build();

        AccountDto dto = mapper.toDto(account);

        assertThat(dto.getInstitution()).isNull();
        assertThat(dto.getName()).isEqualTo(account.getName());
    }

    @Test
    void toDomain() {
        AccountDto dto = new AccountDto("Savings", AccountDto.AccountTypeEnum.SAVINGS, new BigDecimal("3000.00"), AccountDto.CurrencyEnum.USD)
                .id(UUID.randomUUID())
                .institution(UUID.randomUUID());

        Account account = mapper.toDomain(dto);

        assertThat(account.getId()).isEqualTo(dto.getId());
        assertThat(account.getName()).isEqualTo(dto.getName());
        assertThat(account.getAccountType().name()).isEqualTo(dto.getAccountType().name());
        assertThat(account.getBalance()).isEqualByComparingTo(dto.getBalance());
        assertThat(account.getCurrency().name()).isEqualTo(dto.getCurrency().name());
        assertThat(account.getInstitution()).isNull();
        assertThat(account.getTrackingDate()).isNull();
    }
}