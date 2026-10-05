package br.com.fiap.inovagab.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record ProjetoResultadoRequest(@NotBlank String resultadosObtidos) {}
