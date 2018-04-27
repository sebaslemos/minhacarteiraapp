package br.com.sbsistemas.minhacarteira.adapter.to;

import android.support.annotation.NonNull;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 24/08/2017.
 */

public class GrupoAdapterTO implements Comparable<GrupoAdapterTO>{

    private Grupo grupo;
    private int totalContas;
    private BigDecimal totalGastos;

    public GrupoAdapterTO(Grupo grupo, int totalContas, BigDecimal totalGastos){
        this.grupo = grupo;
        this.totalContas = totalContas;
        this.totalGastos = totalGastos;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public int getTotalContas() {
        return totalContas;
    }

    public void setTotalContas(int totalContas) {
        this.totalContas = totalContas;
    }

    public BigDecimal getTotalGastos() {
        return totalGastos;
    }

    public void setTotalGastos(BigDecimal totalGastos) {
        this.totalGastos = totalGastos;
    }

    @Override
    public int compareTo(@NonNull GrupoAdapterTO o) {
        int comparacaoValor =  totalGastos.compareTo(o.getTotalGastos());
        if(comparacaoValor != 0) return comparacaoValor;

        return grupo.getDescricao().compareTo(o.getGrupo().getDescricao());
    }
}
