package astoppello.wallet.service;

import astoppello.wallet.dto.GoalDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface GoalService {

    List<GoalDto> getAll();

    GoalDto getByID(UUID id);

    GoalDto getByName(String name);

    GoalDto save(GoalDto dto);

    GoalDto update(UUID id, GoalDto dto);

    void delete(UUID id);

    GoalDto addSavedAmount(UUID id, @Valid @NotNull BigDecimal savedAmount);

    void markAsReached(UUID id);
}