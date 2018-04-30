package br.com.sbsistemas.minhacarteira;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;
import java.util.zip.Inflater;

import br.com.sbsistemas.minhacarteira.adapter.ListaReceitasAdapter;
import br.com.sbsistemas.minhacarteira.adapter.to.ListaContaAdapterTO;
import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Receita;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

import static android.widget.Toast.LENGTH_LONG;

public class ListaReceitasActivit extends AppCompatActivity {

    private LocalDate dataSelecionada;

    private ListView receitasListView;
    private TextView mesAnoText;
    private TextView totalReceitasView;
    private TextView saldoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_receitas);

        inicializaComponentes();
        configuraEventos();
    }

    private void configuraEventos() {
        receitasListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                if(checked){
                    receitasListView.getChildAt(position).setBackgroundColor(Color.parseColor("#3F51B5"));
                } else{
                    receitasListView.getChildAt(position).setBackground(null);
                }
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
                SparseBooleanArray checkedItemPositions = receitasListView.getCheckedItemPositions();
                deletadasMsg.append("Receitas deletadas: ");

                ControladorReceitas controladorReceitas = new ControladorReceitas(ListaReceitasActivit.this);
                for(int i = 0; i < receitasListView.getAdapter().getCount(); i++){
                    if(checkedItemPositions.get(i)){
                        Receita receitaSelecionada =
                                (Receita) receitasListView.getItemAtPosition(i);
                        controladorReceitas.deletar(receitaSelecionada);
                        deletadasMsg.append(receitaSelecionada.getDescricao() + " ,");
                    }
                }
                atualizaTela();
                deletadasMsg = deletadasMsg.deleteCharAt(deletadasMsg.length() - 1);
                Toast.makeText(ListaReceitasActivit.this, deletadasMsg.toString(), LENGTH_LONG).show();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                int totalContas = receitasListView.getAdapter().getCount();
                for(int i = 0; i < totalContas; i++){
                    receitasListView.getChildAt(i).setBackground(null);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        atualizaTela();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_receitas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, FormularioReceiraActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void selecionaData(View v){
        LocalDate hoje = LocalDate.now();

        DatePickerDialog dialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        dataSelecionada = new LocalDate(year, month + 1, 1);
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

    private void atualizaTela() {
        carregaLista();
        atualizaData();
        atualizaReceitasESaldo();
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

    private void atualizaData() {
        mesAnoText.setText(LocalDateUtils.getMesAno(dataSelecionada));
    }

    private void carregaLista() {
        ControladorReceitas controladorReceitas = new ControladorReceitas(this);
        List<Receita> receitas = controladorReceitas.getReceitas(dataSelecionada.getMonthOfYear(),
                dataSelecionada.getYear());

        ListaReceitasAdapter adapter = new ListaReceitasAdapter(this, receitas);
        receitasListView.setAdapter(adapter);
    }

    private void inicializaComponentes() {
        receitasListView = (ListView) findViewById(R.id.lista_receitas_lista);
        mesAnoText = (TextView) findViewById(R.id.lista_receitas_mes_ano);
        totalReceitasView = (TextView) findViewById(R.id.lista_receitas_recebido);
        saldoView = (TextView) findViewById(R.id.lista_receitas_saldo);

        dataSelecionada = (LocalDate) getIntent().getExtras().get("data");
        if(dataSelecionada == null) dataSelecionada = LocalDate.now();
    }
}
