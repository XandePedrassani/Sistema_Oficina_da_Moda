package com.rooster.sistema.controller;

import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.service.ServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/impressao")
public class ImpressaoController {

    @Autowired
    private ServicoService servicoService;

    @PostMapping("/{idServico}")
    public ResponseEntity<String> imprimirServico(@PathVariable Long idServico) {
        Servico servico = servicoService.findById(idServico);
        if (servico == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            // Procurar a impressora pela descrição exata
            String printerName = "EPSON TM-T20X";
            PrintService selectedPrinter = null;

            for (PrintService ps : PrintServiceLookup.lookupPrintServices(null, null)) {
                if (ps.getName().equalsIgnoreCase(printerName)) {
                    selectedPrinter = ps;
                    break;
                }
            }

            if (selectedPrinter == null) {
                return ResponseEntity.badRequest().body("Impressora '" + printerName + "' não encontrada.");
            }

            // Estilo e formatação
            EscPos escpos = new EscPos(new PrinterOutputStream(selectedPrinter));
            escpos.setCharacterCodeTable(EscPos.CharacterCodeTable.CP860_Portuguese);
            Style titleStyle = new Style().setBold(true).setFontSize(Style.FontSize._2, Style.FontSize._2);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Cabeçalho
            escpos.writeLF(titleStyle, "Oficina da Moda");
            escpos.writeLF("Serviço nº: " + servico.getId());
            escpos.writeLF("Data: " + servico.getDtMovimento().format(formatter));
            escpos.writeLF("Entrega: " + servico.getDtEntrega().format(formatter));
            escpos.writeLF("------------------------------------------");

            // Cliente
            escpos.writeLF("Cliente: " + servico.getCliente().getNome());
            escpos.writeLF("Contato: " + servico.getCliente().getContato());
            escpos.writeLF("------------------------------------------");

            // Produtos
            BigDecimal total = BigDecimal.ZERO;
            for (ServicoProduto sp : servico.getServicoProdutos()) {
                BigDecimal subtotal = sp.getPrecoUnitario().multiply(BigDecimal.valueOf(sp.getQuantidade()));
                total = total.add(subtotal);

                escpos.writeLF(sp.getProduto().getNome());
                escpos.writeLF("  " + sp.getQuantidade() + " x R$ " + sp.getPrecoUnitario() + " = R$ " + subtotal);

                if (sp.getObservacao() != null && !sp.getObservacao().isEmpty()) {
                    escpos.writeLF("  Obs: " + sp.getObservacao());
                }
            }

            // Total e status
            escpos.writeLF("------------------------------------------");
            escpos.writeLF("Total: R$ " + total);
            escpos.writeLF("Status: " + servico.getStatus().getNome());

            // Observação geral
            if (servico.getObservacao() != null && !servico.getObservacao().isEmpty()) {
                escpos.writeLF("------------------------------------------");
                escpos.writeLF("Obs. geral: " + servico.getObservacao());
            }

            // Finaliza impressão
            escpos.feed(3);
            escpos.cut(EscPos.CutMode.FULL);
            escpos.close();

            return ResponseEntity.ok("Serviço impresso com sucesso!");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Erro ao imprimir: " + e.getMessage());
        }
    }
}
