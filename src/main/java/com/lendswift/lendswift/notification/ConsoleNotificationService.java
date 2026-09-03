package com.lendswift.lendswift.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Stands in for real email delivery.
 */
@Service
@Primary
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
}
