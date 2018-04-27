package br.com.sbsistemas.minhacarteira.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

import br.com.sbsistemas.minhacarteira.FormularioCategoriaActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.exception.CategoriaRepetidaException;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 26/08/2017.
 */

public class FormularioCategoriaListAdapter extends BaseExpandableListAdapter {

    private static final long ID_CATEGORIA_ADICIONAR = 999999999;
    private FormularioCategoriaActivity context;
    private List<Grupo> grupos;
    private Map<Grupo, List<Categoria>> categorias;

    public FormularioCategoriaListAdapter(FormularioCategoriaActivity context, List<Grupo> grupos, Map<Grupo, List<Categoria>> categorias){
        this.context = context;
        this.grupos = grupos;
        this.categorias = categorias;
    }

    @Override
    public int getGroupCount() {
        return grupos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categorias.get(grupos.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return grupos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categorias.get(grupos.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return grupos.get(groupPosition).getId();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return categorias.get(grupos.get(groupPosition)).get(childPosition).getId();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = context.getLayoutInflater().inflate(
                    R.layout.formulario_categoria_linha_grupo, parent, false);

        TextView grupoText = (TextView) convertView.findViewById(R.id.formulario_categoria_linha_grupo_text);
        grupoText.setText(grupos.get(groupPosition).getDescricao());

        if(isExpanded){
            grupoText.setTypeface(null, Typeface.BOLD);
        } else {
            grupoText.setTypeface(null, Typeface.NORMAL);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = context.getLayoutInflater().inflate(
                    R.layout.formulario_categoria_linha_categoria,null);

        final Grupo grupo = grupos.get(groupPosition);
        Categoria categoria = categorias.get(grupo).get(childPosition);

        final EditText novaCategoriaText = (EditText) convertView.findViewById(R.id.formulario_categoria_linha_categoria_add);
        Button botaoSalvar = (Button) convertView.findViewById(R.id.formulario_categoria_linha_categoria_salvar);
        TextView categoriaText = (TextView) convertView.findViewById(
                R.id.formulario_categoria_linha_categoria_text);

        if(categoria.getId().equals(Long.valueOf(999999999))){
            novaCategoriaText.setVisibility(View.VISIBLE);
            novaCategoriaText.setHint(categoria.getDescricao());
            botaoSalvar.setVisibility(View.VISIBLE);
            categoriaText.setVisibility(View.GONE);
        } else {
            categoriaText.setVisibility(View.VISIBLE);
            categoriaText.setText(categoria.getDescricao());
            novaCategoriaText.setVisibility(View.GONE);
            botaoSalvar.setVisibility(View.GONE);
        }

        botaoSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Categoria nova = new Categoria();
                nova.setIdGrupo(grupo.getId());
                nova.setDescricao(novaCategoriaText.getText().toString());
                ControladorCategoria controladorCategoria = new ControladorCategoria(context);

                try{
                    controladorCategoria.criarCategoria(nova);
                    String msgSucesso = "Categoria " + nova.getDescricao() + " adicionada no grupo " + grupo.getDescricao();
                    Toast.makeText(context, msgSucesso, Toast.LENGTH_LONG).show();
                    context.iniciaComponentes();
                } catch (IllegalArgumentException | CategoriaRepetidaException e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        ExpandableListView mExpandableListView = (ExpandableListView) parent;
        mExpandableListView.expandGroup(groupPosition);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
