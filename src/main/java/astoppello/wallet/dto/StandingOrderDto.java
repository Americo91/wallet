package astoppello.wallet.dto;

import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StandingOrderDto {

    @Null
    private UUID id;

    @NotNull
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Positive
    @NotNull
    private BigDecimal amount;

    @NotNull
    private Currency currency;

    @NotNull
    private Frequency frequency;

    @NotNull
    private TransactionType type;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    @NotNull
    private LocalDate nextOccurrence;

    @Null
    private UUID account;

    @NotNull
    private UUID category;

    @Nullable
    private Set<UUID> labels;

    @Nullable
    private String description;

    @Nullable
    private String payee;

    private boolean enabled;

    @JsonUnwrapped
    @Valid
    private TrackingDateDto trackingDate;
}
