package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.OrientacaoRequest;
import br.com.fiap.inovagab.backend.model.Orientacao;
import br.com.fiap.inovagab.backend.model.OrientacaoHistorico;
import br.com.fiap.inovagab.backend.repository.OrientacaoRepository;
import br.com.fiap.inovagab.backend.repository.OrientacaoHistoricoRepository;
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
class OrientacaoServiceTest {
    @Mock OrientacaoRepository orientacoes;
    @Mock OrientacaoHistoricoRepository historico;
    @InjectMocks OrientacaoService service;

    @Test void desativacaoPreservaRegistroEHistorico() {
        Orientacao orientacao = new Orientacao();
        orientacao.setId("a"); orientacao.setTitulo("Estratégia"); orientacao.setVigente(true);
        when(orientacoes.findById("a")).thenReturn(Optional.of(orientacao));
        service.deletar("a");
        assertFalse(orientacao.isVigente());
        verify(historico).save(any(OrientacaoHistorico.class));
        verify(orientacoes).save(orientacao);
        verify(orientacoes, never()).deleteById("a");
    }

    @Test void naoAceitaVinculoComEstrategiaDesativada() {
        Orientacao orientacao = new Orientacao(); orientacao.setVigente(false);
        when(orientacoes.findById("a")).thenReturn(Optional.of(orientacao));
        assertThrows(IllegalArgumentException.class, () -> service.exigirVigente("a"));
    }

    @Test void atualizacaoGravaFotografiaDaVersaoAnterior() {
        Orientacao orientacao = new Orientacao();
        orientacao.setId("a"); orientacao.setTitulo("Antigo"); orientacao.setVigente(true);
        when(orientacoes.findById("a")).thenReturn(Optional.of(orientacao));
        when(orientacoes.save(any(Orientacao.class))).thenAnswer(inv -> inv.getArgument(0));
        Orientacao depois = service.atualizar("a", new OrientacaoRequest("Novo", "Descrição", "Categoria", "Campanha"));
        assertEquals("Novo", depois.getTitulo());
        verify(historico).save(argThat(h -> "Antigo".equals(h.getTitulo()) && "ATUALIZACAO".equals(h.getEvento())));
    }
}
