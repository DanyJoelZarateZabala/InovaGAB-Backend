package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.IdeiaRequest;
import br.com.fiap.inovagab.backend.exception.AccessDeniedCustomException;
import br.com.fiap.inovagab.backend.model.Ideia;
import br.com.fiap.inovagab.backend.repository.IdeiaRepository;
import br.com.fiap.inovagab.backend.repository.UsuarioRepository;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdeiaServiceTest {
    @Mock IdeiaRepository ideias;
    @Mock UsuarioRepository usuarios;
    @Mock OrientacaoService orientacoes;
    @InjectMocks IdeiaService service;

    @Test void naoPermiteEditarIdeiaDeOutroOperador() {
        Ideia ideia = ideia("dono", "pendente");
        when(ideias.findById("id")).thenReturn(Optional.of(ideia));
        assertThrows(AccessDeniedCustomException.class, () ->
                service.atualizar("id", new IdeiaRequest("Novo", "Texto", "estrategia"),
                        new AuthenticatedUser("outro", "outro@teste.com", "operador")));
        verify(ideias, never()).save(any());
    }

    @Test void naoPermiteApagarIdeiaJaAprovada() {
        when(ideias.findById("id")).thenReturn(Optional.of(ideia("dono", "aprovada")));
        assertThrows(IllegalArgumentException.class, () ->
                service.deletar("id", new AuthenticatedUser("dono", "dono@teste.com", "operador")));
        verify(ideias, never()).delete(any());
    }

    @Test void atualizaIdeiaPendenteSomenteDoProprioAutor() {
        Ideia ideia = ideia("dono", "pendente");
        when(ideias.findById("id")).thenReturn(Optional.of(ideia));
        when(ideias.save(any(Ideia.class))).thenAnswer(inv -> inv.getArgument(0));
        Ideia atualizada = service.atualizar("id", new IdeiaRequest("Titulo novo", "Texto novo", "estrategia"),
                new AuthenticatedUser("dono", "dono@teste.com", "operador"));
        assertEquals("Titulo novo", atualizada.getTitulo());
        verify(orientacoes).exigirVigente("estrategia");
    }

    private Ideia ideia(String dono, String status) {
        Ideia ideia = new Ideia();
        ideia.setId("id"); ideia.setAutorUid(dono); ideia.setStatus(status);
        return ideia;
    }
}
