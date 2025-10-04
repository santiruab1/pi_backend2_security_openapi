package com.example.pib2.controllers;

import com.example.pib2.models.dtos.TransactionCreateDTO;
import com.example.pib2.models.dtos.TransactionDTO;
import com.example.pib2.servicios.TransactionService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @Valid @RequestBody TransactionCreateDTO transactionCreateDTO) {
        
        TransactionDTO newTransaction = transactionService.createTransaction(transactionCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTransaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getTransactionsByWarehouse(
            @RequestParam Long warehouseId) {
        
        List<TransactionDTO> transactions = transactionService.getTransactionsByWarehouseId(warehouseId);
        return ResponseEntity.ok(transactions);
    }
}