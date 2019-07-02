package br.com.sbsistemas.minhacarteira.helpers;

import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sbsistemas.minhacarteira.FormularioCategoriaActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.FormularioCategoriaListAdapter;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

public class FormularioCategoriaHelper {

    private ExpandableListView lista;
    private FormularioCategoriaActivity context;
    private FormularioCategoriaListAdapter adapter;

    public FormularioCategoriaHelper(FormularioCategoriaActivity context){
        this.context = context;
        this.lista = context.findViewById(R.id.formulario_categoria_lista_tudo);
        iniciaComponentes();
        configuraEventos();
    }

    private void configuraEventos() {
        lista.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ExpandableListAdapter adapter = parent.getExpandableListAdapter();
                Categoria categoriaClicada = (Categoria) adapter.getChild(groupPosition,
                        childPosition);

                Intent intent = new Intent();
                intent.putExtra("CategoriaClicada", categoriaClicada);
                context.setResult(context.RESULT_OK, intent);
                context.finish();
                return true;
            }
        });

    }

    public void iniciaComponentes(){
        ControladorGrupo controladorGrupo = new ControladorGrupo(context);
        ControladorCategoria controladorCategoria = new ControladorCategoria(context);

        List<Grupo> grupos = controladorGrupo.getGrupos();

        Map<Grupo, List<Categoria>> categoriasMap = new HashMap<Grupo, List<Categoria>>();
        for(Grupo grupo : grupos){
            List<Categoria> categoriasDoGrupo = controladorCategoria.getCategorias(grupo);

            Categoria adicionar = new Categoria();
            adicionar.setIdGrupo(grupo.getId());
            adicionar.setId(Long.valueOf(999999999));
            adicionar.setDescricao("Adicionar Categoria...");

            categoriasDoGrupo.add(adicionar);
            categoriasMap.put(grupo, categoriasDoGrupo);
        }

        lista.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        adapter = new FormularioCategoriaListAdapter(context, grupos, categoriasMap);
        lista.setAdapter(adapter);

        for(int i = 1; i < grupos.size(); i++){
            lista.expandGroup(i);
        }
    }

    public ExpandableListView getLista() {
        return lista;
    }

    public Categoria obtemCategoriaNaLista(int posicaoGrupo, int posicaoCategoria) {
        return adapter.getChild(posicaoGrupo, posicaoCategoria);
    }
}
