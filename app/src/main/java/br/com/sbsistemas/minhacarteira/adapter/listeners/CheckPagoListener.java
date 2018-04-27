package br.com.sbsistemas.minhacarteira.adapter.listeners;

import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

/**
 * Created by sebas on 07/09/2017.
 */

public interface CheckPagoListener {

    public void onPagoChecked(int prestacaoParaAtualizar, boolean isChecked);
}
