package astoppello.wallet.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingDate {

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Timestamp updatedAt;

    public static TrackingDate now() {
        Timestamp ts = Timestamp.valueOf(LocalDateTime.now());
        return TrackingDate.builder().createdAt(ts).updatedAt(ts).build();
    }

    public void touch() {
        this.updatedAt = Timestamp.valueOf(LocalDateTime.now());
    }
}