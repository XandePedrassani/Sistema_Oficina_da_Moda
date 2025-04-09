package com.rooster.sistema.controller;

import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.service.ServicoProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servico-produtos")
public class ServicoProdutoController {
    @Autowired
    private ServicoProdutoService service;

    @GetMapping
    public List<ServicoProduto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ServicoProduto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ServicoProduto save(@RequestBody ServicoProduto servicoProduto) {
        return service.save(servicoProduto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}