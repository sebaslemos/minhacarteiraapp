package br.com.sbsistemas.minhacarteira.utils;

import android.graphics.Color;

import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;

/**
 * Created by sebas on 02/11/2017.
 */

public class ColorGradientGenerator {

    private final int RED = 0;
    private final int GREEN = 1;
    private final int BLUE = 2;

    private Integer[] corInicialRGB;
    private int steps;


    public ColorGradientGenerator(Integer[] corInicialRGB, Integer steps) {
        this.corInicialRGB = corInicialRGB;
        this.steps = steps;
    }

    public int[] gerarGradiente(){
        if(steps == 0) return null;

        int[] cores = new int[this.steps];

        cores[0] = Color.rgb(corInicialRGB[RED], corInicialRGB[GREEN], corInicialRGB[BLUE]);

        float constante = 1.0f;
        for(int i = 1; i < steps; i++){
            constante = atualizaConstanteGradiente(constante);
            int corGradiente = enlight(corInicialRGB, constante);
            cores[i] = corGradiente;
        }

        return cores;
    }

    public int enlight(int color, float amount) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = Math.min(1.0f, hsv[2] * amount);
        return Color.HSVToColor(hsv);
    }

    public int enlight(Integer[] color, float amount) {
        return Color.rgb(
                (int) Math.min(255, color[RED] * amount),
                (int) Math.min(255, color[GREEN] * amount),
                (int) Math.min(255, color[BLUE] * amount)
        );
    }

    public float atualizaConstanteGradiente(float constante) {
        CorGrupo corGrupo = new CorGrupo();

        if (corInicialRGB.equals(corGrupo.getCorRGB(GrupoDAO.GRUPO_TRANSPORTE))){
            return constante + 0.13f;
        }
        if (corInicialRGB.equals(corGrupo.getCorRGB(GrupoDAO.GRUPO_DIVERSAS))){
            return constante + 0.3f;
        }
        if (corInicialRGB.equals(corGrupo.getCorRGB(GrupoDAO.GRUPO_EDU_TRAB))){
            return constante + 0.18f;
        }
        if (corInicialRGB.equals(corGrupo.getCorRGB(GrupoDAO.GRUPO_LAZER))){
            return constante + 0.7f;
        }
        if (corInicialRGB.equals(corGrupo.getCorRGB(GrupoDAO.GRUPO_MORADIA))){
            return constante + 0.1f;
        }
        else{ //saude
            return constante + 0.3f;
        }
    }
}
