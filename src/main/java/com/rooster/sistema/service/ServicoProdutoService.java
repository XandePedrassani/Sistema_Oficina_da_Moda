package com.rooster.sistema.service;

import com.rooster.sistema.dto.ServicoProdutoDTO;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.repository.ServicoProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ServicoProdutoService {
    @Autowired
    private ServicoProdutoRepository repository;

    public List<ServicoProduto> findAll() {
        return repository.findAll();
    }

    public ServicoProduto findById(Servico servico, Integer sequencia) {
        return repository.findByServicoAndId_Sequencia(servico, sequencia)
                .orElseThrow(() -> new RuntimeException(
                        "ServicoProduto não encontrado para id_servico: " + servico +
                                " e sequencia: " + sequencia
                ));
    }

    public ServicoProduto save(ServicoProduto servicoProduto) {
        if (servicoProduto.getId().getSequencia() != null && servicoProduto.getServico() != null) {
            Optional<ServicoProduto> existente = repository.findByServicoAndId_Sequencia(
                    servicoProduto.getServico(),
                    servicoProduto.getId().getSequencia()
            );
            if (existente.isPresent() && !existente.get().equals(servicoProduto)) {
                throw new RuntimeException(
                        "Já existe um ServicoProduto com esta sequência para o serviço informado"
                );
            }
        }
        return repository.save(servicoProduto);
    }
    public void delete(Servico servico, Integer sequencia) {
        ServicoProduto servicoProduto = findById(servico, sequencia);
        repository.delete(servicoProduto);
    }

}