package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.DashboardResumoResponse;
import br.com.fiap.inovagab.backend.dto.DashboardProjetoResponse;
import br.com.fiap.inovagab.backend.dto.ResumoPorProjeto;
import br.com.fiap.inovagab.backend.model.Ideia;
import br.com.fiap.inovagab.backend.model.Projeto;
import br.com.fiap.inovagab.backend.repository.IdeiaRepository;
import br.com.fiap.inovagab.backend.repository.ProjetoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardService {
    private final ProjetoRepository projetos;
    private final IdeiaRepository ideias;
    private final OrientacaoService orientacoes;

    public DashboardService(ProjetoRepository projetos, IdeiaRepository ideias, OrientacaoService orientacoes) {
        this.projetos = projetos; this.ideias = ideias; this.orientacoes = orientacoes;
    }

    public DashboardResumoResponse gerarResumo() {
        return montarResumo(projetos.findAll(), ideias.findAll());
    }

    public DashboardResumoResponse porEstrategia(String id) {
        orientacoes.buscarPorId(id); // estratégias desativadas continuam consultáveis no histórico
        List<Ideia> ideiasDaEstrategia = ideias.findAll().stream()
                .filter(i -> id.equals(i.getOrientacaoId())).toList();
        return montarResumo(projetos.findByOrientacaoId(id), ideiasDaEstrategia);
    }

    public DashboardProjetoResponse porProjeto(String id) {
        Projeto p = projetos.findById(id).orElseThrow(() ->
            new br.com.fiap.inovagab.backend.exception.ResourceNotFoundException("Projeto não encontrado: " + id));
        return new DashboardProjetoResponse(p.getId(), p.getTitulo(), p.getStatus(), p.getEtapa(), p.getPrazo(),
                p.getInvestimento(), p.getRetornoFinanceiro(), roi(p.getInvestimento(), p.getRetornoFinanceiro()),
                p.getGanhosProdutividade(), p.getResultadosObtidos(), p.getOrientacaoId());
    }

    private DashboardResumoResponse montarResumo(List<Projeto> projetos, List<Ideia> ideias) {
        double investimento = projetos.stream().mapToDouble(Projeto::getInvestimento).sum();
        double retorno = projetos.stream().mapToDouble(Projeto::getRetornoFinanceiro).sum();
        double produtividade = projetos.stream().mapToDouble(Projeto::getGanhosProdutividade).sum();
        long aprovadas = ideias.stream().filter(i -> "aprovada".equals(i.getStatus())).count();
        long pendentes = ideias.stream().filter(i -> "pendente".equals(i.getStatus())).count();
        List<ResumoPorProjeto> porProjeto = projetos.stream()
                .map(p -> new ResumoPorProjeto(p.getId(), p.getTitulo(), p.getStatus(), p.getInvestimento(),
                        p.getRetornoFinanceiro(), roi(p.getInvestimento(), p.getRetornoFinanceiro()))).toList();
        return new DashboardResumoResponse(projetos.size(), investimento, retorno, roi(investimento, retorno),
                produtividade, ideias.size(), (int) aprovadas, (int) pendentes, porProjeto);
    }

    private double roi(double investimento, double retorno) {
        return investimento <= 0 ? 0 : (retorno - investimento) / investimento * 100;
    }
}
