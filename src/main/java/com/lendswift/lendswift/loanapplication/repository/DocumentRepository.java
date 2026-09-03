package com.lendswift.lendswift.loanapplication.repository;

import com.lendswift.lendswift.loanapplication.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findAllByLoanApplicationId(UUID loanApplicationId);

    Optional<Document> findByIdAndLoanApplicationId(UUID id, UUID loanApplicationId);
}
