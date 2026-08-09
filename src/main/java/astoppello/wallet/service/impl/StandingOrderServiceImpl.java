package astoppello.wallet.service.impl;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.StandingOrderMapper;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.repository.StandingOrderRepository;
import astoppello.wallet.service.StandingOrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class StandingOrderServiceImpl implements StandingOrderService {

    private final StandingOrderMapper mapper;
    private final StandingOrderRepository repository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final LabelRepository labelRepository;

    @Override
    public List<StandingOrderDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public StandingOrderDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    @Override
    public StandingOrderDto save(UUID accountId, StandingOrderDto dto) {
        Account account = getAccount(accountId);
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(Category.class, dto.getCategory()));

        StandingOrder domain = mapper.toDomain(dto);
        domain.setAccount(account);
        domain.setCategory(category);
        domain.setLabels(resolveLabels(dto.getLabels()));
        domain.setTrackingDate(TrackingDate.now());
        if(domain.getStartingDate() == null ) {
            domain.setStartingDate(domain.getTrackingDate().getCreatedAt());
        }
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public StandingOrderDto update(UUID id, StandingOrderDto dto) {
        StandingOrder byId = getById(id);
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(Category.class, dto.getCategory()));

        StandingOrder domain = mapper.toDomain(dto);
        domain.setId(id);
        domain.setAccount(byId.getAccount());
        domain.setCategory(category);
        domain.setLabels(resolveLabels(dto.getLabels()));
        domain.setTrackingDate(byId.getTrackingDate());
        domain.getTrackingDate().touch();
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public void delete(UUID id) {
        repository.delete(getById(id));
    }

    @Override
    public List<StandingOrderDto> getUpcoming(int days) {
        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(days));
        return repository.findByEnabledTrueAndNextOccurrenceBetween(from, to)
                .stream().map(mapper::toDto).toList();
    }

    private @NonNull StandingOrder getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(StandingOrder.class, id));
    }

    private Set<Label> resolveLabels(Set<UUID> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(labelRepository.findAllById(labelIds));
    }

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(Account.class, accountId));
    }
}
