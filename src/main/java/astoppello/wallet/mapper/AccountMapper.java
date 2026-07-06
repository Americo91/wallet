package astoppello.wallet.mapper;

import astoppello.wallet.domain.Account;
import astoppello.wallet.dto.AccountDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class})
public interface AccountMapper {

    @Mapping(target = "institution", source = "institution.id")
    @Mapping(target = "createdAt", source = "trackingDate.createdAt")
    @Mapping(target = "updatedAt", source = "trackingDate.updatedAt")
    AccountDto toDto(Account domain);

    @Mapping(target = "institution", ignore = true)
    @Mapping(target = "trackingDate", ignore = true)
    Account toDomain(AccountDto dto);
}
