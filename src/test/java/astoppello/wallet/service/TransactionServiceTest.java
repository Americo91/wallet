package astoppello.wallet.service;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.Label;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.dto.TransferDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.TransactionMapper;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.repository.TransactionRepository;
import astoppello.wallet.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionMapper mapper;
    @Mock
    private TransactionRepository repository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private TransactionServiceImpl service;

    private UUID transactionId;
    private UUID accountId;
    private UUID categoryId;
    private Account account;
    private Category category;
    private Transaction transaction;
    private TransactionDto dto;
    private TransferDto transferDto = new TransferDto(BigDecimal.TEN);

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        account = Account.builder().id(accountId).name("Checking").balance(BigDecimal.ZERO).build();
        category = Category.builder().id(categoryId).name("Food").build();

        transaction = Transaction.builder()
                .id(transactionId)
                .account(account)
                .category(category)
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(Timestamp.valueOf(LocalDateTime.now()))
                .labels(new HashSet<>())
                .trackingDate(TrackingDate.now())
                .build();

        dto = new TransactionDto()
                .type(TransactionDto.TypeEnum.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(LocalDate.now());
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(transaction));
        when(mapper.toDto(transaction)).thenReturn(dto);

        List<TransactionDto> results = service.getAll();

        assertThat(results).hasSize(1);
        verify(repository).findAll();
        verify(mapper).toDto(transaction);
    }

    @Test
    void getByID() {
        TransactionDto expectedDto = new TransactionDto().id(transactionId);
        when(repository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(mapper.toDto(transaction)).thenReturn(expectedDto);

        TransactionDto result = service.getByID(transactionId);

        assertThat(result.getId()).isEqualTo(transactionId);
        verify(repository).findById(transactionId);
        verify(mapper).toDto(transaction);
    }

    @Test
    void getByID_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByID(id)).isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void save() {
        Transaction unsaved = Transaction.builder()
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(Timestamp.valueOf(LocalDateTime.now()))
                .labels(new HashSet<>())
                .build();
        TransactionDto savedDto = new TransactionDto().id(transactionId);

        dto.setCategory(categoryId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(mapper.toDomain(dto)).thenReturn(unsaved);
        when(repository.save(unsaved)).thenReturn(transaction);
        when(mapper.toDto(transaction)).thenReturn(savedDto);

        TransactionDto result = service.save(accountId, dto);

        assertThat(result.getId()).isEqualTo(transactionId);
        verify(accountRepository).findById(accountId);
        verify(categoryRepository).findById(categoryId);
        verify(mapper).toDomain(dto);
        verify(repository).save(unsaved);
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.TEN.negate());
        verify(accountRepository).save(account);
    }

    @Test
    void save_accountNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.save(accountId, dto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper, repository, categoryRepository);
    }

    @Test
    void save_categoryNotFound() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        dto.setCategory(categoryId);
        assertThatThrownBy(() -> service.save(accountId, dto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper, repository);
    }

    @Test
    void save_withLabels() {
        UUID labelId = UUID.randomUUID();
        Label label = Label.builder().id(labelId).name("urgent").build();
        Transaction unsaved = Transaction.builder()
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(Timestamp.valueOf(LocalDateTime.now()))
                .labels(new HashSet<>())
                .build();
        TransactionDto savedDto = new TransactionDto().id(transactionId).labels(Set.of(labelId));

        dto.setCategory(categoryId);
        dto.setLabels(Set.of(labelId));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(labelRepository.findAllById(Set.of(labelId))).thenReturn(List.of(label));
        when(mapper.toDomain(dto)).thenReturn(unsaved);
        when(repository.save(unsaved)).thenReturn(transaction);
        when(mapper.toDto(transaction)).thenReturn(savedDto);

        TransactionDto result = service.save(accountId, dto);

        assertThat(result.getLabels()).contains(labelId);
        verify(labelRepository).findAllById(Set.of(labelId));
    }

    @Test
    void update() {
        TransactionDto updateDto = new TransactionDto()
                .type(TransactionDto.TypeEnum.INCOME)
                .amount(BigDecimal.ONE)
                .date(LocalDate.now())
                .description("new desc")
                .payee("Shop");
        TransactionDto updatedDto = new TransactionDto().id(transactionId);

        when(repository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(repository.save(transaction)).thenReturn(transaction);
        when(mapper.toDto(transaction)).thenReturn(updatedDto);

        TransactionDto result = service.update(transactionId, updateDto);

        assertThat(result.getId()).isEqualTo(transactionId);
        verify(repository).findById(transactionId);
        verify(repository).save(transaction);
        // Old EXPENSE(10) reversed (+10), new INCOME(1) applied (+1) = +11
        assertThat(account.getBalance()).isEqualByComparingTo(new BigDecimal("11"));
        verify(accountRepository, times(2)).save(account);
        verifyNoInteractions(categoryRepository, labelRepository);
    }

    @Test
    void update_withCategoryAndLabels() {
        UUID newCategoryId = UUID.randomUUID();
        UUID labelId = UUID.randomUUID();
        Category newCategory = Category.builder().id(newCategoryId).name("Travel").build();
        Label label = Label.builder().id(labelId).name("trip").build();
        TransactionDto updatedDto = new TransactionDto().id(transactionId);

        when(repository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategory));
        when(labelRepository.findAllById(Set.of(labelId))).thenReturn(List.of(label));
        when(repository.save(transaction)).thenReturn(transaction);
        when(mapper.toDto(transaction)).thenReturn(updatedDto);

        service.update(transactionId, new TransactionDto().category(newCategoryId).labels(Set.of(labelId)));

        verify(categoryRepository).findById(newCategoryId);
        verify(labelRepository).findAllById(Set.of(labelId));
        assertThat(transaction.getCategory()).isEqualTo(newCategory);
        assertThat(transaction.getLabels()).contains(label);
    }

    @Test
    void update_notFound() {
        when(repository.findById(transactionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(transactionId, dto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void delete() {
        when(repository.findById(transactionId)).thenReturn(Optional.of(transaction));

        service.delete(transactionId);

        // EXPENSE(10) reversed: balance += 10
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.TEN);
        verify(accountRepository).save(account);
        verify(repository).delete(transaction);
    }

    @Test
    void getAllByAccount() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(repository.findByAccount(account)).thenReturn(List.of(transaction));

        service.getAllByAccount(accountId);
        verify(accountRepository).findById(accountId);
        verify(repository).findByAccount(account);
        verify(mapper).toDto(transaction);
    }

    @Test
    void transfer() {
        UUID toAccountId = UUID.randomUUID();
        Account toAccount = Account.builder().id(toAccountId).name("Savings").balance(BigDecimal.ZERO).build();
        Category transferCategory = Category.builder().id(UUID.randomUUID()).name("Transfer").build();

        TransferDto transferDto = new TransferDto(new BigDecimal("100.00"))
                .date(LocalDate.now())
                .description("Monthly savings");

        TransactionDto expenseDto = new TransactionDto().id(UUID.randomUUID()).type(TransactionDto.TypeEnum.EXPENSE);
        TransactionDto incomeDto = new TransactionDto().id(UUID.randomUUID()).type(TransactionDto.TypeEnum.INCOME);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));
        when(categoryRepository.findByName("Transfer")).thenReturn(List.of(transferCategory));
        when(repository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any(Transaction.class))).thenReturn(expenseDto, incomeDto);

        List<TransactionDto> result = service.transfer(accountId, toAccountId, transferDto);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getType()).isEqualTo(TransactionDto.TypeEnum.EXPENSE);
        assertThat(result.get(1).getType()).isEqualTo(TransactionDto.TypeEnum.INCOME);
        assertThat(account.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(-100));
        assertThat(toAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
        verify(repository, times(2)).save(any(Transaction.class));
        verify(accountRepository).save(account);
        verify(accountRepository).save(toAccount);
    }

    @Test
    void transfer_fromAccountNotFound() {
        UUID toAccountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transfer(accountId, toAccountId, transferDto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(repository, mapper);
    }

    @Test
    void transfer_toAccountNotFound() {
        UUID toAccountId = UUID.randomUUID();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.transfer(accountId, toAccountId, transferDto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(repository, mapper);
    }

    @Test
    void transfer_categoryNotFound() {
        UUID toAccountId = UUID.randomUUID();
        Account toAccount = Account.builder().id(toAccountId).name("Savings").balance(BigDecimal.ZERO).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));
        when(categoryRepository.findByName("Transfer")).thenReturn(List.of());

        assertThatThrownBy(() -> service.transfer(accountId, toAccountId, transferDto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(repository, mapper);
    }
}