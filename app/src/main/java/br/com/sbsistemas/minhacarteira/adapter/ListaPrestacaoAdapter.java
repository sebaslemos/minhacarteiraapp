package br.com.sbsistemas.minhacarteira.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import br.com.sbsistemas.minhacarteira.EditaContaActivity;
import br.com.sbsistemas.minhacarteira.FormularioContaActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.listeners.PrestacaoChangedListener;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

/**
 * Created by sebas on 06/12/2018.
 */

public class ListaPrestacaoAdapter extends ArrayAdapter<Prestacao>{

    private final Activity context;
    private final List<Prestacao> prestacoes;
    private PrestacaoChangedListener listener;

    public ListaPrestacaoAdapter(Activity context, List<Prestacao> prestacoes,
                                 PrestacaoChangedListener listener){

        super(context, R.layout.formulario_conta_linha_prestacao, prestacoes);
        this.context = context;
        this.prestacoes = prestacoes;
        this.listener = listener;
    }

    private class ViewHolder{
        public TextView data;
        public TextView numero;
        public Switch pago;
        public Switch ativo;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View linha = convertView;

        final ViewHolder holder;
        if(linha == null){
            linha = context.getLayoutInflater().inflate(R.layout.formulario_conta_linha_prestacao, null);

            holder = new ViewHolder();
            holder.data = (TextView) linha.findViewById(R.id.linha_prestacao_data);
            holder.numero = (TextView) linha.findViewById(R.id.linha_prestacao_numero);
            holder.pago = (Switch) linha.findViewById(R.id.linha_prestacao_pago);
            holder.ativo = (Switch) linha.findViewById(R.id.linha_prestacao_ativo);

            linha.setTag(holder);
        } else{
            holder = (ViewHolder) linha.getTag();
        }

        final Prestacao prestacao = prestacoes.get(position);

        holder.data.setText(prestacao.getData().toString(DateTimeFormat.forPattern("dd/MM/yyyy")));
        holder.numero.setText(prestacao.getNumParcela() + "/" + prestacoes.size());


        holder.pago.setChecked(prestacao.isPago());
        holder.ativo.setChecked(prestacao.isAtivo());

        holder.pago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.atualizaPagoPrestacao(position, holder.pago.isChecked());
            }
        });

        holder.ativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.atualizaAtivoPrestacao(position, holder.ativo.isChecked());
            }
        });
        
        return linha;
    }

    @Nullable
    @Override
    public Prestacao getItem(int position) {
        return prestacoes.get(position);
    }
}
