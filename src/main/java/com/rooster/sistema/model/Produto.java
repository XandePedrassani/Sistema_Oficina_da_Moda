package com.rooster.sistema.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "produto")
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal preco;

    @Column(name = "cod_barras", length = 50)
    private String codBarras;

    @Column(precision = 10, scale = 2)
    private BigDecimal custo;

    @Column(columnDefinition = "TEXT")
    private String caracteristicas;

    @Column(name = "foto")//, columnDefinition = "oid"
    //@JdbcTypeCode(SqlTypes.BINARY)
    private Long fotoOid;

    @Transient
    private byte[] fotoData;

    @Column(columnDefinition = "boolean default true")
    private Boolean status = true;

    @Column(length = 100)
    private String marca;
}