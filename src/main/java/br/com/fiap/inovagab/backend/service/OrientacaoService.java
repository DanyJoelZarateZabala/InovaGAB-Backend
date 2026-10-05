package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.OrientacaoRequest;
import br.com.fiap.inovagab.backend.exception.ResourceNotFoundException;
import br.com.fiap.inovagab.backend.model.Orientacao;
import br.com.fiap.inovagab.backend.model.OrientacaoHistorico;
import br.com.fiap.inovagab.backend.repository.OrientacaoRepository;
import br.com.fiap.inovagab.backend.repository.OrientacaoHistoricoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrientacaoService {
    private final OrientacaoRepository repository;
    private final OrientacaoHistoricoRepository historicoRepository;

    public OrientacaoService(OrientacaoRepository repository, OrientacaoHistoricoRepository historicoRepository) {
        this.repository = repository;
        this.historicoRepository = historicoRepository;
    }

    public List<Orientacao> listarTodas() { return repository.findAll(); }

    public List<Orientacao> listarVigentes() {
        return repository.findAll().stream().filter(Orientacao::isVigente).toList();
    }

    public Orientacao buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orientação não encontrada: " + id));
    }

    /** Reutilizado nos módulos de ideia e projeto para validar vínculo real e vigente. */
    public Orientacao exigirVigente(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Selecione uma orientação estratégica vigente.");
        }
        Orientacao orientacao = buscarPorId(id);
        if (!orientacao.isVigente()) {
            throw new IllegalArgumentException("A orientação escolhida não está mais vigente.");
        }
        return orientacao;
    }

    public Orientacao criar(OrientacaoRequest request) {
        Orientacao orientacao = new Orientacao();
        orientacao.setId(UUID.randomUUID().toString());
        orientacao.setTitulo(request.titulo());
        orientacao.setDescricao(request.descricao());
        orientacao.setCategoria(request.categoria());
        orientacao.setCampanha(request.campanha());
        orientacao.setDataCriacao(System.currentTimeMillis());
        orientacao.setVigente(true);
        return repository.save(orientacao);
    }

    public Orientacao atualizar(String id, OrientacaoRequest request) {
        Orientacao existente = exigirVigente(id);
        registrarHistorico(existente, "ATUALIZACAO");
        existente.setTitulo(request.titulo());
        existente.setDescricao(request.descricao());
        existente.setCategoria(request.categoria());
        existente.setCampanha(request.campanha());
        return repository.save(existente);
    }

    /** DELETE lógico: mantém o registro para consulta e vínculos de ideias/projetos. */
    public void deletar(String id) {
        Orientacao existente = exigirVigente(id);
        registrarHistorico(existente, "DESATIVACAO");
        existente.setVigente(false);
        repository.save(existente);
    }

    public List<OrientacaoHistorico> historico(String id) {
        buscarPorId(id);
        return historicoRepository.findByOrientacaoIdOrderByDataAlteracaoDesc(id);
    }

    private void registrarHistorico(Orientacao anterior, String evento) {
        OrientacaoHistorico h = new OrientacaoHistorico();
        h.setId(UUID.randomUUID().toString());
        h.setOrientacaoId(anterior.getId());
        h.setTitulo(anterior.getTitulo());
        h.setDescricao(anterior.getDescricao());
        h.setCategoria(anterior.getCategoria());
        h.setCampanha(anterior.getCampanha());
        h.setDataCriacao(anterior.getDataCriacao());
        h.setDataAlteracao(System.currentTimeMillis());
        h.setVigente(anterior.isVigente());
        h.setEvento(evento);
        historicoRepository.save(h);
    }
}
