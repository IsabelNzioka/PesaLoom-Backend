package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.ExpenseDto;
import com.pesaloom.pesaloom.admin.dto.RecordExpenseRequest;
import com.pesaloom.pesaloom.expense.entity.Expense;
import com.pesaloom.pesaloom.expense.repository.ExpenseRepository;
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
public class AdminExpenseService {

    public static final List<String> CATEGORIES = List.of(
            "Rent", "Utilities", "Salaries", "Transport", "Office Supplies", "Marketing", "Other"
    );

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    public AdminExpenseService(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ExpenseDto> list(String category, Pageable pageable) {
        List<Expense> expenses = expenseRepository.findAll();

        Map<UUID, String> recordedByNames = userRepository.findAllById(
                expenses.stream().map(Expense::getRecordedBy).distinct().toList()
        ).stream().collect(Collectors.toMap(User::getId, u -> u.getFirstName() + " " + u.getLastName()));

        List<ExpenseDto> dtos = expenses.stream()
                .filter(e -> category == null || category.isBlank() || e.getCategory().equals(category))
                .map(e -> new ExpenseDto(
                        e.getId(), e.getCategory(), e.getDescription(), e.getAmount(),
                        e.getIncurredAt(), recordedByNames.get(e.getRecordedBy()), e.getNotes()
                ))
                .sorted(Comparator.comparing(ExpenseDto::incurredAt).reversed())
                .toList();

        int start = Math.min((int) pageable.getOffset(), dtos.size());
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Transactional
    public ExpenseDto record(RecordExpenseRequest request, UUID recordedByUserId) {
        Expense expense = new Expense(request.category(), request.description(), request.amount(),
                request.incurredAt(), recordedByUserId, request.notes());
        expense = expenseRepository.save(expense);

        String recordedByName = userRepository.findById(recordedByUserId)
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse(null);

        return new ExpenseDto(
                expense.getId(), expense.getCategory(), expense.getDescription(), expense.getAmount(),
                expense.getIncurredAt(), recordedByName, expense.getNotes()
        );
    }
}
