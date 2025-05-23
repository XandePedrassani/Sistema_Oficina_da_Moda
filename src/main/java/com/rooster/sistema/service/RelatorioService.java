package com.rooster.sistema.service;

import com.rooster.sistema.dto.RelatorioServicoDTO;
import com.rooster.sistema.dto.ResultadoMensalDTO;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.model.Status;
import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.repository.ServicoRepository;
import com.rooster.sistema.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ServicoRepository servicoRepository;

    public List<RelatorioServicoDTO> getRelatorioServicos(LocalDate dataInicio, LocalDate dataFim, String status, Long idCliente) {
        List<Servico> servicos = servicoRepository.findAllWithProdutos();
        
        // Filtrar por data
        servicos = servicos.stream()
                .filter(s -> !s.getDtMovimento().isBefore(dataInicio) && !s.getDtMovimento().isAfter(dataFim))
                .collect(Collectors.toList());

        if (status != null && !status.isEmpty()) {
            servicos = servicos.stream()
                    .filter(s -> (s.getStatus() != null && s.getStatus().getNome().equalsIgnoreCase(status)) ||
                                 (s.getStatus() == null && s.getStatus() != null && s.getStatus().getNome().equalsIgnoreCase(status)))
                    .collect(Collectors.toList());
        }

        if (idCliente != null) {
            servicos = servicos.stream()
                    .filter(s -> s.getCliente().getId().equals(idCliente))
                    .collect(Collectors.toList());
        }
        
        // Converter para DTOs com cálculo de valor total
        return servicos.stream()
                .map(RelatorioServicoDTO::fromServico)
                .collect(Collectors.toList());
    }

    public ResultadoMensalDTO getResultadosMensais(Integer ano, Integer mes) {
        // Determinar o primeiro e último dia do mês
        LocalDate primeiroDia = LocalDate.of(ano, mes, 1);
        LocalDate ultimoDia = primeiroDia.withDayOfMonth(primeiroDia.lengthOfMonth());
        
        // Buscar todos os serviços do mês
        List<Servico> servicos = servicoRepository.findAllWithProdutos().stream()
                .filter(s -> !s.getDtMovimento().isBefore(primeiroDia) && !s.getDtMovimento().isAfter(ultimoDia))
                .collect(Collectors.toList());
        
        // Calcular faturamento total
        double faturamentoTotal = servicos.stream()
                .flatMap(s -> s.getServicoProdutos().stream())
                .mapToDouble(sp -> {
                    // Conversão correta para multiplicação
                    BigDecimal quantidade = new BigDecimal(sp.getQuantidade());
                    return quantidade.multiply(sp.getPrecoUnitario()).doubleValue();
                })
                .sum();
        
        // Contar serviços por status (adaptado para status dinâmico)
        Map<String, Integer> servicosPorStatus = new HashMap<>();
        for (Servico servico : servicos) {
            String statusNome = getStatusNome(servico);
            servicosPorStatus.put(statusNome, servicosPorStatus.getOrDefault(statusNome, 0) + 1);
        }
        
        // Calcular faturamento por status (adaptado para status dinâmico)
        Map<String, Double> faturamentoPorStatus = new HashMap<>();
        for (Servico servico : servicos) {
            String statusNome = getStatusNome(servico);
            double valorServico = servico.getServicoProdutos().stream()
                    .mapToDouble(sp -> {
                        // Conversão correta para multiplicação
                        BigDecimal quantidade = new BigDecimal(sp.getQuantidade());
                        return quantidade.multiply(sp.getPrecoUnitario()).doubleValue();
                    })
                    .sum();
            
            faturamentoPorStatus.put(statusNome, 
                    faturamentoPorStatus.getOrDefault(statusNome, 0.0) + valorServico);
        }
        
        // Calcular faturamento por semana
        List<ResultadoMensalDTO.FaturamentoPeriodoDTO> faturamentoPorSemana = calcularFaturamentoPorSemana(servicos);
        
        // Calcular estatísticas de produtos
        List<ResultadoMensalDTO.ProdutoEstatisticaDTO> produtosMaisUtilizados = getProdutosEstatisticas(primeiroDia, ultimoDia);
        
        // Criar e retornar o DTO de resultado mensal
        ResultadoMensalDTO resultado = new ResultadoMensalDTO();
        resultado.setAno(ano);
        resultado.setMes(mes);
        resultado.setFaturamentoTotal(faturamentoTotal);
        resultado.setServicosPorStatus(servicosPorStatus);
        resultado.setFaturamentoPorStatus(faturamentoPorStatus);
        resultado.setFaturamentoPorSemana(faturamentoPorSemana);
        resultado.setProdutosMaisUtilizados(produtosMaisUtilizados);
        
        return resultado;
    }

    // Método auxiliar para obter o nome do status (compatível com ambos os modelos durante migração)
    private String getStatusNome(Servico servico) {
        if (servico.getStatus() != null) {
            return servico.getStatus().getNome();
        } else {
            return "pendente"; // valor padrão
        }
    }

    public List<ResultadoMensalDTO.ProdutoEstatisticaDTO> getProdutosEstatisticas(LocalDate dataInicio, LocalDate dataFim) {
        // Buscar todos os serviços do período
        List<Servico> servicos = servicoRepository.findAllWithProdutos().stream()
                .filter(s -> !s.getDtMovimento().isBefore(dataInicio) && !s.getDtMovimento().isAfter(dataFim))
                .collect(Collectors.toList());
        
        // Agrupar produtos por ID e calcular quantidade e valor total
        Map<Long, ResultadoMensalDTO.ProdutoEstatisticaDTO> produtosMap = new HashMap<>();
        
        for (Servico servico : servicos) {
            for (ServicoProduto sp : servico.getServicoProdutos()) {
                Long idProduto = sp.getProduto().getId();
                String nomeProduto = sp.getProduto().getNome();
                int quantidade = sp.getQuantidade(); // Garantindo que é Integer
                
                // Conversão correta para multiplicação
                BigDecimal qtdBigDecimal = new BigDecimal(quantidade);
                double valorTotal = qtdBigDecimal.multiply(sp.getPrecoUnitario()).doubleValue();
                
                if (produtosMap.containsKey(idProduto)) {
                    ResultadoMensalDTO.ProdutoEstatisticaDTO estatistica = produtosMap.get(idProduto);
                    estatistica.setQuantidade(estatistica.getQuantidade() + quantidade);
                    estatistica.setValorTotal(estatistica.getValorTotal() + valorTotal);
                } else {
                    produtosMap.put(idProduto, new ResultadoMensalDTO.ProdutoEstatisticaDTO(
                            idProduto, nomeProduto, quantidade, valorTotal));
                }
            }
        }
        
        // Ordenar por quantidade (decrescente) e limitar aos 10 mais utilizados
        return produtosMap.values().stream()
                .sorted(Comparator.comparing(ResultadoMensalDTO.ProdutoEstatisticaDTO::getQuantidade).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }
    
    private List<ResultadoMensalDTO.FaturamentoPeriodoDTO> calcularFaturamentoPorSemana(List<Servico> servicos) {
        // Agrupar serviços por semana do mês
        Map<Integer, Double> faturamentoPorSemana = new HashMap<>();
        
        for (Servico servico : servicos) {
            int semana = servico.getDtMovimento().get(WeekFields.of(Locale.getDefault()).weekOfMonth());
            
            double valorServico = servico.getServicoProdutos().stream()
                    .mapToDouble(sp -> {
                        // Conversão correta para multiplicação
                        BigDecimal quantidade = new BigDecimal(sp.getQuantidade());
                        return quantidade.multiply(sp.getPrecoUnitario()).doubleValue();
                    })
                    .sum();
            
            faturamentoPorSemana.put(semana, faturamentoPorSemana.getOrDefault(semana, 0.0) + valorServico);
        }
        
        // Converter para lista de DTOs
        List<ResultadoMensalDTO.FaturamentoPeriodoDTO> resultado = new ArrayList<>();
        for (Map.Entry<Integer, Double> entry : faturamentoPorSemana.entrySet()) {
            resultado.add(new ResultadoMensalDTO.FaturamentoPeriodoDTO(
                    "Semana " + entry.getKey(), entry.getValue()));
        }
        
        // Ordenar por número da semana
        resultado.sort(Comparator.comparing(dto -> Integer.parseInt(dto.getPeriodo().split(" ")[1])));
        
        return resultado;
    }
}
