package com.rooster.sistema.service;

import com.rooster.sistema.dto.RelatorioServicoDTO;
import com.rooster.sistema.dto.ResultadoMensalDTO;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.repository.ServicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelatorioService {

    @Autowired
    private ServicoRepository servicoRepository;

    public List<RelatorioServicoDTO> getRelatorioServicos(LocalDate dataInicio, LocalDate dataFim, String status, Long idCliente) {
        List<Servico> servicos = servicoRepository.findAllWithProdutos();
        
        // Filtrar por data
        servicos = servicos.stream()
                .filter(s -> !s.getDtMovimento().isBefore(dataInicio) && !s.getDtMovimento().isAfter(dataFim))
                .collect(Collectors.toList());
        
        // Filtrar por status se fornecido
        if (status != null && !status.isEmpty()) {
            servicos = servicos.stream()
                    .filter(s -> s.getStatus().equalsIgnoreCase(status))
                    .collect(Collectors.toList());
        }
        
        // Filtrar por cliente se fornecido
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
                .flatMap(s -> s.getProdutos().stream())
                .map(sp -> BigDecimal.valueOf(sp.getQuantidade()).multiply(sp.getPrecoUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        // Contar serviços por status
        Map<String, Integer> servicosPorStatus = servicos.stream()
                .collect(Collectors.groupingBy(
                        Servico::getStatus,
                        Collectors.summingInt(s -> 1)
                ));
        
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
        resultado.setFaturamentoPorSemana(faturamentoPorSemana);
        resultado.setProdutosMaisUtilizados(produtosMaisUtilizados);
        
        return resultado;
    }

    public List<ResultadoMensalDTO.ProdutoEstatisticaDTO> getProdutosEstatisticas(LocalDate dataInicio, LocalDate dataFim) {
        // Buscar todos os serviços do período
        List<Servico> servicos = servicoRepository.findAllWithProdutos().stream()
                .filter(s -> !s.getDtMovimento().isBefore(dataInicio) && !s.getDtMovimento().isAfter(dataFim))
                .collect(Collectors.toList());
        
        // Agrupar produtos por ID e calcular quantidade e valor total
        Map<Long, ResultadoMensalDTO.ProdutoEstatisticaDTO> produtosMap = new HashMap<>();
        
        for (Servico servico : servicos) {
            for (ServicoProduto sp : servico.getProdutos()) {
                Long idProduto = sp.getProduto().getId();
                String nomeProduto = sp.getProduto().getNome() + " - " + sp.getObservacao();
                int quantidade = sp.getQuantidade();
                Double valorTotal = BigDecimal.valueOf(sp.getQuantidade())
                        .multiply(sp.getPrecoUnitario())
                        .doubleValue();


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

            double valorServico = servico.getProdutos().stream()
                    .map(sp -> BigDecimal.valueOf(sp.getQuantidade()).multiply(sp.getPrecoUnitario()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .doubleValue();

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
