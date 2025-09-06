package com.example.pib2.controllers;

import com.example.pib2.models.dtos.LoanDTO;
import com.example.pib2.models.entities.Loan;
import com.example.pib2.models.entities.Item;
import com.example.pib2.models.entities.User;
import com.example.pib2.servicios.LoanService;
import com.example.pib2.servicios.ItemService;
import com.example.pib2.servicios.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/loans")

public class LoanController {
    @Autowired
    private LoanService loanService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private UserService userService;

    private LoanDTO toDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setId(loan.getId());
        dto.setItemId(loan.getItem() != null ? loan.getItem().getId() : null);
        dto.setUserId(loan.getUser() != null ? loan.getUser().getId() : null);
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setReturned(loan.isReturned());
        return dto;
    }

    private Loan toEntity(LoanDTO dto) {
        Loan loan = new Loan();
        loan.setId(dto.getId());
        if (dto.getItemId() != null) {
            Optional<Item> item = itemService.findById(dto.getItemId());
            item.ifPresent(loan::setItem);
        }
        if (dto.getUserId() != null) {
            Optional<User> user = userService.findById(dto.getUserId());
            user.ifPresent(loan::setUser);
        }
        loan.setLoanDate(dto.getLoanDate());
        loan.setReturnDate(dto.getReturnDate());
        loan.setReturned(dto.isReturned());
        return loan;
    }



    @GetMapping
    public List<LoanDTO> getAll() {
        return loanService.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }



    @GetMapping("/{id}")
    public ResponseEntity<LoanDTO> getById(@PathVariable Long id) {
        return loanService.findById(id)
                .map(loan -> ResponseEntity.ok(toDTO(loan)))
                .orElse(ResponseEntity.notFound().build());
    }



    @PostMapping
    public LoanDTO create(@RequestBody LoanDTO loanDTO) {
        Loan loan = toEntity(loanDTO);
        return toDTO(loanService.save(loan));
    }



    @PutMapping("/{id}")
    public ResponseEntity<LoanDTO> update(@PathVariable Long id, @RequestBody LoanDTO loanDTO) {
        return loanService.findById(id)
                .map(existing -> {
                    loanDTO.setId(id);
                    Loan updated = toEntity(loanDTO);
                    return ResponseEntity.ok(toDTO(loanService.save(updated)));
                })
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (loanService.findById(id).isPresent()) {
            loanService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
