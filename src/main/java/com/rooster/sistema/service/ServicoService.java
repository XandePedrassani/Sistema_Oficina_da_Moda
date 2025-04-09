package com.rooster.sistema.service;

import com.rooster.sistema.dto.ServicoRequestDTO;
import com.rooster.sistema.model.*;
import com.rooster.sistema.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoService {
    private final ServicoRepository servicoRepository;

    private final ServicoProdutoRepository servicoProdutoRepository;

    private final ClienteRepository clienteRepository;

    private final UsuarioRepository usuarioRepository;

    private final ProdutoRepository produtoRepository;

    public List<Servico> findAll() {
        return servicoRepository.findAll();
    }

    public Servico findById(Long id) {
        return servicoRepository.findById(id).orElse(null);
    }


    public void delete(Long id) {
        servicoRepository.deleteById(id);
    }

    @Transactional
    public Servico saveWithProdutos(ServicoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.idCliente())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Servico servico = new Servico();
        servico.setDtMovimento(dto.dtMovimento());
        servico.setDtEntrega(dto.dtEntrega());
        servico.setObservacao(dto.observacao());
        servico.setCliente(cliente);
        servico.setUsuario(usuario);
        servico.setStatus(dto.status());
        servicoRepository.save(servico);

        List<ServicoProduto> produtos = dto.produtos().stream()
                .map(p -> {
                    Produto produto = produtoRepository.findById(p.idProduto())
                            .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + p.idProduto()));

                    ServicoProduto sp = new ServicoProduto();
                    sp.setServico(servico);
                    sp.setProduto(produto);
                    sp.setQuantidade(p.quantidade());
                    sp.setPrecoUnitario(p.precoUnitario());
                    sp.setObservacao(p.observacao());
                    sp.setSequencia(p.sequencia());
                    return sp;
                })
                .toList();

        servicoProdutoRepository.saveAll(produtos);

        return servico;
    }
}