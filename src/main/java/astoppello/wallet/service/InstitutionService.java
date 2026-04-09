package astoppello.wallet.service;

import astoppello.wallet.dto.InstitutionDto;

import java.util.List;
import java.util.UUID;

public interface InstitutionService {

    InstitutionDto save(InstitutionDto dto);

    InstitutionDto update(UUID id, InstitutionDto dto);

    void delete(UUID id);

    List<InstitutionDto> getAll();

    InstitutionDto getByID(UUID id);

    InstitutionDto getByName(String name);
}
