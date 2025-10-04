package com.example.pib2.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Importa la entidad Warehouse para que el repositorio sepa qué manejar
import com.example.pib2.models.entities.Warehouse;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}