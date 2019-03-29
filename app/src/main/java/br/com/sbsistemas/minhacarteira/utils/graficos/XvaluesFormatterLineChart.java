package br.com.sbsistemas.minhacarteira.utils.graficos;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

public class XvaluesFormatterLineChart implements IAxisValueFormatter {

    private String[] meses;

    public XvaluesFormatterLineChart(String[] meses){
        this.meses = meses;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return meses[(int) value];
    }
}