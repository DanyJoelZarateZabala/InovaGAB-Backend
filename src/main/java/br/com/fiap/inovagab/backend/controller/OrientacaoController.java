package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.OrientacaoRequest;
import br.com.fiap.inovagab.backend.model.Orientacao;
import br.com.fiap.inovagab.backend.model.OrientacaoHistorico;
import br.com.fiap.inovagab.backend.service.OrientacaoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Consulta: liberada para os 3 perfis autenticados (operador, gestor, lider).
 * CRUD completo (criar/editar/excluir): apenas LIDER.
 */
@RestController
@RequestMapping("/api/orientacoes")
public class OrientacaoController {

    @Autowired
    private OrientacaoService service;

    // GET /api/orientacoes -> qualquer perfil autenticado
    @GetMapping
    public ResponseEntity<List<Orientacao>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/vigentes")
    public ResponseEntity<List<Orientacao>> vigentes() {
        return ResponseEntity.ok(service.listarVigentes());
    }

    @GetMapping("/{id}/historico")
    public ResponseEntity<List<OrientacaoHistorico>> historico(@PathVariable String id) {
        return ResponseEntity.ok(service.historico(id));
    }

    // GET /api/orientacoes/{id} -> qualquer perfil autenticado
    @GetMapping("/{id}")
    public ResponseEntity<Orientacao> buscar(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // POST /api/orientacoes -> apenas liderança
    @PostMapping
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<Orientacao> criar(@Valid @RequestBody OrientacaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(request));
    }

    // PUT /api/orientacoes/{id} -> apenas liderança
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<Orientacao> atualizar(@PathVariable String id, @Valid @RequestBody OrientacaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    // DELETE /api/orientacoes/{id} -> apenas liderança
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
