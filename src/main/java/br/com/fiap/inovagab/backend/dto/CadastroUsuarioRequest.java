package br.com.fiap.inovagab.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CadastroUsuarioRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha,
        @NotBlank @Pattern(regexp = "operador|gestor|lider", message = "perfil deve ser operador, gestor ou lider")
        String perfil
) {}
