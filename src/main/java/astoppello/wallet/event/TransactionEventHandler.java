package astoppello.wallet.event;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.domain.TransactionType;
import astoppello.wallet.repository.AccountRepository;
import astoppello.wallet.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeDelete;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@RepositoryEventHandler
@RequiredArgsConstructor
public class TransactionEventHandler {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @HandleBeforeCreate
    @Transactional
    public void handleCreate(Transaction transaction) {
        applyDelta(transaction.getAccount(), transaction.getType(), transaction.getAmount());
    }

    @HandleBeforeDelete
    @Transactional
    public void handleDelete(Transaction transaction) {
        applyDelta(transaction.getAccount(), opposite(transaction.getType()), transaction.getAmount());
    }

    @HandleBeforeSave
    @Transactional
    public void handleSave(Transaction transaction) {
        transactionRepository.findById(transaction.getId()).ifPresent(old -> {
            applyDelta(old.getAccount(), opposite(old.getType()), old.getAmount());
        });
        applyDelta(transaction.getAccount(), transaction.getType(), transaction.getAmount());
    }

    private void applyDelta(Account account, TransactionType type, BigDecimal amount) {
        BigDecimal delta = type == TransactionType.INCOME ? amount : amount.negate();
        account.setBalance(account.getBalance().add(delta));
        accountRepository.save(account);
    }

    private TransactionType opposite(TransactionType type) {
        return type == TransactionType.INCOME ? TransactionType.EXPENSE : TransactionType.INCOME;
    }
}
