package com.pesaloom.pesaloom.branch.repository;

import com.pesaloom.pesaloom.branch.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    boolean existsByCode(String code);

    Optional<Branch> findByCode(String code);
}
