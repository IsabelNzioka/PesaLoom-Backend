package com.pesaloom.pesaloom.admin;

import com.pesaloom.pesaloom.admin.dto.AddCollateralRequest;
import com.pesaloom.pesaloom.admin.dto.CollateralDto;
import com.pesaloom.pesaloom.exception.LoanApplicationNotFoundException;
import com.pesaloom.pesaloom.loanapplication.entity.Collateral;
import com.pesaloom.pesaloom.loanapplication.entity.LoanApplication;
import com.pesaloom.pesaloom.loanapplication.repository.CollateralRepository;
import com.pesaloom.pesaloom.loanapplication.repository.LoanApplicationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminCollateralService {

    private final CollateralRepository collateralRepository;
    private final LoanApplicationRepository loanApplicationRepository;

    public AdminCollateralService(CollateralRepository collateralRepository, LoanApplicationRepository loanApplicationRepository) {
        this.collateralRepository = collateralRepository;
        this.loanApplicationRepository = loanApplicationRepository;
    }

    @Transactional(readOnly = true)
    public List<CollateralDto> listForApplication(UUID applicationId) {
        LoanApplication application = requireApplication(applicationId);
        return collateralRepository.findByLoanApplicationIdOrderByRegisteredAtDesc(applicationId).stream()
                .map(c -> CollateralDto.from(c, application.getReferenceNumber(), applicantNameOf(application)))
                .toList();
    }

    @Transactional
    public CollateralDto add(UUID applicationId, AddCollateralRequest request, UUID staffUserId) {
        LoanApplication application = requireApplication(applicationId);
        Collateral collateral = new Collateral(applicationId, request.type(), request.description(),
                request.estimatedValue(), staffUserId);
        collateral = collateralRepository.save(collateral);
        return CollateralDto.from(collateral, application.getReferenceNumber(), applicantNameOf(application));
    }

    @Transactional(readOnly = true)
    public Page<CollateralDto> listAll(String search, Pageable pageable) {
        List<Collateral> all = collateralRepository.findAll();

        Map<UUID, LoanApplication> applicationsById = loanApplicationRepository.findAllById(
                all.stream().map(Collateral::getLoanApplicationId).distinct().toList()
        ).stream().collect(Collectors.toMap(LoanApplication::getId, a -> a));

        List<CollateralDto> dtos = all.stream()
                .map(c -> {
                    LoanApplication application = applicationsById.get(c.getLoanApplicationId());
                    return CollateralDto.from(c, application != null ? application.getReferenceNumber() : null,
                            application != null ? applicantNameOf(application) : null);
                })
                .filter(dto -> matchesSearch(dto, search))
                .sorted(Comparator.comparing(CollateralDto::registeredAt).reversed())
                .toList();

        int start = Math.min((int) pageable.getOffset(), dtos.size());
        int end = Math.min(start + pageable.getPageSize(), dtos.size());
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    private LoanApplication requireApplication(UUID applicationId) {
        return loanApplicationRepository.findById(applicationId).orElseThrow(LoanApplicationNotFoundException::new);
    }

    private static String applicantNameOf(LoanApplication a) {
        String personal = a.getPersonalInfo() != null ? a.getPersonalInfo().fullName() : null;
        String business = a.getEmployment() != null ? a.getEmployment().businessName() : null;
        return personal != null ? personal : business;
    }

    private boolean matchesSearch(CollateralDto dto, String search) {
        if (search == null || search.isBlank()) return true;
        String needle = search.trim().toLowerCase();
        return (dto.referenceNumber() != null && dto.referenceNumber().toLowerCase().contains(needle))
                || (dto.applicantName() != null && dto.applicantName().toLowerCase().contains(needle))
                || dto.description().toLowerCase().contains(needle);
    }
}
