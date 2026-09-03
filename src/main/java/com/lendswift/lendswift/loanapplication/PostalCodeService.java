package com.lendswift.lendswift.loanapplication;

import com.lendswift.lendswift.loanapplication.dto.PostalCodeDto;
import com.lendswift.lendswift.loanapplication.repository.PostalCodeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostalCodeService {

    private final PostalCodeRepository postalCodeRepository;

    public PostalCodeService(PostalCodeRepository postalCodeRepository) {
        this.postalCodeRepository = postalCodeRepository;
    }

    public Optional<PostalCodeDto> lookup(String code) {
        if (!KenyaFormats.POSTAL_CODE.matcher(code).matches()) {
            return Optional.empty();
        }
        return postalCodeRepository.findById(code)
                .map(p -> new PostalCodeDto(p.getCode(), p.getTown(), p.getCounty()));
    }
}
