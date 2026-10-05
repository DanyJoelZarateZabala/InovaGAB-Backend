package br.com.fiap.inovagab.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Orientação estratégica da empresa.
 * CRUD exclusivo da liderança; demais perfis apenas consultam.
 * Campos adicionais (categoria, campanha) atendem ao requisito de
 * "registro histórico das estratégias (id, data, categoria, campanha)".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orientacoes")
public class Orientacao {

    @Id
    private String id;

    private String titulo;

    private String descricao;

    /** categoria da estratégia, ex: "expansão", "eficiência", "sustentabilidade" */
    private String categoria;

    /** campanha/iniciativa associada, se houver */
    private String campanha;

    private Long dataCriacao;

    /** Nunca remover fisicamente: preserva o histórico e os vínculos existentes. */
    private boolean vigente = true;
}
