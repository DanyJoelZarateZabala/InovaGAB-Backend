package br.com.fiap.inovagab.backend.dto;

public record LoginResponse(
        String token,
        String uid,
        String nome,
        String email,
        String perfil
) {}
