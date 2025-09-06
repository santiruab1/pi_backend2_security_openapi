package com.example.pib2.controllers;

import com.example.pib2.models.dtos.LoanHistoryDTO;
import com.example.pib2.models.entities.LoanHistory;
import com.example.pib2.models.entities.Loan;
import com.example.pib2.servicios.LoanHistoryService;
import com.example.pib2.servicios.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@RestController
@RequestMapping("/api/loanhistories")

public class LoanHistoryController {
    @Autowired
    private LoanHistoryService loanHistoryService;
    @Autowired
    private LoanService loanService;

    private LoanHistoryDTO toDTO(LoanHistory history) {
        LoanHistoryDTO dto = new LoanHistoryDTO();
        dto.setId(history.getId());
        dto.setLoanId(history.getLoan() != null ? history.getLoan().getId() : null);
        dto.setActionDate(history.getActionDate());
        dto.setAction(history.getAction());
        return dto;
    }

    private LoanHistory toEntity(LoanHistoryDTO dto) {
        LoanHistory history = new LoanHistory();
        history.setId(dto.getId());
        if (dto.getLoanId() != null) {
            Optional<Loan> loan = loanService.findById(dto.getLoanId());
            loan.ifPresent(history::setLoan);
        }
        history.setActionDate(dto.getActionDate());
        history.setAction(dto.getAction());
        return history;
    }



    @GetMapping
    public List<LoanHistoryDTO> getAll() {
        return loanHistoryService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }



    @GetMapping("/{id}")
    public ResponseEntity<LoanHistoryDTO> getById(@PathVariable Long id) {
        return loanHistoryService.findById(id)
                .map(history -> ResponseEntity.ok(toDTO(history)))
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    public LoanHistoryDTO create(@RequestBody LoanHistoryDTO loanHistoryDTO) {
        LoanHistory history = toEntity(loanHistoryDTO);
        return toDTO(loanHistoryService.save(history));
    }



    @PutMapping("/{id}")
    public ResponseEntity<LoanHistoryDTO> update(@PathVariable Long id, @RequestBody LoanHistoryDTO loanHistoryDTO) {
        return loanHistoryService.findById(id)
                .map(existing -> {
                    loanHistoryDTO.setId(id);
                    LoanHistory updated = toEntity(loanHistoryDTO);
                    return ResponseEntity.ok(toDTO(loanHistoryService.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loanHistoryService.findById(id).isPresent()) {
            loanHistoryService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
