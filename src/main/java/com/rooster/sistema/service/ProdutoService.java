package com.rooster.sistema.service;

import com.rooster.sistema.model.Produto;
import com.rooster.sistema.repository.ProdutoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.datasource.DataSourceUtils;

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
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            List<Produto> produtos = repository.findAll();

            for (Produto produto : produtos) {
                if (produto.getFotoOid() != null) {
                    byte[] foto = largeObjectService.readLargeObject(conn, produto.getFotoOid());
                    produto.setFotoData(foto);
                }
            }

            return produtos;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao carregar imagens dos produtos", e);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public Produto findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public Produto save(Produto produto) throws SQLException {
        Connection conn = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);

            if (produto.getFotoData() != null && produto.getFotoData().length > 0) {
                if (produto.getFotoOid() != null) {
                    largeObjectService.deleteLargeObject(conn, produto.getFotoOid());
                }
                Long oid = largeObjectService.saveLargeObject(conn, produto.getFotoData());
                produto.setFotoOid(oid);
            } else {
                produto.setFotoOid(null);
            }

            return repository.save(produto);
        } finally {
            DataSourceUtils.releaseConnection(conn, dataSource);
        }
    }

    public void delete(Long id) {
        Produto produto = findById(id);
        if (produto.getFotoOid() != null) {
            Connection conn = null;
            try {
                conn = DataSourceUtils.getConnection(dataSource);
                largeObjectService.deleteLargeObject(conn, produto.getFotoOid());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                DataSourceUtils.releaseConnection(conn, dataSource);
            }
        }
        repository.deleteById(id);
    }
}
