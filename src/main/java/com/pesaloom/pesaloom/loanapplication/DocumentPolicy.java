package com.pesaloom.pesaloom.loanapplication;

import java.util.Map;
import java.util.Set;


public final class DocumentPolicy {

    private static final Set<String> PDF_IMAGE = Set.of("application/pdf", "image/jpeg", "image/png");
    private static final Set<String> IMAGE_ONLY = Set.of("image/jpeg", "image/png");
    private static final Set<String> PDF_ONLY = Set.of("application/pdf");

    private static final long MB = 1024L * 1024L;

    public record Policy(Set<String> allowedContentTypes, long maxSizeBytes) {
    }

    private static final Map<String, Policy> POLICIES = Map.ofEntries(
            Map.entry("kraPinCertificate", new Policy(PDF_IMAGE, 5 * MB)),
            Map.entry("nationalId", new Policy(PDF_IMAGE, 5 * MB)),
            Map.entry("photograph", new Policy(IMAGE_ONLY, 2 * MB)),
            Map.entry("bankStatements", new Policy(PDF_ONLY, 10 * MB)),
            Map.entry("salarySlips", new Policy(PDF_ONLY, 5 * MB)),
            Map.entry("itr", new Policy(PDF_ONLY, 5 * MB)),
            Map.entry("propertyDocs", new Policy(PDF_ONLY, 10 * MB)),
            Map.entry("businessRegistration", new Policy(PDF_ONLY, 5 * MB)),
            Map.entry("kraTaxComplianceCertificate", new Policy(PDF_ONLY, 5 * MB)),
            Map.entry("signature", new Policy(IMAGE_ONLY, 2 * MB)),
            Map.entry("coApplicantSignature", new Policy(IMAGE_ONLY, 2 * MB)),
            Map.entry("borrowerPhoto", new Policy(IMAGE_ONLY, 2 * MB)),
            Map.entry("borrowerFile", new Policy(PDF_IMAGE, 10 * MB))
    );

    public static Policy of(String documentKey) {
        return POLICIES.get(documentKey);
    }

    private DocumentPolicy() {
    }
}
