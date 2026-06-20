package astoppello.wallet.service;

import astoppello.wallet.dto.BudgetDto;

import java.util.List;
import java.util.UUID;

public interface BudgetService {

    List<BudgetDto> getAll();

    BudgetDto getByID(UUID id);

    BudgetDto save(BudgetDto dto);

    BudgetDto update(UUID id, BudgetDto dto);

    void delete(UUID id);
}