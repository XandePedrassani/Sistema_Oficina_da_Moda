package com.rooster.sistema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class ServicoProdutoId implements Serializable {
    @Column(name = "id_servico")
    private Long idServico;
    private Integer sequencia;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServicoProdutoId that = (ServicoProdutoId) o;
        return Objects.equals(idServico, that.idServico) &&
                Objects.equals(sequencia, that.sequencia);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idServico, sequencia);
    }
}