package com.rooster.sistema.dto;

import com.rooster.sistema.model.Status;

public record StatusDTO(
    Long id,
    String nome,
    Integer ordem,
    String cor
) {
    public static StatusDTO fromEntity(Status status) {
        return new StatusDTO(
            status.getId(),
            status.getNome(),
            status.getOrdem(),
            status.getCor()
        );
    }
    
    public Status toEntity() {
        Status status = new Status();
        status.setId(this.id);
        status.setNome(this.nome);
        status.setOrdem(this.ordem);
        status.setCor(this.cor);
        return status;
    }
}
