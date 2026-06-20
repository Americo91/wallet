package astoppello.wallet.controller;

import astoppello.wallet.dto.BudgetDto;
import astoppello.wallet.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(BudgetController.BUDGET_BASE_PATH)
public class BudgetController {

    public static final String BUDGET_BASE_PATH = "/api/v1/budgets";
    public static final String BUDGET_ID_PATH = "/{budgetId}";

    private final BudgetService budgetService;

    @GetMapping("/")
    public ResponseEntity<List<BudgetDto>> getAll() {
        return new ResponseEntity<>(budgetService.getAll(), HttpStatus.OK);
    }

    @GetMapping(BUDGET_ID_PATH)
    public ResponseEntity<BudgetDto> getById(@PathVariable("budgetId") UUID id) {
        return new ResponseEntity<>(budgetService.getByID(id), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<BudgetDto> handlePost(@RequestBody @Valid BudgetDto dto) {
        return new ResponseEntity<>(budgetService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping(BUDGET_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePut(@PathVariable("budgetId") UUID id, @RequestBody @Valid BudgetDto dto) {
        budgetService.update(id, dto);
    }

    @DeleteMapping(BUDGET_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("budgetId") UUID id) {
        budgetService.delete(id);
    }
}