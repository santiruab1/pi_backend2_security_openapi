package com.example.pib2.models.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "companies")
@Data

public class Company {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    private String address;

    @OneToMany(mappedBy = "company")
    private Set<UserCompany> userCompanies = new HashSet<>();
}