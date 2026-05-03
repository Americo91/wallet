package astoppello.wallet.bootstrap.walletexport;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {WalletExportMapperImpl.class})
class WalletExportMapperTest {

    @Autowired
    private WalletExportMapper mapper;

    @Test
    void toDto_record() {
        WalletExportJson.RecordJson record = new WalletExportJson.RecordJson(
                "acc-1",
                new WalletExportJson.AmountJson("EUR", new BigDecimal("-10.50")),
                new WalletExportJson.AmountJson("EUR", new BigDecimal("-10.50")),
                new WalletExportJson.CategoryJson("#FF0000", 1001, "cat-1", "Restaurant"),
                "2026-04-24T10:43:51Z",
                "rec-1",
                List.of(
                        new WalletExportJson.LabelJson(false, "#ff6f00", "lbl-1", "Guilty Free"),
                        new WalletExportJson.LabelJson(false, "#0D47A1", "lbl-2", "Lavoro")
                ),
                "Lunch",
                "Pizza Place",
                "cash",
                "2026-04-24T10:43:35Z",
                "cleared",
                "expense",
                "2026-04-24T11:19:09Z"
        );

        WalletExportDto.RecordDto dto = mapper.toDto(record);

        assertThat(dto.value()).isEqualTo(new BigDecimal("-10.50"));
        assertThat(dto.category()).isEqualTo("Restaurant");
        assertThat(dto.type()).isEqualTo("expense");
        assertThat(dto.note()).isEqualTo("Lunch");
        assertThat(dto.payee()).isEqualTo("Pizza Place");
        assertThat(dto.createdAt()).isEqualTo("2026-04-24T10:43:51Z");
        assertThat(dto.updatedAt()).isEqualTo("2026-04-24T11:19:09Z");
        assertThat(dto.labels()).containsExactly("Guilty Free", "Lavoro");
    }

    @Test
    void toDto_record_nullLabels() {
        WalletExportJson.RecordJson record = new WalletExportJson.RecordJson(
                "acc-1",
                new WalletExportJson.AmountJson("EUR", new BigDecimal("180")),
                new WalletExportJson.AmountJson("EUR", new BigDecimal("180")),
                new WalletExportJson.CategoryJson("#0097a7", 20001, "cat-2", "Transfer"),
                "2026-04-26T14:31:34Z",
                "rec-2",
                null,
                null,
                null,
                "cash",
                "2026-04-26T14:31:30Z",
                "cleared",
                "income",
                "2026-04-26T14:43:19Z"
        );

        WalletExportDto.RecordDto dto = mapper.toDto(record);

        assertThat(dto.value()).isEqualTo(new BigDecimal("180"));
        assertThat(dto.category()).isEqualTo("Transfer");
        assertThat(dto.type()).isEqualTo("income");
        assertThat(dto.note()).isNull();
        assertThat(dto.payee()).isNull();
        assertThat(dto.labels()).isNull();
    }

    @Test
    void toDto_export() {
        WalletExportJson.RecordJson record = new WalletExportJson.RecordJson(
                "acc-1",
                new WalletExportJson.AmountJson("EUR", new BigDecimal("50")),
                new WalletExportJson.AmountJson("EUR", new BigDecimal("50")),
                new WalletExportJson.CategoryJson(null, 5001, "cat-3", "Parking"),
                "2026-04-20T08:05:22Z",
                "rec-3",
                null,
                null,
                null,
                "cash",
                "2026-04-20T08:00:00Z",
                "cleared",
                "expense",
                "2026-04-20T08:05:22Z"
        );

        WalletExportJson json = new WalletExportJson(
                "acc-1", "Revolut", "2026-04-27", 1, List.of(record)
        );

        WalletExportDto dto = mapper.toDto(json);

        assertThat(dto.accountName()).isEqualTo("Revolut");
        assertThat(dto.exportedAt()).isEqualTo("2026-04-27");
        assertThat(dto.totalRecords()).isEqualTo(1);
        assertThat(dto.records()).hasSize(1);
        assertThat(dto.records().getFirst().category()).isEqualTo("Parking");
        assertThat(dto.records().getFirst().type()).isEqualTo("expense");
    }
}