package br.com.sbsistemas.minhacarteira.modelo;

import java.io.Serializable;

/**
 * Created by sebas on 04/08/2017.
 */

public class Categoria implements Serializable{

    private Long id;
    private Long idGrupo;
    private String descricao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Long idGrupo) {
        this.idGrupo = idGrupo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
