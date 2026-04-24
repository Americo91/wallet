package astoppello.wallet.dto;

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
public class TransactionDto {

    @Null
    private UUID id;

    @Null
    private UUID account;

    @NotNull
    private TransactionType type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Positive
    @NotNull
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate date;

    @NotNull
    private UUID category;

    @Nullable
    private String description;

    @Nullable
    private String merchant;

    @Nullable
    private Set<UUID> labels;

    @JsonUnwrapped
    @Valid
    private TrackingDateDto trackingDate;

}
