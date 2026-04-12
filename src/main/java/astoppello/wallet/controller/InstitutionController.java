package astoppello.wallet.controller;

import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.service.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = InstitutionController.API_V_1_INSTITUTIONS)
public class InstitutionController {
    public static final String API_V_1_INSTITUTIONS = "/api/v1/institutions";
    public static final String INSTITUTION_ID_PATH = "/{institutionId}";

    private final InstitutionService institutionService;

    @GetMapping(INSTITUTION_ID_PATH)
    public ResponseEntity<InstitutionDto> getById(@PathVariable("institutionId") UUID institutionId) {
        return new ResponseEntity<>(institutionService.getByID(institutionId), HttpStatus.OK);
    }

    @GetMapping("/")
    public ResponseEntity<List<InstitutionDto>> getAll() {
        return new ResponseEntity<>(institutionService.getAll(), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<InstitutionDto> handlePost(@RequestBody @Valid InstitutionDto institutionDto) {
        InstitutionDto save = institutionService.save(institutionDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", INSTITUTION_ID_PATH + "/" + save.getId().toString());
        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @PutMapping(INSTITUTION_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePut(@PathVariable("institutionId") UUID institutionId, @RequestBody @Valid InstitutionDto dto) {
        institutionService.update(institutionId, dto);
    }

    @DeleteMapping(INSTITUTION_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("institutionId") UUID institutionId) {
        institutionService.delete(institutionId);
    }
}
