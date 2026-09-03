package com.lendswift.lendswift.loanapplication.repository;

import com.lendswift.lendswift.loanapplication.entity.PostalCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeRepository extends JpaRepository<PostalCode, String> {
}
