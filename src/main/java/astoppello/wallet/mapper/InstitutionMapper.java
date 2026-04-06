package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Institution;
import astoppello.wallet.dto.InstitutionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses = {TrackingMapper.class})
public interface InstitutionMapper {

    @Mapping(target = "accounts", source = "accounts", qualifiedByName = "mapAccounts")
    InstitutionDto toDto(Institution domain);

    @Mapping(target = "trackingDate", ignore = true)
    @Mapping(target = "accounts", ignore = true)
    Institution toDomain(InstitutionDto dto);

    @Named("mapAccounts")
    default Set<String> mapAccounts(Set<Account> accounts) {
        if (accounts == null) return null;
        return accounts.stream()
                .map(Account::getName)
                .collect(Collectors.toSet());
    }
}