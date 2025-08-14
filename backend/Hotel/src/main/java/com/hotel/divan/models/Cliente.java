package com.hotel.divan.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "empresa") // Evita loop infinito
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(unique = true, nullable = false, length = 14) // CPF pode ter pontuação
    private String cpf;

    @Column(nullable = false, length = 20)
    private String celular;

    @Column(name = "celular2", length = 20)
    private String celular2;

    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private LocalDateTime dataCadastro;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 10) // CEP pode ter hífen
    private String cep;

    @Column(columnDefinition = "TEXT")
    private String endereco;

    @Column(length = 100)
    private String cidade;

    @Column(length = 50)
    private String estado;

    @Column(length = 100)
    private String nacionalidade;

    // EMPRESA AGORA É OPCIONAL
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = true, foreignKey = @ForeignKey(name = "fk_cliente_empresa"))
    @JsonBackReference
    private Empresa empresa;

    @PrePersist
    protected void onCreate() {
        if (this.dataCadastro == null) {
            this.dataCadastro = LocalDateTime.now();
        }
        if (this.ativo == null) {
            this.ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // dataCadastro não deve ser alterado em updates
    }
}