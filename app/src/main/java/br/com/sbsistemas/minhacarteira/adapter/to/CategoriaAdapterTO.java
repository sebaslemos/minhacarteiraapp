package br.com.sbsistemas.minhacarteira.adapter.to;

import androidx.annotation.NonNull;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.modelo.Categoria;

/**
 * Created by sebas on 31/08/2017.
 */

public class CategoriaAdapterTO implements Comparable<CategoriaAdapterTO>{

    private Categoria categoria;
    private int totalContas;
    private BigDecimal totalGastos;
    private int backgroundColor;

    public CategoriaAdapterTO(Categoria categoria, int totalContas, BigDecimal totalGastos){
        this.categoria = categoria;
        this.totalContas = totalContas;
        this.totalGastos = totalGastos;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
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
    public int compareTo(@NonNull CategoriaAdapterTO o) {
        int comparacaoValor = totalGastos.compareTo(o.getTotalGastos());
        if(comparacaoValor != 0) return comparacaoValor;

        return categoria.getDescricao().compareTo(o.getCategoria().getDescricao());
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
}
