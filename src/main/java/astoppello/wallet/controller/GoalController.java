package astoppello.wallet.controller;

import astoppello.wallet.dto.GoalDto;
import astoppello.wallet.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(GoalController.GOAL_BASE_PATH)
public class GoalController {

    public static final String GOAL_BASE_PATH = "/api/v1/goals";
    public static final String GOAL_ID_PATH = "/{goalId}";
    private final GoalService goalService;

    @GetMapping("/")
    public ResponseEntity<List<GoalDto>> getAll() {
        return new ResponseEntity<>(goalService.getAll(), HttpStatus.OK);
    }

    @GetMapping(GOAL_ID_PATH)
    public ResponseEntity<GoalDto> getById(@PathVariable("goalId") UUID id) {
        return new ResponseEntity<>(goalService.getByID(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<GoalDto> getByName(@RequestParam("name") String name) {
        return new ResponseEntity<>(goalService.getByName(name), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<GoalDto> handlePost(@RequestBody @Valid GoalDto dto) {
        return new ResponseEntity<>(goalService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping(GOAL_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleUpdate(@PathVariable("goalId") UUID id, @RequestBody @Valid GoalDto dto) {
        goalService.update(id, dto);
    }

    @DeleteMapping(GOAL_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("goalId") UUID id) {
        goalService.delete(id);
    }
}