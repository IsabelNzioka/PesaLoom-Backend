package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AdminSavingsAccountDto;
import com.pesaloom.pesaloom.admin.dto.AdminSavingsSummaryDto;
import com.pesaloom.pesaloom.admin.dto.OpenSavingsAccountRequest;
import com.pesaloom.pesaloom.admin.dto.SavingsTransactionDto;
import com.pesaloom.pesaloom.admin.dto.SavingsTransactionRequest;
import com.pesaloom.pesaloom.exception.SavingsAccountNotFoundException;
import com.pesaloom.pesaloom.loanapplication.ReferenceNumberGenerator;
import com.pesaloom.pesaloom.savings.SavingsTransactionService;
import com.pesaloom.pesaloom.savings.entity.SavingsAccount;
import com.pesaloom.pesaloom.savings.entity.SavingsTransaction;
import com.pesaloom.pesaloom.savings.entity.TransactionType;
import com.pesaloom.pesaloom.savings.repository.SavingsAccountRepository;
import com.pesaloom.pesaloom.savings.repository.SavingsTransactionRepository;
import com.pesaloom.pesaloom.user.User;
import com.pesaloom.pesaloom.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminSavingsService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsTransactionRepository savingsTransactionRepository;
    private final SavingsTransactionService savingsTransactionService;
    private final UserRepository userRepository;

    public AdminSavingsService(SavingsAccountRepository savingsAccountRepository,
                                SavingsTransactionRepository savingsTransactionRepository,
                                SavingsTransactionService savingsTransactionService,
                                UserRepository userRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.savingsTransactionRepository = savingsTransactionRepository;
        this.savingsTransactionService = savingsTransactionService;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<AdminSavingsSummaryDto> list(String search, Pageable pageable) {
        List<SavingsAccount> accounts = savingsAccountRepository.findAll();
        Map<UUID, String> ownerNames = ownerNamesFor(accounts.stream().map(SavingsAccount::getUserId).distinct().toList());

        List<AdminSavingsSummaryDto> dtos = accounts.stream()
                .map(a -> new AdminSavingsSummaryDto(
                        a.getId(), a.getAccountNumber(), ownerNames.get(a.getUserId()),
                        a.getBalance(), a.getStatus().name(), a.getOpenedAt()))
                .filter(dto -> matchesSearch(dto, search))
                .sorted(Comparator.comparing(AdminSavingsSummaryDto::openedAt).reversed())
                .toList();

        int start = Math.min((int) pageable.getOffset(), dtos.size());
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Transactional(readOnly = true)
    public AdminSavingsAccountDto get(UUID id) {
        return toDto(requireById(id));
    }

    @Transactional
    public AdminSavingsAccountDto open(OpenSavingsAccountRequest request, UUID staffUserId) {
        String accountNumber;
        do {
            accountNumber = ReferenceNumberGenerator.generate("SV");
        } while (savingsAccountRepository.existsByAccountNumber(accountNumber));

        SavingsAccount account = new SavingsAccount(request.userId(), accountNumber, request.openingDeposit(), request.interestRate());
        account = savingsAccountRepository.save(account);

        if (request.openingDeposit().signum() > 0) {
            savingsTransactionRepository.save(new SavingsTransaction(
                    account.getId(), TransactionType.DEPOSIT,
                    request.openingDeposit(), account.getBalance(), staffUserId, "Opening deposit"));
        }

        return toDto(account);
    }

    @Transactional
    public AdminSavingsAccountDto deposit(UUID accountId, SavingsTransactionRequest request, UUID staffUserId) {
        savingsTransactionService.deposit(accountId, request.amount(), staffUserId, request.notes());
        return toDto(requireById(accountId));
    }

    @Transactional
    public AdminSavingsAccountDto withdraw(UUID accountId, SavingsTransactionRequest request, UUID staffUserId) {
        savingsTransactionService.withdraw(accountId, request.amount(), staffUserId, request.notes());
        return toDto(requireById(accountId));
    }

    private SavingsAccount requireById(UUID id) {
        return savingsAccountRepository.findById(id).orElseThrow(SavingsAccountNotFoundException::new);
    }

    private AdminSavingsAccountDto toDto(SavingsAccount account) {
        List<SavingsTransaction> transactions = savingsTransactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId());
        Map<UUID, String> recordedByNames = ownerNamesFor(transactions.stream().map(SavingsTransaction::getRecordedBy).distinct().toList());

        List<SavingsTransactionDto> transactionDtos = transactions.stream()
                .map(t -> new SavingsTransactionDto(
                        t.getId(), t.getType(), t.getAmount(), t.getBalanceAfter(),
                        recordedByNames.get(t.getRecordedBy()), t.getNotes(), t.getCreatedAt()))
                .toList();

        Map<UUID, String> ownerNames = ownerNamesFor(List.of(account.getUserId()));

        return new AdminSavingsAccountDto(
                account.getId(), account.getUserId(), account.getAccountNumber(), ownerNames.get(account.getUserId()),
                account.getBalance(), account.getInterestRate(), account.getStatus().name(),
                account.getOpenedAt(), account.getClosedAt(), transactionDtos
        );
    }

    private Map<UUID, String> ownerNamesFor(List<UUID> userIds) {
        if (userIds.isEmpty()) return Map.of();
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u.getFirstName() + " " + u.getLastName()));
    }

    private boolean matchesSearch(AdminSavingsSummaryDto dto, String search) {
        if (search == null || search.isBlank()) return true;
        String needle = search.trim().toLowerCase();
        return (dto.accountNumber() != null && dto.accountNumber().toLowerCase().contains(needle))
                || (dto.ownerName() != null && dto.ownerName().toLowerCase().contains(needle));
    }
}
