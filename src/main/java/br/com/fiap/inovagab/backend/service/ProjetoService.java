package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.ProjetoRequest;
import br.com.fiap.inovagab.backend.exception.AccessDeniedCustomException;
import br.com.fiap.inovagab.backend.exception.ResourceNotFoundException;
import br.com.fiap.inovagab.backend.model.Projeto;
import br.com.fiap.inovagab.backend.repository.ProjetoRepository;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Regras:
 * - gestor cadastra e atualiza projetos (progresso, resultados);
 * - líder apenas consulta (sem CRUD).
 */
@Service
public class ProjetoService {

    @Autowired
    private ProjetoRepository repository;

    @Autowired
    private OrientacaoService orientacoes;

    @Autowired
    private br.com.fiap.inovagab.backend.repository.IdeiaRepository ideias;


    public List<Projeto> listarTodos() {
        return repository.findAll();
    }

    public Projeto buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Projeto não encontrado: " + id));
    }

    public Projeto criar(ProjetoRequest request, AuthenticatedUser gestor) {
        Projeto projeto = new Projeto();
        projeto.setId(UUID.randomUUID().toString());
        preencherCampos(projeto, request);
        projeto.setGestorUid(gestor.uid());
        projeto.setDataCriacao(System.currentTimeMillis());
        return repository.save(projeto);
    }

    public Projeto atualizar(String id, ProjetoRequest request, AuthenticatedUser gestor) {
        Projeto existente = buscarPorId(id);

        if (!existente.getGestorUid().equals(gestor.uid())) {
            throw new AccessDeniedCustomException("Apenas o gestor responsável pode atualizar este projeto");
        }

        preencherCampos(existente, request);
        return repository.save(existente);
    }

    public Projeto registrarResultado(String id, String texto, AuthenticatedUser gestor) {
        Projeto projeto = buscarPorId(id);
        if (!gestor.uid().equals(projeto.getGestorUid())) {
            throw new AccessDeniedCustomException("Apenas o gestor responsável pode registrar resultados");
        }
        projeto.setResultadosObtidos(texto);
        return repository.save(projeto);
    }

    public void deletar(String id, AuthenticatedUser gestor) {
        Projeto existente = buscarPorId(id);
        if (!existente.getGestorUid().equals(gestor.uid())) {
            throw new AccessDeniedCustomException("Apenas o gestor responsável pode remover este projeto");
        }
        repository.deleteById(id);
    }

    private void preencherCampos(Projeto projeto, ProjetoRequest request) {
        orientacoes.exigirVigente(request.orientacaoId());
        if (request.ideiaId() != null && !request.ideiaId().isBlank()) {
            var ideia = ideias.findById(request.ideiaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ideia de origem não encontrada"));
            if (!"aprovada".equals(ideia.getStatus())) {
                throw new IllegalArgumentException("Projeto só pode se vincular a uma ideia aprovada.");
            }
            if (!request.orientacaoId().equals(ideia.getOrientacaoId())) {
                throw new IllegalArgumentException("Ideia e projeto precisam pertencer à mesma estratégia.");
            }
        }

        projeto.setTitulo(request.titulo());
        projeto.setDescricao(request.descricao());
        projeto.setEtapa(request.etapa());
        if (request.status() != null) {
            projeto.setStatus(request.status());
        }
        projeto.setInvestimento(request.investimento());
        projeto.setRetornoFinanceiro(request.retornoFinanceiro());
        projeto.setGanhosProdutividade(request.ganhosProdutividade());
        projeto.setPrazo(request.prazo());
        projeto.setOrientacaoId(request.orientacaoId());
        projeto.setIdeiaId(request.ideiaId());
        projeto.setResultadosObtidos(request.resultadosObtidos());
    }
}
