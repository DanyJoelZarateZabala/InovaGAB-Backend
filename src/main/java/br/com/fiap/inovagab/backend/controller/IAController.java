package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.DashboardResumoResponse;
import br.com.fiap.inovagab.backend.dto.InsightResponse;
import br.com.fiap.inovagab.backend.service.DashboardService;
import br.com.fiap.inovagab.backend.service.IAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Diferencial de IA (Plus): gera um texto de insights sobre os resultados
 * exibidos no dashboard, para apoiar a liderança na tomada de decisão.
 */
@RestController
@RequestMapping("/api/dashboard")
public class IAController {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private IAService iaService;

    // GET /api/dashboard/insight -> apenas liderança
    @GetMapping("/insight")
    @PreAuthorize("hasRole('LIDER')")
    public ResponseEntity<InsightResponse> gerarInsight() {
        DashboardResumoResponse resumo = dashboardService.gerarResumo();
        String texto = iaService.gerarInsight(resumo);
        return ResponseEntity.ok(new InsightResponse(texto));
    }
}
