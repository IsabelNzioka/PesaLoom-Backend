package com.pesaloom.pesaloom.notification;

import com.pesaloom.pesaloom.notification.dto.LoanSubmissionEmailData;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@Profile("!no-mail")
public class SmtpNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmtpNotificationService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public SmtpNotificationService(JavaMailSender mailSender, @Value("${app.mail.from}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendPasswordResetLink(String toEmail, String resetLink) {
        send(toEmail, "Reset your PesaLoom password", EmailTemplates.passwordReset(resetLink), null, null, null);
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        send(toEmail, "Welcome to PesaLoom", EmailTemplates.welcomeEmail(firstName), null, null, null);
    }

    @Override
    public void sendLoanSubmissionConfirmation(LoanSubmissionEmailData data, byte[] pdfAttachment) {
        String filename = "PesaLoom-Application-" + data.referenceNumber() + ".pdf";
        send(data.recipientEmail(), "We've received your PesaLoom application (" + data.referenceNumber() + ")",
                EmailTemplates.loanSubmissionConfirmation(data), pdfAttachment, filename, "application/pdf");
    }

    private void send(String toEmail, String subject, String htmlBody, byte[] attachment, String attachmentName, String attachmentType) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, attachment != null);
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            if (attachment != null) {
                helper.addAttachment(attachmentName, new org.springframework.core.io.ByteArrayResource(attachment), attachmentType);
            }
            mailSender.send(message);
        } catch (MessagingException | MailException e) {

            log.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
