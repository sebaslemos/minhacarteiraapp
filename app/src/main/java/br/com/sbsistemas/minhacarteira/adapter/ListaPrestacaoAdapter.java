package br.com.sbsistemas.minhacarteira.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

/**
 * Created by sebas on 06/12/2018.
 */

public class ListaPrestacaoAdapter extends ArrayAdapter<Prestacao>{

    private final Activity context;
    private final List<Prestacao> prestacoes;

    public ListaPrestacaoAdapter(Activity context, List<Prestacao> prestacoes){
        super(context, R.layout.formulario_conta_linha_prestacao, prestacoes);
        this.context = context;
        this.prestacoes = prestacoes;
    }

    private class ViewHolder{
        public TextView data;
        public TextView numero;
        public Switch pago;
        public Switch ativo;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View linha = convertView;

        if(linha == null){
            linha = context.getLayoutInflater().inflate(R.layout.formulario_conta_linha_prestacao, null);

            ViewHolder holder = new ViewHolder();
            holder.data = (TextView) linha.findViewById(R.id.linha_prestacao_data);
            holder.numero = (TextView) linha.findViewById(R.id.linha_prestacao_numero);
            holder.pago = (Switch) linha.findViewById(R.id.linha_prestacao_pago);
            holder.ativo = (Switch) linha.findViewById(R.id.linha_prestacao_ativo);

            linha.setTag(holder);
        }

        Prestacao prestacao = prestacoes.get(position);
        ViewHolder holder = (ViewHolder) linha.getTag();

        holder.data.setText(prestacao.getData().toString(DateTimeFormat.forPattern("dd/MM/yyyy")));
        holder.numero.setText(prestacao.getNumParcela() + "/" + prestacoes.size());
        holder.pago.setChecked(prestacao.isPago());
        holder.ativo.setChecked(prestacao.isAtivo());


        return linha;
    }
}
