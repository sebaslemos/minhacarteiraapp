package br.com.sbsistemas.minhacarteira.helpers;

import android.app.Activity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.controlador.ControladorReceitas;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.utils.CorGrupo;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;
import br.com.sbsistemas.minhacarteira.utils.graficos.EntryYComparator;
import br.com.sbsistemas.minhacarteira.utils.graficos.XvaluesFormatterLineChart;
import br.com.sbsistemas.minhacarteira.utils.graficos.YValuesFormatterLineChart;

import static br.com.sbsistemas.minhacarteira.R.id.lista_receitas_grafico;

public class GraficoReceitasHelper {

    private Activity context;
    private LineChart grafico;


    public GraficoReceitasHelper(Activity context) {
        this.context = context;
        this.grafico = this.context.findViewById(lista_receitas_grafico);
    }

    public void atualizaGrafico(int mes, int ano){
        LocalDate data = LocalDateUtils.getLocalDate(mes, ano);
        ControladorReceitas control = new ControladorReceitas(this.context);

        String meses[] = new String[9];
        List<Entry> entries = new ArrayList<>();
        for(int i = 8; i >= 0; i--){
            BigDecimal totalReceitas = control.getTotalReceitas(data.getMonthOfYear(),
                    data.getYear());

            Entry entry = new Entry(i, totalReceitas.floatValue());
            entries.add(entry);
            meses[i] = LocalDateUtils.getMesAnoAbreviado(data);
            data = LocalDateUtils.mesAnterior(data);
        }

        Collections.sort(entries, new EntryYComparator<Entry>());

        LineDataSet dataSet = new LineDataSet(entries, "Receitas");
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        dataSet.setDrawFilled(true);
        dataSet.setDrawValues(true);
        Integer cor = new CorGrupo().getCor(GrupoDAO.GRUPO_TODAS);
        dataSet.setFillColor(cor);
        dataSet.setColor(cor);
        dataSet.setCircleColor(cor);

        grafico.setData(new LineData(dataSet));

        XAxis xAxis = grafico.getXAxis();
        xAxis.setGranularity(1l);
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

}
