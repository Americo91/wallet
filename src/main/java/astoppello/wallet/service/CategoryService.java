package astoppello.wallet.service;

import astoppello.wallet.dto.CategoryDto;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryDto getByID(UUID id);

    List<CategoryDto> getAll();

    List<CategoryDto> getByName(String name);

    CategoryDto save(CategoryDto dto);

    CategoryDto update(UUID id, CategoryDto dto);

    void delete(UUID id);
}
