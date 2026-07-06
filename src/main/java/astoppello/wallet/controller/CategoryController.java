package astoppello.wallet.controller;

import astoppello.wallet.api.CategoriesApi;
import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class CategoryController implements CategoriesApi {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<CategoryDto> createCategory(CategoryDto categoryDto) {
        CategoryDto save = categoryService.save(categoryDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", String.format("/api/v1/categories/%s", save.getId()));
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(UUID categoryId) {
        categoryService.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<CategoryDto> getCategoryById(UUID categoryId) {
        return new ResponseEntity<>(categoryService.getByID(categoryId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<CategoryDto>> listCategories(String name) {
        List<CategoryDto> categoryDtos = StringUtils.isBlank(name) ? categoryService.getAll() : categoryService.getByName(name);
        return new ResponseEntity<>(categoryDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateCategory(UUID categoryId, CategoryDto categoryDto) {
        categoryService.update(categoryId, categoryDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
