package astoppello.wallet.bootstrap.walletexport;

import java.math.BigDecimal;
import java.util.List;

public record WalletExportJson(
        String accountId,
        String accountName,
        String exportedAt,
        int totalRecords,
        List<RecordJson> records
) {

    public record RecordJson(
            String accountId,
            AmountJson amount,
            AmountJson baseAmount,
            CategoryJson category,
            String createdAt,
            String id,
            List<LabelJson> labels,
            String note,
            String payee,
            String paymentType,
            String recordDate,
            String recordState,
            String recordType,
            String updatedAt
    ) {}

    public record AmountJson(
            String currencyCode,
            BigDecimal value
    ) {}

    public record CategoryJson(
            String color,
            Integer envelopeId,
            String id,
            String name
    ) {}

    public record LabelJson(
            boolean archived,
            String color,
            String id,
            String name
    ) {}
}