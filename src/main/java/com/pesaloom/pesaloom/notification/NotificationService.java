package com.pesaloom.pesaloom.notification;

import com.pesaloom.pesaloom.notification.dto.LoanSubmissionEmailData;

public interface NotificationService {

    void sendPasswordResetLink(String toEmail, String resetLink);

    void sendWelcomeEmail(String toEmail, String firstName);

    void sendLoanSubmissionConfirmation(LoanSubmissionEmailData data, byte[] pdfAttachment);
}
