package astoppello.wallet.service.impl;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.dto.TransferDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.TransactionMapper;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.repository.TransactionRepository;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.service.TransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

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
        return repository.findAll().stream().map(mapper::toDto).sorted(Comparator.comparing(TransactionDto::getDate, Comparator.nullsLast(Comparator.reverseOrder()))).toList();
    }

    @Override
    public TransactionDto getByID(UUID id) {
        return mapper.toDto(getById(id));
    }

    @Override
    public TransactionDto save(UUID accountId, TransactionDto dto) {
        UUID categoryId = dto.getCategory();
        Set<UUID> labelIds = dto.getLabels();
        Account account = getAccount(accountId);
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
        Transaction saved = repository.save(domain);
        applyDelta(account, computeDelta(saved.getType(), saved.getAmount()));
        return mapper.toDto(saved);
    }

    @Override
    public TransactionDto update(UUID id, TransactionDto dto) {
        Transaction transaction = getById(id);
        UUID categoryId = dto.getCategory();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        // Capture old state for balance reversal
        Account oldAccount = transaction.getAccount();
        TransactionType oldType = transaction.getType();
        BigDecimal oldAmount = transaction.getAmount();

        transaction.setType(TransactionType.valueOf(dto.getType().name()));
        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate() != null
                ? Timestamp.valueOf(dto.getDate().atStartOfDay())
                : Timestamp.valueOf(LocalDateTime.now()));
        transaction.setDescription(dto.getDescription());
        transaction.setPayee(dto.getPayee());
        transaction.setCategory(category);
        if (dto.getAccount() != null) {
            transaction.setAccount(getAccount(dto.getAccount()));
        }
        transaction.setLabels(resolveLabels(dto.getLabels()));
        transaction.getTrackingDate().touch();
        Transaction saved = repository.save(transaction);

        // Reverse old balance, apply new
        applyDelta(oldAccount, computeDelta(oldType, oldAmount).negate());
        applyDelta(saved.getAccount(), computeDelta(saved.getType(), saved.getAmount()));

        return mapper.toDto(saved);
    }

    @Override
    public void delete(UUID id) {
        Transaction transaction = getById(id);
        applyDelta(transaction.getAccount(), computeDelta(transaction.getType(), transaction.getAmount()).negate());
        repository.delete(transaction);
    }

    @Override
    public List<TransactionDto> getAllByAccount(UUID accountId) {
        return repository.findByAccount(getAccount(accountId)).stream().map(mapper::toDto).sorted(Comparator.comparing(TransactionDto::getDate, Comparator.nullsLast(Comparator.reverseOrder()))).toList();
    }

    @Override
    public List<TransactionDto> transfer(UUID fromAccountId, UUID toAccountId, TransferDto dto) {
        Account fromAccount = getAccount(fromAccountId);
        Account toAccount = getAccount(toAccountId);
        Category transferCategory = categoryRepository.findByName("Transfer")
                .stream().findFirst()
                .orElseThrow(() -> new NotFoundException(Category.class, "Transfer"));

        Set<Label> labels = resolveLabels(dto.getLabels());
        Timestamp date = dto.getDate() != null
                ? Timestamp.valueOf(dto.getDate().atStartOfDay())
                : Timestamp.valueOf(LocalDateTime.now());

        Transaction expense = Transaction.builder()
                .account(fromAccount)
                .type(TransactionType.EXPENSE)
                .amount(dto.getAmount())
                .date(date)
                .category(transferCategory)
                .description(dto.getDescription())
                .payee(dto.getPayee())
                .labels(labels)
                .trackingDate(TrackingDate.now())
                .build();

        Transaction income = Transaction.builder()
                .account(toAccount)
                .type(TransactionType.INCOME)
                .amount(dto.getAmount())
                .date(date)
                .category(transferCategory)
                .description(dto.getDescription())
                .payee(dto.getPayee())
                .labels(labels)
                .trackingDate(TrackingDate.now())
                .build();

        Transaction savedExpense = repository.save(expense);
        Transaction savedIncome = repository.save(income);

        applyDelta(fromAccount, computeDelta(TransactionType.EXPENSE, dto.getAmount()));
        applyDelta(toAccount, computeDelta(TransactionType.INCOME, dto.getAmount()));

        return List.of(mapper.toDto(savedExpense), mapper.toDto(savedIncome));
    }

    private void applyDelta(Account account, BigDecimal delta) {
        account.setBalance(account.getBalance().add(delta));
        accountRepository.save(account);
    }

    private BigDecimal computeDelta(TransactionType type, BigDecimal amount) {
        return type == TransactionType.INCOME ? amount : amount.negate();
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

    private Account getAccount(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(Account.class, accountId));
    }
}
