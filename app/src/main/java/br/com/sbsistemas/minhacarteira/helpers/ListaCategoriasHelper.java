package br.com.sbsistemas.minhacarteira.helpers;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.sbsistemas.minhacarteira.ListaCategoriasActivity;
import br.com.sbsistemas.minhacarteira.ListaContasActivity;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.ListaCategoriaAdapter;
import br.com.sbsistemas.minhacarteira.adapter.to.CategoriaAdapterTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.utils.ColorGradientGenerator;
import br.com.sbsistemas.minhacarteira.utils.CorGrupo;

/**
 * Created by sebas on 17/11/2018.
 */

public class ListaCategoriasHelper {

    private ListaCategoriasActivity categoriaActivity;
    private ListView categoriasListView;

    public ListaCategoriasHelper(ListaCategoriasActivity context){

        categoriaActivity = context;
        categoriasListView = categoriaActivity.findViewById(R.id.lista_categoria_lista);
        configuraEventos();
    }

    private void configuraEventos(){
        categoriasListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CategoriaAdapterTO categoriaTOClicada = getCategoriaAtPosition(position);
                Categoria categoria = categoriaTOClicada.getCategoria();

                Intent intent = new Intent(categoriaActivity, ListaContasActivity.class);
                intent.putExtra("categoria", categoria);
                intent.putExtra("data", categoriaActivity.getDataSelecionada());
                categoriaActivity.startActivity(intent);
            }
        });
    }

    public void carregaListaDeCategorias(Grupo grupoSelecionado, LocalDate dataSelecionada) {
        ControladorCategoria controladorCategoria = new ControladorCategoria(categoriaActivity);

        List<Categoria> categoriasComContas = controladorCategoria.getCategoriasComContas(
                grupoSelecionado, dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());

        List<CategoriaAdapterTO> categoriasTO = new ArrayList<>();
        int totalNumeroDeContas = 0;
        BigDecimal totalValorContas = new BigDecimal(0);
        for(Categoria categoria : categoriasComContas){
            int totalContasCategoria = controladorCategoria.getTotalDeContas(categoria,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            BigDecimal totalGastosCategoria = controladorCategoria.getTotalGastosCategoria(categoria,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            CategoriaAdapterTO categoriaTO = new CategoriaAdapterTO(categoria,
                    totalContasCategoria, totalGastosCategoria);
            categoriasTO.add(categoriaTO);

            totalNumeroDeContas += totalContasCategoria;
            totalValorContas = totalValorContas.add(totalGastosCategoria);
        }
        //Cria uma categoria a mais, representando todas as contas do grupo
        categoriasTO.add(criaCategoriaTodas(totalNumeroDeContas, totalValorContas,
                grupoSelecionado));

        Collections.sort(categoriasTO);
        Collections.reverse(categoriasTO);

        //cria o gradiente de cores das categorias do grupo
        ColorGradientGenerator gradiente =
                new ColorGradientGenerator(new CorGrupo().getCorRGB(grupoSelecionado.getDescricao()),
                        categoriasTO.size());
        int[] cores = gradiente.gerarGradiente();
        for(int i = 0; i < categoriasTO.size(); i++){
            categoriasTO.get(i).setBackgroundColor(cores[i]);
        }

        ListaCategoriaAdapter adapter = new ListaCategoriaAdapter(categoriaActivity, categoriasTO);
        categoriasListView.setAdapter(adapter);
    }

    private CategoriaAdapterTO criaCategoriaTodas(int totalNumeroDeContas,
                                                  BigDecimal totalValorContas,
                                                  Grupo grupoSelecionado) {
        Categoria todas = new Categoria();
        todas.setIdGrupo(grupoSelecionado.getId());
        todas.setDescricao("Todas");

        CategoriaAdapterTO todasTO = new CategoriaAdapterTO(todas, totalNumeroDeContas,
                totalValorContas);
        return todasTO;
    }

    public CategoriaAdapterTO getCategoriaAtPosition(int position) {
        return (CategoriaAdapterTO) categoriasListView.getItemAtPosition(position);
    }

    public List<CategoriaAdapterTO> getCategorias() {
        ListaCategoriaAdapter adapter = (ListaCategoriaAdapter) categoriasListView.getAdapter();
        return adapter.getCategoriasAdapterTO();
    }

}
