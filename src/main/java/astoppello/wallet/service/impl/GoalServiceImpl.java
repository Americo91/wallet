package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Goal;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.GoalDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.GoalMapper;
import astoppello.wallet.repository.GoalRepository;
import astoppello.wallet.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository repository;
    private final GoalMapper mapper;

    @Override
    public List<GoalDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public GoalDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    private @NonNull Goal getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Goal.class, id));
    }

    @Override
    public GoalDto getByName(String name) {
        return repository.findByName(name).map(mapper::toDto).orElseThrow(() -> new NotFoundException(Goal.class, name));
    }

    @Override
    public GoalDto save(GoalDto dto) {
        Goal domain = mapper.toDomain(dto);
        domain.setTrackingDate(TrackingDate.now());
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public GoalDto update(UUID id, GoalDto dto) {
        Goal byId = getById(id);
        byId.setName(dto.getName());
        byId.setAmount(dto.getAmount());
        byId.setSavedAmount(dto.getSavedAmount());
        byId.setReached(Boolean.TRUE.equals(dto.getReached()));
        byId.getTrackingDate().touch();
        return mapper.toDto(repository.save(byId));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public GoalDto addSavedAmount(UUID id, BigDecimal savedAmount) {
        Goal byId = getById(id);
        byId.setSavedAmount(byId.getSavedAmount().add(savedAmount));
        byId.getTrackingDate().touch();
        if(byId.getSavedAmount().compareTo(byId.getAmount()) >= 0) {
            byId.setReached(true);
        }
        Goal save = repository.save(byId);
        return mapper.toDto(save);
    }

    @Override
    public void markAsReached(UUID id) {
        Goal byID = getById(id);
        byID.setReached(true);
        byID.getTrackingDate().touch();
        repository.save(byID);
    }
}