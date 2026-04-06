package astoppello.wallet.mapper;

import astoppello.wallet.domain.Label;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.LabelDto;
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
@ContextConfiguration(classes = {LabelMapperImpl.class, DateMapper.class, TrackingMapperImpl.class})
class LabelMapperTest {

    @Autowired
    private LabelMapper mapper;

    @Test
    void toDto() {
        Label label = Label.builder()
                .id(UUID.randomUUID())
                .name("Recurring")
                .trackingDate(TrackingMapperTest.trackingDate)
                .build();

        LabelDto dto = mapper.toDto(label);

        assertThat(dto.getId()).isEqualTo(label.getId());
        assertThat(dto.getName()).isEqualTo("Recurring");
        assertThat(dto.getTrackingDate().getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getTrackingDate().getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDto_nullTrackingDate() {
        Label label = Label.builder()
                .id(UUID.randomUUID())
                .name("Business")
                .build();

        LabelDto dto = mapper.toDto(label);

        assertThat(dto.getName()).isEqualTo("Business");
        assertThat(dto.getTrackingDate()).isNull();
    }

    @Test
    void toDomain() {
        LabelDto dto = LabelDto.builder()
                .id(UUID.randomUUID())
                .name("Tax Deductible")
                .build();

        Label domain = mapper.toDomain(dto);

        assertThat(domain.getId()).isEqualTo(dto.getId());
        assertThat(domain.getName()).isEqualTo("Tax Deductible");
        assertThat(domain.getTrackingDate()).isNull();
    }
}