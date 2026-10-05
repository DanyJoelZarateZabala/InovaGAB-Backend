package br.com.fiap.inovagab.backend.config;

import br.com.fiap.inovagab.backend.model.Usuario;
import br.com.fiap.inovagab.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Com DEMO_SEED_ENABLED=true e DEMO_SEED_PASSWORD definida, provisiona
 * usuários didáticos ausentes, sem sobrescrever os registros existentes.
 * Jamais habilitar com senha conhecida em implantação pública.
 *
 * Contas demonstrativas:
 *   operador@inovagab.com
 *   gestor@inovagab.com
 *   lider@inovagab.com
 */
@Component
@ConditionalOnProperty(name = "demo.seed.enabled", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${demo.seed.password:}")
    private String senhaDemo;

    @Override
    public void run(String... args) {
        if (senhaDemo.isBlank()) {
            throw new IllegalStateException("Defina DEMO_SEED_PASSWORD para habilitar usuários de demonstração");
        }

        criarUsuario("Operador Teste", "operador@inovagab.com", "operador");
        criarUsuario("Gestor Teste", "gestor@inovagab.com", "gestor");
        criarUsuario("Lider Teste", "lider@inovagab.com", "lider");

        System.out.println("=====================================================");
        System.out.println("Usuários de demonstração provisionados (senha definida no ambiente):");
        System.out.println("  operador@inovagab.com");
        System.out.println("  gestor@inovagab.com");
        System.out.println("  lider@inovagab.com");
        System.out.println("=====================================================");
    }

    private void criarUsuario(String nome, String email, String perfil) {
        if (usuarioRepository.existsByEmail(email)) return;
        Usuario usuario = new Usuario();
        usuario.setUid(UUID.randomUUID().toString());
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(passwordEncoder.encode(senhaDemo));
        usuario.setPerfil(perfil);
        usuarioRepository.save(usuario);
    }
}
