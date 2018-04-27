package br.com.sbsistemas.minhacarteira.controlador;

import android.content.Context;

import java.math.BigDecimal;
import java.util.List;

import br.com.sbsistemas.minhacarteira.dao.ReceitaDAO;
import br.com.sbsistemas.minhacarteira.exception.MinhaCarteiraInputException;
import br.com.sbsistemas.minhacarteira.modelo.Receita;

/**
 * Created by sebas on 03/09/2017.
 */

public class ControladorReceitas {

    private Context context;

    public ControladorReceitas(Context context){
        this.context = context;
    }

    public List<Receita> getReceitas(int mes, int ano){
        ReceitaDAO dao = new ReceitaDAO(context);
        List<Receita> receitas = dao.getReceitas(mes, ano);
        dao.close();

        return receitas;
    }

    public Long adicionarReceita(Receita receita) throws MinhaCarteiraInputException {
        valida(receita);

        ReceitaDAO dao = new ReceitaDAO(context);
        Long id = dao.adiciona(receita);
        dao.close();

        return id;
    }

    private void valida(Receita receita) throws MinhaCarteiraInputException {
        if(receita.getDescricao() == null ||
                receita.getDescricao().equals(""))
            throw new MinhaCarteiraInputException("A descrição da receita deve ser preenchida");

        if(receita.getValor() == null ||
                receita.getValor().doubleValue() < 0)
            throw new MinhaCarteiraInputException("O valor deve ser indormado e maior ou igual a 0");

        if(receita.getData() == null)
            throw new MinhaCarteiraInputException("A data deve ser informada");

    }

    public BigDecimal getTotalReceitas(int mes, int ano) {
        List<Receita> receitas = getReceitas(mes, ano);

        BigDecimal total = new BigDecimal(0);
        for(Receita receita : receitas){
            total = total.add(receita.getValor());
        }

        return total;
    }

    public void deletar(Receita receita) {
        ReceitaDAO dao  = new ReceitaDAO(context);
        dao.deletar(receita);
        dao.close();
    }

    public void deletar(List<Receita> receitas){
        ReceitaDAO dao = new ReceitaDAO(context);
        for(Receita receita : receitas){
            dao.deletar(receita);
        }
        dao.close();
    }
}
