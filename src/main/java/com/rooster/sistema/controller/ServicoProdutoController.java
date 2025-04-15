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

    // Novo endpoint para buscar por chave composta
   /* @GetMapping("/{idServico}/{sequencia}")
    public ServicoProduto findById(
            @PathVariable("idServico") Long idServico,
            @PathVariable("sequencia") Integer sequencia) {
        return service.findById(idServico, sequencia);
    }*/

    @PostMapping
    public ServicoProduto save(@RequestBody ServicoProduto servicoProduto) {
        return service.save(servicoProduto);
    }

    // Novo endpoint para deletar por chave composta
    /*@DeleteMapping("/{idServico}/{sequencia}")
    public void delete(
            @PathVariable("idServico") Long idServico,
            @PathVariable("sequencia") Integer sequencia) {
        service.delete(idServico, sequencia);
    }*/
}