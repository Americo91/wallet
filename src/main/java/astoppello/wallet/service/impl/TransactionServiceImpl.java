package astoppello.wallet.service.impl;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.TransactionMapper;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.repository.TransactionRepository;
import astoppello.wallet.service.TransactionService;
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
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper mapper;
    private final TransactionRepository repository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final LabelRepository labelRepository;

    @Override
    public List<TransactionDto> getAll() {
        return repository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public TransactionDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    @Override
    public TransactionDto save(UUID accountId, UUID categoryId, Set<UUID> labelIds, TransactionDto dto) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(Account.class, accountId));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        Transaction domain = mapper.toDomain(dto);
        domain.setAccount(account);
        domain.setCategory(category);
        domain.setLabels(resolveLabels(labelIds));
        domain.setTrackingDate(TrackingDate.now());
        if (domain.getDate() == null) {
            domain.setDate(Timestamp.valueOf(LocalDateTime.now()));
        }
        return mapper.toDto(repository.save(domain));
    }

    @Override
    public TransactionDto update(UUID id, UUID accountId, UUID categoryId, Set<UUID> labelIds, TransactionDto dto) {
        Transaction transaction = getById(id);

        if (dto.getType() != null) {
            transaction.setType(dto.getType());
        }
        if (dto.getAmount() != null) {
            transaction.setAmount(dto.getAmount());
        }
        if (dto.getDate() != null) {
            transaction.setDate(java.sql.Timestamp.valueOf(dto.getDate().atStartOfDay()));
        }
        if (dto.getDescription() != null) {
            transaction.setDescription(dto.getDescription());
        }
        if (dto.getMerchant() != null) {
            transaction.setMerchant(dto.getMerchant());
        }
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException(Category.class, categoryId));
            transaction.setCategory(category);
        }
        if (accountId != null) {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException(Account.class, accountId));
            transaction.setAccount(account);
        }
        if (labelIds != null) {
            transaction.setLabels(resolveLabels(labelIds));
        }
        transaction.getTrackingDate().touch();
        return mapper.toDto(repository.save(transaction));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private @NonNull Transaction getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(Transaction.class, id));
    }

    private Set<Label> resolveLabels(Set<UUID> labelIds) {
        if (labelIds == null || labelIds.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(labelRepository.findAllById(labelIds));
    }
}