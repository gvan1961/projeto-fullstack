package com.hotel.divan.repositories;

import com.hotel.divan.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByCpf(String cpf);
    
    List<Cliente> findByAtivoTrue();
    
    // Clientes COM empresa específica
    List<Cliente> findByEmpresaIdAndAtivoTrue(Long empresaId);
    
    // Clientes SEM empresa (empresa_id é null)
    List<Cliente> findByEmpresaIsNullAndAtivoTrue();
    
    // Buscar clientes por termo E empresa específica
    @Query("SELECT c FROM Cliente c WHERE c.ativo = true AND c.empresa.id = :empresaId AND " +
           "(LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "c.cpf LIKE CONCAT('%', :termo, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Cliente> buscarPorTermoEEmpresa(@Param("termo") String termo, @Param("empresaId") Long empresaId);
    
    // Buscar clientes por termo SEM empresa
    @Query("SELECT c FROM Cliente c WHERE c.ativo = true AND c.empresa IS NULL AND " +
           "(LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "c.cpf LIKE CONCAT('%', :termo, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Cliente> buscarPorTermoSemEmpresa(@Param("termo") String termo);
    
    // Buscar todos os clientes por termo (com ou sem empresa)
    @Query("SELECT c FROM Cliente c WHERE c.ativo = true AND " +
           "(LOWER(c.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "c.cpf LIKE CONCAT('%', :termo, '%') OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Cliente> buscarPorTermo(@Param("termo") String termo);
    
 // Query para trazer clientes com empresa já carregada (JOIN FETCH)
    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.empresa WHERE c.ativo = true")
    List<Cliente> findClientesComEmpresa();

    // Query para estatísticas
    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.ativo = true AND c.empresa IS NOT NULL")
    Long countClientesComEmpresa();

    @Query("SELECT COUNT(c) FROM Cliente c WHERE c.ativo = true AND c.empresa IS NULL")
    Long countClientesSemEmpresa();

    // Query com projeção customizada
    @Query("SELECT new map(" +
           "c.id as clienteId, " +
           "c.nome as clienteNome, " +
           "c.cpf as clienteCpf, " +
           "c.email as clienteEmail, " +
           "CASE WHEN c.empresa IS NOT NULL THEN c.empresa.nomeFantasia ELSE 'Sem Empresa' END as empresaNome, " +
           "CASE WHEN c.empresa IS NOT NULL THEN c.empresa.cnpj ELSE null END as empresaCnpj" +
           ") FROM Cliente c WHERE c.ativo = true")
    List<Map<String, Object>> findClientesComResumoEmpresa();
    
    
}
