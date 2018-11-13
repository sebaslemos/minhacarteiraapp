package br.com.sbsistemas.minhacarteira.helpers;

import android.widget.EditText;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.FormularioReceiraActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.exception.MinhaCarteiraInputException;
import br.com.sbsistemas.minhacarteira.modelo.Receita;

/**
 * Created by sebas on 04/09/2017.
 */

public class FormularioReceitaHelper {

    private EditText descricao;
    private EditText valor;
    private TextView data;

    public FormularioReceitaHelper(FormularioReceiraActivity activity){
        descricao = (EditText) activity.findViewById(R.id.formulario_receita_descricao);
        valor = (EditText) activity.findViewById(R.id.formulario_receita_valor);
        data = (TextView) activity.findViewById(R.id.formulario_receita_data);
    }

    public Receita pegaReceitaDoFormulario() throws MinhaCarteiraInputException {
        if(isNullOuVazio(descricao.getText().toString(),
                valor.getText().toString(),
                data.getText().toString()))
            throw new MinhaCarteiraInputException("Todos os campos devem ser preenchidos.");

        Receita receita = new Receita();
        receita.setDescricao(descricao.getText().toString());
        receita.setValor(new BigDecimal(valor.getText().toString()));

        String dataCompleta = "01/" + data.getText().toString();
        receita.setData(LocalDate.parse(dataCompleta,
                DateTimeFormat.forPattern("dd/MM/yyyy")));

        return receita;
    }

    private boolean isNullOuVazio(String... Stringargs){

        for(String arg : Stringargs)
            if(arg == null || arg.equals("")) return true;

        return false;
    }

}
