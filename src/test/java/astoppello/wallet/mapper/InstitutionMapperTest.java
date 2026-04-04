package astoppello.wallet.mapper;

import astoppello.wallet.domain.Institution;
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
@ContextConfiguration(classes = {InstitutionMapperImpl.class, DateMapper.class})
class InstitutionMapperTest {

    @Autowired
    private InstitutionMapper mapper;

    @Test
    void toDto() {
        Institution institution = Institution.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .color("red")
                .createdAt(Timestamp.valueOf(LocalDateTime.of(2026, 2, 5, 12, 35, 15)))
                .build();

        InstitutionDto dto = mapper.toDto(institution);
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(institution.getId());
        assertThat(dto.getName()).isEqualTo(institution.getName());
        assertThat(dto.getColor()).isEqualTo(institution.getColor());
        assertThat(dto.getCreatedAt()).isEqualTo("2026-02-05T12:35:15Z");
//        assertThat(dto.getAccounts()).isNull();
    }

    @Test
    void toDomain() {
        InstitutionDto dto = InstitutionDto.builder()
                .id(UUID.randomUUID())
                .name("Test")
                .color("red")
                .build();

        Institution domain = mapper.toDomain(dto);
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(dto.getId());
        assertThat(domain.getName()).isEqualTo(dto.getName());
        assertThat(domain.getColor()).isEqualTo(dto.getColor());
        assertThat(domain.getCreatedAt()).isNull();
    }
}