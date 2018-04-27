package br.com.sbsistemas.minhacarteira.adapter.to;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 06/11/2017.
 */

public class EstatisticaTO {

    private BigDecimal valor;
    private LocalDate data;

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        String texto = "";

        if(valor.floatValue() == 0 ||
                valor.floatValue() > 1000000000){
            texto = "Sem contas anteriores";
        } else{
            texto = String.format("R$ %.2f " + LocalDateUtils.getMesAnoAbreviado(data),
                    valor.floatValue());
        }

        return texto;
    }
}
