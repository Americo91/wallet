package astoppello.wallet.controller;

import astoppello.wallet.api.InstitutionsApi;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class InstitutionController implements InstitutionsApi {

    private final InstitutionService institutionService;

    @Override
    public ResponseEntity<InstitutionDto> createInstitution(InstitutionDto institutionDto) {
        InstitutionDto save = institutionService.save(institutionDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", "/api/v1/institutions/" + save.getId().toString());
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteInstitution(UUID institutionId) {
        institutionService.delete(institutionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<InstitutionDto> getInstitutionById(UUID institutionId) {
        return new ResponseEntity<>(institutionService.getByID(institutionId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<InstitutionDto>> listInstitutions(String name) {
        List<InstitutionDto> institutionDtos = StringUtils.isBlank(name) ? institutionService.getAll() : List.of(institutionService.getByName(name));
        return new ResponseEntity<>(institutionDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateInstitution(UUID institutionId, InstitutionDto institutionDto) {
        institutionService.update(institutionId, institutionDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
