package br.com.sbsistemas.minhacarteira.helpers;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.sbsistemas.minhacarteira.ListaCategoriasActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.to.CategoriaAdapterTO;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 14/11/2018.
 *
 */

public class GraficoCategoriaHelper {

    private PieChart graficoCategorias;
    private ListaCategoriasActivity context;
    private Grupo grupo;

    public GraficoCategoriaHelper(ListaCategoriasActivity context, Grupo grupo){

        this.context = context;
        this.grupo = grupo;
        this.graficoCategorias = (PieChart) this.context.findViewById(R.id.lista_categoria_grafico);
        configuraGrafico();
    }

    private void configuraGrafico() {

        graficoCategorias.setRotationEnabled(true);
        graficoCategorias.setHoleRadius(50f);
        graficoCategorias.setTransparentCircleAlpha(0);
        graficoCategorias.setCenterText(grupo.getDescricao());
        graficoCategorias.setCenterTextSize(13);
        graficoCategorias.getDescription().setEnabled(false);
        graficoCategorias.getLegend().setEnabled(false);
        graficoCategorias.setDrawEntryLabels(false);
        graficoCategorias.setUsePercentValues(true);

        graficoCategorias.setOnChartValueSelectedListener(context);
    }

    public void atualizaGrafico(List<CategoriaAdapterTO> categoriasTO){

        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<Integer> cores = new ArrayList<>();

        for(CategoriaAdapterTO categoriaTO : categoriasTO){

            if(categoriaTO.getTotalGastos().equals(new BigDecimal(0))) continue;

            PieEntry entry = new PieEntry(categoriaTO.getTotalGastos().floatValue(),
                    categoriaTO.getCategoria().getId());

            yEntries.add(entry);
            cores.add(categoriaTO.getBackgroundColor());
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, grupo.getDescricao());
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColors(cores);
        pieDataSet.setDrawValues(true);
        pieDataSet.setValueFormatter(new CategoriaYValueFormatter());

        PieData pieData = new PieData(pieDataSet);
        graficoCategorias.setData(pieData);
        graficoCategorias.invalidate();
    }

    private class CategoriaYValueFormatter implements IValueFormatter {

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            String percentagemSemCasas = String.format(Locale.US, "%.0f", value);
            return percentagemSemCasas + "%";
        }
    }

}
