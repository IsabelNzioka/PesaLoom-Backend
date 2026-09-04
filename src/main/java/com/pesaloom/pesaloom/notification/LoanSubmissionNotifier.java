package com.pesaloom.pesaloom.notification;

import com.pesaloom.pesaloom.notification.dto.LoanSubmissionEmailData;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class LoanSubmissionNotifier {

    private final NotificationService notificationService;

    public LoanSubmissionNotifier(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Async("mailTaskExecutor")
    public void notifyAsync(LoanSubmissionEmailData data, byte[] pdfAttachment) {
        notificationService.sendLoanSubmissionConfirmation(data, pdfAttachment);
    }
}
