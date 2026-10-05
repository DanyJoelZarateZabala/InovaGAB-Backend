package br.com.fiap.inovagab.backend.dto;

import java.util.List;

public record DashboardResumoResponse(
        int totalProjetos,
        double investimentoTotal,
        double retornoFinanceiroTotal,
        double roiPercentual,
        double ganhosProdutividadeTotal,
        int totalIdeias,
        int ideiasAprovadas,
        int ideiasPendentes,
        List<ResumoPorProjeto> porProjeto
) {}
