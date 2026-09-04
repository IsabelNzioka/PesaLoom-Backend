package com.pesaloom.pesaloom.savings;

import com.pesaloom.pesaloom.exception.InsufficientBalanceException;
import com.pesaloom.pesaloom.exception.SavingsAccountNotFoundException;
import com.pesaloom.pesaloom.savings.entity.SavingsAccount;
import com.pesaloom.pesaloom.savings.entity.SavingsTransaction;
import com.pesaloom.pesaloom.savings.entity.TransactionType;
import com.pesaloom.pesaloom.savings.repository.SavingsAccountRepository;
import com.pesaloom.pesaloom.savings.repository.SavingsTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Service
public class SavingsTransactionService {

    private final SavingsAccountRepository savingsAccountRepository;
    private final SavingsTransactionRepository savingsTransactionRepository;

    public SavingsTransactionService(SavingsAccountRepository savingsAccountRepository,
                                      SavingsTransactionRepository savingsTransactionRepository) {
        this.savingsAccountRepository = savingsAccountRepository;
        this.savingsTransactionRepository = savingsTransactionRepository;
    }

    @Transactional
    public SavingsAccount deposit(UUID accountId, BigDecimal amount, UUID staffUserId, String notes) {
        SavingsAccount account = requireById(accountId);
        account.applyDeposit(amount);
        account = savingsAccountRepository.save(account);
        savingsTransactionRepository.save(
                new SavingsTransaction(accountId, TransactionType.DEPOSIT, amount, account.getBalance(), staffUserId, notes));
        return account;
    }

    @Transactional
    public SavingsAccount withdraw(UUID accountId, BigDecimal amount, UUID staffUserId, String notes) {
        SavingsAccount account = requireById(accountId);
        if (amount.compareTo(account.getBalance()) > 0) {
            throw new InsufficientBalanceException();
        }
        account.applyWithdrawal(amount);
        account = savingsAccountRepository.save(account);
        savingsTransactionRepository.save(
                new SavingsTransaction(accountId, TransactionType.WITHDRAWAL, amount, account.getBalance(), staffUserId, notes));
        return account;
    }

    private SavingsAccount requireById(UUID id) {
        return savingsAccountRepository.findById(id).orElseThrow(SavingsAccountNotFoundException::new);
    }
}
