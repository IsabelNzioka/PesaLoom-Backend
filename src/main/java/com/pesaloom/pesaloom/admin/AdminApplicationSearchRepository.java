package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus;
import com.pesaloom.pesaloom.loanapplication.entity.LoanType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Repository
public class AdminApplicationSearchRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<LoanApplication> search(LoanApplicationStatus status, LoanType loanType,
                                         Instant from, Instant to, String search, UUID branchId, Pageable pageable) {
        StringBuilder where = baseWhere(loanType, from, to, branchId);
        Map<String, Object> params = baseParams(loanType, from, to, branchId);

        if (status != null) {
            where.append(" AND a.status = :status");
            params.put("status", status);
        }
        if (search != null && !search.isBlank()) {
            where.append("""
                     AND (LOWER(a.referenceNumber) LIKE :search
                          OR LOWER(FUNCTION('jsonb_extract_path_text', a.personalInfo, 'fullName')) LIKE :search
                          OR LOWER(FUNCTION('jsonb_extract_path_text', a.employment, 'businessName')) LIKE :search)
                    """);
            params.put("search", "%" + search.toLowerCase() + "%");
        }

        TypedQuery<LoanApplication> contentQuery = entityManager.createQuery(
                "SELECT a FROM LoanApplication a WHERE " + where + " ORDER BY a.submittedAt DESC", LoanApplication.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(
                "SELECT COUNT(a) FROM LoanApplication a WHERE " + where, Long.class);

        params.forEach((name, value) -> {
            contentQuery.setParameter(name, value);
            countQuery.setParameter(name, value);
        });

        contentQuery.setFirstResult((int) pageable.getOffset());
        contentQuery.setMaxResults(pageable.getPageSize());

        List<LoanApplication> content = contentQuery.getResultList();
        long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }


    public List<LoanApplication> searchAllForSummary(LoanType loanType, Instant from, Instant to, UUID branchId) {
        StringBuilder where = baseWhere(loanType, from, to, branchId);
        Map<String, Object> params = baseParams(loanType, from, to, branchId);

        TypedQuery<LoanApplication> query = entityManager.createQuery(
                "SELECT a FROM LoanApplication a WHERE " + where, LoanApplication.class);
        params.forEach(query::setParameter);
        return query.getResultList();
    }

    private StringBuilder baseWhere(LoanType loanType, Instant from, Instant to, UUID branchId) {
        StringBuilder where = new StringBuilder("a.status <> com.pesaloom.pesaloom.loanapplication.entity.LoanApplicationStatus.DRAFT");
        if (loanType != null) {
            where.append(" AND a.loanType = :loanType");
        }
        if (from != null) {
            where.append(" AND a.submittedAt >= :from");
        }
        if (to != null) {
            where.append(" AND a.submittedAt <= :to");
        }
        if (branchId != null) {
            where.append(" AND a.branchId = :branchId");
        }
        return where;
    }

    private Map<String, Object> baseParams(LoanType loanType, Instant from, Instant to, UUID branchId) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (loanType != null) params.put("loanType", loanType);
        if (from != null) params.put("from", from);
        if (to != null) params.put("to", to);
        if (branchId != null) params.put("branchId", branchId);
        return params;
    }
}
