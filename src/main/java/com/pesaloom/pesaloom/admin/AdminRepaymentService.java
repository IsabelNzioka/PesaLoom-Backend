package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminRepaymentDto;
import com.pesaloom.pesaloom.loan.entity.Loan;
import com.pesaloom.pesaloom.loan.entity.Repayment;
import com.pesaloom.pesaloom.loan.entity.RepaymentMethod;
import com.pesaloom.pesaloom.loan.repository.LoanRepository;
import com.pesaloom.pesaloom.loan.repository.RepaymentRepository;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.repository.LoanApplicationRepository;
import com.pesaloom.pesaloom.user.User;
import com.pesaloom.pesaloom.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AdminRepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final UserRepository userRepository;

    public AdminRepaymentService(RepaymentRepository repaymentRepository, LoanRepository loanRepository,
                                  LoanApplicationRepository loanApplicationRepository, UserRepository userRepository) {
        this.repaymentRepository = repaymentRepository;
        this.loanRepository = loanRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminRepaymentDto> list(Instant from, Instant to, RepaymentMethod method, String search, Pageable pageable) {
        List<Repayment> repayments = repaymentRepository.findAll();

        Map<UUID, Loan> loansById = loanRepository.findAllById(
                repayments.stream().map(Repayment::getLoanId).distinct().toList()
        ).stream().collect(Collectors.toMap(Loan::getId, l -> l));

        Map<UUID, LoanApplication> applicationsById = loanApplicationRepository.findAllById(
                loansById.values().stream().map(Loan::getLoanApplicationId).distinct().toList()
        ).stream().collect(Collectors.toMap(LoanApplication::getId, a -> a));

        Map<UUID, User> usersById = userRepository.findAllById(
                repayments.stream().map(Repayment::getRecordedBy).distinct().toList()
        ).stream().collect(Collectors.toMap(User::getId, u -> u));

        List<AdminRepaymentDto> dtos = repayments.stream()
                .map(r -> toDto(r, loansById.get(r.getLoanId()), applicationsById, usersById))
                .filter(dto -> from == null || !dto.paidAt().isBefore(from))
                .filter(dto -> to == null || !dto.paidAt().isAfter(to))
                .filter(dto -> method == null || dto.method() == method)
                .filter(dto -> matchesSearch(dto, search))
                .sorted(Comparator.comparing(AdminRepaymentDto::paidAt).reversed())
                .toList();

        int start = Math.min((int) pageable.getOffset(), dtos.size());
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    private AdminRepaymentDto toDto(Repayment r, Loan loan, Map<UUID, LoanApplication> applicationsById, Map<UUID, User> usersById) {
        LoanApplication application = loan != null ? applicationsById.get(loan.getLoanApplicationId()) : null;
        String applicantName = application != null ? applicantNameOf(application) : null;
        User recordedBy = usersById.get(r.getRecordedBy());
        String recordedByName = recordedBy != null ? recordedBy.getFirstName() + " " + recordedBy.getLastName() : null;

        return new AdminRepaymentDto(
                r.getId(), r.getPaidAt(), r.getAmount(), r.getMethod(),
                r.getLoanId(), loan != null ? loan.getReferenceNumber() : null, applicantName,
                recordedByName, r.getNotes()
        );
    }

    private static String applicantNameOf(LoanApplication a) {
        String personal = a.getPersonalInfo() != null ? a.getPersonalInfo().fullName() : null;
        String business = a.getEmployment() != null ? a.getEmployment().businessName() : null;
        return personal != null ? personal : business;
    }

    private boolean matchesSearch(AdminRepaymentDto dto, String search) {
        if (search == null || search.isBlank()) return true;
        String needle = search.trim().toLowerCase();
        return (dto.loanReferenceNumber() != null && dto.loanReferenceNumber().toLowerCase().contains(needle))
                || (dto.applicantName() != null && dto.applicantName().toLowerCase().contains(needle));
    }
}
