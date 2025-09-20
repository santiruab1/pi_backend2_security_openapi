package com.example.pib2.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Table(name = "companies")
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name", nullable = false, length = 120)
    private String name;

    @Column(name = "company_identification_number", nullable = false, unique = true, length = 20)
    private String identificationNumber;

    @Column(name = "company_address", nullable = false, length = 200)
    private String address;

    @Column(name = "company_phone", nullable = false, length = 15)
    private String phone;

    @Column(name = "company_email", nullable = false, length = 100)
    private String email;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCompany> UserCompanies;

}
