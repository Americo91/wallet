package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Institution;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.InstitutionMapper;
import astoppello.wallet.repository.InstitutionRepository;
import astoppello.wallet.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class InstitutionServiceImpl implements InstitutionService {

    private final InstitutionMapper mapper;
    private final InstitutionRepository repository;

    @Override
    public InstitutionDto save(InstitutionDto dto) {
        Institution domain = mapper.toDomain(dto);
        domain.setTrackingDate(TrackingDate.now());
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public InstitutionDto update(UUID id, InstitutionDto dto) {
        Institution byId = getById(id);

        if (StringUtils.isNotEmpty(dto.getColor())) {
            byId.setColor(dto.getColor());
        }
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

    @Override
    public List<InstitutionDto> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public InstitutionDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    private @NonNull Institution getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Institution.class, id));
    }

    @Override
    public InstitutionDto getByName(String name) {
        Institution institution = repository.findByName(name).orElseThrow(() -> new NotFoundException(Institution.class, name));
        return mapper.toDto(institution);
    }
}
