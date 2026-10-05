package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.CadastroUsuarioRequest;
import br.com.fiap.inovagab.backend.dto.LoginRequest;
import br.com.fiap.inovagab.backend.dto.LoginResponse;
import br.com.fiap.inovagab.backend.model.Usuario;
import br.com.fiap.inovagab.backend.repository.UsuarioRepository;
import br.com.fiap.inovagab.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("E-mail ou senha incorretos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new BadCredentialsException("E-mail ou senha incorretos");
        }

        String token = jwtUtil.gerarToken(usuario.getUid(), usuario.getEmail(), usuario.getPerfil());

        return new LoginResponse(token, usuario.getUid(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }

    /** Cadastro de usuário autorizado apenas à liderança (SecurityConfig). */
    public LoginResponse cadastrar(CadastroUsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new BadCredentialsException("Já existe um usuário com este e-mail");
        }

        Usuario usuario = new Usuario();
        usuario.setUid(UUID.randomUUID().toString());
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setSenhaHash(passwordEncoder.encode(request.senha()));
        usuario.setPerfil(request.perfil());

        usuarioRepository.save(usuario);

        String token = jwtUtil.gerarToken(usuario.getUid(), usuario.getEmail(), usuario.getPerfil());
        return new LoginResponse(token, usuario.getUid(), usuario.getNome(), usuario.getEmail(), usuario.getPerfil());
    }
}
