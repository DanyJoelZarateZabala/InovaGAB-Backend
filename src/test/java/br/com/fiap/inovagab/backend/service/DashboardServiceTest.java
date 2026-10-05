package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.model.Projeto;
import br.com.fiap.inovagab.backend.repository.IdeiaRepository;
import br.com.fiap.inovagab.backend.repository.ProjetoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {
    @Mock ProjetoRepository projetos;
    @Mock IdeiaRepository ideias;
    @Mock OrientacaoService orientacoes;
    @InjectMocks DashboardService service;

    @Test void roiGeralECalculadoSobreTotalDeInvestimento() {
        Projeto p = new Projeto(); p.setId("1"); p.setInvestimento(100); p.setRetornoFinanceiro(140);
        when(projetos.findAll()).thenReturn(List.of(p));
        when(ideias.findAll()).thenReturn(List.of());
        assertEquals(40, service.gerarResumo().roiPercentual(), 0.001);
    }
}
