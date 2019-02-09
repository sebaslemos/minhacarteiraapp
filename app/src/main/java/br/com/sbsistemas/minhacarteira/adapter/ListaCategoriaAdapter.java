package br.com.sbsistemas.minhacarteira.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.to.CategoriaAdapterTO;

import static br.com.sbsistemas.minhacarteira.R.layout.lista_categoria_linha_categoria;

/**
 * Created by sebas on 31/08/2017.
 */

public class ListaCategoriaAdapter extends ArrayAdapter<CategoriaAdapterTO>{

    private Activity context;
    private List<CategoriaAdapterTO> categoriasAdapterTO;

    public ListaCategoriaAdapter(@NonNull Activity context, @NonNull List<CategoriaAdapterTO> categorias) {
        super(context, lista_categoria_linha_categoria, categorias);
        this.context = context;
        this.categoriasAdapterTO = categorias;
    }

    static class ViewHolder{
        public TextView descricao;
        public TextView numDeContas;
        public TextView totalGasto;
        public ImageView icone;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View linha = convertView;
        if(linha == null){
            linha = context.getLayoutInflater().inflate(lista_categoria_linha_categoria, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.descricao = (TextView) linha.findViewById(R.id.lista_categoria_linha_categoria_descricao);
            viewHolder.numDeContas = (TextView) linha.findViewById(R.id.lista_categoria_linha_categoria_numero_contas);
            viewHolder.totalGasto = (TextView) linha.findViewById(R.id.lista_categoria_linha_categoria_totalGasto);
            viewHolder.icone = (ImageView) linha.findViewById(R.id.lista_categoria_linha_categoria_icone);

            linha.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) linha.getTag();
        CategoriaAdapterTO categoriaTO = categoriasAdapterTO.get(position);

        holder.descricao.setText(categoriaTO.getCategoria().getDescricao());

        int numDeContas = categoriasAdapterTO.get(position).getTotalContas();
        holder.numDeContas.setText(numDeContas + " contas");

        double totalGasto = categoriasAdapterTO.get(position).getTotalGastos().doubleValue();
        String totalGastoFormatado = String.format("R$ %.2f", totalGasto);
        holder.totalGasto.setText(totalGastoFormatado);

        holder.icone.setBackgroundColor(categoriaTO.getBackgroundColor());

        return linha;
    }

    public List<CategoriaAdapterTO> getCategoriasAdapterTO() {

        return categoriasAdapterTO;
    }
}
