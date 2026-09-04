package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminLoanDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanSummaryDto;
import com.pesaloom.pesaloom.admin.dto.CollectionSheetRowDto;
import com.pesaloom.pesaloom.admin.dto.LoanCalculatorResponseDto;
import com.pesaloom.pesaloom.admin.dto.LoanInstallmentDto;
import com.pesaloom.pesaloom.admin.dto.RecordRepaymentRequest;
import com.pesaloom.pesaloom.admin.dto.RepaymentDto;
import com.pesaloom.pesaloom.exception.LoanNotFoundException;
import com.pesaloom.pesaloom.loan.LoanAmortization;
import com.pesaloom.pesaloom.loan.RepaymentService;
import com.pesaloom.pesaloom.loan.entity.Loan;
import com.pesaloom.pesaloom.loan.entity.LoanInstallment;
import com.pesaloom.pesaloom.loan.entity.LoanStatus;
import com.pesaloom.pesaloom.loan.repository.LoanInstallmentRepository;
import com.pesaloom.pesaloom.loan.repository.LoanRepository;
import com.pesaloom.pesaloom.loan.repository.RepaymentRepository;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.repository.LoanApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminLoanService {

    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository loanInstallmentRepository;
    private final RepaymentRepository repaymentRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final RepaymentService repaymentService;

    public AdminLoanService(LoanRepository loanRepository, LoanInstallmentRepository loanInstallmentRepository,
                             RepaymentRepository repaymentRepository, LoanApplicationRepository loanApplicationRepository,
                             RepaymentService repaymentService) {
        this.loanRepository = loanRepository;
        this.loanInstallmentRepository = loanInstallmentRepository;
        this.repaymentRepository = repaymentRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.repaymentService = repaymentService;
    }

    @Transactional(readOnly = true)
    public Page<AdminLoanSummaryDto> list(String view, String search, UUID branchId, Pageable pageable) {
        List<Loan> candidates = switch (view == null ? "all" : view) {
            case "closed" -> loanRepository.findByStatus(LoanStatus.CLOSED);
            case "active", "due_today", "arrears" -> loanRepository.findByStatus(LoanStatus.ACTIVE);
            default -> loanRepository.findAll();
        };
        if (branchId != null) {
            candidates = candidates.stream().filter(l -> branchId.equals(l.getBranchId())).toList();
        }

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Map<UUID, List<LoanInstallment>> installmentsByLoan = installmentsByLoan(candidates);
        Map<UUID, String> applicantNames = applicantNamesFor(candidates);

        List<AdminLoanSummaryDto> summaries = candidates.stream()
                .map(loan -> toSummaryDto(loan, installmentsByLoan.getOrDefault(loan.getId(), List.of()),
                        applicantNames.get(loan.getLoanApplicationId()), today))
                .filter(dto -> matchesView(dto, view, today))
                .filter(dto -> matchesSearch(dto, search))
                .sorted(Comparator.comparing(AdminLoanSummaryDto::nextDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        int start = Math.min((int) pageable.getOffset(), summaries.size());
        int end = Math.min(start + pageable.getPageSize(), summaries.size());
        return new PageImpl<>(summaries.subList(start, end), pageable, summaries.size());
    }

    @Transactional(readOnly = true)
    public AdminLoanDto get(UUID id) {
        Loan loan = requireById(id);
        return toDto(loan);
    }


    @Transactional(readOnly = true)
    public List<AdminLoanSummaryDto> listForUser(UUID userId) {
        List<Loan> loans = loanRepository.findByUserId(userId);
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        Map<UUID, List<LoanInstallment>> installmentsByLoan = installmentsByLoan(loans);
        Map<UUID, String> applicantNames = applicantNamesFor(loans);

        return loans.stream()
                .map(loan -> toSummaryDto(loan, installmentsByLoan.getOrDefault(loan.getId(), List.of()),
                        applicantNames.get(loan.getLoanApplicationId()), today))
                .sorted(Comparator.comparing(AdminLoanSummaryDto::nextDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }


    @Transactional(readOnly = true)
    public List<CollectionSheetRowDto> collectionSheet(LocalDate from, LocalDate to) {
        List<Loan> activeLoans = loanRepository.findByStatus(LoanStatus.ACTIVE);
        Map<UUID, List<LoanInstallment>> installmentsByLoan = installmentsByLoan(activeLoans);
        Map<UUID, String> applicantNames = applicantNamesFor(activeLoans);

        List<CollectionSheetRowDto> rows = new ArrayList<>();
        for (Loan loan : activeLoans) {
            String applicantName = applicantNames.get(loan.getLoanApplicationId());
            for (LoanInstallment installment : installmentsByLoan.getOrDefault(loan.getId(), List.of())) {
                if (installment.isFullyPaid()) continue;
                LocalDate due = installment.getDueDate();
                if (due.isBefore(from) || due.isAfter(to)) continue;
                rows.add(new CollectionSheetRowDto(
                        loan.getId(), loan.getReferenceNumber(), applicantName,
                        installment.getInstallmentNumber(), due, installment.getTotalDue(), installment.balance()
                ));
            }
        }
        rows.sort(Comparator.comparing(CollectionSheetRowDto::dueDate));
        return rows;
    }

    @Transactional
    public AdminLoanDto recordRepayment(UUID loanId, RecordRepaymentRequest request, UUID staffUserId) {
        requireById(loanId);
        repaymentService.record(loanId, request.amount(), request.method(), request.paidAt(), request.notes(), staffUserId);
        return toDto(requireById(loanId));
    }

    public LoanCalculatorResponseDto calculate(BigDecimal principal, BigDecimal interestRatePercent, int tenureMonths) {
        LoanAmortization.Schedule schedule = LoanAmortization.schedule(principal, interestRatePercent, tenureMonths, Instant.now());
        return LoanCalculatorResponseDto.from(schedule);
    }

    private Loan requireById(UUID id) {
        return loanRepository.findById(id).orElseThrow(LoanNotFoundException::new);
    }

    private Map<UUID, List<LoanInstallment>> installmentsByLoan(List<Loan> loans) {
        if (loans.isEmpty()) return Map.of();
        List<UUID> loanIds = loans.stream().map(Loan::getId).toList();
        return loanInstallmentRepository.findByLoanIdInOrderByDueDateAsc(loanIds).stream()
                .collect(Collectors.groupingBy(LoanInstallment::getLoanId));
    }

    private Map<UUID, String> applicantNamesFor(List<Loan> loans) {
        if (loans.isEmpty()) return Map.of();
        List<UUID> applicationIds = loans.stream().map(Loan::getLoanApplicationId).toList();
        return loanApplicationRepository.findAllById(applicationIds).stream()
                .collect(Collectors.toMap(LoanApplication::getId, AdminLoanService::applicantNameOf));
    }

    private static String applicantNameOf(LoanApplication a) {
        String personal = a.getPersonalInfo() != null ? a.getPersonalInfo().fullName() : null;
        String business = a.getEmployment() != null ? a.getEmployment().businessName() : null;
        return personal != null ? personal : business;
    }

    private AdminLoanSummaryDto toSummaryDto(Loan loan, List<LoanInstallment> installments, String applicantName, LocalDate today) {
        BigDecimal totalPaid = installments.stream().map(LoanInstallment::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = loan.getTotalPayable().subtract(totalPaid);

        LoanInstallment nextUnpaid = installments.stream().filter(i -> !i.isFullyPaid()).findFirst().orElse(null);
        LocalDate nextDueDate = nextUnpaid != null ? nextUnpaid.getDueDate() : null;
        boolean overdue = nextUnpaid != null && nextUnpaid.getDueDate().isBefore(today);
        long daysOverdue = overdue ? ChronoUnit.DAYS.between(nextUnpaid.getDueDate(), today) : 0;

        return new AdminLoanSummaryDto(
                loan.getId(), loan.getReferenceNumber(), applicantName, loan.getLoanType().name(),
                loan.getPrincipal(), loan.getEmi(), outstanding, nextDueDate, loan.getStatus().name(),
                overdue, daysOverdue
        );
    }

    private boolean matchesView(AdminLoanSummaryDto dto, String view, LocalDate today) {
        if (view == null) return true;
        return switch (view) {
            case "due_today" -> today.equals(dto.nextDueDate());
            case "arrears" -> dto.overdue();
            default -> true;
        };
    }

    private boolean matchesSearch(AdminLoanSummaryDto dto, String search) {
        if (search == null || search.isBlank()) return true;
        String needle = search.trim().toLowerCase();
        return (dto.referenceNumber() != null && dto.referenceNumber().toLowerCase().contains(needle))
                || (dto.applicantName() != null && dto.applicantName().toLowerCase().contains(needle));
    }

    private AdminLoanDto toDto(Loan loan) {
        List<LoanInstallment> installments = loanInstallmentRepository.findByLoanIdOrderByInstallmentNumberAsc(loan.getId());
        List<RepaymentDto> repayments = repaymentRepository.findByLoanIdOrderByPaidAtDesc(loan.getId()).stream()
                .map(RepaymentDto::from)
                .toList();
        String applicantName = loanApplicationRepository.findById(loan.getLoanApplicationId())
                .map(AdminLoanService::applicantNameOf)
                .orElse(null);

        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<LoanInstallmentDto> schedule = installments.stream()
                .map(i -> LoanInstallmentDto.from(i, today))
                .toList();

        BigDecimal totalPaid = installments.stream().map(LoanInstallment::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal outstanding = loan.getTotalPayable().subtract(totalPaid);

        return new AdminLoanDto(
                loan.getId(), loan.getLoanApplicationId(), loan.getReferenceNumber(), applicantName,
                loan.getLoanType().name(), loan.getPrincipal(), loan.getInterestRate(), loan.getTenureMonths(),
                loan.getEmi(), loan.getTotalPayable(), totalPaid, outstanding, loan.getStatus().name(),
                loan.getDisbursedAt(), loan.getClosedAt(), schedule, repayments
        );
    }
}
