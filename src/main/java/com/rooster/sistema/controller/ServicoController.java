package com.rooster.sistema.controller;

import com.rooster.sistema.dto.ServicoRequestDTO;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {
    @Autowired
    private ServicoService service;

    @GetMapping
    public List<Servico> findAll() {
        return service.findAll();
    }

    @GetMapping("/withProdutos")
    public ResponseEntity<List<ServicoRequestDTO>> findAllWithProdutos() {
        return ResponseEntity.ok(service.findAllWithProdutos());
    }
    @GetMapping("/{id}")
    public Servico findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> createServicoWithProdutos(@RequestBody ServicoRequestDTO dto) {
        try {
            Servico servico = service.saveWithProdutos(dto);
            return ResponseEntity.ok(servico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}