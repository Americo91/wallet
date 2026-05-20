package astoppello.wallet.service;

import astoppello.wallet.dto.GoalDto;

import java.util.List;
import java.util.UUID;

public interface GoalService {

    List<GoalDto> getAll();

    GoalDto getByID(UUID id);

    GoalDto getByName(String name);

    GoalDto save(GoalDto dto);

    GoalDto update(UUID id, GoalDto dto);

    void delete(UUID id);
}