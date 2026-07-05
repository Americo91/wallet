package astoppello.wallet.controller;

import astoppello.wallet.api.AccountsApi;
import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class AccountController implements AccountsApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<AccountDto> createAccount(UUID institutionId, AccountDto accountDto) {
        AccountDto save = accountService.save(institutionId, accountDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", String.format("/api/v1/accounts/%s", save.getId()));
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteAccount(UUID accountId) {
        accountService.delete(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<AccountDto> getAccountById(UUID accountId) {
        return new ResponseEntity<>(accountService.getByID(accountId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<AccountDto>> listAccounts(String name) {
        List<AccountDto> accountDtos = StringUtils.isBlank(name) ? accountService.getAll() : List.of(accountService.getByName(name));
        return new ResponseEntity<>(accountDtos, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateAccount(UUID accountId, AccountDto accountDto) {
        accountService.update(accountId, accountDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
