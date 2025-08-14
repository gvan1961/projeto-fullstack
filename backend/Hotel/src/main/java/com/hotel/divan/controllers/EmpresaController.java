package com.hotel.divan.controllers;

import com.hotel.divan.models.Empresa;
import com.hotel.divan.services.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public ResponseEntity<List<Empresa>> listarTodas() {
        List<Empresa> empresas = empresaService.listarTodas();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Empresa>> listarAtivas() {
        List<Empresa> empresas = empresaService.listarAtivas();
        return ResponseEntity.ok(empresas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empresa> buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
                .map(empresa -> ResponseEntity.ok(empresa))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cnpj/{cnpj}")
    public ResponseEntity<Empresa> buscarPorCnpj(@PathVariable String cnpj) {
        return empresaService.buscarPorCnpj(cnpj)
                .map(empresa -> ResponseEntity.ok(empresa))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Empresa>> buscarPorTermo(@RequestParam String termo) {
        List<Empresa> empresas = empresaService.buscarPorTermo(termo);
        return ResponseEntity.ok(empresas);
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Empresa empresa) {
        try {
            Empresa empresaSalva = empresaService.salvar(empresa);
            return ResponseEntity.status(HttpStatus.CREATED).body(empresaSalva);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Empresa empresa) {
        try {
            Empresa empresaAtualizada = empresaService.atualizar(id, empresa);
            return ResponseEntity.ok(empresaAtualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        empresaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        try {
            empresaService.inativar(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}