package astoppello.wallet.mapper;

import astoppello.wallet.domain.TrackingDate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class TestTrackingData {

    public static final Timestamp CREATED_AT = Timestamp.valueOf(LocalDateTime.of(2026, 1, 10, 9, 0, 0));
    public static final Timestamp UPDATED_AT = Timestamp.valueOf(LocalDateTime.of(2026, 3, 15, 12, 0, 0));
    public static final TrackingDate trackingDate = TrackingDate.builder()
            .createdAt(CREATED_AT)
            .updatedAt(UPDATED_AT)
            .build();
    public static final OffsetDateTime CREATED_AT_OFFSET = OffsetDateTime.of(2026, 1, 10, 9, 0, 0, 0, ZoneOffset.UTC);
    public static final OffsetDateTime UPDATED_AT_OFFSET = OffsetDateTime.of(2026, 3, 15, 12, 0, 0, 0, ZoneOffset.UTC);

    private TestTrackingData() {}
}
