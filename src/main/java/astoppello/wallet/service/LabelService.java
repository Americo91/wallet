package astoppello.wallet.service;

import astoppello.wallet.dto.LabelDto;

import java.util.List;
import java.util.UUID;

public interface LabelService {

    List<LabelDto> getAll();

    LabelDto getByID(UUID id);

    List<LabelDto> getByName(String name);

    LabelDto save(LabelDto dto);

    LabelDto update(UUID id, LabelDto dto);

    void delete(UUID id);
}
