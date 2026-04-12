package astoppello.wallet.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionDto {

    @Null
    private UUID id;
    @NotNull
    private String name;
    @Nullable
    private String color;

    @Null
    private Set<String> accounts;

    @JsonUnwrapped
    @Valid
    private TrackingDateDto trackingDate;


}
