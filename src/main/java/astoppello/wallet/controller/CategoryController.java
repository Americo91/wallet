package astoppello.wallet.controller;

import astoppello.wallet.dto.CategoryDto;
import astoppello.wallet.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(CategoryController.CATEGORY_BASE_PATH)
@RequiredArgsConstructor
public class CategoryController {
    public static final String CATEGORY_BASE_PATH = "/api/v1/categories";
    public static final String CATEGORY_ID_PATH = "/{categoryId}";


    private final CategoryService categoryService;

    @GetMapping("/")
    public ResponseEntity<List<CategoryDto>> getAll() {
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    @GetMapping(CATEGORY_ID_PATH)
    public ResponseEntity<CategoryDto> getById(@PathVariable("categoryId")UUID id) {
        return new ResponseEntity<>(categoryService.getByID(id), HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<CategoryDto>> getByName(@RequestParam("name") String name) {
        return new ResponseEntity<>(categoryService.getByName(name), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<CategoryDto> handlePost(@RequestBody @Valid CategoryDto dto) {
        return new ResponseEntity<>(categoryService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping(CATEGORY_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePut(@PathVariable("categoryId") UUID id, @RequestBody @Valid CategoryDto dto) {
        categoryService.update(id, dto);
    }

    @DeleteMapping(CATEGORY_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("categoryId") UUID id) {
        categoryService.delete(id);
    }

}
