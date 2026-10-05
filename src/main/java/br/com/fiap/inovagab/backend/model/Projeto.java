package br.com.fiap.inovagab.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Projeto/iniciativa cadastrado por um gestor, a partir de uma ideia aprovada.
 * status: "em_andamento", "concluido", "pausado" (livre, definido pelo gestor)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "projetos")
public class Projeto {

    @Id
    private String id;

    private String titulo;

    private String descricao;

    private String etapa;

    private String status = "em_andamento";

    private double investimento;

    private double retornoFinanceiro;

    private double ganhosProdutividade;

    private String prazo;

    private String gestorUid;

    /** vínculo com a orientação/estratégia vigente */
    private String orientacaoId;

    /** vínculo com a ideia que originou o projeto, se houver */
    private String ideiaId;

    private Long dataCriacao;

    /** Relato escrito pelo gestor sobre resultados concretos da iniciativa. */
    private String resultadosObtidos;
}
