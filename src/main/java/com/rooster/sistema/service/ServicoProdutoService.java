package com.rooster.sistema.service;

import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.repository.ServicoProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicoProdutoService {
    @Autowired
    private ServicoProdutoRepository repository;

    public List<ServicoProduto> findAll() {
        return repository.findAll();
    }

    public ServicoProduto findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public ServicoProduto save(ServicoProduto servicoProduto) {
        return repository.save(servicoProduto);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}