package br.com.sbsistemas.minhacarteira.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;

import java.math.BigDecimal;
import java.util.List;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.modelo.Receita;

/**
 * Created by sebas on 04/09/2017.
 */

public class ListaReceitasAdapter extends ArrayAdapter<Receita> {

    private Activity context;
    private List<Receita> receitas;

    public ListaReceitasAdapter(@NonNull Activity context, @NonNull List<Receita> receitas) {
        super(context, R.layout.lista_receitas_linha, receitas);
        this.context = context;
        this.receitas = receitas;
    }

    static class ViewHolder{
        public TextView descricao;
        public TextView dataReceita;
        public TextView valor;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View linha = convertView;

        if(linha == null){
            linha = context.getLayoutInflater().inflate(R.layout.lista_receitas_linha, null);

            ViewHolder holder = new ViewHolder();
            holder.descricao = (TextView) linha.findViewById(R.id.linha_receita_descricao);
            holder.dataReceita = (TextView) linha.findViewById(R.id.linha_receita_data);
            holder.valor = (TextView) linha.findViewById(R.id.linha_receita_valor);

            linha.setTag(holder);
        }

        Receita receita = receitas.get(position);

        ViewHolder holder = (ViewHolder) linha.getTag();
        holder.descricao.setText(receita.getDescricao());

        BigDecimal valor = receita.getValor();
        holder.valor.setText(String.format("R$ %.2f", valor.doubleValue()));
        String data = receita.getData().toString(DateTimeFormat.forPattern("dd/MM/yyyy"));
        holder.dataReceita.setText(data);

        return linha;
    }
}
