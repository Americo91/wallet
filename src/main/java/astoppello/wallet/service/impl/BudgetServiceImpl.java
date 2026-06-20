package astoppello.wallet.service.impl;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.BudgetDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.BudgetMapper;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.BudgetRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BudgetServiceImpl implements astoppello.wallet.service.BudgetService {

    private final BudgetRepository repository;
    private final BudgetMapper mapper;
    private final CategoryRepository categoryRepository;
    private final AccountRepository accountRepository;
    private final LabelRepository labelRepository;

    @Override
    public List<BudgetDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public BudgetDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    private Budget getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Budget.class, id));
    }

    @Override
    @Transactional
    public BudgetDto save(BudgetDto dto) {
        Budget domain = mapper.toDomain(dto);
        domain.setTrackingDate(TrackingDate.now());
        domain.setCategories(resolveCategories(dto.getCategoryIds()));
        domain.setAccounts(resolveAccounts(dto.getAccountIds()));
        domain.setLabels(resolveLabels(dto.getLabelIds()));
        return mapper.toDto(repository.save(domain));
    }

    @Override
    @Transactional
    public BudgetDto update(UUID id, BudgetDto dto) {
        Budget existing = getById(id);
        if (StringUtils.isNotEmpty(dto.getName())) {
            existing.setName(dto.getName());
        }
        if (dto.getPeriod() != null) {
            existing.setPeriod(dto.getPeriod());
        }
        if (dto.getBudgetLimit() != null) {
            existing.setBudgetLimit(dto.getBudgetLimit());
        }
        if (dto.getCategoryIds() != null) {
            existing.setCategories(resolveCategories(dto.getCategoryIds()));
        }
        if (dto.getAccountIds() != null) {
            existing.setAccounts(resolveAccounts(dto.getAccountIds()));
        }
        if (dto.getLabelIds() != null) {
            existing.setLabels(resolveLabels(dto.getLabelIds()));
        }
        existing.setClosed(dto.isClosed());
        existing.getTrackingDate().touch();
        return mapper.toDto(repository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private Set<Category> resolveCategories(Set<UUID> ids) {
        if (ids == null) return new HashSet<>();
        return new HashSet<>(categoryRepository.findAllById(ids));
    }

    private Set<Account> resolveAccounts(Set<UUID> ids) {
        if (ids == null) return new HashSet<>();
        return new HashSet<>(accountRepository.findAllById(ids));
    }

    private Set<Label> resolveLabels(Set<UUID> ids) {
        if (ids == null) return new HashSet<>();
        return new HashSet<>(labelRepository.findAllById(ids));
    }
}