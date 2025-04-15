package com.rooster.sistema.service;

import com.rooster.sistema.dto.ServicoProdutoDTO;
import com.rooster.sistema.dto.ServicoRequestDTO;
import com.rooster.sistema.model.*;
import com.rooster.sistema.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {
    private final ServicoRepository servicoRepository;

    private final ServicoProdutoRepository servicoProdutoRepository;

    private final ServicoProdutoService servicoProdutoService;

    public List<Servico> findAll() {
        return servicoRepository.findAll();
    }

    public List<ServicoRequestDTO> findAllWithProdutos(){
        List<Servico> servicos = servicoRepository.findAllWithProdutos();

        return servicos.stream().map(this::toDTO).toList();
    }

    private ServicoRequestDTO toDTO(Servico servico) {
        List<ServicoProdutoDTO> produtosDTO = servico.getProdutos().stream()
                .map(sp -> new ServicoProdutoDTO(
                        sp.getProduto(),
                        sp.getQuantidade(),
                        sp.getPrecoUnitario(),
                        sp.getObservacao(),
                        sp.getId().getSequencia()
                )).toList();

        return new ServicoRequestDTO(
                servico.getId(),
                servico.getDtMovimento(),
                servico.getDtEntrega(),
                servico.getObservacao(),
                servico.getCliente(),
                servico.getUsuario(),
                servico.getStatus(),
                produtosDTO
        );
    }


    public Servico findById(Long id) {
        return servicoRepository.findById(id).orElse(null);
    }


    public void delete(Long id) {
        servicoRepository.deleteById(id);
    }

    @Transactional
    public Servico saveWithProdutos(ServicoRequestDTO dto) {

        /*Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));*/

        Servico servico = new Servico();
        servico.setDtMovimento(dto.dtMovimento());
        servico.setDtEntrega(dto.dtEntrega());
        servico.setObservacao(dto.observacao());
        servico.setCliente(dto.cliente());
        servico.setUsuario(dto.usuario());
        servico.setStatus(dto.status());
        servicoRepository.save(servico);

        List<ServicoProduto> produtos = dto.produtos().stream()
                .map(p -> {
                    ServicoProduto sp = new ServicoProduto();
                    sp.setServico(servico);
                    sp.setProduto(p.produto());
                    sp.setQuantidade(p.quantidade());
                    sp.setPrecoUnitario(p.precoUnitario());
                    sp.setObservacao(p.observacao());
                    sp.getId().setSequencia(p.sequencia());
                    return sp;
                })
                .toList();

        servicoProdutoRepository.saveAll(produtos);

        return servico;
    }
}