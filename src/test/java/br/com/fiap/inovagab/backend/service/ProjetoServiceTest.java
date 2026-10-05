package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.ProjetoRequest;
import br.com.fiap.inovagab.backend.model.Ideia;
import br.com.fiap.inovagab.backend.repository.IdeiaRepository;
import br.com.fiap.inovagab.backend.repository.ProjetoRepository;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {
    @Mock ProjetoRepository projetos;
    @Mock IdeiaRepository ideias;
    @Mock OrientacaoService orientacoes;
    @InjectMocks ProjetoService service;

    @Test void bloqueiaProjetoVinculadoAIdeiaNaoAprovada() {
        Ideia ideia = new Ideia(); ideia.setStatus("pendente"); ideia.setOrientacaoId("estrategia");
        when(ideias.findById("ideia")).thenReturn(Optional.of(ideia));
        ProjetoRequest request = new ProjetoRequest("Título", "Descrição", "etapa", "em_andamento",
                100, 200, 5, "2026-12-01", "estrategia", "ideia", null);
        assertThrows(IllegalArgumentException.class, () ->
                service.criar(request, new AuthenticatedUser("gestor", "gestor@teste.com", "gestor")));
        verify(projetos, never()).save(any());
    }
}
