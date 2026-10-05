package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.DashboardResumoResponse;
import br.com.fiap.inovagab.backend.dto.DashboardProjetoResponse;
import br.com.fiap.inovagab.backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/** Relatórios restritos à liderança, inclusive ao chamar diretamente a API. */
@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasRole('LIDER')")
public class DashboardController {
    private final DashboardService service;
    public DashboardController(DashboardService service) { this.service = service; }

    @GetMapping("/resumo")
    public ResponseEntity<DashboardResumoResponse> resumo() {
        return ResponseEntity.ok(service.gerarResumo());
    }
    @GetMapping("/estrategias/{id}")
    public ResponseEntity<DashboardResumoResponse> estrategia(@PathVariable String id) {
        return ResponseEntity.ok(service.porEstrategia(id));
    }
    @GetMapping("/projetos/{id}")
    public ResponseEntity<DashboardProjetoResponse> projeto(@PathVariable String id) {
        return ResponseEntity.ok(service.porProjeto(id));
    }
}
