package astoppello.wallet.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalDto {

    @Null
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal savedAmount = BigDecimal.ZERO;

    private boolean reached;

    @JsonUnwrapped
    @Valid
    private TrackingDateDto trackingDate;
}