package br.com.sbsistemas.minhacarteira.modelo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by sebas on 04/08/2017.
 */

public class Conta implements Serializable {

    private Long id;
    private String descricao;
    private Integer numeroDePrestacoes;
    private BigDecimal valor;
    private Long categoriaId;
    private Long tagId;

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

    public Integer getNumeroDePrestacoes() {
        return numeroDePrestacoes;
    }

    public void setNumeroDePrestacoes(Integer numeroDePrestacoes) {
        this.numeroDePrestacoes = numeroDePrestacoes;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Long getCategoria() {
        return categoriaId;
    }

    public void setCategoria(Long categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Long getTag() {
        return tagId;
    }

    public void setTag(Long tagId) {
        this.tagId = tagId;
    }

    public String getValorFormatado(){
        return String.format("R$ %.2f", this.valor.doubleValue());
    }

}
