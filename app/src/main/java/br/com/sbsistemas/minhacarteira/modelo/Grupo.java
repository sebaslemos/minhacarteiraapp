package br.com.sbsistemas.minhacarteira.modelo;

import java.io.Serializable;

/**
 * Created by sebas on 04/08/2017.
 */

public class Grupo implements Serializable{

    private String descricao;

    private Long id;




    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return getDescricao();
    }
}
