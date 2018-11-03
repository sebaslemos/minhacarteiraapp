package br.com.sbsistemas.minhacarteira.utils;

import android.content.Context;

import org.joda.time.LocalDate;

/**
 * Created by sebas on 13/08/2017.
 */

public class LocalDateUtils {

    private LocalDate hoje;
    private Context context;

    //evita começar os meses com 0
    private static final String[] MESES = {"", "JANEIRO", "FEVEREIRO", "MARÇO", "ABRIL", "MAIO", "JUNHO",
            "JULHO", "AGOSTO", "SETEMBRO", "OUTUBRO", "NOVEMBRO", "DEZEMBRO"};

    private static final String[] MESES_ABREVIADOS = {"", "JAN", "FEV", "MAR", "ABR", "MAI", "JUN",
            "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};


    /**
     * Cria uma objeto configurando a data atual manualmente, para propósito de testes
     * @param hoje
     */
    public LocalDateUtils(LocalDate hoje, Context context){
        this.hoje = hoje;
        this.context = context;
    }

    /**
     * Cria um objeto configurando a data atual pelo sistema.
     */
    public LocalDateUtils(Context context){
        this.context = context;
        this.hoje = new LocalDate();
    }

    public LocalDate getHoje() {
        if(hoje == null) return new LocalDate();

        return hoje;
    }

    public static String getInicioMes(int mes, int ano){
        String mesFormatado = mes < 10 ? "0" + mes : mes + "";
        return ano + "-" + mesFormatado + "-" + "01";
    }

    public static String getFinalMes(int mes, int ano){
        String mesFormatado = mes < 10 ? "0" + mes : mes + "";
        return ano + "-" + mesFormatado + "-" + "31";
    }

    /**
     * imprime o mes e o ano de uma Data
     * @param data
     * @return
     */
    public static String getMesAno(LocalDate data){
        int mes = data.getMonthOfYear();
        int ano = data.getYear();

        return MESES[mes] + " " + ano;
    }

    /**
     * imprime o mes e o ano de uma Data
     * @param data
     * @return
     */
    public static String getMesAnoAbreviado(LocalDate data){
        int mes = data.getMonthOfYear();
        int ano = data.getYear();

        ano = ano - 2000;
        return MESES_ABREVIADOS[mes] + "/" + ano;
    }

    public static LocalDate getLocalDate(int mes, int ano){
        return new LocalDate(ano, mes, 01);
    }

    public static LocalDate mesAnterior(LocalDate data){
        return  data.minusMonths(1);
    }

    public static LocalDate mesPosterior(LocalDate data){
        return data.plusMonths(1);
    }
}
