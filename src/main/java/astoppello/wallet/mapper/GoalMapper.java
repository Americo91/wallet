package astoppello.wallet.mapper;

import astoppello.wallet.domain.Goal;
import astoppello.wallet.dto.GoalDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class})
public interface GoalMapper {

    @Mapping(target = "createdAt", source = "trackingDate.createdAt")
    @Mapping(target = "updatedAt", source = "trackingDate.updatedAt")
    GoalDto toDto(Goal domain);

    @Mapping(target = "trackingDate", ignore = true)
    Goal toDomain(GoalDto dto);
}
