package com.lendswift.lendswift.notification;

public interface NotificationService {

    void sendPasswordResetLink(String toEmail, String resetLink);

    void sendWelcomeEmail(String toEmail, String firstName);
}
