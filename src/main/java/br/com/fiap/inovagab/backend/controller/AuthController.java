package br.com.fiap.inovagab.backend.controller;

import br.com.fiap.inovagab.backend.dto.CadastroUsuarioRequest;
import br.com.fiap.inovagab.backend.dto.LoginRequest;
import br.com.fiap.inovagab.backend.dto.LoginResponse;
import br.com.fiap.inovagab.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /api/auth/login -> { token, uid, nome, email, perfil }
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/cadastro -> somente liderança autenticada cria novos usuários
    @PostMapping("/cadastro")
    public ResponseEntity<LoginResponse> cadastrar(@Valid @RequestBody CadastroUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.cadastrar(request));
    }
}
