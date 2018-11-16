package br.com.sbsistemas.minhacarteira.dao;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.to.ListaContaAdapterTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;

import static org.junit.Assert.assertEquals;

/**
 * Created by sebas on 18/08/2017 v
 */
@RunWith(AndroidJUnit4.class)
public class ContaTest {

    private ControladorConta ctrlConta;
    private Long ID_CAT_MANUTENCAO;
    private Long ID_CAT_SAIDAS;

    @Before
    public void init(){
        ctrlConta = new ControladorConta(InstrumentationRegistry.getTargetContext());

        ContaDAO contaDAO = new ContaDAO(InstrumentationRegistry.getTargetContext());
        contaDAO.limpaBanco();
        PrestacoesDAO prestDAO = new PrestacoesDAO(InstrumentationRegistry.getTargetContext());
        prestDAO.limpaBanco();
        CategoriaDAO catDAO = new CategoriaDAO(InstrumentationRegistry.getTargetContext());
        catDAO.limpaBanco();

        Categoria manutencao = new Categoria();
        manutencao.setDescricao("Manutenção");
        manutencao.setIdGrupo(2L);
        ID_CAT_MANUTENCAO = catDAO.inserir(manutencao);

        Categoria saida = new Categoria();
        saida.setDescricao("Saídas");
        saida.setIdGrupo(2L);
        ID_CAT_SAIDAS = catDAO.inserir(saida);

        catDAO.close();
        prestDAO.close();
        contaDAO.close();
    }

    @After
    public void finaliza(){
    }

    @Test
    public void testaBuscaContasEmCategoriaVazia(){
        Categoria manutencao = new Categoria();
        manutencao.setId(ID_CAT_MANUTENCAO); //mock

        Conta compraMartelo = new Conta();
        compraMartelo.setValor(BigDecimal.valueOf(10.5));
        compraMartelo.setDescricao("Compra de martelo");
        compraMartelo.setNumeroDePrestacoes(1);
        compraMartelo.setCategoria(manutencao.getId());

        ctrlConta.criarConta(compraMartelo, true, new LocalDate(2017, 12, 31), true);

        Categoria vazia = new Categoria();
        vazia.setId(ID_CAT_SAIDAS);
        List<ListaContaAdapterTO> contas = ctrlConta.getContas(vazia, 12, 2017);

        assertEquals(0, contas.size());
    }

    @Test
    public void testaBuscaEmCategoriaComUmaConta(){
        Categoria manutencao = new Categoria();
        manutencao.setId(ID_CAT_MANUTENCAO);
        manutencao.setIdGrupo(0L);

        Conta compraMartelo = new Conta();
        compraMartelo.setValor(BigDecimal.valueOf(10.5));
        compraMartelo.setDescricao("Compra de martelo");
        compraMartelo.setNumeroDePrestacoes(1);
        compraMartelo.setCategoria(manutencao.getId());

        ctrlConta.criarConta(compraMartelo, true, new LocalDate(2017, 2, 28), true);

        Categoria outraCategoria = new Categoria();
        outraCategoria.setId(ID_CAT_SAIDAS);
        outraCategoria.setIdGrupo(0L);
        Conta compraOutra = new Conta();
        compraOutra.setValor(BigDecimal.valueOf(10.5));
        compraOutra.setDescricao("Outra");
        compraOutra.setNumeroDePrestacoes(1);
        compraOutra.setCategoria(outraCategoria.getId());

        ctrlConta.criarConta(compraOutra, true, new LocalDate(2017, 2, 1), true);

        List<ListaContaAdapterTO> contasManutencao = ctrlConta.getContas(manutencao, 2, 2017);
        List<ListaContaAdapterTO> contasOutras = ctrlConta.getContas(outraCategoria, 2, 2017);

        assertEquals(1, contasOutras.size());
        assertEquals(1, contasManutencao.size());
    }

    @Test
    public void testaBuscaEmPrestacaoInativa(){
        Categoria manutencao = new Categoria();
        manutencao.setId(ID_CAT_MANUTENCAO); //mock
        manutencao.setIdGrupo(0L);

        Conta compraMartelo = new Conta();
        compraMartelo.setValor(BigDecimal.valueOf(10.5));
        compraMartelo.setDescricao("Compra de martelo");
        compraMartelo.setNumeroDePrestacoes(1);
        compraMartelo.setCategoria(manutencao.getId());

        ctrlConta.criarConta(compraMartelo, true, new LocalDate(2017, 12, 31), false);

        List<ListaContaAdapterTO> contasManutencao = ctrlConta.getContas(manutencao, 12, 2017);
        assertEquals(1, contasManutencao.size());
    }

}
