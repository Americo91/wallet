package astoppello.wallet.service;

import astoppello.wallet.domain.Label;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.LabelDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.LabelMapper;
import astoppello.wallet.repository.LabelRepository;
import astoppello.wallet.service.impl.LabelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {

    public static final String LABEL_NAME = "labelName";

    @Mock
    private LabelMapper mapper;
    @Mock
    private LabelRepository repository;

    @InjectMocks
    private LabelServiceImpl service;

    private LabelDto dto;
    private Label domain;

    @BeforeEach
    void setUp() {
        dto = LabelDto.builder().name(LABEL_NAME).build();
        domain = Label.builder().id(UUID.randomUUID()).name(LABEL_NAME).trackingDate(TrackingDate.now()).build();
    }

    @Test
    void getAll() {
        when(repository.findAll()).thenReturn(List.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        List<LabelDto> results = service.getAll();
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo(LABEL_NAME);
        verify(repository).findAll();
        verify(mapper).toDto(domain);
    }

    @Test
    void getByID() {
        UUID id = domain.getId();
        dto.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        LabelDto result = service.getByID(id);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(LABEL_NAME);
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
        when(repository.findByName(LABEL_NAME)).thenReturn(Optional.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        List<LabelDto> results = service.getByName(LABEL_NAME);
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getName()).isEqualTo(LABEL_NAME);
        verify(repository).findByName(LABEL_NAME);
        verify(mapper).toDto(domain);
    }

    @Test
    void save() {
        UUID id = UUID.randomUUID();
        Label unsaved = Label.builder().name(LABEL_NAME).build();
        Label saved = Label.builder().id(id).name(LABEL_NAME).trackingDate(TrackingDate.now()).build();
        LabelDto savedDto = LabelDto.builder().id(id).name(LABEL_NAME).build();

        when(mapper.toDomain(dto)).thenReturn(unsaved);
        when(repository.save(unsaved)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(savedDto);

        LabelDto result = service.save(dto);
        assertThat(result.getId()).isEqualTo(id);
        assertThat(result.getName()).isEqualTo(LABEL_NAME);
        verify(mapper).toDomain(dto);
        verify(repository).save(unsaved);
        verify(mapper).toDto(saved);
    }

    @Test
    void update() {
        UUID id = UUID.randomUUID();
        Label existing = Label.builder().id(id).name("oldName").trackingDate(TrackingDate.now()).build();
        LabelDto updatedDto = LabelDto.builder().id(id).name(LABEL_NAME).build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(updatedDto);

        LabelDto result = service.update(id, dto);
        assertThat(result.getName()).isEqualTo(LABEL_NAME);
        verify(repository).findById(id);
        verify(repository).save(existing);
        verify(mapper).toDto(existing);
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