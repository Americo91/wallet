package astoppello.wallet.controller;

import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.service.TransactionService;
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
public class TransactionController {

    public static final String TRANSACTION_BASE_URL = "/api/v1/transactions";
    public static final String TRANSACTION_ID_PATH = "/api/v1/transactions/{transactionId}";
    public static final String ACCOUNT_TRANSACTION_BASE_URL = "/api/v1/accounts/{accountId}/transactions";

    private final TransactionService transactionService;

    @GetMapping(TRANSACTION_BASE_URL + "/")
    public ResponseEntity<List<TransactionDto>> getAll() {
        return new ResponseEntity<>(transactionService.getAll(), HttpStatus.OK);
    }

    @GetMapping(TRANSACTION_ID_PATH)
    public ResponseEntity<TransactionDto> getById(@PathVariable("transactionId") UUID id) {
        return new ResponseEntity<>(transactionService.getByID(id), HttpStatus.OK);
    }

    @PostMapping(ACCOUNT_TRANSACTION_BASE_URL + "/")
    public ResponseEntity<TransactionDto> handlePost(
            @PathVariable("accountId") @NotNull UUID accountId,
            @RequestBody @Valid TransactionDto dto) {
        return new ResponseEntity<>(transactionService.save(accountId, dto), HttpStatus.CREATED);
    }

    @GetMapping(ACCOUNT_TRANSACTION_BASE_URL)
    public ResponseEntity<List<TransactionDto>> getAllByAccount(@PathVariable("accountId") @NotNull UUID accountId) {
        return new ResponseEntity<>(transactionService.getAllByAccount(accountId), HttpStatus.OK);
    }

    @PutMapping(TRANSACTION_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handlePut(
            @PathVariable("transactionId") UUID id,
            @RequestBody @Valid TransactionDto dto) {
        transactionService.update(id, dto);
    }

    @DeleteMapping(TRANSACTION_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleDelete(@PathVariable("transactionId") UUID id) {
        transactionService.delete(id);
    }
}