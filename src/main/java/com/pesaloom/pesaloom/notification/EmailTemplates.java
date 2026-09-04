package com.pesaloom.pesaloom.notification;

import com.pesaloom.pesaloom.notification.dto.LoanSubmissionEmailData;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;


final class EmailTemplates {

    private static final String BRAND_COLOR = "#1F4E79";
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy").withZone(ZoneOffset.UTC);

    static String welcomeEmail(String firstName) {
        return wrap("Welcome to PesaLoom", """
                <p>Hi %s,</p>
                <p>Your PesaLoom account is ready. You can now start a loan application whenever you're ready —
                your progress is saved automatically as you go.</p>
                """.formatted(escape(firstName)));
    }

    static String passwordReset(String resetLink) {
        return wrap("Reset your password", """
                <p>We received a request to reset your PesaLoom password.</p>
                <p><a href="%s" style="display:inline-block;background:%s;color:#ffffff;text-decoration:none;
                    padding:10px 20px;border-radius:6px;font-weight:600;">Reset password</a></p>
                <p>If you didn't request this, you can safely ignore this email.</p>
                """.formatted(resetLink, BRAND_COLOR));
    }

    static String loanSubmissionConfirmation(LoanSubmissionEmailData data) {
        String submitted = data.submittedAt() != null ? DATE_FORMAT.format(data.submittedAt()) : "";
        return wrap("We've received your application", """
                <p>Hi %s,</p>
                <p>Thanks for applying with PesaLoom. We've received your <strong>%s loan</strong> application
                and our team is reviewing it now.</p>
                <table style="width:100%%;border-collapse:collapse;margin:16px 0;">
                    %s
                    %s
                    %s
                </table>
                <p>Your full application summary is attached as a PDF for your records. We'll be in touch with
                a decision soon.</p>
                """.formatted(
                escape(data.recipientFirstName()),
                escape(formatLoanType(data.loanType())),
                row("Reference number", data.referenceNumber()),
                row("Amount requested", currency(data.loanAmount())),
                row("Submitted on", submitted)
        ));
    }

    private static String row(String label, String value) {
        return """
                <tr>
                    <td style="padding:4px 12px 4px 0;color:#64748B;font-size:13px;">%s</td>
                    <td style="padding:4px 0;font-weight:600;font-size:13px;">%s</td>
                </tr>
                """.formatted(escape(label), escape(value));
    }

    private static String wrap(String heading, String bodyHtml) {
        return """
                <div style="font-family:Arial,Helvetica,sans-serif;max-width:480px;margin:0 auto;color:#1E293B;">
                    <h2 style="color:%s;margin-bottom:8px;">PesaLoom</h2>
                    <h3 style="margin-top:0;">%s</h3>
                    %s
                    <p style="color:#94A3B8;font-size:12px;margin-top:24px;">This is an automated message from PesaLoom.</p>
                </div>
                """.formatted(BRAND_COLOR, escape(heading), bodyHtml);
    }

    private static String currency(BigDecimal amount) {
        return amount == null ? "—" : "KES " + amount.toPlainString();
    }

    private static String formatLoanType(String loanType) {
        if (loanType == null || loanType.isBlank()) {
            return "";
        }
        String lower = loanType.toLowerCase().replace('_', ' ');
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private EmailTemplates() {
    }
}
