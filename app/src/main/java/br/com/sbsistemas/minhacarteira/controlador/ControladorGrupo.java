package br.com.sbsistemas.minhacarteira.controlador;

import android.content.Context;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.to.EstatisticaTO;
import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 24/08/2017.
 */

public class ControladorGrupo {

    private static final int MESES_A_CONTAR_ESTATISTICAS = 8;

    private Context context;

    public ControladorGrupo(Context context){
        this.context = context;
    }

    public List<Grupo> getGrupos(){
        GrupoDAO grupoDAO = new GrupoDAO(context);

        List<Grupo> todos = grupoDAO.recuperaTodos();
        grupoDAO.close();
        return todos;
    }

    public Grupo getGrupo(String descricao) {
        GrupoDAO dao = new GrupoDAO(context);
        Grupo grupo = dao.getGrupo(descricao);
        dao.close();

        return grupo;
    }

    public QuantidadeValorTO getQuantidadeValor(Grupo grupo, int mes, int ano){
        GrupoDAO dao = new GrupoDAO(context);
        QuantidadeValorTO quantitadeValor = dao.getQuantitadeValor(grupo, mes, ano);
        dao.close();
        return quantitadeValor;
    }

    public Grupo getGrupo(Long idGrupo) {
        GrupoDAO dao = new GrupoDAO(context);
        Grupo grupo = dao.getGrupo(idGrupo);
        dao.close();
        return grupo;
    }

    public EstatisticaTO calculaMenorValorGasto(Grupo grupoSelecionado, LocalDate data) {
        EstatisticaTO resultado = new EstatisticaTO();

        BigDecimal menorValor = BigDecimal.valueOf(Double.MAX_VALUE);
        LocalDate menorData = data;
        for(int i = 1; i <= MESES_A_CONTAR_ESTATISTICAS; i++){
            data = data.minusMonths(1);
            BigDecimal totalMes = new BigDecimal(getQuantidadeValor(grupoSelecionado,
                    data.getMonthOfYear(), data.getYear()).getValor());

            if(!totalMes.equals(new BigDecimal(0)) && totalMes.compareTo(menorValor) <= 0 ){
                menorValor = totalMes;
                menorData = data;
            }
        }

        resultado.setValor(menorValor);
        resultado.setData(menorData);
        return resultado;
    }

    public EstatisticaTO calculaMaiorValorGasto(Grupo grupoSelecionado, LocalDate data) {
        EstatisticaTO resultado = new EstatisticaTO();

        BigDecimal maiorValor = BigDecimal.valueOf(Double.MIN_VALUE);
        LocalDate maiorData = data;
        for(int i = 1; i <= MESES_A_CONTAR_ESTATISTICAS; i++){
            data = data.minusMonths(1);
            BigDecimal totalMes = new BigDecimal(getQuantidadeValor(grupoSelecionado,
                    data.getMonthOfYear(), data.getYear()).getValor());

            if(totalMes.compareTo(maiorValor) >= 0){
                maiorValor = totalMes;
                maiorData = data;
            }
        }

        resultado.setValor(maiorValor);
        resultado.setData(maiorData);
        return resultado;
    }

    public BigDecimal calculaMedia(Grupo grupoSelecionado, LocalDate data){
        BigDecimal soma = new BigDecimal(0);
        int mesesComConta = 0;

        for(int i = 1; i <= MESES_A_CONTAR_ESTATISTICAS; i++){
            data = data.minusMonths(1);
            BigDecimal totalGasto = new BigDecimal(getQuantidadeValor(grupoSelecionado,
                    data.getMonthOfYear(), data.getYear()).getValor());

            if(!totalGasto.equals(new BigDecimal(0))){
                soma = soma.add(totalGasto);
                mesesComConta++;
            }
        }

        if(mesesComConta > 0)
            return soma.divide(new BigDecimal(mesesComConta), BigDecimal.ROUND_HALF_DOWN);

        return new BigDecimal(0);
    }
}
