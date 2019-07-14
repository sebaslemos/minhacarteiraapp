package br.com.sbsistemas.minhacarteira.utils;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;

/**
 * Created by sebas on 19/09/2017.
 */

public class CorGrupo {

    private static final Map<String, Integer[]> corDoGrupo = new HashMap<>();
    private static final Map<String, Integer> iconeGrupoPequeno = new HashMap<>();
    private static final Map<String, Integer> iconeGrupoGrande = new HashMap<>();

    static {
        corDoGrupo.put(GrupoDAO.GRUPO_DIVERSAS, new Integer[]{144, 68, 154});//{144, 68, 154});
        corDoGrupo.put(GrupoDAO.GRUPO_LAZER, new Integer[]{222 , 36, 76});
        corDoGrupo.put(GrupoDAO.GRUPO_MORADIA, new Integer[]{251, 176, 21});
        corDoGrupo.put(GrupoDAO.GRUPO_SAUDE, new Integer[]{121, 186, 63});
        corDoGrupo.put(GrupoDAO.GRUPO_EDU_TRAB, new Integer[]{161, 129, 102});
        corDoGrupo.put(GrupoDAO.GRUPO_TRANSPORTE, new Integer[]{0, 152, 220});
        corDoGrupo.put(GrupoDAO.GRUPO_TODAS, new Integer[]{19, 120, 114});

        iconeGrupoPequeno.put(GrupoDAO.GRUPO_DIVERSAS, R.drawable.grupo_diversas_peq);
        iconeGrupoPequeno.put(GrupoDAO.GRUPO_LAZER, R.drawable.grupo_lazer_peq);
        iconeGrupoPequeno.put(GrupoDAO.GRUPO_MORADIA,  R.drawable.grupo_moradia_peq);
        iconeGrupoPequeno.put(GrupoDAO.GRUPO_SAUDE,  R.drawable.grupo_saude_peq);
        iconeGrupoPequeno.put(GrupoDAO.GRUPO_EDU_TRAB,  R.drawable.grupo_trabalho_peq);
        iconeGrupoPequeno.put(GrupoDAO.GRUPO_TRANSPORTE, R.drawable.grupo_transporte_peq);

        iconeGrupoGrande.put(GrupoDAO.GRUPO_DIVERSAS, R.drawable.grupo_diversas);
        iconeGrupoGrande.put(GrupoDAO.GRUPO_LAZER, R.drawable.grupo_lazer);
        iconeGrupoGrande.put(GrupoDAO.GRUPO_MORADIA,  R.drawable.grupo_moradia);
        iconeGrupoGrande.put(GrupoDAO.GRUPO_SAUDE,  R.drawable.grupo_saude);
        iconeGrupoGrande.put(GrupoDAO.GRUPO_EDU_TRAB,  R.drawable.grupo_trabalho);
        iconeGrupoGrande.put(GrupoDAO.GRUPO_TRANSPORTE, R.drawable.grupo_transporte);
    }

    public static Integer getCor(String descricaoGrupo){
        Integer[] cor = corDoGrupo.get(descricaoGrupo);
        return Color.rgb(cor[0], cor[1], cor[2]);
    }

    public static Integer[] getCorRGB(String descricao){
        return corDoGrupo.get(descricao);
    }

    public static int getIconePequeno(String descricaoGrupo) {
        return iconeGrupoPequeno.get(descricaoGrupo);
    }

    public static int getIconeGrande(String descricaoGrupo) {
        return iconeGrupoGrande.get(descricaoGrupo);
    }
}
