package astoppello.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Overlimit {
    @Column(name = "budget_limit")
    private BigDecimal limit;
    private BigDecimal periodAmount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
}