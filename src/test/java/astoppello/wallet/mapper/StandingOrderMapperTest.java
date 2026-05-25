package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StandingOrderMapperImpl.class, DateMapper.class, TrackingMapperImpl.class})
class StandingOrderMapperTest {

    @Autowired
    private StandingOrderMapper standingOrderMapper;

    @Test
    void toDto() {
        Account account = Account.builder().id(UUID.randomUUID()).name("Revolut").build();
        Category category = Category.builder().id(UUID.randomUUID()).name("Subscriptions").build();
        StandingOrder standingOrder = StandingOrder.builder()
                .id(UUID.randomUUID())
                .name("Spotify")
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(Timestamp.valueOf(LocalDateTime.of(2026, 6, 15, 0, 0)))
                .account(account)
                .category(category)
                .note("Spotify subscription")
                .payee("Matteo")
                .enabled(true)
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();

        StandingOrderDto dto = standingOrderMapper.toDto(standingOrder);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(standingOrder.getId());
        assertThat(dto.getName()).isEqualTo("Spotify");
        assertThat(dto.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(3));
        assertThat(dto.getCurrency()).isEqualTo(Currency.EUR);
        assertThat(dto.getFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(dto.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(dto.getNextOccurrence().toString()).isEqualTo("2026-06-15");
        assertThat(dto.getAccount()).isEqualTo(account.getId());
        assertThat(dto.getCategory()).isEqualTo(category.getId());
        assertThat(dto.getNote()).isEqualTo("Spotify subscription");
        assertThat(dto.getPayee()).isEqualTo("Matteo");
        assertThat(dto.isEnabled()).isTrue();
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDomain() {
        StandingOrderDto dto = StandingOrderDto.builder()
                .name("Spotify")
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(LocalDate.of(2026, 6, 15))
                .note("Spotify subscription")
                .payee("Matteo")
                .enabled(true)
                .build();

        StandingOrder domain = standingOrderMapper.toDomain(dto);

        assertThat(domain).isNotNull();
        assertThat(domain.getName()).isEqualTo("Spotify");
        assertThat(domain.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(3));
        assertThat(domain.getCurrency()).isEqualTo(Currency.EUR);
        assertThat(domain.getFrequency()).isEqualTo(Frequency.MONTHLY);
        assertThat(domain.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(domain.getNextOccurrence()).isEqualTo(Timestamp.valueOf(LocalDateTime.of(2026, 6, 15, 0, 0)));
        assertThat(domain.getNote()).isEqualTo("Spotify subscription");
        assertThat(domain.getPayee()).isEqualTo("Matteo");
        assertThat(domain.getAccount()).isNull();
        assertThat(domain.getCategory()).isNull();
        assertThat(domain.getLabels()).isNull();
    }
}
