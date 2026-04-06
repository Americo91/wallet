package astoppello.wallet.mapper;

import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.dto.TrackingDateDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface TrackingMapper {

    TrackingDateDto toDto(TrackingDate trackingDate);
    TrackingDate toDomain(TrackingDateDto trackingDateDto);
}
