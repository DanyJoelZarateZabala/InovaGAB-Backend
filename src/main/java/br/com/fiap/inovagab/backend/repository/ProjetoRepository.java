package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.Projeto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjetoRepository extends MongoRepository<Projeto, String> {
    List<Projeto> findByGestorUid(String gestorUid);
    List<Projeto> findByOrientacaoId(String orientacaoId);
}
