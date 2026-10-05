package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.Ideia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeiaRepository extends MongoRepository<Ideia, String> {
    List<Ideia> findByAutorUid(String autorUid);
    List<Ideia> findByStatus(String status);
}
