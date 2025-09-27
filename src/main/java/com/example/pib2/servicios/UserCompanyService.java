package com.example.pib2.servicios;

import com.example.pib2.models.entities.UserCompany;
import com.example.pib2.models.dtos.UserCompanyDto;
import com.example.pib2.repositories.UserCompanyRepository;
import com.example.pib2.repositories.UserRepository;
import com.example.pib2.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserCompanyService {

    @Autowired
    private UserCompanyRepository userCompanyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;

    private UserCompanyDto toDto(UserCompany userCompany) {
        UserCompanyDto dto = new UserCompanyDto();
        dto.setId(userCompany.getId());
        dto.setUserId(userCompany.getUser().getId());
        dto.setCompanyId(userCompany.getCompany().getId());
        return dto;
    }

    private UserCompany toEntity(UserCompanyDto dto) {
        UserCompany userCompany = new UserCompany();
        userCompany.setId(dto.getId());
        userCompany.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        userCompany.setCompany(companyRepository.findById(dto.getCompanyId()).orElse(null));
        return userCompany;
    }

    public List<UserCompanyDto> getAllUserCompanies() {
        return userCompanyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public UserCompanyDto getUserCompanyById(Long id) {
        return userCompanyRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public UserCompanyDto createUserCompany(UserCompanyDto dto) {
        UserCompany userCompany = toEntity(dto);
        UserCompany saved = userCompanyRepository.save(userCompany);
        return toDto(saved);
    }

    public void deleteUserCompany(Long id) {
        userCompanyRepository.deleteById(id);
    }
}
