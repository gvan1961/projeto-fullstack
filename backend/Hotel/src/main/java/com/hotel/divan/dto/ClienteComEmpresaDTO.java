package com.hotel.divan.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteComEmpresaDTO {
    
    // Dados do Cliente
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String celular;
    private String cidade;
    private String estado;
    
    // Informações sobre empresa
    private Boolean temEmpresa;
    private String tipoCliente; // "Pessoa Física" ou "Pessoa Jurídica"
    
    // Dados da Empresa (se houver)
    private Long empresaId;
    private String empresaNome;
    private String empresaCnpj;
    private String empresaEmail;
}