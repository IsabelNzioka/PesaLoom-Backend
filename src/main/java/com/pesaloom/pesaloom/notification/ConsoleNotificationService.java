package com.pesaloom.pesaloom.notification;

import com.pesaloom.pesaloom.notification.dto.LoanSubmissionEmailData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;


@Service
@Profile("no-mail")
public class ConsoleNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleNotificationService.class);

    @Override
    public void sendPasswordResetLink(String toEmail, String resetLink) {
        log.info("[DEV EMAIL] Password reset link for {}: {}", toEmail, resetLink);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        log.info("[DEV EMAIL] Welcome email for {} ({})", toEmail, firstName);
    }

    @Override
    public void sendLoanSubmissionConfirmation(LoanSubmissionEmailData data, byte[] pdfAttachment) {
        log.info("[DEV EMAIL] Loan submission confirmation for {} (ref {})", data.recipientEmail(), data.referenceNumber());
    }
}
