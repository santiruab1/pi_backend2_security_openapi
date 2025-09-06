package com.example.pib2.servicios;

import com.example.pib2.models.entities.LoanHistory;
import com.example.pib2.repositories.LoanHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoanHistoryService {
    @Autowired
    private LoanHistoryRepository loanHistoryRepository;

    public List<LoanHistory> findAll() {
        return loanHistoryRepository.findAll();
    }

    public Optional<LoanHistory> findById(Long id) {
        return loanHistoryRepository.findById(id);
    }

    public LoanHistory save(LoanHistory loanHistory) {
        return loanHistoryRepository.save(loanHistory);
    }

    public void deleteById(Long id) {
        loanHistoryRepository.deleteById(id);
    }
}
