package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.dto.TransactionDto;
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
@ContextConfiguration(classes = {TransactionMapperImpl.class, DateMapper.class, TrackingMapperImpl.class})
class TransactionMapperTest {

    @Autowired
    private TransactionMapper transactionMapper;

    @Test
    void toDto() {
        Account accountName = Account.builder().id(UUID.randomUUID()).name("accountName").build();
        Category categoryName = Category.builder().name("categoryName").build();
        Transaction transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .account(accountName)
                .type(TransactionType.INCOME)
                .amount(new BigDecimal("20.00"))
                .date(Timestamp.valueOf(LocalDateTime.of(2026, 3, 3, 0, 0)))
                .category(categoryName)
                .description("description")
                .merchant("merchant")
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();

        TransactionDto dto = transactionMapper.toDto(transaction);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(transaction.getId());
        assertThat(dto.getAccount()).isEqualTo(accountName.getId());
        assertThat(dto.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(dto.getAmount()).isEqualTo(new BigDecimal("20.00"));
        assertThat(dto.getDate().toString()).isEqualTo("2026-03-03");
        assertThat(dto.getCategory()).isEqualTo(categoryName.getId());
        assertThat(dto.getDescription()).isEqualTo("description");
        assertThat(dto.getMerchant()).isEqualTo("merchant");
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDomain() {
        TransactionDto dto = TransactionDto.builder()
                .type(TransactionType.INCOME)
                .date(LocalDate.of(2026, 2, 1))
                .description("description")
                .merchant("merchant")
                .build();

        Transaction domain = transactionMapper.toDomain(dto);
        assertThat(domain).isNotNull();
        assertThat(domain.getType()).isEqualTo(TransactionType.INCOME);
        assertThat(domain.getDate()).isEqualTo(Timestamp.valueOf(dto.getDate().atStartOfDay()));
        assertThat(domain.getDescription()).isEqualTo(dto.getDescription());
        assertThat(domain.getMerchant()).isEqualTo(dto.getMerchant());
        assertThat(domain.getAccount()).isNull();
        assertThat(domain.getCategory()).isNull();
        assertThat(domain.getLabels()).isNull();


    }
}