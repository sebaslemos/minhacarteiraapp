package br.com.sbsistemas.minhacarteira.helpers;

import android.app.Activity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.utils.CorGrupo;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;
import br.com.sbsistemas.minhacarteira.utils.graficos.EntryYComparator;
import br.com.sbsistemas.minhacarteira.utils.graficos.XvaluesFormatterLineChart;
import br.com.sbsistemas.minhacarteira.utils.graficos.YValuesFormatterLineChart;

import static br.com.sbsistemas.minhacarteira.R.id.lista_contas_grafico;

/**
 * Created by sebas on 14/08/2018.
 *
 */

public class GraficoContasHelper {

    private LineChart grafico;
    private Activity activity;
    private Categoria categoria;

    public GraficoContasHelper(Activity activity, Categoria categoria){
        this.activity = activity;
        this.categoria = categoria;
        grafico = this.activity.findViewById(lista_contas_grafico);
    }

    public void atualizaGrafico(int mes, int ano){
        LocalDate date = LocalDateUtils.getLocalDate(mes, ano);
        ControladorCategoria ctrl = new ControladorCategoria(this.activity);

        List<Entry> entries = new ArrayList<>();
        String[] meses = new String[9];
        for(int i = 8; i >= 0; i--){
            BigDecimal totalGastosCategoria = ctrl.
                    getTotalGastosCategoria(this.categoria, date.getMonthOfYear(), date.getYear());

            Entry entry = new Entry(i, totalGastosCategoria.floatValue());
            entries.add(entry);
            meses[i] = LocalDateUtils.getMesAnoAbreviado(date);

            date = LocalDateUtils.mesAnterior(date);
        }
        Collections.sort(entries, new EntryYComparator<Entry>());

        Grupo grupo;
        if(categoria != null)
                grupo = new ControladorGrupo(activity).getGrupo(categoria.getIdGrupo());
        else{
            grupo = new Grupo();
            grupo.setDescricao(GrupoDAO.GRUPO_TODAS);
        }
        LineDataSet dataSet = new LineDataSet(entries, criaLabel(grupo));
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(true);
        dataSet.setValueFormatter(new CenterValuesFormatter());
        dataSet.setFillColor(criaCor(grupo));
        dataSet.setColor(criaCor(grupo));
        dataSet.setCircleColor(criaCor(grupo));

        grafico.setData(new LineData(dataSet));

        XAxis xAxis = grafico.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new XvaluesFormatterLineChart(meses));

        YAxis yAxis = grafico.getAxisLeft();
        yAxis.setValueFormatter(new YValuesFormatterLineChart());
        yAxis.setDrawGridLines(false);
        YAxis axisRight = grafico.getAxisRight();
        axisRight.setValueFormatter(new YValuesFormatterLineChart());
        axisRight.setDrawGridLines(false);

        grafico.setDoubleTapToZoomEnabled(false);
        grafico.setClickable(false);
        grafico.setDescription(null);
        grafico.animateXY(1000,2000);
        grafico.invalidate();
    }

    private int criaCor(Grupo grupo) {
        return new CorGrupo().getCor(grupo.getDescricao());
    }

    private String criaLabel(Grupo grupo) {
        if(categoria == null) return "Todas";

        return grupo.getDescricao() + "/" + categoria.getDescricao();
    }

    private class CenterValuesFormatter implements IValueFormatter{
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return String.format(Locale.US, "R$ %.2f", value);
        }
    }
}
