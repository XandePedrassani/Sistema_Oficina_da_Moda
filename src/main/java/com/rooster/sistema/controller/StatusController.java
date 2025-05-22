package com.rooster.sistema.controller;

import com.rooster.sistema.dto.StatusDTO;
import com.rooster.sistema.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {
    
    private final StatusService statusService;
    
    @GetMapping
    public ResponseEntity<List<StatusDTO>> findAll() {
        return ResponseEntity.ok(statusService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StatusDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(statusService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<StatusDTO> create(@RequestBody StatusDTO statusDTO) {
        return ResponseEntity.ok(statusService.save(statusDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StatusDTO> update(@PathVariable Long id, @RequestBody StatusDTO statusDTO) {
        if (!id.equals(statusDTO.id())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(statusService.save(statusDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        statusService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
