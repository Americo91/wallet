package astoppello.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingDateDto {

    @Null
    @JsonFormat(pattern = "yyyy-MM-dd-'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime createdAt;

    @Null
    @JsonFormat(pattern = "yyyy-MM-dd-'T'HH:mm:ssZ", shape = JsonFormat.Shape.STRING)
    private OffsetDateTime updatedAt;
}
