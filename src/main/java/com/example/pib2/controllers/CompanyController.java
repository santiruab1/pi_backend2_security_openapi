package com.example.pib2.controllers;

import com.example.pib2.models.entities.Company;
import com.example.pib2.models.dtos.CompanyDto;
import com.example.pib2.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    // Convertir entidad a DTO
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

    // Obtener todas las compañías
    @GetMapping
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Obtener compañía por ID
    @GetMapping("/{id}")
    public CompanyDto getCompanyById(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    // Crear nueva compañía
    @PostMapping
    public CompanyDto createCompany(@RequestBody CompanyDto companyDto) {
        Company company = new Company();
        company.setName(companyDto.getName());
        company.setIdentificationNumber(companyDto.getIdentificationNumber());
        company.setAddress(companyDto.getAddress());
        company.setPhone(companyDto.getPhone());
        company.setEmail(companyDto.getEmail());
        company.setCreatedAt(java.time.LocalDateTime.now());
        company.setUpdatedAt(java.time.LocalDateTime.now());
        Company saved = companyRepository.save(company);
        return toDto(saved);
    }

    // Actualizar compañía
    @PutMapping("/{id}")
    public CompanyDto updateCompany(@PathVariable Long id, @RequestBody CompanyDto companyDto) {
        return companyRepository.findById(id).map(company -> {
            company.setName(companyDto.getName());
            company.setIdentificationNumber(companyDto.getIdentificationNumber());
            company.setAddress(companyDto.getAddress());
            company.setPhone(companyDto.getPhone());
            company.setEmail(companyDto.getEmail());
            company.setUpdatedAt(java.time.LocalDateTime.now());
            Company updated = companyRepository.save(company);
            return toDto(updated);
        }).orElse(null);
    }

    // Eliminar compañía
    @DeleteMapping("/{id}")
    public void deleteCompany(@PathVariable Long id) {
        companyRepository.deleteById(id);
    }
}
