package astoppello.wallet.domain;

import astoppello.wallet.model.AccountTypeEnum;
import astoppello.wallet.model.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "institution_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Institution institution;

    @Column
    @Enumerated(EnumType.STRING)
    private AccountTypeEnum accountType;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Embedded
    private TrackingDate trackingDate;
}