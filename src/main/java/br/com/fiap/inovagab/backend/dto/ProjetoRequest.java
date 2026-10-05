package br.com.fiap.inovagab.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record ProjetoRequest(
        @NotBlank String titulo,
        String descricao,
        String etapa,
        String status,
        @PositiveOrZero double investimento,
        @PositiveOrZero double retornoFinanceiro,
        @PositiveOrZero double ganhosProdutividade,
        String prazo,
        @NotBlank String orientacaoId,
        String ideiaId,
        String resultadosObtidos
) {}
