package com.lendswift.lendswift.loanapplication;

import com.lendswift.lendswift.exception.DocumentNotFoundException;
import com.lendswift.lendswift.exception.DocumentValidationException;
import com.lendswift.lendswift.exception.InvalidApplicationStateException;
import com.lendswift.lendswift.loanapplication.dto.DocumentDto;
import com.lendswift.lendswift.loanapplication.entity.Document;
import com.lendswift.lendswift.loanapplication.entity.LoanApplication;
import com.lendswift.lendswift.loanapplication.repository.DocumentRepository;
import com.lendswift.lendswift.loanapplication.storage.DocumentStorage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.UUID;

@Service
public class DocumentService {

    private final LoanApplicationService loanApplicationService;
    private final DocumentRepository documentRepository;
    private final DocumentStorage documentStorage;

    public DocumentService(LoanApplicationService loanApplicationService,
                            DocumentRepository documentRepository,
                            DocumentStorage documentStorage) {
        this.loanApplicationService = loanApplicationService;
        this.documentRepository = documentRepository;
        this.documentStorage = documentStorage;
    }

    @Transactional
    public DocumentDto upload(UUID userId, UUID loanApplicationId, String documentKey, MultipartFile file) {
        LoanApplication application = loanApplicationService.requireOwn(userId, loanApplicationId);
        if (!application.isDraft()) {
            throw new InvalidApplicationStateException("Documents can only be uploaded to a draft application");
        }

        DocumentPolicy.Policy policy = DocumentPolicy.of(documentKey);
        if (policy == null) {
            throw new DocumentValidationException("Unknown document type");
        }
        if (file.isEmpty()) {
            throw new DocumentValidationException("File is empty");
        }
        if (file.getSize() > policy.maxSizeBytes()) {
            throw new DocumentValidationException("File exceeds the maximum allowed size for this document type");
        }
        String contentType = file.getContentType();
        if (contentType == null || !policy.allowedContentTypes().contains(contentType)) {
            throw new DocumentValidationException("Unsupported file type for this document");
        }

        UUID documentId = UUID.randomUUID();
        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";

        String storagePath;
        try (InputStream in = file.getInputStream()) {
            storagePath = documentStorage.store(userId, loanApplicationId, documentId, originalFilename, in);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded file", e);
        }

        Document document = new Document(loanApplicationId, documentKey, originalFilename, contentType, file.getSize(), storagePath);
        document = documentRepository.save(document);

        return new DocumentDto(document.getId(), document.getDocumentKey(), document.getOriginalFilename(),
                document.getContentType(), document.getSizeBytes(), document.getUploadedAt());
    }

    @Transactional
    public void delete(UUID userId, UUID loanApplicationId, UUID documentId) {
        loanApplicationService.requireOwn(userId, loanApplicationId);
        Document document = documentRepository.findByIdAndLoanApplicationId(documentId, loanApplicationId)
                .orElseThrow(DocumentNotFoundException::new);
        documentStorage.delete(document.getStoragePath());
        documentRepository.delete(document);
    }

    public LoadedDocument load(UUID userId, UUID loanApplicationId, UUID documentId) {
        loanApplicationService.requireOwn(userId, loanApplicationId);
        Document document = documentRepository.findByIdAndLoanApplicationId(documentId, loanApplicationId)
                .orElseThrow(DocumentNotFoundException::new);
        InputStream content = documentStorage.read(document.getStoragePath());
        return new LoadedDocument(document.getOriginalFilename(), document.getContentType(), content);
    }

    public record LoadedDocument(String originalFilename, String contentType, InputStream content) {
    }
}
