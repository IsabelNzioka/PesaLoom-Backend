package com.pesaloom.pesaloom.loanapplication.repository;

import com.pesaloom.pesaloom.loanapplication.entity.PostalCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostalCodeRepository extends JpaRepository<PostalCode, String> {
}
