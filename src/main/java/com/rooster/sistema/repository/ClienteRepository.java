package com.rooster.sistema.repository;

import com.rooster.sistema.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByCpfcnpj(String cpfcnpj);
}