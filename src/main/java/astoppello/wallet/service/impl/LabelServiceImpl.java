package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Label;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.LabelMapper;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LabelServiceImpl implements LabelService {

    private final LabelRepository repository;
    private final LabelMapper mapper;


    @Override
    public List<LabelDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public LabelDto getByID(UUID id) {
        Label byId = getById(id);
        return mapper.toDto(byId);
    }

    private @NonNull Label getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Label.class, id));
    }

    @Override
    public List<LabelDto> getByName(String name) {
        return repository.findByName(name).stream().map(mapper::toDto).toList();
    }

    @Override
    public LabelDto save(LabelDto dto) {
        Label domain = mapper.toDomain(dto);
        domain.setTrackingDate(TrackingDate.now());
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public LabelDto update(UUID id, LabelDto dto) {
        Label byId = getById(id);
        if (StringUtils.isNotEmpty(dto.getName())) {
            byId.setName(dto.getName());
        }
        byId.getTrackingDate().touch();
        return mapper.toDto(repository.save(byId));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
