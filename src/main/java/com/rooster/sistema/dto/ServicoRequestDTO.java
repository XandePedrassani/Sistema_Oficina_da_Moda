package com.rooster.sistema.dto;

import java.time.LocalDate;
import java.util.List;


public record ServicoRequestDTO(
    LocalDate dtMovimento,
    LocalDate dtEntrega,
    String observacao,
    Long idCliente,
    Long idUsuario,
    String status,
    List<ServicoProdutoDTO> produtos)
{}