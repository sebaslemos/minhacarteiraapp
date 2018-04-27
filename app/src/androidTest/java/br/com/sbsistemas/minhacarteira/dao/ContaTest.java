package br.com.sbsistemas.minhacarteira.dao;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.TestCase;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

import static org.junit.Assert.assertEquals;

/**
 * Created by sebas on 18/08/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ContaTest {

    private ContaDAO contaDAO;
    private PrestacoesDAO prestDAO;
    private CategoriaDAO catDAO;

    @Before
    public void init(){
        contaDAO = new ContaDAO(InstrumentationRegistry.getTargetContext());
        contaDAO.limpaBanco();
        prestDAO = new PrestacoesDAO(InstrumentationRegistry.getTargetContext());
        prestDAO.limpaBanco();
        catDAO = new CategoriaDAO(InstrumentationRegistry.getTargetContext());
        catDAO.limpaBanco();

        Categoria manutencao = new Categoria();
        manutencao.setDescricao("Manutenção");
        manutencao.setIdGrupo(Long.valueOf(0));
        catDAO.inserir(manutencao);

        Categoria saida = new Categoria();
        saida.setDescricao("Saídas");
        saida.setIdGrupo(Long.valueOf(0));
        catDAO.inserir(saida);

        catDAO.close();
    }

    @After
    public void finaliza(){
        contaDAO.close();
        prestDAO.close();
    }

    @Test
    public void testaBuscaContasEmCategoriaVazia(){
        Categoria manutencao = new Categoria();
        manutencao.setId(Long.valueOf(0)); //mock

        Conta compraMartelo = new Conta();
        compraMartelo.setValor(BigDecimal.valueOf(10.5));
        compraMartelo.setDescricao("Compra de martelo");
        compraMartelo.setNumeroDePrestacoes(1);
        compraMartelo.setCategoria(manutencao.getId());
        Long id = contaDAO.inserir(compraMartelo);

        Prestacao prestacao = new Prestacao();
        prestacao.setAtivo(true);
        prestacao.setContaID(id);
        prestacao.setData(new LocalDate(2017, 12, 31));
        prestacao.setNumParcela(1);
        prestacao.setPago(true);
        prestDAO.inserir(prestacao);

        Categoria vazia = new Categoria();
        vazia.setId(Long.valueOf(1));
        List<Conta> contas = contaDAO.getContas(vazia, 12, 2017);

        assertEquals(0, contas.size());
    }

    @Test
    public void testaBuscaEmCategoriaComUmaConta(){
        Categoria manutencao = new Categoria();
        manutencao.setId(Long.valueOf(0)); //mock

        Conta compraMartelo = new Conta();
        compraMartelo.setValor(BigDecimal.valueOf(10.5));
        compraMartelo.setDescricao("Compra de martelo");
        compraMartelo.setNumeroDePrestacoes(1);
        compraMartelo.setCategoria(manutencao.getId());
        Long id = contaDAO.inserir(compraMartelo);

        Prestacao prestacao = new Prestacao();
        prestacao.setAtivo(true);
        prestacao.setContaID(id);
        prestacao.setData(new LocalDate(2017, 02, 28));
        prestacao.setNumParcela(1);
        prestacao.setPago(true);
        prestDAO.inserir(prestacao);

        Categoria outraCategoria = new Categoria();
        outraCategoria.setId(Long.valueOf(1));
        Conta compraOutra = new Conta();
        compraOutra.setValor(BigDecimal.valueOf(10.5));
        compraOutra.setDescricao("Outra");
        compraOutra.setNumeroDePrestacoes(1);
        compraOutra.setCategoria(outraCategoria.getId());
        Long idOutra = contaDAO.inserir(compraOutra);

        Prestacao prestacaoOutra = new Prestacao();
        prestacaoOutra.setAtivo(true);
        prestacaoOutra.setContaID(idOutra);
        prestacaoOutra.setData(new LocalDate(2017, 02, 01));
        prestacaoOutra.setNumParcela(1);
        prestacaoOutra.setPago(true);
        prestDAO.inserir(prestacaoOutra);

        List<Conta> contasManutencao = contaDAO.getContas(manutencao, 02, 2017);
        List<Conta> contasOutras = contaDAO.getContas(outraCategoria, 02, 2017);

        assertEquals(1, contasOutras.size());
        assertEquals(1, contasManutencao.size());
    }

    @Test
    public void testaBuscaEmPrestacaoInativa(){
        Categoria manutencao = new Categoria();
        manutencao.setId(Long.valueOf(0)); //mock

        Conta compraMartelo = new Conta();
        compraMartelo.setValor(BigDecimal.valueOf(10.5));
        compraMartelo.setDescricao("Compra de martelo");
        compraMartelo.setNumeroDePrestacoes(1);
        compraMartelo.setCategoria(manutencao.getId());
        Long id = contaDAO.inserir(compraMartelo);

        Prestacao prestacao = new Prestacao();
        prestacao.setAtivo(false);
        prestacao.setContaID(id);
        prestacao.setData(new LocalDate(2017, 12, 31));
        prestacao.setNumParcela(1);
        prestacao.setPago(true);
        prestDAO.inserir(prestacao);

        List<Conta> contasManutencao = contaDAO.getContas(manutencao, 12, 2017);
        assertEquals(1, contasManutencao.size());
    }

}
