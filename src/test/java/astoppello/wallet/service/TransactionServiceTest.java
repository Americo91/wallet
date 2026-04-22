package astoppello.wallet.service;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.Label;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.TransactionDto;
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

    @BeforeEach
    void setUp() {
        transactionId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        account = Account.builder().id(accountId).name("Checking").build();
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

        dto = TransactionDto.builder()
                .type(TransactionType.EXPENSE)
                .amount(BigDecimal.TEN)
                .date(LocalDate.now())
                .build();
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
        TransactionDto expectedDto = TransactionDto.builder().id(transactionId).build();
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
        TransactionDto savedDto = TransactionDto.builder().id(transactionId).build();

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
        verify(mapper).toDto(transaction);
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
        TransactionDto savedDto = TransactionDto.builder().id(transactionId).labels(Set.of(labelId)).build();

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
        TransactionDto updateDto = TransactionDto.builder()
                .type(TransactionType.INCOME)
                .amount(BigDecimal.ONE)
                .date(LocalDate.now())
                .description("new desc")
                .merchant("Shop")
                .build();
        TransactionDto updatedDto = TransactionDto.builder().id(transactionId).build();

        when(repository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(repository.save(transaction)).thenReturn(transaction);
        when(mapper.toDto(transaction)).thenReturn(updatedDto);

        TransactionDto result = service.update(transactionId, updateDto);

        assertThat(result.getId()).isEqualTo(transactionId);
        verify(repository).findById(transactionId);
        verify(repository).save(transaction);
        verifyNoInteractions(categoryRepository, labelRepository);
    }

    @Test
    void update_withCategoryAndLabels() {
        UUID newCategoryId = UUID.randomUUID();
        UUID labelId = UUID.randomUUID();
        Category newCategory = Category.builder().id(newCategoryId).name("Travel").build();
        Label label = Label.builder().id(labelId).name("trip").build();
        TransactionDto updatedDto = TransactionDto.builder().id(transactionId).build();

        when(repository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryRepository.findById(newCategoryId)).thenReturn(Optional.of(newCategory));
        when(labelRepository.findAllById(Set.of(labelId))).thenReturn(List.of(label));
        when(repository.save(transaction)).thenReturn(transaction);
        when(mapper.toDto(transaction)).thenReturn(updatedDto);

        service.update(transactionId, TransactionDto.builder().category(newCategoryId).labels(Set.of(labelId)).build());

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
        service.delete(transactionId);
        verify(repository).deleteById(transactionId);
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
}