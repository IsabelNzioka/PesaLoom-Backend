package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.BranchDto;
import com.pesaloom.pesaloom.admin.dto.CreateBranchRequest;
import com.pesaloom.pesaloom.branch.entity.Branch;
import com.pesaloom.pesaloom.branch.repository.BranchRepository;
import com.pesaloom.pesaloom.exception.BranchCodeAlreadyInUseException;
import com.pesaloom.pesaloom.loan.entity.Loan;
import com.pesaloom.pesaloom.loan.entity.LoanStatus;
import com.pesaloom.pesaloom.loan.repository.LoanRepository;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import com.pesaloom.pesaloom.loanapplication.repository.LoanApplicationRepository;
import com.pesaloom.pesaloom.user.Role;
import com.pesaloom.pesaloom.user.User;
import com.pesaloom.pesaloom.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class AdminBranchService {

    private final BranchRepository branchRepository;
    private final UserRepository userRepository;
    private final LoanApplicationRepository loanApplicationRepository;
    private final LoanRepository loanRepository;

    public AdminBranchService(BranchRepository branchRepository, UserRepository userRepository,
                               LoanApplicationRepository loanApplicationRepository, LoanRepository loanRepository) {
        this.branchRepository = branchRepository;
        this.userRepository = userRepository;
        this.loanApplicationRepository = loanApplicationRepository;
        this.loanRepository = loanRepository;
    }

    @Transactional(readOnly = true)
    public List<BranchDto> list() {
        List<Branch> branches = branchRepository.findAll();

        Map<UUID, Long> staffCounts = userRepository.findAllByRoleOrderByFirstNameAsc(Role.ADMIN).stream()
                .filter(u -> u.getBranchId() != null)
                .collect(Collectors.groupingBy(User::getBranchId, Collectors.counting()));

        Map<UUID, Long> applicationCounts = loanApplicationRepository.findAll().stream()
                .filter(a -> a.getStatus() != LoanApplicationStatus.DRAFT && a.getBranchId() != null)
                .collect(Collectors.groupingBy(LoanApplication::getBranchId, Collectors.counting()));

        Map<UUID, Long> activeLoanCounts = loanRepository.findByStatus(LoanStatus.ACTIVE).stream()
                .filter(l -> l.getBranchId() != null)
                .collect(Collectors.groupingBy(Loan::getBranchId, Collectors.counting()));

        return branches.stream()
                .map(b -> new BranchDto(
                        b.getId(), b.getName(), b.getCode(), b.getAddress(), b.getPhone(),
                        staffCounts.getOrDefault(b.getId(), 0L),
                        applicationCounts.getOrDefault(b.getId(), 0L),
                        activeLoanCounts.getOrDefault(b.getId(), 0L)
                ))
                .toList();
    }

    @Transactional
    public BranchDto create(CreateBranchRequest request) {
        if (branchRepository.existsByCode(request.code())) {
            throw new BranchCodeAlreadyInUseException();
        }
        Branch branch = new Branch(request.name(), request.code(), request.address(), request.phone());
        branch = branchRepository.save(branch);
        return new BranchDto(branch.getId(), branch.getName(), branch.getCode(), branch.getAddress(), branch.getPhone(), 0, 0, 0);
    }
}
