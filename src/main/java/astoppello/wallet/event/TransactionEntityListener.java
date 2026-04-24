package astoppello.wallet.event;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.repository.AccountRepository;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * JPA entity listener for Transaction. Uses a static ApplicationContext reference so
 * that callbacks work even when Hibernate instantiates the listener directly (bypassing
 * Spring injection), which happens in sliced test contexts such as @DataJpaTest.
 */
@Slf4j
@Component
public class TransactionEntityListener implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    @Autowired
    public void setApplicationContext(@NonNull ApplicationContext ctx) {
        TransactionEntityListener.applicationContext = ctx;
    }

    private final ThreadLocal<PreUpdateSnapshot> preUpdateState = new ThreadLocal<>();

    @PostPersist
    public void postPersist(Transaction transaction) {
        log.debug("postPersist transaction {}", transaction.getId());
        applyDelta(transaction.getAccount(), computeDelta(transaction.getType(), transaction.getAmount()));
    }

    @PreUpdate
    public void preUpdate(Transaction transaction) {
        // Native SQL via JdbcTemplate bypasses the JPA 1st-level cache,
        // giving us the values currently in the DB (i.e. the old values before the flush).
        Map<String, Object> row = jdbc().queryForMap(
                "SELECT type, amount, account_id FROM transactions WHERE id = ?",
                transaction.getId());
        UUID oldAccountId = UUID.fromString(row.get("account_id").toString());
        TransactionType oldType = TransactionType.valueOf((String) row.get("type"));
        BigDecimal oldAmount = (BigDecimal) row.get("amount");
        preUpdateState.set(new PreUpdateSnapshot(oldAccountId, oldType, oldAmount));
    }

    @PostUpdate
    public void postUpdate(Transaction transaction) {
        PreUpdateSnapshot old = preUpdateState.get();
        preUpdateState.remove();
        if (old == null) return;
        log.debug("postUpdate transaction {}", transaction.getId());
        Account oldAccount = accounts().findById(old.accountId()).orElseThrow();
        applyDelta(oldAccount, computeDelta(old.type(), old.amount()).negate());
        applyDelta(transaction.getAccount(), computeDelta(transaction.getType(), transaction.getAmount()));
    }

    @PostRemove
    public void postRemove(Transaction transaction) {
        log.debug("postRemove transaction {}", transaction.getId());
        applyDelta(transaction.getAccount(), computeDelta(transaction.getType(), transaction.getAmount()).negate());
    }

    private void applyDelta(Account account, BigDecimal delta) {
        account.setBalance(account.getBalance().add(delta));
        accounts().save(account);
    }

    private BigDecimal computeDelta(TransactionType type, BigDecimal amount) {
        return type == TransactionType.INCOME ? amount : amount.negate();
    }

    private AccountRepository accounts() {
        return applicationContext.getBean(AccountRepository.class);
    }

    private JdbcTemplate jdbc() {
        return applicationContext.getBean(JdbcTemplate.class);
    }

    private record PreUpdateSnapshot(UUID accountId, TransactionType type, BigDecimal amount) {}
}