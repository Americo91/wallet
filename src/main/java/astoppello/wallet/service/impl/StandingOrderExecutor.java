package astoppello.wallet.service.impl;

import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.domain.Transaction;
import astoppello.wallet.domain.TrackingDate;
import astoppello.wallet.mapper.StandingOrderMapper;
import astoppello.wallet.mapper.TransactionMapper;
import astoppello.wallet.repository.StandingOrderRepository;
import astoppello.wallet.repository.TransactionRepository;
import astoppello.wallet.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StandingOrderExecutor {

    private final StandingOrderRepository standingOrderRepository;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final StandingOrderMapper standingOrderMapper;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void executeStandingOrders() {
        LocalDate today = LocalDate.now();
        Timestamp startOfDay = Timestamp.valueOf(today.atStartOfDay());
        Timestamp endOfDay = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        List<StandingOrder> dueOrders = standingOrderRepository
                .findByEnabledTrueAndNextOccurrenceBetween(startOfDay, endOfDay);

        if (dueOrders.isEmpty()) {
            log.info("No standing orders due for {}", today);
            return;
        }

        log.info("Executing {} standing orders for {}", dueOrders.size(), today);

        for (StandingOrder order : dueOrders) {
            try {
                createTransaction(order);
                updateNextOccurrence(order);
                log.info("Executed standing order '{}' for {} {}", order.getName(), order.getAmount(), order.getCurrency());
            } catch (Exception e) {
                log.error("Failed to execute standing order '{}'", order.getName(), e);
            }
        }
    }

    private void createTransaction(StandingOrder order) {
        Transaction transaction = standingOrderMapper.toTransaction(order);
        transaction.setTrackingDate(TrackingDate.now());

        transactionRepository.save(transaction);

        BigDecimal delta = switch (order.getType()) {
            case INCOME -> order.getAmount();
            case EXPENSE -> order.getAmount().negate();
        };
        order.getAccount().setBalance(order.getAccount().getBalance().add(delta));
        accountRepository.save(order.getAccount());
    }

    private void updateNextOccurrence(StandingOrder order) {
        LocalDateTime current = order.getNextOccurrence().toLocalDateTime();

        LocalDateTime next = switch (order.getFrequency()) {
            case DAILY -> current.plusDays(1);
            case WEEKLY -> current.plusWeeks(1);
            case MONTHLY -> current.plusMonths(1);
            case YEARLY -> current.plusYears(1);
            case UNA_TANTUM -> {
                order.setEnabled(false);
                yield current;
            }
        };

        order.setNextOccurrence(Timestamp.valueOf(next));
        standingOrderRepository.save(order);
    }
}
