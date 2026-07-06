package astoppello.wallet.service.impl;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.StandingOrderMapper;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.repository.StandingOrderRepository;
import astoppello.wallet.service.StandingOrderService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public StandingOrderDto update(UUID id, StandingOrderDto dto) {
        StandingOrder domain = getById(id);

        if (StringUtils.isNotEmpty(dto.getName())) {
            domain.setName(dto.getName());
        }
        if (dto.getAmount() != null) {
            domain.setAmount(dto.getAmount());
        }
        if (dto.getCurrency() != null) {
            domain.setCurrency(Currency.valueOf(dto.getCurrency().name()));
        }
        if (dto.getFrequency() != null) {
            domain.setFrequency(Frequency.valueOf(dto.getFrequency().name()));
        }
        if (dto.getType() != null) {
            domain.setType(TransactionType.valueOf(dto.getType().name()));
        }
        if (dto.getNextOccurrence() != null) {
            domain.setNextOccurrence(Timestamp.valueOf(dto.getNextOccurrence().atStartOfDay()));
        }
        if (dto.getCategory() != null) {
            Category category = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(Category.class, dto.getCategory()));
            domain.setCategory(category);
        }
        if (dto.getAccount() != null) {
            Account account = getAccount(dto.getAccount());
            domain.setAccount(account);
        }
        if (CollectionUtils.isNotEmpty(dto.getLabels())) {
            domain.setLabels(resolveLabels(dto.getLabels()));
        }
        if (dto.getDescription() != null) {
            domain.setDescription(dto.getDescription());
        }
        if (dto.getPayee() != null) {
            domain.setPayee(dto.getPayee());
        }
        if (dto.getEnabled() != null) {
            domain.setEnabled(dto.getEnabled());
        }
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
