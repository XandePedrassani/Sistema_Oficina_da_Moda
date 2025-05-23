package com.rooster.sistema.service;

import com.rooster.sistema.dto.ServicoRequestDTO;
import com.rooster.sistema.dto.ServicoProdutoDTO;
import com.rooster.sistema.model.Servico;
import com.rooster.sistema.model.ServicoProduto;
import com.rooster.sistema.model.ServicoProdutoId;
import com.rooster.sistema.model.Status;
import com.rooster.sistema.repository.ServicoRepository;
import com.rooster.sistema.repository.ServicoProdutoRepository;
import com.rooster.sistema.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoService {
    private final ServicoRepository repository;
    private final ServicoProdutoRepository servicoProdutoRepository;
    private final StatusService statusService;

    public List<Servico> findAll() {
        return repository.findAll();
    }

    public List<ServicoRequestDTO> findAllWithProdutos() {
        List<Servico> servicos = repository.findAllWithProdutos();
        return servicos.stream().map(this::toDTO).toList();
    }

    /**
     * Novo método para buscar serviços com paginação e filtros
     */
    public Page<ServicoRequestDTO> findAllWithProdutosPaginados(
            Long statusId,
            LocalDate dataEntrega,
            String textoBusca,
            Pageable pageable) {

        // Implementar a busca paginada com filtros
        Page<Servico> servicosPage;

        if (statusId != null && dataEntrega != null && textoBusca != null && !textoBusca.isEmpty()) {
            // Filtrar por status, data e texto
            servicosPage = repository.findByStatusIdAndDtEntregaAndTextoContaining(
                    statusId, dataEntrega, textoBusca, pageable);
        } else if (statusId != null && dataEntrega != null) {
            // Filtrar por status e data
            servicosPage = repository.findByStatusIdAndDtEntrega(statusId, dataEntrega, pageable);
        } else if (statusId != null && textoBusca != null && !textoBusca.isEmpty()) {
            // Filtrar por status e texto
            servicosPage = repository.findByStatusIdAndTextoContaining(statusId, textoBusca, pageable);
        } else if (dataEntrega != null && textoBusca != null && !textoBusca.isEmpty()) {
            // Filtrar por data e texto
            servicosPage = repository.findByDtEntregaAndTextoContaining(dataEntrega, textoBusca, pageable);
        } else if (statusId != null) {
            // Filtrar apenas por status
            servicosPage = repository.findByStatusId(statusId, pageable);
        } else if (dataEntrega != null) {
            // Filtrar apenas por data
            servicosPage = repository.findByDtEntrega(dataEntrega, pageable);
        } else if (textoBusca != null && !textoBusca.isEmpty()) {
            // Filtrar apenas por texto
            servicosPage = repository.findByTextoContaining(textoBusca, pageable);
        } else {
            // Sem filtros, retornar todos paginados
            servicosPage = repository.findAll(pageable);
        }

        // Converter para DTOs usando o método auxiliar toDTO
        List<ServicoRequestDTO> dtos = servicosPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, servicosPage.getTotalElements());
    }

    public Servico findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
    }

    public ServicoRequestDTO findDTOById(Long id) {
        Servico servico = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        return toDTO(servico);
    }

    @Transactional
    public Servico saveWithProdutos(ServicoRequestDTO dto) {
        Servico servico = toEntity(dto);
        servico = repository.save(servico);

        Servico finalServico = servico;
        List<ServicoProduto> produtos = dto.produtos().stream()
                .map(p -> {
                    ServicoProduto sp = new ServicoProduto();
                    sp.setServico(finalServico);
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

        Servico servico = repository.findById(dto.id())
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

        repository.save(servico);
        servicoProdutoRepository.saveAll(produtos);

        return servico;
    }

    @Transactional
    public void delete(Long id) {
        servicoProdutoRepository.deleteAllById_idServico(id);
        repository.deleteById(id);
    }

    @Transactional
    public Servico atualizarStatus(Long id, Long statusId) {
        Servico servico = findById(id);

        Status status = statusService.findById(statusId).toEntity();

        servico.setStatus(status);
        return repository.save(servico);
    }

    private ServicoRequestDTO toDTO(Servico servico) {
        List<ServicoProdutoDTO> produtosDTO = servico.getServicoProdutos().stream()
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

    /**
     * Método auxiliar para converter ServicoRequestDTO para Servico
     */
    private Servico toEntity(ServicoRequestDTO dto) {
        Servico servico = new Servico();
        servico.setId(dto.id());
        servico.setCliente(dto.cliente());
        servico.setUsuario(dto.usuario());
        servico.setDtMovimento(dto.dtMovimento());
        servico.setDtEntrega(dto.dtEntrega());
        servico.setObservacao(dto.observacao());
        servico.setStatus(dto.status());
        return servico;
    }
}
