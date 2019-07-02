package br.com.sbsistemas.minhacarteira;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import br.com.sbsistemas.minhacarteira.dialogs.ExclusaoCategoriaDialogFragment;
import br.com.sbsistemas.minhacarteira.helpers.FormularioCategoriaHelper;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

public class FormularioCategoriaActivity extends AppCompatActivity implements
        ExclusaoCategoriaDialogFragment.ExclusaoCategoriaDialogListener {

    private FormularioCategoriaHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_categoria);
        iniciaComponentes();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
        //aborta o menu se longclick ocorre em um grupo
        if(ExpandableListView.getPackedPositionType(info.packedPosition) ==
                ExpandableListView.PACKED_POSITION_TYPE_GROUP) return;

        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Excluir Categoria");
        menu.add("Excluir");
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.iniciaComponentes();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("Excluir")){
            DialogFragment dialog = new ExclusaoCategoriaDialogFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("categoria", obtemCategoriaClicada(item));

            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "ExclusaoCategoriaFragmet");
            return true;
        }
        return super.onContextItemSelected(item);
    }

    private Categoria obtemCategoriaClicada(MenuItem item) {
        ExpandableListView.ExpandableListContextMenuInfo info =
                (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
        int posicaoGrupo = ExpandableListView.getPackedPositionGroup(info.packedPosition);
        int posicaoCategoria = ExpandableListView.getPackedPositionChild(info.packedPosition);
        return helper.obtemCategoriaNaLista(posicaoGrupo, posicaoCategoria);
    }

    public void iniciaComponentes() {
        helper = new FormularioCategoriaHelper(this);
        registerForContextMenu(helper.getLista());
    }

    @Override
    public void onPositiveClick(DialogFragment dialog, Categoria categoria) {
        new ControladorCategoria(this).excluirCategoria(categoria);
        helper.iniciaComponentes();
        Toast.makeText(this, "Categoria " + categoria.getDescricao()
                + " excluída com sucesso", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNegativeClick(DialogFragment dialog) {
        //do nothing
    }
}
