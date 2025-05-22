package com.rooster.sistema.service;

import com.rooster.sistema.dto.StatusDTO;
import com.rooster.sistema.model.Status;
import com.rooster.sistema.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {
    
    private final StatusRepository statusRepository;
    
    public List<StatusDTO> findAll() {
        return statusRepository.findAllByOrderByOrdemAsc()
                .stream()
                .map(StatusDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public StatusDTO findById(Long id) {
        return statusRepository.findById(id)
                .map(StatusDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));
    }
    
    public Optional<Status> findByNome(String nome) {
        return statusRepository.findByNome(nome);
    }
    
    public StatusDTO save(StatusDTO statusDTO) {
        Status status = statusDTO.toEntity();
        status = statusRepository.save(status);
        return StatusDTO.fromEntity(status);
    }
    
    public void delete(Long id) {
        statusRepository.deleteById(id);
    }
    
    public Status getDefaultStatus() {
        return statusRepository.findByNome("pendente")
                .orElseGet(() -> {
                    Status defaultStatus = new Status();
                    defaultStatus.setNome("pendente");
                    defaultStatus.setOrdem(1);
                    defaultStatus.setCor("#FFA500");
                    return statusRepository.save(defaultStatus);
                });
    }
}
