package br.com.sbsistemas.minhacarteira;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sbsistemas.minhacarteira.adapter.FormularioCategoriaListAdapter;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

public class FormularioCategoriaActivity extends AppCompatActivity {

    ExpandableListView lista;
    ControladorGrupo controladorGrupo;
    ControladorCategoria controladorCategoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_categoria);

        controladorGrupo = new ControladorGrupo(this);
        controladorCategoria = new ControladorCategoria(this);
        iniciaComponentes();
        configuraEventos();
    }

    private void configuraEventos() {
        lista.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                ExpandableListAdapter adapter = parent.getExpandableListAdapter();
                Categoria categoriaClicada = (Categoria) adapter.getChild(groupPosition, childPosition);

                Intent intent = new Intent();
                intent.putExtra("CategoriaClicada", categoriaClicada);
                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
        });
    }

    public void iniciaComponentes() {
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

        lista = (ExpandableListView) findViewById(R.id.formulario_categoria_lista_tudo);
        lista.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        FormularioCategoriaListAdapter adapter = new FormularioCategoriaListAdapter(this, grupos, categoriasMap);
        lista.setAdapter(adapter);

        for(int i = 1; i < grupos.size(); i++){
            lista.expandGroup(i);
        }
    }
}
