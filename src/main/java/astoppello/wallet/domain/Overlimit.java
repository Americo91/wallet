package astoppello.wallet.domain;

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
    private BigDecimal limit;
    private BigDecimal periodAmount;
    private LocalDate periodStart;
    private LocalDate periodEnd;
}