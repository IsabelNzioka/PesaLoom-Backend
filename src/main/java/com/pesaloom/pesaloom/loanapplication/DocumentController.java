package com.pesaloom.pesaloom.loanapplication;

import com.pesaloom.pesaloom.loanapplication.dto.DocumentDto;
import com.pesaloom.pesaloom.security.AuthPrincipal;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/loan-applications/{applicationId}/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public DocumentDto upload(@AuthenticationPrincipal AuthPrincipal principal,
                               @PathVariable UUID applicationId,
                               @RequestParam String documentKey,
                               @RequestParam MultipartFile file) {
        return documentService.upload(principal.userId(), applicationId, documentKey, file);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal AuthPrincipal principal,
                                        @PathVariable UUID applicationId,
                                        @PathVariable UUID documentId) {
        documentService.delete(principal.userId(), applicationId, documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<InputStreamResource> download(@AuthenticationPrincipal AuthPrincipal principal,
                                                          @PathVariable UUID applicationId,
                                                          @PathVariable UUID documentId) {
        DocumentService.LoadedDocument doc = documentService.load(principal.userId(), applicationId, documentId);
        ContentDisposition disposition = ContentDisposition.inline().filename(doc.originalFilename()).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(doc.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(new InputStreamResource(doc.content()));
    }
}
