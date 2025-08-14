package com.hotel.divan.services;

import com.hotel.divan.models.Empresa;
import com.hotel.divan.repositories.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<Empresa> listarTodas() {
        return empresaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarAtivas() {
        return empresaRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorId(Long id) {
        return empresaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Empresa> buscarPorCnpj(String cnpj) {
        return empresaRepository.findByCnpj(cnpj);
    }

    @Transactional(readOnly = true)
    public List<Empresa> buscarPorTermo(String termo) {
        return empresaRepository.buscarPorTermo(termo);
    }

    @Transactional
    public Empresa salvar(Empresa empresa) {
        // Validar se CNPJ já existe
        if (empresa.getId() == null && empresaRepository.findByCnpj(empresa.getCnpj()).isPresent()) {
            throw new RuntimeException("CNPJ já cadastrado");
        }
        return empresaRepository.save(empresa);
    }

    @Transactional
    public Empresa atualizar(Long id, Empresa empresaAtualizada) {
        return empresaRepository.findById(id)
                .map(empresa -> {
                    empresa.setRazaoSocial(empresaAtualizada.getRazaoSocial());
                    empresa.setNomeFantasia(empresaAtualizada.getNomeFantasia());
                    empresa.setEmail(empresaAtualizada.getEmail());
                    empresa.setTelefone(empresaAtualizada.getTelefone());
                    empresa.setEndereco(empresaAtualizada.getEndereco());
                    empresa.setCep(empresaAtualizada.getCep());
                    empresa.setAtivo(empresaAtualizada.getAtivo());
                    return empresaRepository.save(empresa);
                })
                .orElseThrow(() -> new RuntimeException("Empresa não encontrada"));
    }

    @Transactional
    public void deletar(Long id) {
        empresaRepository.deleteById(id);
    }

    @Transactional
    public void inativar(Long id) {
        empresaRepository.findById(id)
                .ifPresentOrElse(
                        empresa -> {
                            empresa.setAtivo(false);
                            empresaRepository.save(empresa);
                        },
                        () -> { throw new RuntimeException("Empresa não encontrada"); }
                );
    }
}