package astoppello.wallet.service;

import astoppello.wallet.domain.Institution;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.InstitutionMapper;
import astoppello.wallet.repository.InstitutionRepository;
import astoppello.wallet.service.impl.InstitutionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

    public static final String BANK = "Bank";
    @Mock
    private InstitutionMapper mapper;

    @Mock
    private InstitutionRepository repository;

    @InjectMocks
    private InstitutionServiceImpl service;

    @Test
    void save() {
        InstitutionDto dto = InstitutionDto.builder().name(BANK).build();
        Institution domain = Institution.builder().name(BANK).build();
        Institution saved = Institution.builder().id(UUID.randomUUID()).name(BANK).build();
        InstitutionDto savedDto = InstitutionDto.builder().id(saved.getId()).name(BANK).build();

        when(mapper.toDomain(dto)).thenReturn(domain);
        when(repository.save(domain)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(savedDto);

        InstitutionDto result = service.save(dto);

        assertThat(result.getId()).isEqualTo(saved.getId());
        assertThat(result.getName()).isEqualTo(BANK);
        verify(mapper).toDomain(dto);
        verify(repository).save(domain);
        verify(mapper).toDto(saved);
    }

    @Test
    void getAll() {
        Institution inst = Institution.builder().id(UUID.randomUUID()).name(BANK).build();
        InstitutionDto dto = InstitutionDto.builder().id(inst.getId()).name(BANK).build();

        when(repository.findAll()).thenReturn(List.of(inst));
        when(mapper.toDto(inst)).thenReturn(dto);

        List<InstitutionDto> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo(BANK);
        verify(repository).findAll();
        verify(mapper).toDto(inst);
    }

    @Test
    void getByID() {
        UUID id = UUID.randomUUID();
        Institution inst = Institution.builder().id(id).name(BANK).build();
        InstitutionDto dto = InstitutionDto.builder().id(id).name(BANK).build();

        when(repository.findById(id)).thenReturn(Optional.of(inst));
        when(mapper.toDto(inst)).thenReturn(dto);

        InstitutionDto result = service.getByID(id);

        assertThat(result.getId()).isEqualTo(id);
        verify(repository).findById(id);
        verify(mapper).toDto(inst);
    }

    @Test
    void getByID_notFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByID(id))
                .isInstanceOf(NotFoundException.class);
        verify(repository).findById(id);
    }

    @Test
    void getByName() {
        Institution inst = Institution.builder().id(UUID.randomUUID()).name(BANK).build();
        InstitutionDto dto = InstitutionDto.builder().id(inst.getId()).name(BANK).build();

        when(repository.findByName(BANK)).thenReturn(Optional.of(inst));
        when(mapper.toDto(inst)).thenReturn(dto);

        InstitutionDto result = service.getByName(BANK);

        assertThat(result.getName()).isEqualTo(BANK);
        verify(repository).findByName(BANK);
        verify(mapper).toDto(inst);
    }

    @Test
    void getByName_notFound() {
        when(repository.findByName("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByName("missing"))
                .isInstanceOf(NotFoundException.class);
        verify(repository).findByName("missing");
    }

    @Test
    void update_nameAndColor() {
        UUID id = UUID.randomUUID();
        Institution existing = Institution.builder().id(id).name("Old").color("red").build();
        existing.setTrackingDate(new astoppello.wallet.domain.TrackingDate());
        existing.getTrackingDate().setUpdatedAt(java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

        InstitutionDto updateDto = InstitutionDto.builder().name("New").color("blue").build();
        InstitutionDto updatedDto = InstitutionDto.builder().id(id).name("New").color("blue").build();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        when(mapper.toDto(existing)).thenReturn(updatedDto);

        InstitutionDto result = service.update(id, updateDto);

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getColor()).isEqualTo("blue");

        verify(repository).findById(id);
        verify(repository).save(existing);
        verify(mapper).toDto(existing);
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(repository).deleteById(id);
    }
}
