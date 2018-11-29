package br.com.sbsistemas.minhacarteira.helpers;

import android.support.annotation.Nullable;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.ListaCategoriasActivity;
import br.com.sbsistemas.minhacarteira.ListaContasActivity;
import br.com.sbsistemas.minhacarteira.adapter.to.EstatisticaTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_maior;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_media;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_menor;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_mes_anterior;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_mes_atual;
import static br.com.sbsistemas.minhacarteira.R.id.lista_categoria_estatistica_titulo;

/**
 * Created by sebas on 17/11/2018.
 */

public class EstatisticasCategoriaHelper {

    private ListaCategoriasActivity categoriaActivity;
    private TextView tituloEstatistica;
    private TextView valorAtualEstatistica;
    private TextView valorMesAnteriorEstatistica;
    private TextView menorValorEstatistica;
    private TextView maiorValorEstatistica;
    private TextView mediaEstatistica;


    public EstatisticasCategoriaHelper(ListaCategoriasActivity context){

        categoriaActivity = context;
        tituloEstatistica = (TextView) categoriaActivity.findViewById(lista_categoria_estatistica_titulo);
        valorAtualEstatistica = (TextView) categoriaActivity.findViewById(lista_categoria_estatistica_mes_atual);
        menorValorEstatistica = (TextView) categoriaActivity.findViewById(lista_categoria_estatistica_menor);
        maiorValorEstatistica = (TextView) categoriaActivity.findViewById(lista_categoria_estatistica_maior);
        mediaEstatistica = (TextView) categoriaActivity.findViewById(lista_categoria_estatistica_media);
        valorMesAnteriorEstatistica = (TextView) categoriaActivity.findViewById(lista_categoria_estatistica_mes_anterior);
    }

    public void atualizaEstatisticas(@Nullable Categoria categoria, Grupo grupoSelecionado,
                                     LocalDate dataSelecionada) {

        BigDecimal totalAtual = new BigDecimal(0);
        BigDecimal totalMesAnterior = new BigDecimal(0);
        EstatisticaTO menorValor = new EstatisticaTO();
        EstatisticaTO maiorValor = new EstatisticaTO();
        BigDecimal media = new BigDecimal(0);
        String titulo = "";

        if(categoria == null){//mostra estatisticas do grupo
            ControladorGrupo controladorGrupo = new ControladorGrupo(categoriaActivity);
            titulo = grupoSelecionado.getDescricao();
            totalAtual = new BigDecimal(controladorGrupo.getQuantidadeValor(grupoSelecionado,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear()).getValor());
            totalMesAnterior = new BigDecimal(controladorGrupo.getQuantidadeValor(grupoSelecionado,
                    dataSelecionada.minusMonths(1).getMonthOfYear(), dataSelecionada.minusMonths(1).getYear()).getValor());
            menorValor = controladorGrupo.calculaMenorValorGasto(grupoSelecionado,
                    dataSelecionada);
            maiorValor = controladorGrupo.calculaMaiorValorGasto(grupoSelecionado,
                    dataSelecionada);
            media = controladorGrupo.calculaMedia(grupoSelecionado, dataSelecionada);
        } else{
            //mostra estatistica da categoria selecionada
            ControladorCategoria ctrlCategoria = new ControladorCategoria(categoriaActivity);
            titulo = categoria.getDescricao();
            totalAtual = ctrlCategoria.getTotalGastosCategoria(categoria,
                    dataSelecionada.getMonthOfYear(), dataSelecionada.getYear());
            totalMesAnterior = ctrlCategoria.getTotalGastosCategoria(categoria,
                    dataSelecionada.minusMonths(1).getMonthOfYear(), dataSelecionada.minusMonths(1).getYear());
            menorValor = ctrlCategoria.calculaMenorValorGasto(categoria, dataSelecionada);
            maiorValor = ctrlCategoria.calculaMaiorValorGasto(categoria, dataSelecionada);
            media = ctrlCategoria.calculaMedia(categoria, dataSelecionada);
        }

        tituloEstatistica.setText(titulo);
        valorAtualEstatistica.setText(String.format("Atual: R$ %.2f", totalAtual.floatValue()));
        valorMesAnteriorEstatistica.setText(String.format("Anterior: R$ %.2f", totalMesAnterior.floatValue()));
        maiorValorEstatistica.setText("Maior: " + maiorValor.toString());
        menorValorEstatistica.setText("Menor: " + menorValor.toString());
        mediaEstatistica.setText(String.format("Média: R$ %.2f", media.floatValue()));
    }

}
