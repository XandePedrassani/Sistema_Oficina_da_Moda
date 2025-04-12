package com.rooster.sistema.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, length = 20)
    private String contato;

    @Column
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private LocalDate dataCadastro;

    @Column( length = 20, unique = true)
    private String cpfcnpj;

    @Column
    private String endereco;

    @Column(unique = true)
    private String email;
}