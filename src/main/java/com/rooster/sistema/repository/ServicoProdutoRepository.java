package com.rooster.sistema.repository;

import com.rooster.sistema.model.ServicoProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServicoProdutoRepository extends JpaRepository<ServicoProduto, Long> {
}