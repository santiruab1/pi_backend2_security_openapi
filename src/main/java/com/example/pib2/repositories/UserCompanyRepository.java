package com.example.pib2.repositories;

import com.example.pib2.models.entities.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompany, Long> {
}
