package br.com.fiap.inovagab.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Ideia de inovação / problema cadastrado por um operador.
 * status: "pendente", "aprovada", "rejeitada" (mesmos valores do app)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "ideias")
public class Ideia {

    @Id
    private String id;

    private String titulo;

    private String descricao;

    private String autorUid;

    private String autorNome;

    /** "pendente", "aprovada", "rejeitada" */
    private String status = "pendente";

    private int prioridade;

    /** vínculo com a orientação/estratégia vigente */
    private String orientacaoId;

    private Long dataCriacao;
}
