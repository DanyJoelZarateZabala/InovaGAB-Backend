package br.com.fiap.inovagab.backend.repository;

import br.com.fiap.inovagab.backend.model.OrientacaoHistorico;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrientacaoHistoricoRepository extends MongoRepository<OrientacaoHistorico, String> {
    List<OrientacaoHistorico> findByOrientacaoIdOrderByDataAlteracaoDesc(String orientacaoId);
}
