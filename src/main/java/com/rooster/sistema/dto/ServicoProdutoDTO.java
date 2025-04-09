package com.rooster.sistema.dto;

import java.math.BigDecimal;

public record ServicoProdutoDTO(
        Long idProduto,
        Integer quantidade,
        BigDecimal precoUnitario,
        String observacao,
        Integer sequencia
) {}