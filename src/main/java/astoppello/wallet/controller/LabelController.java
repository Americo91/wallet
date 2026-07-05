package astoppello.wallet.controller;

import astoppello.wallet.api.LabelsApi;
import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.service.LabelService;
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
public class LabelController implements LabelsApi {

    private final LabelService labelService;

    @Override
    public ResponseEntity<LabelDto> createLabel(LabelDto labelDto) {
        LabelDto save = labelService.save(labelDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", String.format("/api/v1/labels/%s", save.getId()));
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteLabel(UUID labelId) {
        labelService.delete(labelId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<LabelDto> getLabelById(UUID labelId) {
        return new ResponseEntity<>(labelService.getByID(labelId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<LabelDto>> listLabels(String name) {
        List<LabelDto> labelDtos = StringUtils.isBlank(name) ? labelService.getAll() : List.of(labelService.getByName(name));
        return new ResponseEntity<>(labelDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateLabel(UUID labelId, LabelDto labelDto) {
        labelService.update(labelId, labelDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
