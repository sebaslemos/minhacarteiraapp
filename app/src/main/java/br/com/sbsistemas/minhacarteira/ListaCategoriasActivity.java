package br.com.sbsistemas.minhacarteira;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.adapter.to.CategoriaAdapterTO;
import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.helpers.EstatisticasCategoriaHelper;
import br.com.sbsistemas.minhacarteira.helpers.GraficoCategoriaHelper;
import br.com.sbsistemas.minhacarteira.helpers.ListaCategoriasHelper;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

public class ListaCategoriasActivity extends AppCompatActivity {

    private Grupo grupoSelecionado;

    private LocalDate dataSelecionada;

    private TextView mesAnoTextView;
    private TextView totalReceitasView;
    private TextView saldoView;

    private ListaCategoriasHelper listaCategoriasHelper;
    private GraficoCategoriaHelper graficoCategoriasHelper;
    private EstatisticasCategoriaHelper estatisticasCategoriaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_categorias);

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

    public void atualizaTela(){
        listaCategoriasHelper.carregaListaDeCategorias(grupoSelecionado, dataSelecionada);
        estatisticasCategoriaHelper.atualizaEstatisticas(null, grupoSelecionado, dataSelecionada);
        graficoCategoriasHelper.atualizaGrafico(listaCategoriasHelper.getCategorias());
        mesAnoTextView.setText(new LocalDateUtils(null).getMesAno(dataSelecionada));
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

    private void inicializaComponentes() {
        grupoSelecionado = (Grupo) getIntent().getExtras().get("grupo");
        dataSelecionada = (LocalDate) getIntent().getExtras().get("data");
        if(dataSelecionada == null) dataSelecionada = LocalDate.now();

        mesAnoTextView = (TextView) findViewById(R.id.lista_categoria_mes_ano);
        mesAnoTextView.setText(LocalDateUtils.getMesAno(dataSelecionada));
        totalReceitasView = (TextView) findViewById(R.id.lista_categoria_recebido);
        saldoView = (TextView) findViewById(R.id.lista_categoria_saldo);

        listaCategoriasHelper = new ListaCategoriasHelper(this);
        estatisticasCategoriaHelper = new EstatisticasCategoriaHelper(this);
        graficoCategoriasHelper = new GraficoCategoriaHelper(this, grupoSelecionado, estatisticasCategoriaHelper);
    }

    public LocalDate getDataSelecionada() {
        return dataSelecionada;
    }

    public Grupo getGrupoSelecionado() {
        return grupoSelecionado;
    }

}
