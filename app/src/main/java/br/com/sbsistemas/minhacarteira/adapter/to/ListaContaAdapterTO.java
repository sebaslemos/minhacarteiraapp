package br.com.sbsistemas.minhacarteira.adapter.to;

import android.support.annotation.NonNull;

import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

/**
 * Created by sebas on 01/09/2017.
 */

public class ListaContaAdapterTO implements Comparable<ListaContaAdapterTO> {

    private Conta conta;
    private Prestacao prestacao;

    public ListaContaAdapterTO(Conta conta, Prestacao prestacao) {
        this.conta = conta;
        this.prestacao = prestacao;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Prestacao getPrestacao() {
        return prestacao;
    }

    public void setPrestacao(Prestacao prestacao) {
        this.prestacao = prestacao;
    }

    @Override
    public int compareTo(@NonNull ListaContaAdapterTO o) {
        int comparacaoData = prestacao.getData().compareTo(o.getPrestacao().getData());
        if(comparacaoData != 0) return comparacaoData;

        //se as datas forem iguais, a conta com menor ID vem primeiro
        return conta.getId().compareTo(o.getConta().getId());
    }
}
