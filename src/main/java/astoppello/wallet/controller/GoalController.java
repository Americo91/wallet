package astoppello.wallet.controller;

import astoppello.wallet.api.GoalsApi;
import astoppello.wallet.dto.GoalDto;
import astoppello.wallet.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class GoalController implements GoalsApi {

    private final GoalService goalService;

    @Override
    public ResponseEntity<GoalDto> addSavedAmount(UUID goalId, Double body) {
        return new ResponseEntity<>(goalService.addSavedAmount(goalId, BigDecimal.valueOf(body)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<GoalDto> createGoal(GoalDto goalDto) {
        GoalDto save = goalService.save(goalDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", String.format("/api/v1/goals/%s", save.getId()));
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteGoal(UUID goalId) {
        goalService.delete(goalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<GoalDto> getGoalById(UUID goalId) {
        return new ResponseEntity<>(goalService.getByID(goalId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<GoalDto>> listGoals(String name) {
        List<GoalDto> goalDtos = StringUtils.isBlank(name) ? goalService.getAll() : List.of(goalService.getByName(name));
        return new ResponseEntity<>(goalDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> markGoalAsReached(UUID goalId) {
        goalService.markAsReached(goalId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<Void> updateGoal(UUID goalId, GoalDto goalDto) {
        goalService.update(goalId, goalDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
