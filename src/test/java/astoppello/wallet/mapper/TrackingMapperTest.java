package astoppello.wallet.mapper;

import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.TrackingDateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TrackingMapperImpl.class, DateMapper.class})
class TrackingMapperTest {

    public static final Timestamp CREATED_AT = Timestamp.valueOf(LocalDateTime.of(2026, 1, 10, 9, 0, 0));
    public static final Timestamp UPDATED_AT = Timestamp.valueOf(LocalDateTime.of(2026, 3, 15, 12, 0, 0));
    public static final TrackingDate trackingDate = TrackingDate.builder()
            .createdAt(CREATED_AT)
            .updatedAt(UPDATED_AT)
            .build();
    public static final OffsetDateTime CREATED_AT_OFFSET = OffsetDateTime.of(2026, 1, 10, 9, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime UPDATED_AT_OFFSET = OffsetDateTime.of(2026, 3, 15, 12, 0, 0, 0, ZoneOffset.UTC);
    @Autowired
    private TrackingMapper trackingMapper;

    @Test
    void toDto() {
        TrackingDateDto dto = trackingMapper.toDto(trackingDate);
        assertThat(dto).isNotNull();
        assertThat(dto.getCreatedAt()).isEqualTo("2026-01-10T09:00:00Z");
        assertThat(dto.getUpdatedAt()).isEqualTo("2026-03-15T12:00:00Z");
    }

    @Test
    void toDomain() {
        TrackingDateDto dto = TrackingDateDto.builder()
                .createdAt(CREATED_AT_OFFSET)
                .updatedAt(UPDATED_AT_OFFSET)
                .build();

        TrackingDate domain = trackingMapper.toDomain(dto);
        assertThat(domain).isNotNull();
        assertThat(domain.getCreatedAt()).isEqualTo(CREATED_AT);
        assertThat(domain.getUpdatedAt()).isEqualTo(UPDATED_AT);
    }
}