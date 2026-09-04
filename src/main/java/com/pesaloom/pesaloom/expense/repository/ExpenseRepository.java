package com.pesaloom.pesaloom.expense.repository;

import com.pesaloom.pesaloom.expense.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
}
