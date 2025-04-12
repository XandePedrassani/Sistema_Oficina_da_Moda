package com.rooster.sistema.service;

import com.rooster.sistema.model.Produto;
import com.rooster.sistema.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository repository;
    @Autowired
    private LargeObjectService largeObjectService;

    @Autowired
    private DataSource dataSource;
    public List<Produto> findAll() throws SQLException {
        List<Produto> produtos = repository.findAll();
        Connection conn = dataSource.getConnection();
        boolean previousAutoCommit = conn.getAutoCommit();
        try {
            conn.setAutoCommit(false);
            for (Produto produto : produtos) {
                if (produto.getFotoOid() != null) {
                    byte[] foto = largeObjectService.readLargeObject(conn, produto.getFotoOid());
                    produto.setFotoData(foto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar imagens dos produtos", e);
        }finally {
            conn.setAutoCommit(previousAutoCommit);
        }

        return produtos;
    }

    public Produto findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Produto save(Produto produto) throws SQLException {
        Connection conn = dataSource.getConnection();
        boolean previousAutoCommit = conn.getAutoCommit();
        try{
            conn.setAutoCommit(false);

            if (produto.getFotoData() != null && produto.getFotoData().length > 0) {
                if (produto.getFotoOid() != null) {
                    largeObjectService.deleteLargeObject(conn, produto.getFotoOid());
                }
                Long oid = largeObjectService.saveLargeObject(conn, produto.getFotoData());
                produto.setFotoOid(oid);
                conn.commit();
            }else {
                produto.setFotoOid(null);
            }
            Produto saved = repository.save(produto);
            conn.commit();
            return saved;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }
    public void delete(Long id) {
        Produto produto = findById(id);
        if (produto.getFotoOid() != null) {
            try (Connection conn = dataSource.getConnection()) {
                largeObjectService.deleteLargeObject(conn, produto.getFotoOid());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        repository.deleteById(id);
    }
}