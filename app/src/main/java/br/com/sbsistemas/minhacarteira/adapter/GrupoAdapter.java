package br.com.sbsistemas.minhacarteira.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.to.GrupoAdapterTO;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 24/08/2017.
 */

public class GrupoAdapter extends ArrayAdapter<GrupoAdapterTO>{

    private final Activity context;

    private GrupoAdapterTO[] grupos;

    public GrupoAdapter(@NonNull Activity context, @NonNull GrupoAdapterTO[] grupos) {
        super(context, R.layout.lista_grupos_linha_grupo, grupos);
        this.grupos = grupos;
        this.context = context;
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
            LayoutInflater inflater = context.getLayoutInflater();
            linha = inflater.inflate(R.layout.lista_grupos_linha_grupo, null);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.descricao = (TextView) linha.findViewById(R.id.linha_grupo_descricao);
            viewHolder.numDeContas = (TextView) linha.findViewById(R.id.linha_grupo_numero_contas);
            viewHolder.totalGasto = (TextView) linha.findViewById(R.id.linha_grupo_totalGasto);
            viewHolder.icone = (ImageView) linha.findViewById(R.id.linha_grupo_icone);

            linha.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) linha.getTag();
        Grupo grupo = grupos[position].getGrupo();
        viewHolder.descricao.setText(grupo.getDescricao());
        int numDeContas = grupos[position].getTotalContas();
        viewHolder.numDeContas.setText(numDeContas + " contas");
        double totalGasto = grupos[position].getTotalGastos().doubleValue();
        String totalGastoFormatado = String.format("R$ %.2f", totalGasto);
        viewHolder.totalGasto.setText(totalGastoFormatado);

        switch (grupo.getDescricao()){
            case GrupoDAO.GRUPO_DIVERSAS:
                viewHolder.icone.setImageResource(R.drawable.grupo_diversas);
                break;
            case GrupoDAO.GRUPO_LAZER:
                viewHolder.icone.setImageResource(R.drawable.grupo_lazer);
                break;
            case GrupoDAO.GRUPO_MORADIA:
                viewHolder.icone.setImageResource(R.drawable.grupo_moradia);
                break;
            case GrupoDAO.GRUPO_EDU_TRAB:
                viewHolder.icone.setImageResource(R.drawable.grupo_trabalho);
                break;
            case GrupoDAO.GRUPO_SAUDE:
                viewHolder.icone.setImageResource(R.drawable.grupo_saude);
                break;
            case GrupoDAO.GRUPO_TRANSPORTE:
                viewHolder.icone.setImageResource(R.drawable.grupo_transporte);
                break;
            default:
                viewHolder.icone.setImageResource(R.drawable.grupo_transporte);
                break;
        }

        return linha;
    }

    public GrupoAdapterTO[] getGrupos() {
        return grupos;
    }
}
