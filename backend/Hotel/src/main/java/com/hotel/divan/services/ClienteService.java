package com.hotel.divan.services;

import com.hotel.divan.models.Cliente;
import com.hotel.divan.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarPorEmpresa(Long empresaId) {
        return clienteRepository.findByEmpresaIdAndAtivoTrue(empresaId);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarSemEmpresa() {
        return clienteRepository.findByEmpresaIsNullAndAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf);
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorTermoEEmpresa(String termo, Long empresaId) {
        return clienteRepository.buscarPorTermoEEmpresa(termo, empresaId);
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorTermoSemEmpresa(String termo) {
        return clienteRepository.buscarPorTermoSemEmpresa(termo);
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorTermo(String termo) {
        return clienteRepository.buscarPorTermo(termo);
    }

    @Transactional
    public Cliente salvar(Cliente cliente) {
        // Validar se CPF já existe
        if (cliente.getId() == null && clienteRepository.findByCpf(cliente.getCpf()).isPresent()) {
            throw new RuntimeException("CPF já cadastrado");
        }
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        return clienteRepository.findById(id)
                .map(cliente -> {
                    cliente.setNome(clienteAtualizado.getNome());
                    cliente.setEmail(clienteAtualizado.getEmail());
                    cliente.setCelular(clienteAtualizado.getCelular());
                    cliente.setCelular2(clienteAtualizado.getCelular2());
                    cliente.setDataNascimento(clienteAtualizado.getDataNascimento());
                    cliente.setCep(clienteAtualizado.getCep());
                    cliente.setEndereco(clienteAtualizado.getEndereco());
                    cliente.setCidade(clienteAtualizado.getCidade());
                    cliente.setEstado(clienteAtualizado.getEstado());
                    cliente.setNacionalidade(clienteAtualizado.getNacionalidade());
                    cliente.setEmpresa(clienteAtualizado.getEmpresa()); // Permite alterar empresa (inclusive para null)
                    cliente.setAtivo(clienteAtualizado.getAtivo());
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    @Transactional
    public void deletar(Long id) {
        clienteRepository.deleteById(id);
    }

    @Transactional
    public void inativar(Long id) {
        clienteRepository.findById(id)
                .ifPresentOrElse(
                        cliente -> {
                            cliente.setAtivo(false);
                            clienteRepository.save(cliente);
                        },
                        () -> { throw new RuntimeException("Cliente não encontrado"); }
                );
    }

    @Transactional
    public Cliente vincularEmpresa(Long clienteId, Long empresaId) {
        return clienteRepository.findById(clienteId)
                .map(cliente -> {
                    // Aqui você pode adicionar validação se a empresa existe
                    cliente.setEmpresa(empresaId != null ? new com.hotel.divan.models.Empresa() {{ setId(empresaId); }} : null);
                    return clienteRepository.save(cliente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }
    
    @Transactional(readOnly = true)
    public Map<String, Object> gerarRelatorioClientesEmpresas() {
        List<Cliente> todosClientes = clienteRepository.findByAtivoTrue();
        
        long clientesComEmpresa = todosClientes.stream()
                .filter(c -> c.getEmpresa() != null)
                .count();
        
        long clientesSemEmpresa = todosClientes.stream()
                .filter(c -> c.getEmpresa() == null)
                .count();
        
        Map<String, Object> relatorio = new HashMap<>();
        relatorio.put("totalClientes", todosClientes.size());
        relatorio.put("clientesComEmpresa", clientesComEmpresa);
        relatorio.put("clientesSemEmpresa", clientesSemEmpresa);
        relatorio.put("percentualComEmpresa", 
            todosClientes.size() > 0 ? (clientesComEmpresa * 100.0 / todosClientes.size()) : 0);
        
        // Agrupar por empresa
        Map<String, List<String>> clientesPorEmpresa = todosClientes.stream()
                .filter(c -> c.getEmpresa() != null)
                .collect(Collectors.groupingBy(
                    c -> c.getEmpresa().getNomeFantasia(),
                    Collectors.mapping(Cliente::getNome, Collectors.toList())
                ));
        
        relatorio.put("clientesPorEmpresa", clientesPorEmpresa);
        
        return relatorio;
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarClientesComDetalhesEmpresa() {
        return clienteRepository.findClientesComEmpresa();
    }
    
}
