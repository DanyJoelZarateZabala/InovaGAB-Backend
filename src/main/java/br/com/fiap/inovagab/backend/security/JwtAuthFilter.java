package br.com.fiap.inovagab.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import br.com.fiap.inovagab.backend.repository.UsuarioRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro que intercepta toda requisição, lê o header Authorization: Bearer <token>,
 * valida o JWT e popula o SecurityContext com o perfil (role) do usuário,
 * para que @PreAuthorize("hasRole('GESTOR')") etc funcionem nos controllers.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarios;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            if (jwtUtil.tokenValido(token)) {
                String email = jwtUtil.extrairEmail(token);
                String uid = jwtUtil.extrairUid(token);
                // Perfil vem do MongoDB e não de claim possivelmente desatualizada.
                usuarios.findById(uid).filter(u -> email.equals(u.getEmail()))
                        .filter(u -> List.of("operador", "gestor", "lider").contains(u.getPerfil()))
                        .ifPresent(usuario -> {
                            String perfil = usuario.getPerfil();
                            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + perfil.toUpperCase()));
                            var authToken = new UsernamePasswordAuthenticationToken(
                                    new AuthenticatedUser(uid, email, perfil), null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        });
            }
        }

        filterChain.doFilter(request, response);
    }
}
