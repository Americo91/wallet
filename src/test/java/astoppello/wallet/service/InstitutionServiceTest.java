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
        InstitutionDto dto = new InstitutionDto(BANK);
        Institution domain = Institution.builder().name(BANK).build();
        Institution saved = Institution.builder().id(UUID.randomUUID()).name(BANK).build();
        InstitutionDto savedDto = new InstitutionDto(BANK).id(saved.getId());

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
        InstitutionDto dto = new InstitutionDto(BANK).id(inst.getId());

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
        InstitutionDto dto = new InstitutionDto(BANK).id(id);

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
        InstitutionDto dto = new InstitutionDto(BANK).id(inst.getId());

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
        Institution existing = Institution.builder().id(id).name("Old").color("red")
                .trackingDate(astoppello.wallet.domain.TrackingDate.now()).build();

        InstitutionDto updateDto = new InstitutionDto("New").color("blue");
        Institution mapped = Institution.builder().name("New").color("blue").build();
        InstitutionDto updatedDto = new InstitutionDto("New").id(id).color("blue");

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(mapper.toDomain(updateDto)).thenReturn(mapped);
        when(repository.save(mapped)).thenReturn(mapped);
        when(mapper.toDto(mapped)).thenReturn(updatedDto);

        InstitutionDto result = service.update(id, updateDto);

        assertThat(result.getName()).isEqualTo("New");
        assertThat(result.getColor()).isEqualTo("blue");
        // the new entity keeps the existing id and createdAt
        assertThat(mapped.getId()).isEqualTo(id);
        assertThat(mapped.getTrackingDate().getCreatedAt()).isEqualTo(existing.getTrackingDate().getCreatedAt());

        verify(repository).findById(id);
        verify(repository).save(mapped);
        verify(mapper).toDto(mapped);
    }

    @Test
    void delete() {
        UUID id = UUID.randomUUID();

        service.delete(id);

        verify(repository).deleteById(id);
    }
}
