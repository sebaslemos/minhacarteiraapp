package br.com.sbsistemas.minhacarteira.adapter.listeners;

/**
 * Created by sebas on 06/12/2018.
 */

public interface PrestacaoChangedListener {

    public void atualizaPagoPrestacao(int position, boolean isChecked);

    public void atualizaAtivoPrestacao(int position, boolean isChecked);

}
