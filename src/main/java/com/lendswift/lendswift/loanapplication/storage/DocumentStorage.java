package com.lendswift.lendswift.loanapplication.storage;

import java.io.InputStream;
import java.util.UUID;

public interface DocumentStorage {


    String store(UUID userId, UUID loanApplicationId, UUID documentId, String originalFilename, InputStream content);

    InputStream read(String storagePath);

    void delete(String storagePath);
}
