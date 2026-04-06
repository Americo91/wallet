package astoppello.wallet.mapper;

import astoppello.wallet.domain.Label;
import astoppello.wallet.dto.LabelDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {TrackingMapper.class})
public interface LabelMapper {

    @Mapping(target = "trackingDate", source = "trackingDate")
    LabelDto toDto(Label domain);

    @Mapping(target = "trackingDate", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    Label toDomain(LabelDto dto);
}
