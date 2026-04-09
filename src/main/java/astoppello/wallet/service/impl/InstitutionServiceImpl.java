package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Institution;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.InstitutionMapper;
import astoppello.wallet.repository.InstitutionRepository;
import astoppello.wallet.service.InstitutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        return mapper.toDto(repository.save(mapper.toDomain(dto)));
    }

    @Override
    public InstitutionDto update(UUID id, InstitutionDto dto) {
        Institution byId = getById(id);

        if (dto.getColor() != null) {
            byId.setColor(dto.getColor());
        }
        if (dto.getName() != null) {
            byId.setName(dto.getName());
        }
        byId.getTrackingDate().setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));

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
