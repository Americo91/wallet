package astoppello.wallet.mapper;

import astoppello.wallet.domain.Label;
import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.dto.StandingOrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(uses = {TrackingMapper.class, DateMapper.class})
public interface StandingOrderMapper {

    @Mapping(target = "trackingDate", source = "trackingDate")
    @Mapping(target = "account", source = "account.id")
    @Mapping(target = "category", source = "category.id")
    @Mapping(target = "labels", source = "labels", qualifiedByName = "mapLabels")
    StandingOrderDto toDto(StandingOrder domain);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "labels", ignore = true)
    StandingOrder toDomain(StandingOrderDto dto);

    @Named("mapLabels")
    default Set<UUID> mapLabels(Set<Label> labels) {
        if (labels == null) return null;
        return labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
