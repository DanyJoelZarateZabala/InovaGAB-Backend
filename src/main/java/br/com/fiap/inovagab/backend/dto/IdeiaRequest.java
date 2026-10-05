package br.com.fiap.inovagab.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record IdeiaRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String orientacaoId
) {}
