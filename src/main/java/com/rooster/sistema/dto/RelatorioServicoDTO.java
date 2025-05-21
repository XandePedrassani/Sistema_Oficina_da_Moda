package com.rooster.sistema.dto;

import com.rooster.sistema.model.Cliente;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioServicoDTO {
    private Long id;
    private LocalDate dtMovimento;
    private LocalDate dtEntrega;
    private String observacao;
    private Cliente cliente;
    private Usuario usuario;
    private String status;
    private List<ServicoProdutoDTO> produtos;
    private Double valorTotal;

    public static RelatorioServicoDTO fromServico(Servico servico) {
        RelatorioServicoDTO dto = new RelatorioServicoDTO();
        dto.setId(servico.getId());
        dto.setDtMovimento(servico.getDtMovimento());
        dto.setDtEntrega(servico.getDtEntrega());
        dto.setObservacao(servico.getObservacao());
        dto.setCliente(servico.getCliente());
        dto.setUsuario(servico.getUsuario());
        dto.setStatus(servico.getStatus());
        
        List<ServicoProdutoDTO> produtosDTO = servico.getProdutos().stream()
                .map(sp -> new ServicoProdutoDTO(
                        sp.getProduto(),
                        sp.getQuantidade(),
                        sp.getPrecoUnitario(),
                        sp.getObservacao(),
                        sp.getId().getSequencia()
                )).toList();
        
        dto.setProdutos(produtosDTO);
        
        // Calcula o valor total do serviço
        double valorTotal = servico.getProdutos().stream()
                .map(sp -> BigDecimal.valueOf(sp.getQuantidade()).multiply(sp.getPrecoUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        dto.setValorTotal(valorTotal);
        return dto;
    }
}
