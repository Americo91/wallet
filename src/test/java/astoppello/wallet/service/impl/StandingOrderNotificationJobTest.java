package astoppello.wallet.service.impl;

import astoppello.wallet.domain.Account;
import astoppello.wallet.domain.Category;
import astoppello.wallet.domain.StandingOrder;
import astoppello.wallet.model.Currency;
import astoppello.wallet.model.Frequency;
import astoppello.wallet.model.TransactionType;
import astoppello.wallet.repository.StandingOrderRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StandingOrderNotificationJobTest {

    @Mock
    private StandingOrderRepository repository;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private StandingOrderNotificationJob job;

    @Test
    void checkUpcomingStandingOrders_disabled() {
        ReflectionTestUtils.setField(job, "notificationEnabled", false);
        ReflectionTestUtils.setField(job, "recipient", "");

        job.checkUpcomingStandingOrders();

        verifyNoInteractions(repository, mailSender);
    }

    @Test
    void checkUpcomingStandingOrders_noUpcoming() {
        ReflectionTestUtils.setField(job, "notificationEnabled", true);
        ReflectionTestUtils.setField(job, "daysAhead", 3);
        ReflectionTestUtils.setField(job, "recipient", "test@example.com");

        when(repository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of());

        job.checkUpcomingStandingOrders();

        verify(repository).findByEnabledTrueAndNextOccurrenceBetween(any(), any());
        verifyNoInteractions(mailSender);
    }

    @Test
    void checkUpcomingStandingOrders_sendsEmail() {
        ReflectionTestUtils.setField(job, "notificationEnabled", true);
        ReflectionTestUtils.setField(job, "daysAhead", 3);
        ReflectionTestUtils.setField(job, "recipient", "test@example.com");

        Account account = Account.builder().id(UUID.randomUUID()).name("Revolut").build();
        Category category = Category.builder().id(UUID.randomUUID()).name("Subscriptions").build();
        StandingOrder order = StandingOrder.builder()
                .name("Spotify")
                .amount(BigDecimal.valueOf(3))
                .currency(Currency.EUR)
                .frequency(Frequency.MONTHLY)
                .type(TransactionType.EXPENSE)
                .nextOccurrence(Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
                .account(account)
                .category(category)
                .enabled(true)
                .build();

        when(repository.findByEnabledTrueAndNextOccurrenceBetween(any(), any()))
                .thenReturn(List.of(order));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        job.checkUpcomingStandingOrders();

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}
