package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminDashboardSummaryDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanApplicationDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanApplicationSummaryDto;
import com.pesaloom.pesaloom.admin.dto.StaffSummaryDto;
import com.pesaloom.pesaloom.exception.InvalidStatusTransitionException;
import com.pesaloom.pesaloom.exception.LoanApplicationNotFoundException;
import com.pesaloom.pesaloom.loan.LoanOriginationService;
import com.pesaloom.pesaloom.loan.repository.LoanRepository;
import com.pesaloom.pesaloom.loanapplication.DocumentService;
import com.pesaloom.pesaloom.loanapplication.EmiCalculator;
import com.pesaloom.pesaloom.loanapplication.LoanApplicationService;
import com.pesaloom.pesaloom.loanapplication.LoanConfig;
import com.pesaloom.pesaloom.loanapplication.ReferenceNumberGenerator;
import com.pesaloom.pesaloom.loanapplication.entity.EmploymentInfo;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import com.pesaloom.pesaloom.loanapplication.entity.LoanType;
import com.pesaloom.pesaloom.loanapplication.repository.LoanApplicationRepository;
import com.pesaloom.pesaloom.user.Role;
import com.pesaloom.pesaloom.user.User;
import com.pesaloom.pesaloom.user.UserRepository;
import com.pesaloom.pesaloom.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminApplicationService {

    private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM").withZone(ZoneOffset.UTC);

    private final LoanApplicationRepository loanApplicationRepository;
    private final AdminApplicationSearchRepository adminApplicationSearchRepository;
    private final LoanApplicationService loanApplicationService;
    private final DocumentService documentService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final LoanOriginationService loanOriginationService;
    private final LoanRepository loanRepository;

    public AdminApplicationService(LoanApplicationRepository loanApplicationRepository,
                                    AdminApplicationSearchRepository adminApplicationSearchRepository,
                                    LoanApplicationService loanApplicationService,
                                    DocumentService documentService,
                                    UserService userService,
                                    UserRepository userRepository,
                                    LoanOriginationService loanOriginationService,
                                    LoanRepository loanRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.adminApplicationSearchRepository = adminApplicationSearchRepository;
        this.loanApplicationService = loanApplicationService;
        this.documentService = documentService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.loanOriginationService = loanOriginationService;
        this.loanRepository = loanRepository;
    }

    public Page<AdminLoanApplicationSummaryDto> list(List<LoanApplicationStatus> status, LoanType loanType,
                                                       Instant from, Instant to, String search, UUID branchId, Pageable pageable) {
        return adminApplicationSearchRepository.search(status, loanType, from, to, search, branchId, pageable)
                .map(AdminLoanApplicationSummaryDto::from);
    }

    public AdminLoanApplicationDto get(UUID id) {
        return toAdminDto(requireById(id));
    }

    @Transactional
    public AdminLoanApplicationDto transition(UUID adminUserId, UUID id, LoanApplicationStatus targetStatus, String reviewNotes) {
        LoanApplication application = requireById(id);
        AdminStatusTransitions.requireLegal(application.getStatus(), targetStatus);

        if (targetStatus == LoanApplicationStatus.REJECTED && (reviewNotes == null || reviewNotes.isBlank())) {
            throw new InvalidStatusTransitionException("Review notes are required when rejecting an application");
        }

        if (targetStatus == LoanApplicationStatus.DISBURSED
                && (application.getLoanAmount() == null || application.getEmi() == null
                    || application.getLoanTenureMonths() == null || application.getInterestRate() == null)) {
            throw new InvalidStatusTransitionException("This application has no loan terms attached yet");
        }

        application.transitionTo(targetStatus, adminUserId, reviewNotes);
        application = loanApplicationRepository.save(application);

        if (targetStatus == LoanApplicationStatus.DISBURSED) {
            loanOriginationService.originate(application);
        }

        return toAdminDto(application);
    }

    @Transactional
    public AdminLoanApplicationDto createBorrower(String businessName, String workingStatus, String description,
                                                   LoanType loanType, BigDecimal loanAmount, Integer loanTenureMonths,
                                                   UUID branchId, List<UUID> staffAccessUserIds,
                                                   MultipartFile photo, List<MultipartFile> files) {
        User shellUser = userService.createShellUser(businessName, "Borrower");

        LoanApplication application = LoanApplication.createdByStaff(shellUser.getId(), ReferenceNumberGenerator.generate());
        application.setEmployment(new EmploymentInfo(workingStatus, null, null, null, null,
                businessName, null, null, null, null, null, null));
        application.setDescription(description);
        application.setStaffAccess(staffAccessUserIds);
        application.setBranchId(branchId);

        LoanConfig.Config config = LoanConfig.of(loanType);
        EmiCalculator.Breakdown breakdown = EmiCalculator.calculate(loanAmount, config.interestRate(), loanTenureMonths);
        application.attachLoanTerms(loanType, loanAmount, loanTenureMonths, breakdown.emi(), breakdown.interestRate(),
                breakdown.processingFee(), breakdown.totalInterest(), breakdown.totalPayable());

        application = loanApplicationRepository.save(application);

        if (photo != null && !photo.isEmpty()) {
            documentService.uploadForAdmin(shellUser.getId(), application.getId(), "borrowerPhoto", photo);
        }
        if (files != null) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    documentService.uploadForAdmin(shellUser.getId(), application.getId(), "borrowerFile", file);
                }
            }
        }

        return toAdminDto(requireById(application.getId()));
    }


    public AdminDashboardSummaryDto dashboardSummary(LoanType loanType, Instant from, Instant to, UUID branchId) {
        List<LoanApplication> apps = adminApplicationSearchRepository.searchAllForSummary(loanType, from, to, branchId);

        Map<String, Long> countsByStatus = apps.stream()
                .collect(Collectors.groupingBy(a -> a.getStatus().name(), Collectors.counting()));

        Map<String, Long> loanTypeBreakdown = apps.stream()
                .filter(a -> a.getLoanType() != null)
                .collect(Collectors.groupingBy(a -> a.getLoanType().name(), Collectors.counting()));

        Map<String, Long> genderBreakdown = apps.stream()
                .map(a -> a.getPersonalInfo() != null ? a.getPersonalInfo().gender() : null)
                .filter(g -> g != null && !g.isBlank())
                .collect(Collectors.groupingBy(g -> g, Collectors.counting()));

        Map<String, Long> monthlyCounts = apps.stream()
                .filter(a -> a.getSubmittedAt() != null)
                .collect(Collectors.groupingBy(a -> MONTH_FORMAT.format(a.getSubmittedAt()), Collectors.counting()));
        List<AdminDashboardSummaryDto.MonthlyPoint> monthlyTrend = monthlyCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new AdminDashboardSummaryDto.MonthlyPoint(e.getKey(), e.getValue()))
                .toList();

        BigDecimal totalRequested = apps.stream()
                .map(LoanApplication::getLoanAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDisbursed = apps.stream()
                .filter(a -> a.getStatus() == LoanApplicationStatus.DISBURSED)
                .map(LoanApplication::getLoanAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BigDecimal> loanAmounts = apps.stream().map(LoanApplication::getLoanAmount).filter(java.util.Objects::nonNull).toList();
        BigDecimal averageLoanAmount = loanAmounts.isEmpty() ? null
                : totalRequested.divide(BigDecimal.valueOf(loanAmounts.size()), 0, RoundingMode.HALF_UP);

        long registeredBorrowers = apps.stream().map(LoanApplication::getUserId).distinct().count();

        long awaitingReviewCount = apps.stream()
                .filter(a -> a.getStatus() == LoanApplicationStatus.SUBMITTED || a.getStatus() == LoanApplicationStatus.UNDER_REVIEW)
                .count();

        long approvedOrDisbursed = apps.stream()
                .filter(a -> a.getStatus() == LoanApplicationStatus.APPROVED || a.getStatus() == LoanApplicationStatus.DISBURSED)
                .count();
        long rejected = apps.stream().filter(a -> a.getStatus() == LoanApplicationStatus.REJECTED).count();
        long decided = approvedOrDisbursed + rejected;
        Double approvalRatePercent = decided == 0 ? null : (approvedOrDisbursed * 100.0) / decided;

        List<Double> decisionDays = apps.stream()
                .filter(a -> a.getSubmittedAt() != null && a.getReviewedAt() != null)
                .map(a -> (a.getReviewedAt().getEpochSecond() - a.getSubmittedAt().getEpochSecond()) / 86400.0)
                .toList();
        Double averageDecisionDays = decisionDays.isEmpty() ? null
                : decisionDays.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        return new AdminDashboardSummaryDto(
                countsByStatus, totalRequested, totalDisbursed, averageLoanAmount, monthlyTrend,
                loanTypeBreakdown, genderBreakdown, registeredBorrowers, awaitingReviewCount,
                approvalRatePercent, averageDecisionDays
        );
    }

    public List<StaffSummaryDto> listStaff() {
        return userService.findAllByRole(Role.ADMIN).stream()
                .map(StaffSummaryDto::from)
                .toList();
    }

    private LoanApplication requireById(UUID id) {
        return loanApplicationRepository.findById(id).orElseThrow(LoanApplicationNotFoundException::new);
    }

    private AdminLoanApplicationDto toAdminDto(LoanApplication a) {
        String userEmail = userRepository.findById(a.getUserId()).map(User::getEmail).orElse(null);
        UUID loanId = loanRepository.findByLoanApplicationId(a.getId()).map(loan -> loan.getId()).orElse(null);
        return new AdminLoanApplicationDto(
                loanApplicationService.toDto(a),
                a.getUserId(),
                userEmail,
                a.isCreatedByStaff(),
                a.getReviewedBy(),
                a.getReviewedAt(),
                a.getReviewNotes(),
                a.getDisbursedAt(),
                a.getAssignedTo(),
                a.getDescription(),
                a.getStaffAccess(),
                loanId
        );
    }
}
