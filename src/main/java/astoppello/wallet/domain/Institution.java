package astoppello.wallet.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Setter
@Table(name = "institutions")
public class Institution {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, columnDefinition = "varchar", updatable = false, nullable = false)
    private UUID id;

    @Column
    private String name;

    @OneToMany
    private Set<Account> accounts;

    @Column
    private String color;

    @Embedded
    private TrackingDate trackingDate;
}