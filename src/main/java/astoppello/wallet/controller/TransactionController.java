package astoppello.wallet.controller;

import astoppello.wallet.api.TransactionsApi;
import astoppello.wallet.dto.TransactionDto;
import astoppello.wallet.dto.TransferDto;
import astoppello.wallet.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class TransactionController implements TransactionsApi {

    private final TransactionService transactionService;

    @Override
    public ResponseEntity<TransactionDto> createTransaction(UUID accountId, TransactionDto transactionDto) {
        TransactionDto save = transactionService.save(accountId, transactionDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("location", String.format("/api/v1/transactions/%s", save.getId()));
        return new ResponseEntity<>(save, httpHeaders, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> deleteTransaction(UUID transactionId) {
        transactionService.delete(transactionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<TransactionDto> getTransactionById(UUID transactionId) {
        return new ResponseEntity<>(transactionService.getByID(transactionId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TransactionDto>> listTransactions() {
        return new ResponseEntity<>(transactionService.getAll(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TransactionDto>> listTransactionsByAccount(UUID accountId) {
        return new ResponseEntity<>(transactionService.getAllByAccount(accountId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<TransactionDto>> transferBetweenAccounts(UUID fromAccountId, UUID toAccountId, TransferDto transferDto) {
        return new ResponseEntity<>(transactionService.transfer(fromAccountId, toAccountId, transferDto), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Void> updateTransaction(UUID transactionId, TransactionDto transactionDto) {
        transactionService.update(transactionId, transactionDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}