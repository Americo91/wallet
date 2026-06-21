package astoppello.wallet.domain;

import astoppello.wallet.model.Frequency;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency period;

    @Column(nullable = false)
    private BigDecimal budgetLimit;

    @ManyToMany
    @JoinTable(name = "budget_category", joinColumns = @JoinColumn(name = "budget_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
    @Builder.Default
    private Set<Category> categories = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "budget_account", joinColumns = @JoinColumn(name = "budget_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
    @Builder.Default
    private Set<Account> accounts = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "budget_label", joinColumns = @JoinColumn(name = "budget_id"), inverseJoinColumns = @JoinColumn(name = "label_id"))
    @Builder.Default
    private Set<Label> labels = new HashSet<>();

    @Column(nullable = false)
    private boolean closed;

    @Embedded
    private TrackingDate trackingDate;

    @ElementCollection
    @CollectionTable(name = "budget_overlimit", joinColumns = @JoinColumn(name = "budget_id"))
    @Builder.Default
    private Set<Overlimit> overLimit = new HashSet<>();
}
