package com.rooster.sistema.repository;

import com.rooster.sistema.model.Servico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    @Query("SELECT DISTINCT s FROM Servico s LEFT JOIN FETCH s.servicoProdutos ORDER BY s.dtEntrega")
    List<Servico> findAllWithProdutos();
    @EntityGraph(attributePaths = {"servicoProdutos", "cliente", "usuario", "status"})
    Page<Servico> findAll(Pageable pageable);

    Page<Servico> findByStatusId(Long statusId, Pageable pageable);

    Page<Servico> findByDtEntrega(LocalDate dataEntrega, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Servico s WHERE " +
            "LOWER(s.cliente.nome) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "CAST(s.id AS string) LIKE CONCAT('%', :texto, '%') OR " +
            "LOWER(s.observacao) LIKE LOWER(CONCAT('%', :texto, '%'))")
    Page<Servico> findByTextoContaining(@Param("texto") String texto, Pageable pageable);

    Page<Servico> findByStatusIdAndDtEntrega(Long statusId, LocalDate dataEntrega, Pageable pageable);

    @Query("SELECT DISTINCT s FROM Servico s WHERE " +
            "s.status.id = :statusId AND (" +
            "LOWER(s.cliente.nome) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "CAST(s.id AS string) LIKE CONCAT('%', :texto, '%') OR " +
            "LOWER(s.observacao) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Servico> findByStatusIdAndTextoContaining(
            @Param("statusId") Long statusId,
            @Param("texto") String texto,
            Pageable pageable);

    @Query("SELECT DISTINCT s FROM Servico s WHERE " +
            "s.dtEntrega = :dataEntrega AND (" +
            "LOWER(s.cliente.nome) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "CAST(s.id AS string) LIKE CONCAT('%', :texto, '%') OR " +
            "LOWER(s.observacao) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Servico> findByDtEntregaAndTextoContaining(
            @Param("dataEntrega") LocalDate dataEntrega,
            @Param("texto") String texto,
            Pageable pageable);

    @Query("SELECT DISTINCT s FROM Servico s WHERE " +
            "s.status.id = :statusId AND " +
            "s.dtEntrega = :dataEntrega AND (" +
            "LOWER(s.cliente.nome) LIKE LOWER(CONCAT('%', :texto, '%')) OR " +
            "CAST(s.id AS string) LIKE CONCAT('%', :texto, '%') OR " +
            "LOWER(s.observacao) LIKE LOWER(CONCAT('%', :texto, '%')))")
    Page<Servico> findByStatusIdAndDtEntregaAndTextoContaining(
            @Param("statusId") Long statusId,
            @Param("dataEntrega") LocalDate dataEntrega,
            @Param("texto") String texto,
            Pageable pageable);
}
