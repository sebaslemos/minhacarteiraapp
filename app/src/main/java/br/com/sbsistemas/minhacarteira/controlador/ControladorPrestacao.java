package br.com.sbsistemas.minhacarteira.controlador;

import android.content.Context;

import br.com.sbsistemas.minhacarteira.dao.PrestacoesDAO;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

/**
 * Created by sebas on 24/08/2017.
 */

public class ControladorPrestacao {

    private Context context;

    public ControladorPrestacao(Context context){
        this.context = context;
    }


    public Prestacao getPrestacao(Conta conta, int mes, int ano) {
        PrestacoesDAO prestacoesDAO = new PrestacoesDAO(context);
        Prestacao prestacao = prestacoesDAO.getPrestacao(conta, mes, ano);
        prestacoesDAO.close();

        return prestacao;
    }

    public void atualiza(Prestacao prestacao) {
        PrestacoesDAO dao = new PrestacoesDAO(context);
        dao.atualiza(prestacao);
        dao.close();
    }
}
