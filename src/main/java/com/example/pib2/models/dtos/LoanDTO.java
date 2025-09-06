package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDate;

@Data
public class LoanDTO {
    private Long id;
    private Long itemId;
    private Long userId;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private boolean returned;
}
