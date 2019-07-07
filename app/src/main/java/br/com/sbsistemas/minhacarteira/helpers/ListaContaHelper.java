package br.com.sbsistemas.minhacarteira.helpers;

import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.util.Collections;
import java.util.List;

import br.com.sbsistemas.minhacarteira.EditaContaActivity;
import br.com.sbsistemas.minhacarteira.ListaContasActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.ListaContasAdapter;
import br.com.sbsistemas.minhacarteira.adapter.listeners.CheckPagoListener;
import br.com.sbsistemas.minhacarteira.adapter.to.ListaContaAdapterTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.controlador.ControladorPrestacao;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Created by sebas on 22/11/2018.
 */

public class ListaContaHelper implements CheckPagoListener, AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener {

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
        contasListView.setMultiChoiceModeListener(this);
        contasListView.setOnItemClickListener(this);
    }

    public void atualizalistaDeContas(Categoria categoriaSelecionada, LocalDate dataSelecionada){
        ControladorConta controladorConta = new ControladorConta(contaActivity);

        List<ListaContaAdapterTO> listaContaAdapterTOs = controladorConta.getContas(categoriaSelecionada,
                dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());

        Collections.sort(listaContaAdapterTOs);
        ListaContasAdapter adapter = new ListaContasAdapter(contaActivity, listaContaAdapterTOs, this);
        contasListView.setAdapter(adapter);
    }

    public ListaContasAdapter getAdapter(){
        return (ListaContasAdapter) contasListView.getAdapter();
    }

    public ListaContaAdapterTO getContaNaPosicao(int posicao){
        return getAdapter().getItem(posicao);
    }

    @Override
    public void onPagoChecked(int posicaoPrestacao, boolean isChecked) {
        ListaContaAdapterTO contaTO = getContaNaPosicao(posicaoPrestacao);
        Prestacao prestacaoParaAtualizar = contaTO.getPrestacao();
        ControladorPrestacao controladorPrestacao = new ControladorPrestacao(contaActivity);
        prestacaoParaAtualizar.setPago(isChecked);
        controladorPrestacao.atualiza(prestacaoParaAtualizar);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListaContaAdapterTO contaTO = getContaNaPosicao(position);

        Intent intent = new Intent(contaActivity, EditaContaActivity.class);
        intent.putExtra("conta", contaTO.getConta());
        contaActivity.startActivity(intent);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        ListaContasAdapter mAdaper = getAdapter();

        mAdaper.alternarSelecao(position);

        String msg = mAdaper.getTotalSelecionadas() > 1 ? " conta selecionada" : " contas selecionadas";
        mode.setTitle(mAdaper.getTotalSelecionadas() + msg);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        contaActivity.getMenuInflater().inflate(R.menu.menu_deletar, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        StringBuilder deletadasMsg = new StringBuilder();
        SparseBooleanArray checkedItemPositions =
                getContasListView().getCheckedItemPositions();
        deletadasMsg.append("Contas deletadas: ");


        for(int i = 0; i < getAdapter().getCount(); i++){
            if(checkedItemPositions.get(i)){
                ListaContaAdapterTO contaSelecionadaTO = getContaNaPosicao(i);
                Conta contaSelecionada = contaSelecionadaTO.getConta();
                new ControladorConta(contaActivity).deletar(contaSelecionada);
                deletadasMsg.append(contaSelecionada.getDescricao() + " ,");
            }
        }

        contaActivity.atualizaTela();
        deletadasMsg = deletadasMsg.deleteCharAt(deletadasMsg.length() - 1);
        Toast.makeText(contaActivity, deletadasMsg.toString(), LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        getAdapter().removeSelection();
    }
}
