package com.rooster.sistema.service;

import com.rooster.sistema.model.Cliente;
import com.rooster.sistema.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository repository;

    public List<Cliente> findAll() {
        return repository.findAll();
    }

    public Cliente findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Cliente save(Cliente cliente) {
        return repository.save(cliente);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Cliente findByCpfcnpj(String cpfcnpj) {
        return repository.findByCpfcnpj(cpfcnpj);
    }

    public Cliente update(Long id, Cliente cliente) {
        Cliente clienteAtualizado = repository.findById(id).orElseThrow(() -> new RuntimeException("Cliente não encontrado com o id: " + id));

        clienteAtualizado.setNome(cliente.getNome());
        clienteAtualizado.setCpfcnpj(cliente.getCpfcnpj());
        clienteAtualizado.setEmail(cliente.getEmail());
        clienteAtualizado.setContato(cliente.getContato());
        clienteAtualizado.setDataNascimento(cliente.getDataNascimento());
        return repository.save(clienteAtualizado);
    }
}