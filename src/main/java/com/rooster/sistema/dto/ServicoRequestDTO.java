package com.rooster.sistema.dto;

import com.rooster.sistema.model.Cliente;
import com.rooster.sistema.model.Status;
import com.rooster.sistema.model.Usuario;

import java.time.LocalDate;
import java.util.List;


public record ServicoRequestDTO(
    Long id,
    LocalDate dtMovimento,
    LocalDate dtEntrega,
    String observacao,
    Cliente cliente,
    Usuario usuario,
    Status status,
    List<ServicoProdutoDTO> produtos)
{

}
