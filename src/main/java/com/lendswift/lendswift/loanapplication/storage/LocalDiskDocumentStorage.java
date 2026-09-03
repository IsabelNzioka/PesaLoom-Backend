package com.lendswift.lendswift.loanapplication.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalDiskDocumentStorage implements DocumentStorage {

    private final Path rootDir;

    public LocalDiskDocumentStorage(@Value("${app.storage.root-dir}") String rootDir) {
        this.rootDir = Path.of(rootDir).toAbsolutePath().normalize();
    }

    @Override
    public String store(UUID userId, UUID loanApplicationId, UUID documentId, String originalFilename, InputStream content) {
        String sanitized = sanitize(originalFilename);
        Path relative = Path.of(userId.toString(), loanApplicationId.toString(), documentId + "_" + sanitized);
        Path target = rootDir.resolve(relative);

        try {
            Files.createDirectories(target.getParent());
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to store document", e);
        }

        return relative.toString();
    }

    @Override
    public InputStream read(String storagePath) {
        try {
            return Files.newInputStream(resolveWithinRoot(storagePath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read document", e);
        }
    }

    @Override
    public void delete(String storagePath) {
        try {
            Files.deleteIfExists(resolveWithinRoot(storagePath));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete document", e);
        }
    }


    private Path resolveWithinRoot(String storagePath) {
        Path resolved = rootDir.resolve(storagePath).normalize();
        if (!resolved.startsWith(rootDir)) {
            throw new IllegalArgumentException("Invalid storage path");
        }
        return resolved;
    }

    private String sanitize(String filename) {
        String base = Path.of(filename).getFileName().toString();
        return base.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
