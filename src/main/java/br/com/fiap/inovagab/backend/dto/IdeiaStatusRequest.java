package br.com.fiap.inovagab.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;

public record IdeiaStatusRequest(
        @NotBlank @Pattern(regexp = "pendente|aprovada|rejeitada") String status,
        @Min(0) Integer prioridade
) {}
