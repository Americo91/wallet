package astoppello.wallet.controller;

import astoppello.wallet.api.BudgetsApi;
import astoppello.wallet.dto.BudgetDto;
import astoppello.wallet.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class BudgetController implements BudgetsApi {

    private final BudgetService budgetService;

    @Override
    public ResponseEntity<BudgetDto> createBudget(BudgetDto budgetDto) {
        BudgetDto save = budgetService.save(budgetDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", String.format("/api/v1/budgets/%s", save.getId()));
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteBudget(UUID budgetId) {
        budgetService.delete(budgetId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<BudgetDto> getBudgetById(UUID budgetId) {
        return new ResponseEntity<>(budgetService.getByID(budgetId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<BudgetDto>> listBudgets() {
        return new ResponseEntity<>(budgetService.getAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateBudget(UUID budgetId, BudgetDto budgetDto) {
        budgetService.update(budgetId, budgetDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
