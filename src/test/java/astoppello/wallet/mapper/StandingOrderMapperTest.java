package astoppello.wallet.mapper;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StandingOrderMapperImpl.class, DateMapper.class, TrackingMapperImpl.class})
class StandingOrderMapperTest {

    private static Category category;
    private static Account account;
    private static Label label;
    private static StandingOrder standingOrder;
    @Autowired
    private StandingOrderMapper standingOrderMapper;

    @BeforeAll
    static void setup() {
        account = Account.builder().id(UUID.randomUUID()).name("Revolut").build();
        category = Category.builder().id(UUID.randomUUID()).name("Subscriptions").build();
        label = Label.builder().id(UUID.randomUUID()).name("Spotify").build();
        standingOrder = StandingOrder.builder()
                .id(UUID.randomUUID())
                .name("Spotify")
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(Timestamp.valueOf(LocalDateTime.of(2026, 6, 15, 0, 0)))
                .account(account)
                .category(category)
                .description("Spotify subscription")
                .payee("Matteo")
                .enabled(true)
                .labels(Set.of(label))
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();
    }

    @Test
    void toDto() {
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
        assertThat(dto.getDescription()).isEqualTo("Spotify subscription");
        assertThat(dto.getPayee()).isEqualTo("Matteo");
        assertThat(dto.isEnabled()).isTrue();
        assertThat(dto.getLabels()).containsExactly(label.getId());
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
                .description("Spotify subscription")
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
        assertThat(domain.getDescription()).isEqualTo("Spotify subscription");
        assertThat(domain.getPayee()).isEqualTo("Matteo");
        assertThat(domain.getAccount()).isNull();
        assertThat(domain.getCategory()).isNull();
        assertThat(domain.getLabels()).isNull();
    }

    @Test
    void toTransaction() {
        Transaction transaction = standingOrderMapper.toTransaction(standingOrder);

        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isNull();
        assertThat(transaction.getAccount()).isEqualTo(account);
        assertThat(transaction.getType()).isEqualTo(TransactionType.EXPENSE);
        assertThat(transaction.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(3));
        assertThat(transaction.getDate()).isEqualTo(Timestamp.valueOf("2026-06-15 00:00:00"));
        assertThat(transaction.getCategory()).isEqualTo(category);
        assertThat(transaction.getDescription()).isEqualTo("Spotify subscription");
        assertThat(transaction.getPayee()).isEqualTo("Matteo");
        assertThat(transaction.getLabels()).containsExactly(label);
        assertThat(transaction.getTrackingDate()).isNull();
    }
}
