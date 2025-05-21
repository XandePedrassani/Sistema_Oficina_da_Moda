package com.rooster.sistema.controller;

import com.rooster.sistema.dto.RelatorioServicoDTO;
import com.rooster.sistema.dto.ResultadoMensalDTO;
import com.rooster.sistema.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/servicos")
    public ResponseEntity<Map<String, Object>> getRelatorioServicos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long idCliente) {
        
        List<RelatorioServicoDTO> servicos = relatorioService.getRelatorioServicos(dataInicio, dataFim, status, idCliente);
        double valorTotal = servicos.stream()
                .map(RelatorioServicoDTO::getValorTotal)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();

        Map<String, Object> response = Map.of(
            "servicos", servicos,
            "totalServicos", servicos.size(),
            "valorTotal", valorTotal
        );
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resultados-mensais")
    public ResponseEntity<ResultadoMensalDTO> getResultadosMensais(
            @RequestParam Integer ano,
            @RequestParam Integer mes) {
        
        ResultadoMensalDTO resultado = relatorioService.getResultadosMensais(ano, mes);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/produtos-estatisticas")
    public ResponseEntity<List<ResultadoMensalDTO.ProdutoEstatisticaDTO>> getProdutosEstatisticas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        
        List<ResultadoMensalDTO.ProdutoEstatisticaDTO> estatisticas = 
            relatorioService.getProdutosEstatisticas(dataInicio, dataFim);
        
        return ResponseEntity.ok(estatisticas);
    }
}
