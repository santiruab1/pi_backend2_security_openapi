package com.example.pib2.servicios;

import com.example.pib2.models.dtos.TransactionCreateDTO;
import com.example.pib2.models.dtos.TransactionDTO;
import com.example.pib2.models.entities.Transaction;
import com.example.pib2.models.entities.Warehouse;
import com.example.pib2.repositories.TransactionRepository;
import com.example.pib2.repositories.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public TransactionDTO createTransaction(TransactionCreateDTO transactionCreateDTO) {
        Warehouse warehouse = warehouseRepository.findById(transactionCreateDTO.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));

        Transaction transaction = modelMapper.map(transactionCreateDTO, Transaction.class);
        transaction.setWarehouse(warehouse);

        Transaction savedTransaction = transactionRepository.save(transaction);

        return modelMapper.map(savedTransaction, TransactionDTO.class);
    }

    @Transactional
    public List<TransactionDTO> getTransactionsByWarehouseId(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Almacén no encontrado"));

        List<Transaction> transactions = warehouse.getTransactions();

        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

}