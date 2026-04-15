package astoppello.wallet.domain;

import astoppello.wallet.model.CategoryType;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column
    @Getter(lombok.AccessLevel.NONE)
    private CategoryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> subcategories = new ArrayList<>();

    @Embedded
    private TrackingDate trackingDate;

    public CategoryType getType() {
        if (type != null) return type;
        return parent != null ? parent.getType() : null;
    }

    public void addSubcategory(Category category) {
        if (CollectionUtils.isEmpty(subcategories)) {
            subcategories = new ArrayList<>();
        }
        subcategories.add(category);
    }
}
