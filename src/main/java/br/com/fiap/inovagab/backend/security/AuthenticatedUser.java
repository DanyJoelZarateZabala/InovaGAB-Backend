package br.com.fiap.inovagab.backend.security;

/**
 * Representa o usuário autenticado extraído do JWT, disponível via
 * SecurityContextHolder durante o processamento da requisição.
 */
public record AuthenticatedUser(String uid, String email, String perfil) {
}
