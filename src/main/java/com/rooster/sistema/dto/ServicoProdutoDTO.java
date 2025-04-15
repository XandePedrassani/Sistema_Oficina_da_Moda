package com.rooster.sistema.dto;

import com.rooster.sistema.model.Produto;

import java.math.BigDecimal;

public record ServicoProdutoDTO(
        Produto produto,
        Integer quantidade,
        BigDecimal precoUnitario,
        String observacao,
        Integer sequencia
) {}