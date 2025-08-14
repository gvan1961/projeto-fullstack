package com.hotel.divan.repositories;

import com.hotel.divan.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
    Optional<Empresa> findByCnpj(String cnpj);
    
    List<Empresa> findByAtivoTrue();
    
    @Query("SELECT e FROM Empresa e WHERE e.ativo = true AND " +
           "(LOWER(e.razaoSocial) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "LOWER(e.nomeFantasia) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
           "e.cnpj LIKE CONCAT('%', :termo, '%'))")
    List<Empresa> buscarPorTermo(@Param("termo") String termo);
}