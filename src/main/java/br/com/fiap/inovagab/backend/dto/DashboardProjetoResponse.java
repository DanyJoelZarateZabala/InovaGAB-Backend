package br.com.fiap.inovagab.backend.dto;

public record DashboardProjetoResponse(
        String id, String titulo, String status, String etapa, String prazo,
        double investimento, double retornoFinanceiro, double roiPercentual,
        double ganhosProdutividade, String resultadosObtidos, String orientacaoId
) {}
