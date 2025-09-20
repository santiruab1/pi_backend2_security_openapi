package com.example.pib2.models.dtos;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String identification;
    private String email;
    private String firstName;
    private String lastName;
}
