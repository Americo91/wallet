package astoppello.wallet.service;

import astoppello.wallet.domain.Goal;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.GoalDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.GoalMapper;
import astoppello.wallet.repository.GoalRepository;
import astoppello.wallet.service.impl.GoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {

    public static final String GOAL_NAME = "Emergency Fund";
    public static final BigDecimal GOAL_AMOUNT = new BigDecimal("10000.00");

    @Mock
    private GoalMapper mapper;
    @Mock
    private GoalRepository repository;

    @InjectMocks
    private GoalServiceImpl service;

    private GoalDto dto;
    private Goal domain;

    @BeforeEach
    void setUp() {
        dto = new GoalDto(GOAL_NAME, GOAL_AMOUNT);
        domain = Goal.builder().id(UUID.randomUUID()).name(GOAL_NAME).amount(GOAL_AMOUNT).trackingDate(TrackingDate.now()).build();
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        List<GoalDto> results = service.getAll();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo(GOAL_NAME);
        verify(repository).findAll();
        verify(mapper).toDto(domain);
    }

    @Test
    void getByID() {
        UUID id = domain.getId();
        dto.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        GoalDto result = service.getByID(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(GOAL_NAME);
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
    void getByName() {
        when(repository.findByName(GOAL_NAME)).thenReturn(Optional.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        GoalDto result = service.getByName(GOAL_NAME);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(GOAL_NAME);
        verify(repository).findByName(GOAL_NAME);
        verify(mapper).toDto(domain);
    }

    @Test
    void getByName_notFound() {
        when(repository.findByName(GOAL_NAME)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByName(GOAL_NAME)).isInstanceOf(NotFoundException.class);
        verifyNoInteractions(mapper);
    }

    @Test
    void save() {
        UUID id = UUID.randomUUID();
        Goal unsaved = Goal.builder().name(GOAL_NAME).amount(GOAL_AMOUNT).build();
        Goal saved = Goal.builder().id(id).name(GOAL_NAME).amount(GOAL_AMOUNT).trackingDate(TrackingDate.now()).build();
        GoalDto savedDto = new GoalDto(GOAL_NAME, GOAL_AMOUNT).id(id);

        when(mapper.toDomain(dto)).thenReturn(unsaved);
        when(repository.save(unsaved)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(savedDto);

        GoalDto result = service.save(dto);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(GOAL_NAME);
        assertThat(result.getAmount()).isEqualTo(GOAL_AMOUNT);
        verify(mapper).toDomain(dto);
        verify(repository).save(unsaved);
        verify(mapper).toDto(saved);
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        Goal existing = Goal.builder().id(id).name("Old Goal").amount(new BigDecimal("5000.00")).trackingDate(TrackingDate.now()).build();
        Goal mapped = Goal.builder().name(GOAL_NAME).amount(GOAL_AMOUNT).build();
        GoalDto updatedDto = new GoalDto(GOAL_NAME, GOAL_AMOUNT).id(id);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(mapper.toDomain(dto)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(mapped);
        when(mapper.toDto(mapped)).thenReturn(updatedDto);

        GoalDto result = service.update(id, dto);
        assertThat(result.getName()).isEqualTo(GOAL_NAME);
        assertThat(result.getAmount()).isEqualTo(GOAL_AMOUNT);
        // the new entity keeps the existing id and createdAt
        assertThat(mapped.getId()).isEqualTo(id);
        assertThat(mapped.getTrackingDate().getCreatedAt()).isEqualTo(existing.getTrackingDate().getCreatedAt());
        verify(repository).findById(id);
        verify(repository).save(mapped);
        verify(mapper).toDto(mapped);
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

    @Test
    void addSavedAmount() {
        UUID id = domain.getId();
        dto.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(domain));
        when(repository.save(domain)).thenReturn(domain);
        when(mapper.toDto(domain)).thenReturn(dto);

        service.addSavedAmount(id, BigDecimal.TEN);

        verify(repository).findById(id);
        verify(repository).save(domain);
        verify(mapper).toDto(domain);
    }

    @Test
    void markAsReached() {
        when(repository.findById(domain.getId())).thenReturn(Optional.of(domain));

        service.markAsReached(domain.getId());

        verify(repository).findById(domain.getId());
        verify(repository).save(domain);
    }
}