package com.rooster.sistema.controller;

import com.rooster.sistema.model.Cliente;
import com.rooster.sistema.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    @Autowired
    private ClienteService service;

    @GetMapping
    public List<Cliente> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Cliente findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public Cliente save(@RequestBody Cliente cliente) {
        return service.save(cliente);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/cpfcnpj/{cpfcnpj}")
    public Cliente findByCpfcnpj(@PathVariable String cpfcnpj) {
        return service.findByCpfcnpj(cpfcnpj);
    }
}