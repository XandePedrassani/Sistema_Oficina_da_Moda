package com.rooster.sistema.controller;

import com.rooster.sistema.dto.ServicoRequestDTO;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    @Autowired
    private ServicoService service;

    @GetMapping
    public List<Servico> findAll() {
        return service.findAll();
    }

    /**
     * Endpoint original (mantido para compatibilidade)
     */
    @GetMapping("/withProdutos")
    public ResponseEntity<List<ServicoRequestDTO>> findAllWithProdutos() {
        return ResponseEntity.ok(service.findAllWithProdutos());
    }

    /**
     * Novo endpoint com paginação e filtros
     */
    @GetMapping("/paginados")
    public ResponseEntity<Page<ServicoRequestDTO>> findAllWithProdutosPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long statusId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataEntrega,
            @RequestParam(required = false) String textoBusca,
            @RequestParam(defaultValue = "dtEntrega,asc") String[] sort) {
        
        // Criar ordenação a partir dos parâmetros
        List<Sort.Order> orders = new ArrayList<>();
        for (String sortParam : sort) {
            String[] parts = sortParam.split(",");
            orders.add(new Sort.Order(
                parts.length > 1 && parts[1].equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC, 
                parts[0]
            ));
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        return ResponseEntity.ok(service.findAllWithProdutosPaginados(statusId, dataEntrega, textoBusca, pageable));
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

    @PutMapping
    public ResponseEntity<?> updateServicoWithProdutos(@RequestBody ServicoRequestDTO dto) {
        try {
            Servico servico = service.updateWithProdutos(dto);
            return ResponseEntity.ok(servico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> atualizarStatus(@PathVariable Long id, @RequestBody Map<String, Long> payload) {
        try {
            Long statusId = payload.get("statusId");
            if (statusId == null) {
                return ResponseEntity.badRequest().body("statusId é obrigatório");
            }
            Servico servico = service.atualizarStatus(id, statusId);
            return ResponseEntity.ok(servico);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
