package astoppello.wallet.service.impl;

import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.repository.StandingOrderRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
//@Component
@RequiredArgsConstructor
public class StandingOrderNotificationJob {

    private final StandingOrderRepository repository;
    private final JavaMailSender mailSender;

    @Value("${wallet.notification.enabled:false}")
    private boolean notificationEnabled;

    @Value("${wallet.notification.days-ahead:3}")
    private int daysAhead;

    @Value("${wallet.notification.recipient:}")
    private String recipient;


    @Scheduled(cron = "0 0 8 * * *")
    public void checkUpcomingStandingOrders() {
        if (!notificationEnabled || recipient.isBlank()) {
            log.debug("Standing order notifications disabled or no recipient configured");
            return;
        }

        Timestamp from = Timestamp.valueOf(LocalDateTime.now());
        Timestamp to = Timestamp.valueOf(LocalDateTime.now().plusDays(daysAhead));
        List<StandingOrder> upcoming = repository.findByEnabledTrueAndNextOccurrenceBetween(from, to);

        if (upcoming.isEmpty()) {
            log.info("No upcoming standing orders in the next {} days", daysAhead);
            return;
        }

        log.info("Found {} upcoming standing orders, sending notification", upcoming.size());
        sendNotification(upcoming);
    }

    private void sendNotification(List<StandingOrder> orders) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(recipient);
            helper.setSubject("Wallet - Upcoming expenses (" + LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + ")");
            helper.setText(buildHtmlBody(orders), true);

            mailSender.send(message);
            log.info("Standing order notification sent to {}", recipient);
        } catch (MessagingException e) {
            log.error("Failed to send standing order notification", e);
        }
    }

    private String buildHtmlBody(List<StandingOrder> orders) {
        BigDecimal total = orders.stream()
                .map(StandingOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String rows = orders.stream()
                .map(o -> String.format(
                        "<tr><td>%s</td><td>%s</td><td>%s %s</td><td>%s</td></tr>",
                        o.getName(),
                        o.getType(),
                        o.getAmount(),
                        o.getCurrency(),
                        o.getNextOccurrence().toLocalDateTime().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .collect(Collectors.joining("\n"));

        return """
                <html>
                <body>
                <h2>Upcoming Expenses - Next %d days</h2>
                <table border="1" cellpadding="8" cellspacing="0" style="border-collapse: collapse;">
                <tr style="background-color: #f2f2f2;">
                    <th>Name</th><th>Type</th><th>Amount</th><th>Due Date</th>
                </tr>
                %s
                </table>
                <br>
                <p><strong>Total: %s</strong></p>
                </body>
                </html>
                """.formatted(daysAhead, rows, total);
    }
}
