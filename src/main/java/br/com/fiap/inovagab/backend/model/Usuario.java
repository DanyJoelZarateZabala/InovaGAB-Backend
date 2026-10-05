package br.com.fiap.inovagab.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Representa um usuário da plataforma.
 * perfil aceita: "operador", "gestor", "lider"
 * (mesmos valores usados no app Android, em Usuario.kt)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "usuarios")
public class Usuario {

    @Id
    private String uid;

    private String nome;

    private String email;

    /** senha armazenada como hash (BCrypt) - nunca em texto puro */
    private String senhaHash;

    /** "operador", "gestor" ou "lider" */
    private String perfil;
}
