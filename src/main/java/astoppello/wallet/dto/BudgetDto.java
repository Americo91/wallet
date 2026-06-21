package astoppello.wallet.dto;

import astoppello.wallet.domain.Overlimit;
import astoppello.wallet.model.Frequency;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {

    @Null
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private Frequency period;

    @NotNull
    private BigDecimal budgetLimit;

    @NotNull
    private Set<UUID> categoryIds;

    @NotNull
    private Set<UUID> accountIds;

    @NotNull
    private Set<UUID> labelIds;

    private boolean closed;

    @JsonUnwrapped
    @Valid
    private TrackingDateDto trackingDate;

    private Set<Overlimit> overLimit;
}
