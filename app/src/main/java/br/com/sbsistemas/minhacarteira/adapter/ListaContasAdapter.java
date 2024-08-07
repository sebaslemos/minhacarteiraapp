package br.com.sbsistemas.minhacarteira.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.joda.time.format.DateTimeFormat;

import java.util.List;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.listeners.CheckPagoListener;
import br.com.sbsistemas.minhacarteira.adapter.to.ContaTO;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

import static br.com.sbsistemas.minhacarteira.dao.GrupoDAO.GRUPO_DIVERSAS;
import static br.com.sbsistemas.minhacarteira.dao.GrupoDAO.GRUPO_EDU_TRAB;
import static br.com.sbsistemas.minhacarteira.dao.GrupoDAO.GRUPO_LAZER;
import static br.com.sbsistemas.minhacarteira.dao.GrupoDAO.GRUPO_MORADIA;
import static br.com.sbsistemas.minhacarteira.dao.GrupoDAO.GRUPO_SAUDE;
import static br.com.sbsistemas.minhacarteira.dao.GrupoDAO.GRUPO_TRANSPORTE;


/**
 * Created by sebas on 31/08/2017.
 */

public class ListaContasAdapter extends ArrayAdapter<ContaTO> {

    private Activity context;
    private List<ContaTO> contaAdapterTOs;
    private CheckPagoListener listener;
    private SparseBooleanArray mSelecionados;

    public ListaContasAdapter(@NonNull Activity context, @NonNull List<ContaTO> contaAdapterTOs, CheckPagoListener listener) {
        super(context, R.layout.lista_contas_linha_conta , contaAdapterTOs);
        this.context = context;
        this.contaAdapterTOs = contaAdapterTOs;
        this.listener = listener;
        mSelecionados = new SparseBooleanArray();
    }

    static class ViewHolder {

        public TextView descricao;
        public TextView data;
        public TextView valor;
        public CheckBox pago;
        public ImageView icone;
    }
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View linha = convertView;
        final ViewHolder holder;
        if(linha == null){
            linha = context.getLayoutInflater().inflate(R.layout.lista_contas_linha_conta, null);

            holder = new ViewHolder();
            holder.descricao = (TextView) linha.findViewById(R.id.lista_contas_linha_descricao);
            holder.data = (TextView) linha.findViewById(R.id.lista_contas_linha_data);
            holder.valor = (TextView) linha.findViewById(R.id.lista_contas_linha_valor);
            holder.pago = (CheckBox) linha.findViewById(R.id.lista_contas_linha_pago);
            holder.icone = (ImageView) linha.findViewById(R.id.lista_contas_linha_icone);

            linha.setTag(holder);
        } else{
            holder = (ViewHolder) linha.getTag();
        }

        ContaTO contaAdapterTO = this.contaAdapterTOs.get(position);
        Conta conta = contaAdapterTO.getConta();
        final Prestacao prestacao = contaAdapterTO.getPrestacao();

        String descricao = conta.getDescricao();
        if(!prestacao.isAtivo()) descricao = descricao;
        holder.descricao.setText(descricao);

        String data = prestacao.getData().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        String prestacaoAtual = prestacao.getNumParcela() + "/" + conta.getNumeroDePrestacoes();
        holder.data.setText(data + " " + prestacaoAtual);

        holder.valor.setText(conta.getValorFormatado());

        holder.pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPagoChecked(position, holder.pago.isChecked());
            }
        });
        holder.pago.setChecked(prestacao.isPago());

        if(!prestacao.isAtivo()){
            holder.descricao.setTextColor(Color.GRAY);
            holder.descricao.setTypeface(null, Typeface.ITALIC);
            holder.valor.setTextColor(Color.GRAY);
            holder.valor.setTypeface(null, Typeface.ITALIC);
            holder.data.setTextColor(Color.GRAY);
            holder.data.setTypeface(null, Typeface.ITALIC);
            holder.pago.setHintTextColor(Color.GRAY);
            holder.pago.setTypeface(null, Typeface.ITALIC);
        } else{
            holder.descricao.setTextColor(Color.BLACK);
            holder.descricao.setTypeface(null, Typeface.NORMAL);
            holder.valor.setTextColor(Color.BLACK);
            holder.valor.setTypeface(null, Typeface.NORMAL);
            holder.data.setTextColor(Color.BLACK);
            holder.data.setTypeface(null, Typeface.NORMAL);
            holder.pago.setHintTextColor(Color.BLACK);
            holder.pago.setTypeface(null, Typeface.NORMAL);
        }

        //background de itens selecionados
        if (mSelecionados.get(position)) {
            linha.setBackgroundColor(Color.parseColor("#3F51B5"));
        } else{
            linha.setBackground(null);
        }

        Grupo grupo = contaAdapterTO.getGrupo();
        switch (grupo.getDescricao()){
            case GRUPO_DIVERSAS:
                holder.icone.setImageResource(R.drawable.grupo_diversas);
                break;
            case GRUPO_LAZER:
                holder.icone.setImageResource(R.drawable.grupo_lazer);
                break;
            case GRUPO_MORADIA:
                holder.icone.setImageResource(R.drawable.grupo_moradia);
                break;
            case GRUPO_EDU_TRAB:
                holder.icone.setImageResource(R.drawable.grupo_trabalho);
                break;
            case GRUPO_SAUDE:
                holder.icone.setImageResource(R.drawable.grupo_saude);
                break;
            case GRUPO_TRANSPORTE:
                holder.icone.setImageResource(R.drawable.grupo_transporte);
                break;
            default:
                holder.icone.setImageResource(R.drawable.grupo_transporte);
                break;
        }

        return linha;
    }

    @Nullable
    @Override
    public ContaTO getItem(int position) {
        return contaAdapterTOs.get(position);
    }

    public void removeSelection(){
        mSelecionados = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void alternarSelecao(int posicao){
        seleciona(posicao, !mSelecionados.get(posicao));
    }

    private void seleciona(int posicao, boolean valor) {
        if(valor) mSelecionados.put(posicao, valor);
        else mSelecionados.delete(posicao);

        notifyDataSetChanged();
    }

    public int getTotalSelecionadas() {
        return mSelecionados.size();
    }

}
