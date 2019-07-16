package br.com.sbsistemas.minhacarteira;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.helpers.GraficoGruposHelper;
import br.com.sbsistemas.minhacarteira.helpers.ListaGruposHelper;
import br.com.sbsistemas.minhacarteira.notificacoes.ContasAPagarNotificationHandler;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

public class ListaGrupos extends AppCompatActivity {

    private TextView mesAnoText;
    private ListaGruposHelper listaGruposHelper;
    private GraficoGruposHelper graficoHelper;

    private LocalDate dataSelecionada;
    private TextView totalReceitasView;
    private TextView saldoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_grupos);

        iniciaComponentes();
        ContasAPagarNotificationHandler.agendarNotificacao(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizaTela();
    }

    private void atualizaGrafico() {
        graficoHelper.atualizaGrafico(listaGruposHelper.getGrupos());
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
                mesAnoText.setText(LocalDateUtils.getMesAno(dataSelecionada));
                atualizaTela();
            }
        }, hoje.getYear(), hoje.getMonthOfYear() - 1, hoje.getDayOfMonth());

        dialog.getDatePicker().findViewById(getResources().getIdentifier("day","id","android")).setVisibility(View.GONE);
        dialog.show();
    }

    public void voltaUmMes(View v){
        dataSelecionada = dataSelecionada.minusMonths(1);
        mesAnoText.setText(new LocalDateUtils(null).getMesAno(dataSelecionada));
        atualizaTela();
    }

    public void avancaUmMes(View v){
        dataSelecionada = dataSelecionada.plusMonths(1);
        mesAnoText.setText(new LocalDateUtils(null).getMesAno(dataSelecionada));
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
        listaGruposHelper.carregaListaGrupos(dataSelecionada);
        atualizaGrafico();
        atualizaReceitasESaldo();
    }

    private void iniciaComponentes() {
        //configura a data atual
        dataSelecionada = LocalDate.now();

        listaGruposHelper = new ListaGruposHelper(this);
        mesAnoText = (TextView) findViewById(R.id.lista_grupos_mes_ano);
        mesAnoText.setText(new LocalDateUtils(null).getMesAno(dataSelecionada));
        saldoView = (TextView) findViewById(R.id.lista_grupos_saldo);
        totalReceitasView = (TextView) findViewById(R.id.lista_grupos_recebido);
        graficoHelper = new GraficoGruposHelper(this);
    }

    public LocalDate getDataSelecionada() {
        return dataSelecionada;
    }

}
