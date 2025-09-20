package com.example.pib2.servicios;

import com.example.pib2.models.entities.Company;
import com.example.pib2.models.dtos.CompanyDto;
import com.example.pib2.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    private CompanyDto toDto(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setIdentificationNumber(company.getIdentificationNumber());
        dto.setAddress(company.getAddress());
        dto.setPhone(company.getPhone());
        dto.setEmail(company.getEmail());
        return dto;
    }

    private Company toEntity(CompanyDto dto) {
        Company company = new Company();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setIdentificationNumber(dto.getIdentificationNumber());
        company.setAddress(dto.getAddress());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());
        return company;
    }

    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CompanyDto getCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public CompanyDto createCompany(CompanyDto companyDto) {
        Company company = toEntity(companyDto);
        company.setCreatedAt(LocalDateTime.now());
        company.setUpdatedAt(LocalDateTime.now());
        Company saved = companyRepository.save(company);
        return toDto(saved);
    }

    public CompanyDto updateCompany(Long id, CompanyDto companyDto) {
        return companyRepository.findById(id).map(company -> {
            company.setName(companyDto.getName());
            company.setIdentificationNumber(companyDto.getIdentificationNumber());
            company.setAddress(companyDto.getAddress());
            company.setPhone(companyDto.getPhone());
            company.setEmail(companyDto.getEmail());
            company.setUpdatedAt(LocalDateTime.now());
            Company updated = companyRepository.save(company);
            return toDto(updated);
        }).orElse(null);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }
}
