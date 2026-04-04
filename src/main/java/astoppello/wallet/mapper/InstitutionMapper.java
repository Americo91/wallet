package astoppello.wallet.mapper;

import astoppello.wallet.domain.Institution;
import astoppello.wallet.dto.InstitutionDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface InstitutionMapper {

    InstitutionDto toDto(Institution domain);
    Institution toDomain(InstitutionDto dto);
}
