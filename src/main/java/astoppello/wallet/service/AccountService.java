package astoppello.wallet.service;

import astoppello.wallet.dto.AccountDto;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountDto save(UUID institutionId, AccountDto dto);

    AccountDto update(UUID id, AccountDto dto);

    void delete(UUID id);

    List<AccountDto> getAll();

    AccountDto getByID(UUID id);

    AccountDto getByName(String name);
}
