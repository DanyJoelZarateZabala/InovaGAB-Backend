package br.com.fiap.inovagab.backend.dto;

public record ResumoPorProjeto(
        String id,
        String titulo,
        String status,
        double investimento,
        double retornoFinanceiro,
        double roiPercentual
) {}
