package br.com.fiap.inovagab.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record OrientacaoRequest(
        @NotBlank String titulo,
        @NotBlank String descricao,
        String categoria,
        String campanha
) {}
