package astoppello.wallet.controller;

import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(LabelController.LABEL_BASE_PATH)
public class LabelController {

    public static final String LABEL_BASE_PATH = "/api/v1/labels";
    public static final String LABEL_ID_PATH = "/{labelId}";
    private final LabelService labelService;

    @GetMapping("/")
    public ResponseEntity<List<LabelDto>> getAll() {
        return new ResponseEntity<>(labelService.getAll(), HttpStatus.OK);
    }

    @GetMapping(LABEL_ID_PATH)
    public ResponseEntity<LabelDto> getById(@PathVariable("labelId") UUID id) {
        return new ResponseEntity<>(labelService.getByID(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<LabelDto>> getByName(@RequestParam("name") String name) {
        return new ResponseEntity<>(labelService.getByName(name), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<LabelDto> handlePost(@RequestBody @Valid LabelDto dto) {
        return new ResponseEntity<>(labelService.save(dto), HttpStatus.CREATED);
    }

    @PutMapping(LABEL_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleUpdate(@PathVariable("labelId") UUID id, @RequestBody @Valid LabelDto dto) {
        labelService.update(id, dto);
    }

    @DeleteMapping(LABEL_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("labelId") UUID id) {
        labelService.delete(id);
    }
}
