package astoppello.wallet.service;

import astoppello.wallet.domain.*;
import astoppello.wallet.dto.StandingOrderDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.StandingOrderMapper;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.CategoryRepository;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.repository.StandingOrderRepository;
import astoppello.wallet.service.impl.StandingOrderServiceImpl;
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
class StandingOrderServiceTest {

    @Mock
    private StandingOrderMapper mapper;
    @Mock
    private StandingOrderRepository repository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private LabelRepository labelRepository;

    @InjectMocks
    private StandingOrderServiceImpl service;

    private UUID standingOrderId;
    private UUID accountId;
    private UUID categoryId;
    private Account account;
    private Category category;
    private StandingOrder standingOrder;
    private StandingOrderDto dto;

    @BeforeEach
    void setUp() {
        standingOrderId = UUID.randomUUID();
        accountId = UUID.randomUUID();
        categoryId = UUID.randomUUID();

        account = Account.builder().id(accountId).name("Revolut").balance(BigDecimal.ZERO).build();
        category = Category.builder().id(categoryId).name("Subscriptions").build();

        standingOrder = StandingOrder.builder()
                .id(standingOrderId)
                .name("Spotify")
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(Timestamp.valueOf(LocalDateTime.now().plusDays(5)))
                .account(account)
                .category(category)
                .labels(new HashSet<>())
                .enabled(true)
                .trackingDate(TrackingDate.now())
                .build();

        dto = new StandingOrderDto("Spotify", new BigDecimal("3"),
                StandingOrderDto.CurrencyEnum.EUR, StandingOrderDto.FrequencyEnum.MONTHLY,
                StandingOrderDto.TypeEnum.EXPENSE, LocalDate.now().plusDays(5), categoryId)
                .enabled(true);
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(standingOrder));
        when(mapper.toDto(standingOrder)).thenReturn(dto);

        List<StandingOrderDto> results = service.getAll();

        assertThat(results).hasSize(1);
        verify(repository).findAll();
        verify(mapper).toDto(standingOrder);
    }

    @Test
    void getByID() {
        StandingOrderDto expectedDto = new StandingOrderDto().id(standingOrderId);
        when(repository.findById(standingOrderId)).thenReturn(Optional.of(standingOrder));
        when(mapper.toDto(standingOrder)).thenReturn(expectedDto);

        StandingOrderDto result = service.getByID(standingOrderId);

        assertThat(result.getId()).isEqualTo(standingOrderId);
        verify(repository).findById(standingOrderId);
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
        StandingOrder unsaved = StandingOrder.builder()
                .name("Spotify")
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .labels(new HashSet<>())
                .build();
        StandingOrderDto savedDto = new StandingOrderDto().id(standingOrderId);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(mapper.toDomain(dto)).thenReturn(unsaved);
        when(repository.save(unsaved)).thenReturn(standingOrder);
        when(mapper.toDto(standingOrder)).thenReturn(savedDto);

        StandingOrderDto result = service.save(accountId, dto);

        assertThat(result.getId()).isEqualTo(standingOrderId);
        verify(accountRepository).findById(accountId);
        verify(categoryRepository).findById(categoryId);
        verify(mapper).toDomain(dto);
        verify(repository).save(unsaved);
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

        assertThatThrownBy(() -> service.save(accountId, dto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper, repository);
    }

    @Test
    void save_withLabels() {
        UUID labelId = UUID.randomUUID();
        Label label = Label.builder().id(labelId).name("Fixed Cost").build();
        StandingOrder unsaved = StandingOrder.builder()
                .name("Spotify")
                .amount(BigDecimal.valueOf(3))
                .labels(new HashSet<>())
                .build();
        StandingOrderDto savedDto = new StandingOrderDto().id(standingOrderId).labels(Set.of(labelId));

        dto.setLabels(Set.of(labelId));

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(labelRepository.findAllById(Set.of(labelId))).thenReturn(List.of(label));
        when(mapper.toDomain(dto)).thenReturn(unsaved);
        when(repository.save(unsaved)).thenReturn(standingOrder);
        when(mapper.toDto(standingOrder)).thenReturn(savedDto);

        StandingOrderDto result = service.save(accountId, dto);

        assertThat(result.getLabels()).contains(labelId);
        verify(labelRepository).findAllById(Set.of(labelId));
    }

    @Test
    void update() {
        StandingOrderDto updateDto = new StandingOrderDto()
                .name("Spotify Premium")
                .amount(new BigDecimal("9.99"))
                .frequency(StandingOrderDto.FrequencyEnum.MONTHLY)
                .enabled(true);
        StandingOrderDto updatedDto = new StandingOrderDto().id(standingOrderId);

        when(repository.findById(standingOrderId)).thenReturn(Optional.of(standingOrder));
        when(repository.save(standingOrder)).thenReturn(standingOrder);
        when(mapper.toDto(standingOrder)).thenReturn(updatedDto);

        StandingOrderDto result = service.update(standingOrderId, updateDto);

        assertThat(result.getId()).isEqualTo(standingOrderId);
        assertThat(standingOrder.getName()).isEqualTo("Spotify Premium");
        assertThat(standingOrder.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(9.99));
        verify(repository).findById(standingOrderId);
        verify(repository).save(standingOrder);
    }

    @Test
    void update_notFound() {
        when(repository.findById(standingOrderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(standingOrderId, dto))
                .isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void delete() {
        when(repository.findById(standingOrderId)).thenReturn(Optional.of(standingOrder));

        service.delete(standingOrderId);

        verify(repository).delete(standingOrder);
    }

    @Test
    void getUpcoming() {
        when(repository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(standingOrder));
        when(mapper.toDto(standingOrder)).thenReturn(dto);

        List<StandingOrderDto> results = service.getUpcoming(3);

        assertThat(results).hasSize(1);
        verify(repository).findByEnabledTrueAndNextOccurrenceBetween(any(), any());
    }
}
