package com.lendswift.lendswift.loanapplication.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents")
public class Document {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "loan_application_id", nullable = false)
    private UUID loanApplicationId;

    @Column(name = "document_key", nullable = false)
    private String documentKey;

    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "size_bytes", nullable = false)
    private long sizeBytes;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private Instant uploadedAt;

    protected Document() {
        // JPA
    }

    public Document(UUID loanApplicationId, String documentKey, String originalFilename,
                     String contentType, long sizeBytes, String storagePath) {
        this.loanApplicationId = loanApplicationId;
        this.documentKey = documentKey;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.storagePath = storagePath;
        this.uploadedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getLoanApplicationId() {
        return loanApplicationId;
    }

    public String getDocumentKey() {
        return documentKey;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }
}
