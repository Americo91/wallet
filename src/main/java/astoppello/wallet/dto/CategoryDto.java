package astoppello.wallet.dto;

import astoppello.wallet.domain.CategoryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @Null
    private UUID id;

    @NotNull
    private String name;

    private CategoryType type;

    private UUID parentId;

    @Null
    private List<CategoryDto> subcategories;
}