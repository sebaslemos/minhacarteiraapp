package br.com.sbsistemas.minhacarteira.helpers;

import android.widget.ListView;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.sbsistemas.minhacarteira.ListaGrupos;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.GrupoAdapter;
import br.com.sbsistemas.minhacarteira.adapter.to.GrupoAdapterTO;
import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 17/11/2018.
 */

public class ListaGruposHelper {

    private ListaGrupos listaGrupoContext;
    private ListView gruposListView;

    public ListaGruposHelper(ListaGrupos contexto){

        this.listaGrupoContext = contexto;
        gruposListView = (ListView) listaGrupoContext.findViewById(R.id.lista_grupos_lista);
        gruposListView.setOnItemClickListener(listaGrupoContext);
    }

    public void carregaListaGrupos(LocalDate dataSelecionada) {
        ControladorGrupo controladorGrupo = new ControladorGrupo(listaGrupoContext);
        List<Grupo> grupos = controladorGrupo.getGrupos();
        List<GrupoAdapterTO> grupoAdapterTOs = new ArrayList<GrupoAdapterTO>();

        for(Grupo grupo : grupos){
            QuantidadeValorTO quantidadeValorTO =
                    controladorGrupo.getQuantidadeValor(grupo, dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            int totalDeContas = quantidadeValorTO.getQuantidade();
            BigDecimal totalGastosDoGrupo = new BigDecimal(quantidadeValorTO.getValor());
            GrupoAdapterTO grupoTO = new GrupoAdapterTO(grupo, totalDeContas, totalGastosDoGrupo);

            grupoAdapterTOs.add(grupoTO);
        }

        //apresenta os grupos em ordem de maior gasto
        Collections.sort(grupoAdapterTOs);
        Collections.reverse(grupoAdapterTOs);
        GrupoAdapter adapter = new GrupoAdapter(listaGrupoContext, grupoAdapterTOs.toArray(new GrupoAdapterTO[grupoAdapterTOs.size()]));
        gruposListView.setAdapter(adapter);
    }

    /**
     *
     * @return os grupos da lista de grupos
     */
    public GrupoAdapterTO[] getGrupos() {
        GrupoAdapter adapter = (GrupoAdapter) gruposListView.getAdapter();
        return adapter.getGrupos();
    }

    public GrupoAdapterTO getGrupoTOAtPosition(int position) {
        return (GrupoAdapterTO) gruposListView.getItemAtPosition(position);
    }
}
