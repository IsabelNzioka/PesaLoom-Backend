package com.pesaloom.pesaloom.loan;

import com.pesaloom.pesaloom.loan.entity.Loan;
import com.pesaloom.pesaloom.loan.entity.LoanInstallment;
import com.pesaloom.pesaloom.loan.repository.LoanInstallmentRepository;
import com.pesaloom.pesaloom.loan.repository.LoanRepository;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class LoanOriginationService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;

    public LoanOriginationService(LoanRepository loanRepository, LoanInstallmentRepository loanInstallmentRepository) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
    }

    @Transactional
    public Loan originate(LoanApplication application) {
        LoanAmortization.Schedule schedule = LoanAmortization.schedule(
                application.getLoanAmount(), application.getInterestRate(),
                application.getLoanTenureMonths(), application.getDisbursedAt());

        Loan loan = new Loan(
                application.getId(), application.getUserId(), application.getReferenceNumber(),
                application.getLoanType(), application.getLoanAmount(), application.getInterestRate(),
                application.getLoanTenureMonths(), schedule.emi(), schedule.totalPayable(),
                application.getDisbursedAt(), application.getBranchId());
        Loan savedLoan = loanRepository.save(loan);

        List<LoanInstallment> installments = schedule.installments().stream()
                .map(i -> new LoanInstallment(savedLoan.getId(), i.number(), i.dueDate(), i.principalDue(), i.interestDue(), i.totalDue()))
                .toList();
        loanInstallmentRepository.saveAll(installments);

        return savedLoan;
    }
}
