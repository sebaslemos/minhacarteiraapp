package br.com.sbsistemas.minhacarteira;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DrawableUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.GrupoAdapter;
import br.com.sbsistemas.minhacarteira.adapter.to.GrupoAdapterTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.utils.CorGrupo;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

public class ListaGrupos extends AppCompatActivity {

    private ListView listaGrupos;
    private TextView mesAnoText;
    private BarChart graficoGrupos;

    private LocalDate dataSelecionada;
    private TextView totalReceitasView;
    private TextView saldoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_grupos);

        iniciaComponentes();
        configuraEventos();
    }

    private void configuraEventos() {
        listaGrupos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GrupoAdapterTO grupoTO = (GrupoAdapterTO) listaGrupos.getItemAtPosition(position);
                Grupo grupo = grupoTO.getGrupo();

                if(grupo.getDescricao().equals(GrupoDAO.GRUPO_TODAS)){
                    Intent intent = new Intent(ListaGrupos.this, ListaContasActivity.class);
                    intent.putExtra("data", dataSelecionada);
                    startActivity(intent);
                } else{
                    Intent intent = new Intent(ListaGrupos.this, ListaCategoriasActivity.class);
                    intent.putExtra("data", dataSelecionada);
                    intent.putExtra("grupo", grupo);
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        atualizaTela();
    }

    private void atualizaGrafico() {
        BarData data = new BarData();

        List<BarEntry> entries = new ArrayList<>();
        int numGrupos = listaGrupos.getAdapter().getCount();
        int[] colors = new int[numGrupos - 1];
        CorGrupo corGrupo = new CorGrupo();
        for(int i = 0; i < numGrupos; i++){
            GrupoAdapterTO grupoAdapterTO = (GrupoAdapterTO) listaGrupos.getItemAtPosition(i);

            if(!grupoAdapterTO.getGrupo().getDescricao().equals(GrupoDAO.GRUPO_TODAS)){
                BarEntry barEntry = new BarEntry(i, grupoAdapterTO.getTotalGastos().floatValue());
                barEntry.setIcon(getResources().getDrawable(corGrupo.
                        getIcone(grupoAdapterTO.getGrupo().getDescricao()), null));
                entries.add(barEntry);
                colors[i - 1] = corGrupo.getCor(grupoAdapterTO.getGrupo().getDescricao());
            }
        }
        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(colors);
        data.addDataSet(set);

        graficoGrupos.setData(data);
        graficoGrupos.invalidate();
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
        BigDecimal gastos = controladorGrupo.getTotalGastosDoGrupo(controladorGrupo.getGrupo(GrupoDAO.GRUPO_TODAS),
                dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());

        BigDecimal saldo = totalReceitas.subtract(gastos);
        saldoView.setText(String.format("Saldo R$ %.2f", saldo.doubleValue()));
    }

    private void atualizaTela(){
        carregaListaGrupos();
        atualizaGrafico();
        atualizaReceitasESaldo();
    }

    private void carregaListaGrupos() {
        ControladorGrupo controladorGrupo = new ControladorGrupo(this);
        List<Grupo> grupos = controladorGrupo.getGrupos();
        List<GrupoAdapterTO> grupoAdapterTOs = new ArrayList<GrupoAdapterTO>();

        for(Grupo grupo : grupos){
            int totalDeContas = controladorGrupo.totalDeContas(grupo, dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            BigDecimal totalGastosDoGrupo = controladorGrupo.getTotalGastosDoGrupo(grupo, dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            GrupoAdapterTO grupoTO = new GrupoAdapterTO(grupo, totalDeContas, totalGastosDoGrupo);

            grupoAdapterTOs.add(grupoTO);
        }

        //apresenta os grupos em ordem de maior gasto
        Collections.sort(grupoAdapterTOs);
        Collections.reverse(grupoAdapterTOs);
        GrupoAdapter adapter = new GrupoAdapter(this, grupoAdapterTOs.toArray(new GrupoAdapterTO[grupoAdapterTOs.size()]));
        listaGrupos.setAdapter(adapter);
    }

    private void iniciaComponentes() {
        //configura a data atual
        dataSelecionada = LocalDate.now();

        listaGrupos = (ListView) findViewById(R.id.lista_grupos_lista);
        mesAnoText = (TextView) findViewById(R.id.lista_grupos_mes_ano);
        mesAnoText.setText(new LocalDateUtils(null).getMesAno(dataSelecionada));
        saldoView = (TextView) findViewById(R.id.lista_grupos_saldo);
        totalReceitasView = (TextView) findViewById(R.id.lista_grupos_recebido);

        graficoGrupos = (BarChart) findViewById(R.id.lista_grupos_grafico);

        YAxis yAxisLeft = graficoGrupos.getAxisLeft();
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setEnabled(true);

        YAxis yAxisRight = graficoGrupos.getAxisRight();
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setEnabled(false);

        XAxis xAxis = graficoGrupos.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawLabels(false);

        graficoGrupos.setFitBars(true);
        graficoGrupos.getLegend().setEnabled(false);
    }
    
    
}
