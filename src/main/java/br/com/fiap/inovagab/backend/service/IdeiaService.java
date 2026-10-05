package br.com.fiap.inovagab.backend.service;

import br.com.fiap.inovagab.backend.dto.IdeiaRequest;
import br.com.fiap.inovagab.backend.dto.IdeiaStatusRequest;
import br.com.fiap.inovagab.backend.exception.AccessDeniedCustomException;
import br.com.fiap.inovagab.backend.exception.ResourceNotFoundException;
import br.com.fiap.inovagab.backend.model.Ideia;
import br.com.fiap.inovagab.backend.model.Usuario;
import br.com.fiap.inovagab.backend.repository.IdeiaRepository;
import br.com.fiap.inovagab.backend.repository.UsuarioRepository;
import br.com.fiap.inovagab.backend.security.AuthenticatedUser;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class IdeiaService {
    private final IdeiaRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final OrientacaoService orientacoes;

    public IdeiaService(IdeiaRepository repository, UsuarioRepository usuarioRepository, OrientacaoService orientacoes) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.orientacoes = orientacoes;
    }

    public List<Ideia> listarMinhas(String uid) { return repository.findByAutorUid(uid); }
    public List<Ideia> listarTodas() { return repository.findAll(); }
    public Ideia buscarPorId(String id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ideia não encontrada: " + id));
    }

    public Ideia criar(IdeiaRequest request, AuthenticatedUser autor) {
        orientacoes.exigirVigente(request.orientacaoId());
        Usuario usuario = usuarioRepository.findById(autor.uid())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        Ideia ideia = new Ideia();
        ideia.setId(UUID.randomUUID().toString());
        ideia.setTitulo(request.titulo());
        ideia.setDescricao(request.descricao());
        ideia.setAutorUid(usuario.getUid());
        ideia.setAutorNome(usuario.getNome());
        ideia.setStatus("pendente");
        ideia.setPrioridade(0);
        ideia.setOrientacaoId(request.orientacaoId());
        ideia.setDataCriacao(System.currentTimeMillis());
        return repository.save(ideia);
    }

    public void validarDonoOuLancarErro(Ideia ideia, String uid) {
        if (!uid.equals(ideia.getAutorUid())) {
            throw new AccessDeniedCustomException("Você não tem permissão para acessar esta ideia");
        }
    }

    /** Operador edita somente a própria ideia ainda pendente. */
    public Ideia atualizar(String id, IdeiaRequest request, AuthenticatedUser autor) {
        Ideia ideia = buscarPorId(id);
        validarDonoOuLancarErro(ideia, autor.uid());
        exigirPendente(ideia);
        orientacoes.exigirVigente(request.orientacaoId());
        ideia.setTitulo(request.titulo());
        ideia.setDescricao(request.descricao());
        ideia.setOrientacaoId(request.orientacaoId());
        return repository.save(ideia);
    }

    public void deletar(String id, AuthenticatedUser autor) {
        Ideia ideia = buscarPorId(id);
        validarDonoOuLancarErro(ideia, autor.uid());
        exigirPendente(ideia);
        repository.delete(ideia);
    }

    private void exigirPendente(Ideia ideia) {
        if (!"pendente".equals(ideia.getStatus())) {
            throw new IllegalArgumentException("Apenas ideias pendentes podem ser editadas ou excluídas pelo operador.");
        }
    }

    public Ideia atualizarStatus(String id, IdeiaStatusRequest request) {
        Ideia ideia = buscarPorId(id);
        ideia.setStatus(request.status());
        if (request.prioridade() != null) {
            if (request.prioridade() < 0) throw new IllegalArgumentException("Prioridade não pode ser negativa");
            ideia.setPrioridade(request.prioridade());
        }
        return repository.save(ideia);
    }
}
