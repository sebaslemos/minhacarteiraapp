package br.com.sbsistemas.minhacarteira;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.helpers.GraficoContasHelper;
import br.com.sbsistemas.minhacarteira.helpers.ListaContaHelper;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

public class ListaContasActivity extends AppCompatActivity {

    private Categoria categoriaSelecionada;
    private LocalDate dataSelecionada;
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

    public void atualizaTela(){
        atualizaData();
        listaContasHelper.atualizalistaDeContas(categoriaSelecionada, dataSelecionada);
        graficohelper.atualizaGrafico(dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
        atualizaReceitasESaldo();
    }

    private void atualizaData() {
        mesAnoTextView.setText(LocalDateUtils.getMesAno(dataSelecionada));
    }

    private void inicializaComponentes() {
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

}
