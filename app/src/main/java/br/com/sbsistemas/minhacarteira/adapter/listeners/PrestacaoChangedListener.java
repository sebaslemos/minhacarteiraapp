package br.com.sbsistemas.minhacarteira.adapter.listeners;

import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

/**
 * Created by sebas on 06/12/2018.
 */

public interface PrestacaoChangedListener {

    public void atualizaPagoPrestacao(int position, boolean isChecked);

    public void atualizaAtivoPrestacao(int position, boolean isChecked);

}
