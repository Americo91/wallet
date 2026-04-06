package astoppello.wallet.mapper;

import astoppello.wallet.domain.Institution;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.InstitutionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {InstitutionMapperImpl.class, TrackingMapperImpl.class, DateMapper.class})
class InstitutionMapperTest {

    @Autowired
    private InstitutionMapper mapper;

    @Test
    void toDto() {
        Institution institution = Institution.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .color("red")
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();

        InstitutionDto dto = mapper.toDto(institution);

        assertThat(dto.getId()).isEqualTo(institution.getId());
        assertThat(dto.getName()).isEqualTo(institution.getName());
        assertThat(dto.getColor()).isEqualTo(institution.getColor());
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDto_nullColor() {
        Institution institution = Institution.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .color(null)
                .build();

        InstitutionDto dto = mapper.toDto(institution);

        assertThat(dto.getColor()).isNull();
        assertThat(dto.getName()).isEqualTo(institution.getName());
    }

    @Test
    void toDomain() {
        InstitutionDto dto = InstitutionDto.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .color("red")
                .build();

        Institution domain = mapper.toDomain(dto);

        assertThat(domain.getId()).isEqualTo(dto.getId());
        assertThat(domain.getName()).isEqualTo(dto.getName());
        assertThat(domain.getColor()).isEqualTo(dto.getColor());
        assertThat(domain.getTrackingDate()).isNull();
    }
}