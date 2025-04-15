package com.rooster.sistema.repository;

import com.rooster.sistema.dto.ServicoProdutoDTO;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.model.ServicoProdutoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ServicoProdutoRepository extends JpaRepository<ServicoProduto, ServicoProdutoId> {
    Optional<ServicoProduto> findByServicoAndId_Sequencia(Servico servico, Integer sequencia);

    void deleteAllByServico(Servico servico);
    void deleteAllById_idServico(Long idServico);
}