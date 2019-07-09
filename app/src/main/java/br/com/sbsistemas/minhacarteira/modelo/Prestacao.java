package br.com.sbsistemas.minhacarteira.modelo;

import org.joda.time.LocalDate;

import java.io.Serializable;

/**
 * Created by sebas on 04/08/2017.
 */

public class Prestacao implements Serializable {

    private Long id;
    private Long contaId;
    private boolean pago;
    private LocalDate data;
    private Integer numParcela;
    private boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaID(Long contaId) {
        this.contaId = contaId;
    }

    public boolean isPago() {
        return pago;
    }

    public void setPago(boolean pago) {
        this.pago = pago;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public Integer getNumParcela() {
        return numParcela;
    }

    public void setNumParcela(Integer numParcela) {
        this.numParcela = numParcela;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}
