package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminBorrowerDetailDto;
import com.pesaloom.pesaloom.admin.dto.AdminBorrowerSummaryDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanApplicationSummaryDto;
import com.pesaloom.pesaloom.admin.dto.AdminLoanSummaryDto;
import com.pesaloom.pesaloom.exception.LoanApplicationNotFoundException;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import com.pesaloom.pesaloom.loanapplication.repository.LoanApplicationRepository;
import com.pesaloom.pesaloom.user.User;
import com.pesaloom.pesaloom.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AdminBorrowerService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;
    private final AdminLoanService adminLoanService;

    public AdminBorrowerService(LoanApplicationRepository loanApplicationRepository,
                                 UserRepository userRepository, AdminLoanService adminLoanService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
        this.adminLoanService = adminLoanService;
    }

    @Transactional(readOnly = true)
    public Page<AdminBorrowerSummaryDto> list(String search, Pageable pageable) {
        List<LoanApplication> applications = loanApplicationRepository.findAll().stream()
                .filter(a -> a.getStatus() != LoanApplicationStatus.DRAFT)
                .toList();

        Map<UUID, List<LoanApplication>> applicationsByUser = applications.stream()
                .collect(Collectors.groupingBy(LoanApplication::getUserId));

        Map<UUID, User> usersById = userRepository.findAllById(applicationsByUser.keySet()).stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<AdminBorrowerSummaryDto> summaries = applicationsByUser.entrySet().stream()
                .map(entry -> toSummaryDto(entry.getKey(), entry.getValue(), usersById.get(entry.getKey())))
                .filter(dto -> matchesSearch(dto, search))
                .sorted(Comparator.comparing(AdminBorrowerSummaryDto::joinedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        int start = Math.min((int) pageable.getOffset(), summaries.size());
        int end = Math.min(start + pageable.getPageSize(), summaries.size());
        return new PageImpl<>(summaries.subList(start, end), pageable, summaries.size());
    }

    @Transactional(readOnly = true)
    public AdminBorrowerDetailDto get(UUID userId) {
        List<LoanApplication> applications = loanApplicationRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .filter(a -> a.getStatus() != LoanApplicationStatus.DRAFT)
                .toList();
        if (applications.isEmpty()) {
            throw new LoanApplicationNotFoundException();
        }

        User user = userRepository.findById(userId).orElse(null);
        String name = applicantNameOf(applications.get(0));
        Instant joinedAt = earliestCreatedAt(applications);

        return new AdminBorrowerDetailDto(
                userId, name, user != null ? user.getEmail() : null, joinedAt,
                applications.stream().map(AdminLoanApplicationSummaryDto::from).toList(),
                adminLoanService.listForUser(userId)
        );
    }

    private AdminBorrowerSummaryDto toSummaryDto(UUID userId, List<LoanApplication> applications, User user) {
        String name = applicantNameOf(applications.get(0));
        Instant joinedAt = earliestCreatedAt(applications);

        List<AdminLoanSummaryDto> loans = adminLoanService.listForUser(userId);
        long activeLoansCount = loans.stream().filter(l -> "ACTIVE".equals(l.status())).count();
        BigDecimal totalOutstanding = loans.stream()
                .filter(l -> "ACTIVE".equals(l.status()))
                .map(AdminLoanSummaryDto::outstandingBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new AdminBorrowerSummaryDto(
                userId, name, user != null ? user.getEmail() : null,
                applications.size(), activeLoansCount, totalOutstanding, joinedAt
        );
    }

    private static Instant earliestCreatedAt(List<LoanApplication> applications) {
        return applications.stream().map(LoanApplication::getCreatedAt).min(Comparator.naturalOrder()).orElse(null);
    }

    private static String applicantNameOf(LoanApplication a) {
        String personal = a.getPersonalInfo() != null ? a.getPersonalInfo().fullName() : null;
        String business = a.getEmployment() != null ? a.getEmployment().businessName() : null;
        return personal != null ? personal : business;
    }

    private boolean matchesSearch(AdminBorrowerSummaryDto dto, String search) {
        if (search == null || search.isBlank()) return true;
        String needle = search.trim().toLowerCase();
        return (dto.name() != null && dto.name().toLowerCase().contains(needle))
                || (dto.email() != null && dto.email().toLowerCase().contains(needle));
    }
}
