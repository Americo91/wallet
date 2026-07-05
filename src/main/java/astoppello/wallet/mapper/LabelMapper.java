package astoppello.wallet.mapper;

import astoppello.wallet.domain.Label;
import astoppello.wallet.dto.LabelDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class})
public interface LabelMapper {

    @Mapping(target = "createdAt", source = "trackingDate.createdAt")
    @Mapping(target = "updatedAt", source = "trackingDate.updatedAt")
    LabelDto toDto(Label domain);

    @Mapping(target = "trackingDate", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    Label toDomain(LabelDto dto);
}
