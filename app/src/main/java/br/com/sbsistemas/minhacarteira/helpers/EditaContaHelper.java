package br.com.sbsistemas.minhacarteira.helpers;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 31/10/2017.
 */

public class EditaContaHelper {

    private Conta conta;

    private final EditText descricao;
    private final TextView grupo;
    private final TextView categoria;
    private final EditText valor;
    private final ListView prestacoes;

    private ControladorCategoria controladorCategoria;
    private ControladorGrupo controladorGrupo;

    public EditaContaHelper(Activity context, Conta conta){
        controladorCategoria = new ControladorCategoria(context);
        controladorGrupo = new ControladorGrupo(context);

        descricao = (EditText) context.findViewById(R.id.edita_conta_descricao);
        grupo = (TextView) context.findViewById(R.id.edita_conta_grupo);
        categoria = (TextView) context.findViewById(R.id.edita_conta_categoria);
        valor = (EditText) context.findViewById(R.id.edita_conta_valor);
        prestacoes = (ListView) context.findViewById(R.id.edita_conta_prestacoes);

        this.conta = conta;
        preencheCampos();
    }

    private void preencheCampos() {
        Categoria categoriaSelecionada = controladorCategoria.getCategoria(conta.getCategoria());
        Grupo grupoSelecionado = controladorGrupo.getGrupo(categoriaSelecionada.getIdGrupo());

        descricao.setText(conta.getDescricao());
        grupo.setText(grupoSelecionado.getDescricao());
        categoria.setText(categoriaSelecionada.getDescricao());

        BigDecimal valor = conta.getValor();
        this.valor.setText(String.format("%.2f", valor.doubleValue()));

        prencheLista();
    }

    public Conta recuperaConta(){
        this.conta.setDescricao(descricao.getText().toString());
        Grupo grupoSelecionado = controladorGrupo.getGrupo(grupo.getText().toString());
        Categoria categoriaSelecionada = controladorCategoria.getCategoria(categoria.getText().toString(), grupoSelecionado);
        this.conta.setCategoria(categoriaSelecionada.getId());

        String valorFormatado = valor.getText().toString().replace(",", ".");
        conta.setValor(new BigDecimal(valorFormatado));

        return this.conta;
    }

    private void prencheLista() {
        //TODO prestações
    }


}
