package com.hotel.divan.controllers;

import com.hotel.divan.dto.ClienteComEmpresaDTO;
import com.hotel.divan.models.Cliente;
import com.hotel.divan.services.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<Cliente>> listarAtivos() {
        List<Cliente> clientes = clienteService.listarAtivos();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Cliente>> listarPorEmpresa(@PathVariable Long empresaId) {
        List<Cliente> clientes = clienteService.listarPorEmpresa(empresaId);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/sem-empresa")
    public ResponseEntity<List<Cliente>> listarSemEmpresa() {
        List<Cliente> clientes = clienteService.listarSemEmpresa();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable String cpf) {
        return clienteService.buscarPorCpf(cpf)
                .map(cliente -> ResponseEntity.ok(cliente))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Cliente>> buscarPorTermo(@RequestParam String termo) {
        List<Cliente> clientes = clienteService.buscarPorTermo(termo);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/buscar/empresa/{empresaId}")
    public ResponseEntity<List<Cliente>> buscarPorTermoEEmpresa(
            @RequestParam String termo, 
            @PathVariable Long empresaId) {
        List<Cliente> clientes = clienteService.buscarPorTermoEEmpresa(termo, empresaId);
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/buscar/sem-empresa")
    public ResponseEntity<List<Cliente>> buscarPorTermoSemEmpresa(@RequestParam String termo) {
        List<Cliente> clientes = clienteService.buscarPorTermoSemEmpresa(termo);
        return ResponseEntity.ok(clientes);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Cliente cliente) {
        try {
            Cliente clienteSalvo = clienteService.salvar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteSalvo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/com-empresas")
    public ResponseEntity<List<ClienteComEmpresaDTO>> listarClientesComEmpresas() {
        List<Cliente> clientes = clienteService.listarAtivos();
        List<ClienteComEmpresaDTO> resultado = clientes.stream()
                .map(this::mapearClienteComEmpresa)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/relatorio-completo")
    public ResponseEntity<Map<String, Object>> relatorioClientesEmpresas() {
        Map<String, Object> relatorio = clienteService.gerarRelatorioClientesEmpresas();
        return ResponseEntity.ok(relatorio);
    }

    private ClienteComEmpresaDTO mapearClienteComEmpresa(Cliente cliente) {
        ClienteComEmpresaDTO dto = new ClienteComEmpresaDTO();
        dto.setId(cliente.getId());
        dto.setNome(cliente.getNome());
        dto.setCpf(cliente.getCpf());
        dto.setEmail(cliente.getEmail());
        dto.setCelular(cliente.getCelular());
        dto.setCidade(cliente.getCidade());
        dto.setEstado(cliente.getEstado());
        
        if (cliente.getEmpresa() != null) {
            dto.setTemEmpresa(true);
            dto.setEmpresaId(cliente.getEmpresa().getId());
            dto.setEmpresaNome(cliente.getEmpresa().getNomeFantasia());
            dto.setEmpresaCnpj(cliente.getEmpresa().getCnpj());
            dto.setEmpresaEmail(cliente.getEmpresa().getEmail());
        } else {
            dto.setTemEmpresa(false);
            dto.setTipoCliente("Pessoa Física");
        }
        
        return dto;
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Cliente cliente) {
        try {
            Cliente clienteAtualizado = clienteService.atualizar(id, cliente);
            return ResponseEntity.ok(clienteAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{clienteId}/vincular-empresa/{empresaId}")
    public ResponseEntity<?> vincularEmpresa(
            @PathVariable Long clienteId, 
            @PathVariable Long empresaId) {
        try {
            Cliente cliente = clienteService.vincularEmpresa(clienteId, empresaId);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{clienteId}/desvincular-empresa")
    public ResponseEntity<?> desvincularEmpresa(@PathVariable Long clienteId) {
        try {
            Cliente cliente = clienteService.vincularEmpresa(clienteId, null);
            return ResponseEntity.ok(cliente);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            clienteService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
