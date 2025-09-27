package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DocumentDto {
    private Long id;
    private int documentTypeId;
    private LocalDate documentDate;
    private LocalDate documentReception;
    private String documentPrefix;
    private String documentNumber;
    private LocalDate documentDueDate;
    private Long thirdPartyId;
}
