package astoppello.wallet.service;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.BudgetDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.BudgetMapper;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.BudgetRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.service.impl.BudgetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BudgetServiceTest {

    public static final String BUDGET_NAME = "Monthly groceries";

    @Mock
    private BudgetMapper mapper;
    @Mock
    private BudgetRepository repository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private BudgetServiceImpl service;

    private BudgetDto dto;
    private Budget domain;

    @BeforeEach
    void setUp() {
        UUID categoryId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        UUID labelId = UUID.randomUUID();

        dto = BudgetDto.builder()
                .name(BUDGET_NAME)
                .period(Frequency.MONTHLY)
                .budgetLimit(new BigDecimal("500.00"))
                .categoryIds(Set.of(categoryId))
                .accountIds(Set.of(accountId))
                .labelIds(Set.of(labelId))
                .build();

        domain = Budget.builder()
                .id(UUID.randomUUID())
                .name(BUDGET_NAME)
                .period(Frequency.MONTHLY)
                .budgetLimit(new BigDecimal("500.00"))
                .categories(Set.of(Category.builder().id(categoryId).build()))
                .accounts(Set.of(Account.builder().id(accountId).build()))
                .labels(Set.of(Label.builder().id(labelId).build()))
                .trackingDate(TrackingDate.now())
                .build();
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        List<BudgetDto> results = service.getAll();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo(BUDGET_NAME);
        verify(repository).findAll();
        verify(mapper).toDto(domain);
    }

    @Test
    void getByID() {
        UUID id = domain.getId();
        dto.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        BudgetDto result = service.getByID(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(BUDGET_NAME);
        verify(repository).findById(id);
        verify(mapper).toDto(domain);
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
        UUID id = UUID.randomUUID();
        Budget unsaved = Budget.builder().name(BUDGET_NAME).period(Frequency.MONTHLY).budgetLimit(new BigDecimal("500.00")).build();
        Budget saved = Budget.builder().id(id).name(BUDGET_NAME).period(Frequency.MONTHLY).budgetLimit(new BigDecimal("500.00")).trackingDate(TrackingDate.now()).build();
        BudgetDto savedDto = BudgetDto.builder().id(id).name(BUDGET_NAME).period(Frequency.MONTHLY).budgetLimit(new BigDecimal("500.00")).build();

        when(mapper.toDomain(dto)).thenReturn(unsaved);
        when(categoryRepository.findAllById(dto.getCategoryIds())).thenReturn(List.of());
        when(accountRepository.findAllById(dto.getAccountIds())).thenReturn(List.of());
        when(labelRepository.findAllById(dto.getLabelIds())).thenReturn(List.of());
        when(repository.save(unsaved)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(savedDto);

        BudgetDto result = service.save(dto);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(BUDGET_NAME);
        verify(mapper).toDomain(dto);
        verify(repository).save(unsaved);
        verify(mapper).toDto(saved);
    }

    @Test
    void update() {
        UUID id = domain.getId();
        BudgetDto updatedDto = BudgetDto.builder().id(id).name(BUDGET_NAME).period(Frequency.MONTHLY).budgetLimit(new BigDecimal("500.00")).build();

        when(repository.findById(id)).thenReturn(Optional.of(domain));
        when(categoryRepository.findAllById(dto.getCategoryIds())).thenReturn(new ArrayList<>(domain.getCategories()));
        when(accountRepository.findAllById(dto.getAccountIds())).thenReturn(new ArrayList<>(domain.getAccounts()));
        when(labelRepository.findAllById(dto.getLabelIds())).thenReturn(new ArrayList<>(domain.getLabels()));
        when(repository.save(domain)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(updatedDto);

        BudgetDto result = service.update(id, dto);
        assertThat(result.getName()).isEqualTo(BUDGET_NAME);
        verify(repository).findById(id);
        verify(repository).save(domain);
        verify(mapper).toDto(domain);
    }

    @Test
    void update_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(id, dto)).isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();
        service.delete(id);
        verify(repository).deleteById(id);
    }
}