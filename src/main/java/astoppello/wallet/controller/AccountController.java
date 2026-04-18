package astoppello.wallet.controller;

import astoppello.wallet.dto.AccountDto;
import astoppello.wallet.service.AccountService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class AccountController {
    public static final String ACCOUNT_BASE_URL = "/api/v1/accounts";
    public static final String ACCOUNT_ID_PATH = "/api/v1/accounts/{accountId}";
    public static final String INSTITUTION_ACCOUNT_BASE_URL = "/api/v1/institutions/{institutionId}/accounts";

    private final AccountService accountService;

    @GetMapping(ACCOUNT_BASE_URL + "/")
    public ResponseEntity<List<AccountDto>> getAll() {
        return new ResponseEntity<>(accountService.getAll(), HttpStatus.OK);
    }

    @GetMapping(ACCOUNT_ID_PATH)
    public ResponseEntity<AccountDto> getById(@PathVariable("accountId") UUID id) {
        return new ResponseEntity<>(accountService.getByID(id), HttpStatus.OK);
    }

    @GetMapping(ACCOUNT_BASE_URL)
    public ResponseEntity<AccountDto> getByName(@RequestParam("name") String name){
        return new ResponseEntity<>(accountService.getByName(name), HttpStatus.OK);
    }

    @PostMapping(INSTITUTION_ACCOUNT_BASE_URL + "/")
    public ResponseEntity<AccountDto> handlePost(@PathVariable("institutionId") @NotNull UUID institutionId, @RequestBody @Valid AccountDto dto) {
        return new ResponseEntity<>(accountService.save(institutionId, dto), HttpStatus.CREATED);
    }

    @PutMapping(ACCOUNT_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePut(@PathVariable("accountId") UUID id, @RequestBody @Valid AccountDto dto) {
        accountService.update(id, dto);
    }

    @DeleteMapping(ACCOUNT_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("accountId") UUID id) {
        accountService.delete(id);
    }
}
