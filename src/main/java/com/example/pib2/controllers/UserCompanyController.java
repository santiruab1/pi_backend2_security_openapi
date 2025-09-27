package com.example.pib2.controllers;

import com.example.pib2.models.entities.UserCompany;
import com.example.pib2.models.dtos.UserCompanyDto;
import com.example.pib2.repositories.UserCompanyRepository;
import com.example.pib2.repositories.UserRepository;
import com.example.pib2.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user-companies")
public class UserCompanyController {

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

    // Obtener todas las relaciones usuario-compañía
    @GetMapping
    public List<UserCompanyDto> getAllUserCompanies() {
        return userCompanyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Obtener relación por ID
    @GetMapping("/{id}")
    public UserCompanyDto getUserCompanyById(@PathVariable Long id) {
        return userCompanyRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    // Crear nueva relación usuario-compañía
    @PostMapping
    public UserCompanyDto createUserCompany(@RequestBody UserCompanyDto dto) {
        UserCompany userCompany = new UserCompany();
        userCompany.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        userCompany.setCompany(companyRepository.findById(dto.getCompanyId()).orElse(null));
        UserCompany saved = userCompanyRepository.save(userCompany);
        return toDto(saved);
    }

    // Eliminar relación usuario-compañía
    @DeleteMapping("/{id}")
    public void deleteUserCompany(@PathVariable Long id) {
        userCompanyRepository.deleteById(id);
    }
}
