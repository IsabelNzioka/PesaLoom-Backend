package com.pesaloom.pesaloom.loan;

import com.pesaloom.pesaloom.exception.LoanNotFoundException;
import com.pesaloom.pesaloom.loan.entity.Loan;
import com.pesaloom.pesaloom.loan.entity.LoanInstallment;
import com.pesaloom.pesaloom.loan.entity.Repayment;
import com.pesaloom.pesaloom.loan.entity.RepaymentMethod;
import com.pesaloom.pesaloom.loan.repository.LoanInstallmentRepository;
import com.pesaloom.pesaloom.loan.repository.LoanRepository;
import com.pesaloom.pesaloom.loan.repository.RepaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class RepaymentService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final RepaymentRepository repaymentRepository;

    public RepaymentService(LoanRepository loanRepository, LoanInstallmentRepository loanInstallmentRepository,
                             RepaymentRepository repaymentRepository) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.repaymentRepository = repaymentRepository;
    }

    @Transactional
    public Loan record(UUID loanId, BigDecimal amount, RepaymentMethod method, Instant paidAt, String notes, UUID staffUserId) {
        Loan loan = loanRepository.findById(loanId).orElseThrow(LoanNotFoundException::new);

        repaymentRepository.save(new Repayment(loanId, amount, method, paidAt, staffUserId, notes));

        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanIdOrderByInstallmentNumberAsc(loanId);
        BigDecimal remaining = amount;
        for (LoanInstallment installment : installments) {
            if (remaining.signum() <= 0) break;
            BigDecimal applied = installment.applyPayment(remaining);
            remaining = remaining.subtract(applied);
        }
        loanInstallmentRepository.saveAll(installments);

        boolean fullyPaid = installments.stream().allMatch(LoanInstallment::isFullyPaid);
        if (fullyPaid) {
            loan.close();
            loan = loanRepository.save(loan);
        }

        return loan;
    }
}
