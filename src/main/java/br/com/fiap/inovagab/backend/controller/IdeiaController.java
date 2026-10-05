package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.IdeiaRequest;
import br.com.fiap.inovagab.backend.dto.IdeiaStatusRequest;
import br.com.fiap.inovagab.backend.model.Ideia;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import br.com.fiap.inovagab.backend.service.IdeiaService;
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
 * - operador: cadastra e consulta APENAS as próprias ideias;
 * - gestor: consulta TODAS as ideias e pode priorizar/aprovar/rejeitar.
 */
@RestController
@RequestMapping("/api/ideias")
public class IdeiaController {

    @Autowired
    private IdeiaService service;

    // GET /api/ideias -> operador vê só as suas; gestor vê todas
    @GetMapping
    public ResponseEntity<List<Ideia>> listar() {
        AuthenticatedUser usuario = usuarioAutenticado();
        if ("operador".equals(usuario.perfil())) {
            return ResponseEntity.ok(service.listarMinhas(usuario.uid()));
        }
        return ResponseEntity.ok(service.listarTodas());
    }

    // GET /api/ideias/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Ideia> buscar(@PathVariable String id) {
        Ideia ideia = service.buscarPorId(id);
        AuthenticatedUser usuario = usuarioAutenticado();
        if ("operador".equals(usuario.perfil())) {
            service.validarDonoOuLancarErro(ideia, usuario.uid());
        }
        return ResponseEntity.ok(ideia);
    }

    // POST /api/ideias -> apenas operador cadastra ideia
    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Ideia> criar(@Valid @RequestBody IdeiaRequest request) {
        Ideia ideia = service.criar(request, usuarioAutenticado());
        return ResponseEntity.status(HttpStatus.CREATED).body(ideia);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Ideia> atualizar(@PathVariable String id, @Valid @RequestBody IdeiaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request, usuarioAutenticado()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id, usuarioAutenticado());
        return ResponseEntity.noContent().build();
    }

    // PATCH /api/ideias/{id}/status -> apenas gestor prioriza/aprova/rejeita
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<Ideia> atualizarStatus(@PathVariable String id, @Valid @RequestBody IdeiaStatusRequest request) {
        return ResponseEntity.ok(service.atualizarStatus(id, request));
    }

    private AuthenticatedUser usuarioAutenticado() {
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
