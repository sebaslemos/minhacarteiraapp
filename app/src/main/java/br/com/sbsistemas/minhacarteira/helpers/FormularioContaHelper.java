package br.com.sbsistemas.minhacarteira.helpers;

import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.FormularioContaActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 27/08/2017.
 */

public class FormularioContaHelper {

    private final TextView grupo;
    private final TextView categoria;
    private final EditText descricao;
    private final TextView data;
    private final EditText valor;
    private final EditText numPrestacoes;
    private final Switch pago;
    private final Switch ativo;

    private ControladorCategoria controladorCategoria;
    private ControladorGrupo controladorGrupo;

    public FormularioContaHelper(FormularioContaActivity activity){
        grupo = (TextView) activity.findViewById(R.id.formulario_conta_grupo);
        categoria = (TextView) activity.findViewById(R.id.formulario_conta_categoria);
        descricao = (EditText) activity.findViewById(R.id.formulario_conta_descricao);
        data = (TextView) activity.findViewById(R.id.formulario_conta_data);
        valor = (EditText) activity.findViewById(R.id.formulario_conta_valor);
        numPrestacoes = (EditText) activity.findViewById(R.id.formulario_conta_num_prest);
        pago = (Switch) activity.findViewById(R.id.formulario_conta_pago);
        ativo = (Switch) activity.findViewById(R.id.formulario_conta_ativa);

        controladorCategoria = new ControladorCategoria(activity);
        controladorGrupo = new ControladorGrupo(activity);
    }

    public Conta pegaConta(){
        Grupo grupoSelecionado = controladorGrupo.getGrupo(grupo.getText().toString());
        if(grupoSelecionado == null) throw new IllegalArgumentException("Nenhum grupo ou categoria selecioando");
        Categoria categoriaSelecionada = controladorCategoria.getCategoria(categoria.getText().toString(), grupoSelecionado);
        Conta conta = new Conta();
        conta.setCategoria(categoriaSelecionada.getId());
        conta.setNumeroDePrestacoes(Integer.valueOf(numPrestacoes.getText().toString()));
        conta.setValor(new BigDecimal(valor.getText().toString()));
        conta.setDescricao(descricao.getText().toString());
        conta.setTag(null);

        return conta;
    }

    public boolean getPago(){
        return pago.isChecked();
    }

    public boolean getAtiva(){
        return ativo.isChecked();
    }

    public LocalDate getData(){
        //dd/mm/yyyy
        String dataSelecionada = data.getText().toString();
        return LocalDate.parse(dataSelecionada, DateTimeFormat.forPattern("dd/MM/yyyy"));
    }
}
