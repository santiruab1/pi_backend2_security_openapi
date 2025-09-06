package com.example.pib2.models.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class LoanHistoryDTO {
    private Long id;
    private Long loanId;
    private LocalDateTime actionDate;
    private String action;
}
