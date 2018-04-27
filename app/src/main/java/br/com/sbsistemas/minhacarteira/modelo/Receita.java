package br.com.sbsistemas.minhacarteira.modelo;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Created by sebas on 05/08/2017.
 */

public class Receita {

    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
