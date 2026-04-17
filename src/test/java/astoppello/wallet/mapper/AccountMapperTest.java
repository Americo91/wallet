package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.domain.Institution;
import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.dto.TrackingDateDto;
import astoppello.wallet.model.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {AccountMapperImpl.class, TrackingMapperImpl.class, DateMapper.class})
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
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();

        AccountDto dto = mapper.toDto(account);

        assertThat(dto.getId()).isEqualTo(account.getId());
        assertThat(dto.getName()).isEqualTo(account.getName());
        assertThat(dto.getInstitution()).isEqualTo(institution.getId());
        assertThat(dto.getAccountType()).isEqualTo(account.getAccountType());
        assertThat(dto.getBalance()).isEqualByComparingTo(account.getBalance());
        assertThat(dto.getCurrency()).isEqualTo(account.getCurrency());
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
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
        TrackingDateDto trackingDateDto = TrackingDateDto.builder()
                .createdAt(OffsetDateTime.of(2026, 1, 10, 9, 0, 0, 0, ZoneOffset.UTC))
                .updatedAt(OffsetDateTime.of(2026, 3, 15, 12, 0, 0, 0, ZoneOffset.UTC))
                .build();

        AccountDto dto = AccountDto.builder()
                .id(UUID.randomUUID())
                .name("Savings")
                .institution(UUID.randomUUID())
                .accountType(AccountTypeEnum.SAVINGS)
                .balance(new BigDecimal("3000.00"))
                .currency(Currency.USD)
                .trackingDate(trackingDateDto)
                .build();

        Account account = mapper.toDomain(dto);

        assertThat(account.getId()).isEqualTo(dto.getId());
        assertThat(account.getName()).isEqualTo(dto.getName());
        assertThat(account.getAccountType()).isEqualTo(dto.getAccountType());
        assertThat(account.getBalance()).isEqualByComparingTo(dto.getBalance());
        assertThat(account.getCurrency()).isEqualTo(dto.getCurrency());
        assertThat(account.getInstitution()).isNull();
        assertThat(account.getTrackingDate().getCreatedAt()).isEqualTo(Timestamp.valueOf(LocalDateTime.of(2026, 1, 10, 9, 0, 0)));
        assertThat(account.getTrackingDate().getUpdatedAt()).isEqualTo(Timestamp.valueOf(LocalDateTime.of(2026, 3, 15, 12, 0, 0)));
    }
}