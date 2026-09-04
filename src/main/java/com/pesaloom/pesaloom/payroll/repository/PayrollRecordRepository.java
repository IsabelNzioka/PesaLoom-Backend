package com.pesaloom.pesaloom.payroll.repository;

import com.pesaloom.pesaloom.payroll.entity.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, UUID> {
}
