package astoppello.wallet.mapper;

import astoppello.wallet.domain.Goal;
import astoppello.wallet.dto.GoalDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {TrackingMapper.class})
public interface GoalMapper {

    @Mapping(target = "trackingDate", source = "trackingDate")
    GoalDto toDto(Goal domain);

    @Mapping(target = "trackingDate", ignore = true)
    Goal toDomain(GoalDto dto);
}