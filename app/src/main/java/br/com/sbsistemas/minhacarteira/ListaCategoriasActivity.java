package br.com.sbsistemas.minhacarteira;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.ListaCategoriaAdapter;
import br.com.sbsistemas.minhacarteira.adapter.to.EstatisticaTO;
import br.com.sbsistemas.minhacarteira.adapter.to.ListaCategoriaAdapterTO;
import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.utils.ColorGradientGenerator;
import br.com.sbsistemas.minhacarteira.utils.CorGrupo;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_maior;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_media;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_menor;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_mes_anterior;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_mes_atual;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_titulo;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_grafico;

public class ListaCategoriasActivity extends AppCompatActivity {


    private Grupo grupoSelecionado;
    private LocalDate dataSelecionada;

    private ListView listaCategorias;
    private TextView mesAnoTextView;
    private TextView totalReceitasView;
    private TextView saldoView;

    private PieChart graficoCategorias;
    private TextView tituloEstatistica;
    private TextView valorAtualEstatistica;
    private TextView valorMesAnteriorEstatistica;
    private TextView menorValorEstatistica;
    private TextView maiorValorEstatistica;
    private TextView mediaEstatistica;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_categorias);

        inicializaComponentes();
        ocnfiguraEventos();
    }

    private void ocnfiguraEventos() {
        listaCategorias.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListaCategoriaAdapterTO categoriaTOClicada =
                        (ListaCategoriaAdapterTO) listaCategorias.getItemAtPosition(position);
                Categoria categoria = categoriaTOClicada.getCategoria();

                Intent intent = new Intent(ListaCategoriasActivity.this, ListaContasActivity.class);
                intent.putExtra("categoria", categoria);
                intent.putExtra("data", dataSelecionada);
                startActivity(intent);
            }
        });

        graficoCategorias.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Long idCategoria = (Long) e.getData();
                Categoria categoriaSelecionada =
                        new ControladorCategoria(ListaCategoriasActivity.this).getCategoria(idCategoria);

                atualizaEstatisticas(categoriaSelecionada);
            }

            @Override
            public void onNothingSelected() {
                atualizaEstatisticas(null);
            }
        });
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
        carregaListaDeCategorias();
        atualizaData();
        atualizaReceitasESaldo();
        atualizaGrafico();
        atualizaEstatisticas(null);
    }

    /**
     * Ultimos oito meses
     * @param categoria
     */
    private void atualizaEstatisticas(Categoria categoria) {

        BigDecimal totalAtual = new BigDecimal(0);
        BigDecimal totalMesAnterior = new BigDecimal(0);
        EstatisticaTO menorValor = new EstatisticaTO();
        EstatisticaTO maiorValor = new EstatisticaTO();
        BigDecimal media = new BigDecimal(0);
        String titulo = "";

        if(categoria == null){//mostra estatisticas do grupo
            ControladorGrupo controladorGrupo = new ControladorGrupo(this);
            titulo = grupoSelecionado.getDescricao();
            totalAtual = new BigDecimal(controladorGrupo.getQuantidadeValor(grupoSelecionado,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear()).getValor());
            totalMesAnterior = new BigDecimal(controladorGrupo.getQuantidadeValor(grupoSelecionado,
                    dataSelecionada.minusMonths(1).getMonthOfYear(), dataSelecionada.minusMonths(1).getYear()).getValor());
            menorValor = controladorGrupo.calculaMenorValorGasto(grupoSelecionado,
                    dataSelecionada);
            maiorValor = controladorGrupo.calculaMaiorValorGasto(grupoSelecionado,
                    dataSelecionada);
            media = controladorGrupo.calculaMedia(grupoSelecionado, dataSelecionada);
        } else{
            //mostra estatistica da categoria selecionada
            ControladorCategoria ctrlCategoria = new ControladorCategoria(this);
            titulo = categoria.getDescricao();
            totalAtual = ctrlCategoria.getTotalGastosCategoria(categoria,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            totalMesAnterior = ctrlCategoria.getTotalGastosCategoria(categoria,
                    dataSelecionada.minusMonths(1).getMonthOfYear(), dataSelecionada.minusMonths(1).getYear());
            menorValor = ctrlCategoria.calculaMenorValorGasto(categoria, dataSelecionada);
            maiorValor = ctrlCategoria.calculaMaiorValorGasto(categoria, dataSelecionada);
            media = ctrlCategoria.calculaMedia(categoria, dataSelecionada);
        }

        tituloEstatistica.setText(titulo);
        valorAtualEstatistica.setText(String.format("Atual: R$ %.2f", totalAtual.floatValue()));
        valorMesAnteriorEstatistica.setText(String.format("Anterior: R$ %.2f", totalMesAnterior.floatValue()));
        maiorValorEstatistica.setText("Maior: " + maiorValor.toString());
        menorValorEstatistica.setText("Menor: " + menorValor.toString());
        mediaEstatistica.setText(String.format("Média: R$ %.2f", media.floatValue()));
    }

    private void atualizaGrafico() {
        graficoCategorias.setRotationEnabled(true);
        graficoCategorias.setHoleRadius(50f);
        graficoCategorias.setTransparentCircleAlpha(0);
        graficoCategorias.setCenterText(grupoSelecionado.getDescricao());
        graficoCategorias.setCenterTextSize(13);
        graficoCategorias.getDescription().setEnabled(false);
        graficoCategorias.getLegend().setEnabled(false);
        graficoCategorias.setDrawEntryLabels(false);
        graficoCategorias.setUsePercentValues(true);

        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<Integer> cores = new ArrayList<>();

        //valores do grafico
        ListaCategoriaAdapter adapter = (ListaCategoriaAdapter) listaCategorias.getAdapter();
        for(int i = 0; i < adapter.getCount(); i++){
            ListaCategoriaAdapterTO categoriaTO = adapter.getItem(i);

            if(categoriaTO.getTotalGastos().equals(new BigDecimal(0))) continue;

            PieEntry entry = new PieEntry(categoriaTO.getTotalGastos().floatValue(),
                    categoriaTO.getCategoria().getId());

            yEntries.add(entry);
            cores.add(categoriaTO.getBackgroundColor());
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, grupoSelecionado.getDescricao());
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColors(cores);
        pieDataSet.setDrawValues(true);
        pieDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                String percentagemSemCasas = String.format("%.0f", value);
                return percentagemSemCasas + "%";
            }
        });


        //create pie data object
        PieData pieData = new PieData(pieDataSet);
        graficoCategorias.setData(pieData);
        graficoCategorias.invalidate();
    }

    private void atualizaData() {
        mesAnoTextView.setText(new LocalDateUtils(null).getMesAno(dataSelecionada));
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

    private void carregaListaDeCategorias() {
        ControladorCategoria controladorCategoria = new ControladorCategoria(this);

        List<Categoria> categoriasComContas = controladorCategoria.getCategoriasComContas(
                grupoSelecionado, dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());

        List<ListaCategoriaAdapterTO> categoriasTO = new ArrayList<>();
        for(Categoria categoria : categoriasComContas){
            int totalContas = controladorCategoria.getTotalDeContas(categoria,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            BigDecimal totalGastosCategoria = controladorCategoria.getTotalGastosCategoria(categoria,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            ListaCategoriaAdapterTO categoriaTO = new ListaCategoriaAdapterTO(categoria,
                    totalContas, totalGastosCategoria);
            categoriasTO.add(categoriaTO);
        }

        Collections.sort(categoriasTO);
        Collections.reverse(categoriasTO);

        //cria o gradiente de cores das categorias do grupo
        ColorGradientGenerator gradiente =
                new ColorGradientGenerator(new CorGrupo().getCorRGB(grupoSelecionado.getDescricao()),
                        categoriasComContas.size());
        int[] cores = gradiente.gerarGradiente();
        for(int i = 0; i < categoriasTO.size(); i++){
            categoriasTO.get(i).setBackgroundColor(cores[i]);
        }

        ListaCategoriaAdapter adapter = new ListaCategoriaAdapter(this, categoriasTO);
        listaCategorias.setAdapter(adapter);
    }

    private void inicializaComponentes() {
        grupoSelecionado = (Grupo) getIntent().getExtras().get("grupo");
        dataSelecionada = (LocalDate) getIntent().getExtras().get("data");
        if(dataSelecionada == null) dataSelecionada = LocalDate.now();

        listaCategorias = (ListView) findViewById(R.id.lista_categoria_lista);
        mesAnoTextView = (TextView) findViewById(R.id.lista_categoria_mes_ano);
        mesAnoTextView.setText(LocalDateUtils.getMesAno(dataSelecionada));
        totalReceitasView = (TextView) findViewById(R.id.lista_categoria_recebido);
        saldoView = (TextView) findViewById(R.id.lista_categoria_saldo);

        graficoCategorias = (PieChart) findViewById(lista_categoria_grafico);
        tituloEstatistica = (TextView) findViewById(lista_categoria_estatistica_titulo);
        valorAtualEstatistica = (TextView) findViewById(lista_categoria_estatistica_mes_atual);
        menorValorEstatistica = (TextView) findViewById(lista_categoria_estatistica_menor);
        maiorValorEstatistica = (TextView) findViewById(lista_categoria_estatistica_maior);
        mediaEstatistica = (TextView) findViewById(lista_categoria_estatistica_media);
        valorMesAnteriorEstatistica = (TextView) findViewById(lista_categoria_estatistica_mes_anterior);
    }
}
