package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.dto.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {TrackingMapper.class})
public interface AccountMapper {

    @Mapping(target = "institution", source = "institution.id")
    AccountDto toDto(Account domain);

    @Mapping(target = "institution", ignore = true)
    Account toDomain(AccountDto dto);
}
