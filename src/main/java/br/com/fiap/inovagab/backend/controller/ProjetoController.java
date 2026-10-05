package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.ProjetoRequest;
import br.com.fiap.inovagab.backend.dto.ProjetoResultadoRequest;
import br.com.fiap.inovagab.backend.model.Projeto;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import br.com.fiap.inovagab.backend.service.ProjetoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Regras:
 * - gestor: cadastra e atualiza projetos (progresso, resultados);
 * - lider: apenas consulta o andamento;
 * - consulta (GET) liberada para os 3 perfis autenticados.
 */
@RestController
@RequestMapping("/api/projetos")
public class ProjetoController {

    @Autowired
    private ProjetoService service;

    // GET /api/projetos -> qualquer perfil autenticado
    @GetMapping
    public ResponseEntity<List<Projeto>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // GET /api/projetos/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Projeto> buscar(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // POST /api/projetos -> apenas gestor
    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Projeto> criar(@Valid @RequestBody ProjetoRequest request) {
        Projeto projeto = service.criar(request, usuarioAutenticado());
        return ResponseEntity.status(HttpStatus.CREATED).body(projeto);
    }

    // PUT /api/projetos/{id} -> apenas o gestor responsável pelo projeto
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Projeto> atualizar(@PathVariable String id, @Valid @RequestBody ProjetoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request, usuarioAutenticado()));
    }

    @PatchMapping("/{id}/resultados")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Projeto> registrarResultado(@PathVariable String id,
             @Valid @RequestBody ProjetoResultadoRequest request) {
        return ResponseEntity.ok(service.registrarResultado(id, request.resultadosObtidos(), usuarioAutenticado()));
    }

    // DELETE /api/projetos/{id} -> apenas o gestor responsável pelo projeto
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id, usuarioAutenticado());
        return ResponseEntity.noContent().build();
    }

    private AuthenticatedUser usuarioAutenticado() {
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
