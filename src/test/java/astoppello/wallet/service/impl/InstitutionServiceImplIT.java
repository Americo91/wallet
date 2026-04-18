package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Institution;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.InstitutionDto;
import astoppello.wallet.exception.NotFoundException;
import astoppello.wallet.mapper.DateMapper;
import astoppello.wallet.mapper.InstitutionMapperImpl;
import astoppello.wallet.mapper.TrackingMapperImpl;
import astoppello.wallet.repository.InstitutionRepository;
import astoppello.wallet.service.InstitutionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@ComponentScan(basePackages = {"astoppello.wallet.service.impl", "astoppello.wallet.mapper"})
class InstitutionServiceImplIT {

    @Autowired
    private InstitutionService service;

    private InstitutionDto buildDto(String name) {
        return InstitutionDto.builder().name(name).color("blue").build();
    }

    @Test
    void save() {
        InstitutionDto saved = service.save(buildDto("Bank"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Bank");
        assertThat(saved.getColor()).isEqualTo("blue");
        assertThat(saved.getTrackingDate()).isNotNull();
        assertThat(saved.getTrackingDate().getCreatedAt()).isNotNull();
        assertThat(saved.getTrackingDate().getUpdatedAt()).isNotNull();
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void getAll() {
        service.save(buildDto("Bank A"));
        service.save(buildDto("Bank B"));

        List<InstitutionDto> result = service.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(InstitutionDto::getName).containsExactlyInAnyOrder("Bank A", "Bank B");
    }

    @Test
    void getByID() {
        InstitutionDto saved = service.save(buildDto("Bank"));

        InstitutionDto found = service.getByID(saved.getId());

        assertThat(found.getId()).isEqualTo(saved.getId());
        assertThat(found.getName()).isEqualTo("Bank");
    }

    @Test
    void getByID_notFound() {
        assertThatThrownBy(() -> service.getByID(UUID.randomUUID()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getByName() {
        service.save(buildDto("Bank"));

        InstitutionDto found = service.getByName("Bank");

        assertThat(found.getName()).isEqualTo("Bank");
    }

    @Test
    void getByName_notFound() {
        assertThatThrownBy(() -> service.getByName("missing"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update() {
        InstitutionDto saved = service.save(buildDto("Bank"));

        InstitutionDto updated = service.update(saved.getId(),
                InstitutionDto.builder().name("New Name").color("red").build());

        assertThat(updated.getName()).isEqualTo("New Name");
        assertThat(updated.getColor()).isEqualTo("red");
    }

    @Test
    void delete() {
        InstitutionDto saved = service.save(buildDto("Bank"));

        service.delete(saved.getId());

        assertThat(service.getAll()).hasSize(0);
    }
}