package com.example.pib2.repositories;

import com.example.pib2.models.entities.LoanHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoanHistoryRepository extends JpaRepository<LoanHistory, Long> {
}
