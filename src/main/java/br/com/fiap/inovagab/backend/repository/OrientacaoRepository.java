package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.Orientacao;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrientacaoRepository extends MongoRepository<Orientacao, String> {
}
