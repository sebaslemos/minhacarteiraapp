package br.com.sbsistemas.minhacarteira.helpers;

import android.widget.ListView;

import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.List;

import br.com.sbsistemas.minhacarteira.ListaContasActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.ListaContasAdapter;
import br.com.sbsistemas.minhacarteira.adapter.to.ListaContaAdapterTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.controlador.ControladorPrestacao;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;

/**
 * Created by sebas on 22/11/2018.
 */

public class ListaContaHelper {

    private ListaContasActivity contaActivity;

    public ListView getContasListView() {
        return contasListView;
    }

    private ListView contasListView;

    public ListaContaHelper(ListaContasActivity context){

        contaActivity = context;
        contasListView = (ListView) contaActivity.findViewById(R.id.lista_contas_lista);
        configuraEventos();
    }

    private void configuraEventos() {
        contasListView.setMultiChoiceModeListener(contaActivity);

        contasListView.setOnItemClickListener(contaActivity);
    }

    public void atualizalistaDeContas(Categoria categoriaSelecionada, LocalDate dataSelecionada){
        ControladorConta controladorConta = new ControladorConta(contaActivity);
        ControladorPrestacao controladorPrestacao = new ControladorPrestacao(contaActivity);

        List<ListaContaAdapterTO> listaContaAdapterTOs = controladorConta.getContas(categoriaSelecionada,
                dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());

        Collections.sort(listaContaAdapterTOs);
        ListaContasAdapter adapter = new ListaContasAdapter(contaActivity, listaContaAdapterTOs);
        contasListView.setAdapter(adapter);
    }

    public ListaContasAdapter getAdapter(){
        return (ListaContasAdapter) contasListView.getAdapter();
    }

    public ListaContaAdapterTO getContaNaPosicao(int posicao){
        return getAdapter().getItem(posicao);
    }
}
