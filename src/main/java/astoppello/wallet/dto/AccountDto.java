package astoppello.wallet.dto;

import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    @Null
    private UUID id;

    @NotNull
    private String name;

    @Null
    private String institution;

    @NotNull
    private AccountTypeEnum accountType;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Positive
    @NotNull
    private BigDecimal balance;

    @NotNull
    private Currency currency;

    @JsonUnwrapped
    @Valid
    private TrackingDateDto trackingDate;
}
