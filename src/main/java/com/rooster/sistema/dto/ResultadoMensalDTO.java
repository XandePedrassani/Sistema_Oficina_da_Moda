package com.rooster.sistema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoMensalDTO {
    private Integer ano;
    private Integer mes;
    private Double faturamentoTotal;
    private Map<String, Integer> servicosPorStatus;
    private Map<String, Double> faturamentoPorStatus; // Novo campo para totalizadores por status
    private List<FaturamentoPeriodoDTO> faturamentoPorSemana;
    private List<ProdutoEstatisticaDTO> produtosMaisUtilizados;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FaturamentoPeriodoDTO {
        private String periodo;
        private Double valor;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProdutoEstatisticaDTO {
        private Long idProduto;
        private String nomeProduto;
        private Integer quantidade; // Garantindo que é Integer e não Double
        private Double valorTotal;
    }
}
