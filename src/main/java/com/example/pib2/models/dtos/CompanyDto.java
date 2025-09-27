package com.example.pib2.models.dtos;

import lombok.Data;

@Data
public class CompanyDto {
    private Long id;
    private String name;
    private String identificationNumber;
    private String address;
    private String phone;
    private String email;
}
