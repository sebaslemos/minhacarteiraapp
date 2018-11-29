package br.com.sbsistemas.minhacarteira;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.adapter.ListaContasAdapter;
import br.com.sbsistemas.minhacarteira.adapter.listeners.CheckPagoListener;
import br.com.sbsistemas.minhacarteira.adapter.to.ListaContaAdapterTO;
import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.controlador.ControladorPrestacao;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.helpers.GraficoContasHelper;
import br.com.sbsistemas.minhacarteira.helpers.ListaContaHelper;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

import static android.widget.Toast.LENGTH_LONG;

public class ListaContasActivity extends AppCompatActivity implements
        AbsListView.MultiChoiceModeListener, AdapterView.OnItemClickListener,
        CheckPagoListener{

    private Categoria categoriaSelecionada;
    private LocalDate dataSelecionada;
    private ControladorConta controladorConta;

    private ListaContaHelper listaContasHelper;
    private TextView mesAnoTextView;
    private TextView totalReceitasView;
    private TextView saldoView;

    private GraficoContasHelper graficohelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_contas);

        inicializaComponentes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizaTela();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_grupo, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.menu_lista_grupo_add_receita:
                intent = new Intent(this, ListaReceitasActivit.class);
                intent.putExtra("data", dataSelecionada);
                startActivity(intent);
                break;
            case R.id.menu_lista_grupo_add_conta:
                intent = new Intent(this, FormularioContaActivity.class);
                startActivity(intent);
                break;
            default:
                return true;
        }
        return true;
    }

    public void selecionaData(View v){
        LocalDate hoje = LocalDate.now();

        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        dataSelecionada = new LocalDate(year, month + 1, dayOfMonth);
                        atualizaTela();
                    }
                }, hoje.getYear(), hoje.getMonthOfYear() - 1, hoje.getDayOfMonth());

        dialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        dialog.show();
    }

    public void voltaUmMes(View v){
        dataSelecionada = dataSelecionada.minusMonths(1);
        atualizaTela();
    }

    public void avancaUmMes(View v){
        dataSelecionada = dataSelecionada.plusMonths(1);
        atualizaTela();
    }

    private void atualizaReceitasESaldo() {
        ControladorReceitas controladorReceitas = new ControladorReceitas(this);
        BigDecimal totalReceitas =
                controladorReceitas.getTotalReceitas(dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());

        String totalFormatado = String.format("Receitas R$ %.2f", totalReceitas.doubleValue());
        totalReceitasView.setText(totalFormatado);

        ControladorGrupo controladorGrupo = new ControladorGrupo(this);
        QuantidadeValorTO quantidadeValorTO = controladorGrupo.getQuantidadeValor(controladorGrupo.getGrupo(GrupoDAO.GRUPO_TODAS),
                dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());

        BigDecimal saldo = totalReceitas.subtract(new BigDecimal(quantidadeValorTO.getValor()));
        saldoView.setText(String.format("Saldo R$ %.2f", saldo.doubleValue()));
    }

    private void atualizaTela(){
        atualizaData();
        listaContasHelper.atualizalistaDeContas(categoriaSelecionada, dataSelecionada);
        graficohelper.atualizaGrafico(dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
        atualizaReceitasESaldo();
    }

    private void atualizaData() {
        mesAnoTextView.setText(new LocalDateUtils(null).getMesAno(dataSelecionada));
    }

    private void inicializaComponentes() {
        controladorConta = new ControladorConta(this);
        categoriaSelecionada = (Categoria) getIntent().getExtras().get("categoria");
        dataSelecionada = (LocalDate) getIntent().getExtras().get("data");
        if(dataSelecionada == null) dataSelecionada = LocalDate.now();

        mesAnoTextView = (TextView) findViewById(R.id.lista_contas_mes_ano);
        mesAnoTextView.setText(LocalDateUtils.getMesAno(dataSelecionada));
        totalReceitasView = (TextView) findViewById(R.id.lista_contas_recebido);
        saldoView = (TextView) findViewById(R.id.lista_contas_saldo);

        graficohelper = new GraficoContasHelper(this, categoriaSelecionada);
        listaContasHelper = new ListaContaHelper(this);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        ListaContasAdapter mAdaper = listaContasHelper.getAdapter();

        mAdaper.alternarSelecao(position);

        String msg = mAdaper.getTotalSelecionadas() > 1 ? " conta selecionada" : " contas selecionadas";
        mode.setTitle(mAdaper.getTotalSelecionadas() + msg);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deletar, menu);
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
                listaContasHelper.getContasListView().getCheckedItemPositions();
        deletadasMsg.append("Contas deletadas: ");


        for(int i = 0; i < listaContasHelper.getAdapter().getCount(); i++){
            if(checkedItemPositions.get(i)){
                ListaContaAdapterTO contaSelecionadaTO = listaContasHelper.getContaNaPosicao(i);
                Conta contaSelecionada = contaSelecionadaTO.getConta();
                controladorConta.deletar(contaSelecionada);
                deletadasMsg.append(contaSelecionada.getDescricao() + " ,");
            }
        }

        atualizaTela();
        deletadasMsg = deletadasMsg.deleteCharAt(deletadasMsg.length() - 1);
        Toast.makeText(ListaContasActivity.this, deletadasMsg.toString(), LENGTH_LONG).show();
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listaContasHelper.getAdapter().removeSelection();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListaContaAdapterTO contaTO = listaContasHelper.getContaNaPosicao(position);

        Intent intent = new Intent(ListaContasActivity.this, EditaContaActivity.class);
        intent.putExtra("conta", contaTO.getConta());
        startActivity(intent);
    }

    @Override
    public void onPagoChecked(int posicaoPrestacao, boolean isChecked) {
        ListaContaAdapterTO contaTO = listaContasHelper.getContaNaPosicao(posicaoPrestacao);
        Prestacao prestacaoParaAtualizar = contaTO.getPrestacao();
        ControladorPrestacao controladorPrestacao = new ControladorPrestacao(this);
        prestacaoParaAtualizar.setPago(isChecked);
        controladorPrestacao.atualiza(prestacaoParaAtualizar);
    }
}
