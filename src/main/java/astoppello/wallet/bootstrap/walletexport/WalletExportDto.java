package astoppello.wallet.bootstrap.walletexport;

import java.math.BigDecimal;
import java.util.List;

public record WalletExportDto(
        String accountName,
        String exportedAt,
        int totalRecords,
        List<RecordDto> records
) {

    public record RecordDto(
            BigDecimal value,
            String category,
            String note,
            String payee,
            String type,
            String date,
            String createdAt,
            String updatedAt,
            List<String> labels
    ) {
    }
}
