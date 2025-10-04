package com.example.pib2.models.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_companies")
@Data
public class UserCompany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

}