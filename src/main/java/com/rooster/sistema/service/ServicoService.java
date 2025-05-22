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
    private final StatusService statusService;

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

    @Transactional
    public void delete(Long id) {
        servicoProdutoRepository.deleteAllById_idServico(id);
        servicoRepository.deleteById(id);
    }

    @Transactional
    public Servico saveWithProdutos(ServicoRequestDTO dto) {
        Servico servico = new Servico();
        servico.setDtMovimento(dto.dtMovimento());
        servico.setDtEntrega(dto.dtEntrega());
        servico.setObservacao(dto.observacao());
        servico.setCliente(dto.cliente());
        servico.setUsuario(dto.usuario());
        
        // Verifica se o status foi fornecido, caso contrário usa o status padrão
        if (dto.status() != null) {
            servico.setStatus(dto.status());
        } else {
            servico.setStatus(statusService.getDefaultStatus());
        }
        
        servicoRepository.save(servico);

        List<ServicoProduto> produtos = dto.produtos().stream()
                .map(p -> {
                    ServicoProduto sp = new ServicoProduto();
                    sp.setServico(servico);
                    sp.setProduto(p.produto());
                    sp.setQuantidade(p.quantidade());
                    sp.setPrecoUnitario(p.precoUnitario());
                    sp.setObservacao(p.observacao());
                    if (sp.getId() == null) {
                        sp.setId(new ServicoProdutoId());
                    }
                    sp.getId().setSequencia(p.sequencia());
                    return sp;
                })
                .toList();

        servicoProdutoRepository.saveAll(produtos);

        return servico;
    }

    @Transactional
    public Servico updateWithProdutos(ServicoRequestDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("ID do serviço não pode ser nulo para atualização");
        }

        Servico servico = servicoRepository.findById(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));

        servico.setDtMovimento(dto.dtMovimento());
        servico.setDtEntrega(dto.dtEntrega());
        servico.setObservacao(dto.observacao());
        servico.setCliente(dto.cliente());
        servico.setUsuario(dto.usuario());
        
        // Verifica se o status foi fornecido, caso contrário mantém o status atual
        if (dto.status() != null) {
            servico.setStatus(dto.status());
        }

        // Remove produtos antigos
        servicoProdutoRepository.deleteAllByServico(servico);

        // Adiciona os novos produtos
        List<ServicoProduto> produtos = dto.produtos().stream()
                .map(p -> {
                    ServicoProduto sp = new ServicoProduto();
                    sp.setServico(servico);
                    sp.setProduto(p.produto());
                    sp.setQuantidade(p.quantidade());
                    sp.setPrecoUnitario(p.precoUnitario());
                    sp.setObservacao(p.observacao());
                    if (sp.getId() == null) {
                        sp.setId(new ServicoProdutoId());
                    }
                    sp.getId().setSequencia(p.sequencia());
                    return sp;
                })
                .toList();

        servicoRepository.save(servico);
        servicoProdutoRepository.saveAll(produtos);

        return servico;
    }
    
    @Transactional
    public Servico atualizarStatus(Long id, Long statusId) {
        Servico servico = servicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
                
        Status status = statusService.findById(statusId).toEntity();
        servico.setStatus(status);
        
        return servicoRepository.save(servico);
    }
}
