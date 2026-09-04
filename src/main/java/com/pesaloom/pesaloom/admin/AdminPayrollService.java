package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.PayrollRecordDto;
import com.pesaloom.pesaloom.admin.dto.RecordPayrollRequest;
import com.pesaloom.pesaloom.payroll.entity.PayrollRecord;
import com.pesaloom.pesaloom.payroll.repository.PayrollRecordRepository;
import com.pesaloom.pesaloom.user.User;
import com.pesaloom.pesaloom.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminPayrollService {

    private final PayrollRecordRepository payrollRecordRepository;
    private final UserRepository userRepository;

    public AdminPayrollService(PayrollRecordRepository payrollRecordRepository, UserRepository userRepository) {
        this.payrollRecordRepository = payrollRecordRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<PayrollRecordDto> list(String periodMonth, Pageable pageable) {
        List<PayrollRecord> records = payrollRecordRepository.findAll();

        Map<UUID, String> staffNames = userRepository.findAllById(
                records.stream().map(PayrollRecord::getStaffUserId).distinct().toList()
        ).stream().collect(Collectors.toMap(User::getId, u -> u.getFirstName() + " " + u.getLastName()));

        List<PayrollRecordDto> dtos = records.stream()
                .filter(r -> periodMonth == null || periodMonth.isBlank() || r.getPeriodMonth().equals(periodMonth))
                .map(r -> new PayrollRecordDto(
                        r.getId(), r.getStaffUserId(), staffNames.get(r.getStaffUserId()), r.getPeriodMonth(),
                        r.getBaseSalary(), r.getDeductions(), r.getNetPay(), r.getPaidAt(), r.getNotes()
                ))
                .sorted(Comparator.comparing(PayrollRecordDto::paidAt).reversed())
                .toList();

        int start = Math.min((int) pageable.getOffset(), dtos.size());
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Transactional
    public PayrollRecordDto record(RecordPayrollRequest request, UUID recordedByUserId) {
        BigDecimal deductions = request.deductions() != null ? request.deductions() : BigDecimal.ZERO;
        PayrollRecord record = new PayrollRecord(request.staffUserId(), request.periodMonth(), request.baseSalary(),
                deductions, recordedByUserId, request.notes());
        record = payrollRecordRepository.save(record);

        String staffName = userRepository.findById(record.getStaffUserId())
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .orElse(null);

        return new PayrollRecordDto(
                record.getId(), record.getStaffUserId(), staffName, record.getPeriodMonth(),
                record.getBaseSalary(), record.getDeductions(), record.getNetPay(), record.getPaidAt(), record.getNotes()
        );
    }
}
