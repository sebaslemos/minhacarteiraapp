package br.com.sbsistemas.minhacarteira.helpers;

import android.app.Activity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.to.GrupoAdapterTO;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.utils.CorGrupo;

/**
 * Created by sebas on 14/11/2018.
 */

public class GraficoGruposHelper {

    private Activity contexto;
    private BarChart graficoGrupos;

    public GraficoGruposHelper(Activity contexto){

        this.contexto = contexto;
        graficoGrupos = (BarChart) contexto.findViewById(R.id.lista_grupos_grafico);

        configuraGrafico();
    }

    private void configuraGrafico() {
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

    public void atualizaGrafico(GrupoAdapterTO[] grupos) {
        BarData data = new BarData();

        List<BarEntry> entries = new ArrayList<>();
        int numGrupos = grupos.length;
        int[] colors = new int[numGrupos - 1];
        int indiceArrayCor = 0;
        CorGrupo corGrupo = new CorGrupo();


        for(int i = 0; i < numGrupos; i++){
            GrupoAdapterTO grupoAdapterTO = grupos[i];

            if(!grupoAdapterTO.getGrupo().getDescricao().equals(GrupoDAO.GRUPO_TODAS)){
                BarEntry barEntry = new BarEntry(i, grupoAdapterTO.getTotalGastos().floatValue());
                barEntry.setIcon(contexto.getResources().getDrawable(corGrupo.
                        getIconePequeno(grupoAdapterTO.getGrupo().getDescricao()), null));
                entries.add(barEntry);
                colors[indiceArrayCor++] = corGrupo.getCor(grupoAdapterTO.getGrupo().getDescricao());
            }
        }
        BarDataSet set = new BarDataSet(entries, "");
        set.setColors(colors);
        data.addDataSet(set);

        graficoGrupos.setData(data);
        graficoGrupos.invalidate();
    }


}
