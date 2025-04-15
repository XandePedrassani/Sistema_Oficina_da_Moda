package com.rooster.sistema.repository;

import com.rooster.sistema.model.Servico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {
    @Query("SELECT s FROM Servico s LEFT JOIN FETCH s.produtos p LEFT JOIN FETCH p.produto")
    List<Servico> findAllWithProdutos();
}