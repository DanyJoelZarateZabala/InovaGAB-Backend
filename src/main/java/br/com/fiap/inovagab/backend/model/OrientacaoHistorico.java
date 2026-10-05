package br.com.fiap.inovagab.backend.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/** Fotografia da orientação ANTES de cada alteração ou desativação. */
@Data
@Document(collection = "orientacoes_historico")
public class OrientacaoHistorico {
    @Id private String id;
    private String orientacaoId;
    private String titulo;
    private String descricao;
    private String categoria;
    private String campanha;
    private Long dataCriacao;
    private Long dataAlteracao;
    private boolean vigente;
    private String evento;
}
