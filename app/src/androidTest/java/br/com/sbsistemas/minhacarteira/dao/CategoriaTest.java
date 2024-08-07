package br.com.sbsistemas.minhacarteira.dao;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.List;

import br.com.sbsistemas.minhacarteira.controlador.ControladorCategoria;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.controlador.ControladorGrupo;
import br.com.sbsistemas.minhacarteira.exception.CategoriaRepetidaException;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * Created by sebas on 06/08/2017.
 */
@RunWith(AndroidJUnit4.class)
public class CategoriaTest {

    private ControladorCategoria ctrlCat;
    private Long ID_CAT_COMBUSTIVEL;
    private Long ID_CAT_SAIDAS;

    @Before
    public void configuraTestes(){
        ctrlCat = new ControladorCategoria(InstrumentationRegistry.getTargetContext());

        CategoriaDAO dao = new CategoriaDAO(InstrumentationRegistry.getTargetContext());
        dao.limpaBanco();
        ContaDAO contaDAO = new ContaDAO(InstrumentationRegistry.getTargetContext());
        contaDAO.limpaBanco();
        contaDAO.close();
        PrestacoesDAO pDAO = new PrestacoesDAO(InstrumentationRegistry.getTargetContext());
        pDAO.limpaBanco();
        pDAO.close();

        //cria duas categorias iniciais
        Categoria combustivel = new Categoria();
        combustivel.setDescricao("Combustível");
        combustivel.setIdGrupo(new Long(4));//transporte
        ID_CAT_COMBUSTIVEL = dao.inserir(combustivel);

        Categoria saidas = new Categoria();
        saidas.setDescricao("Saídas");
        saidas.setIdGrupo(new Long(0));
        ID_CAT_SAIDAS = dao.inserir(saidas);
        dao.close();
    }

    @After
    public void finaliza(){
    }

    @Test
    public void testaBuscaCategoriaPorGrupo(){
        Grupo todas = new Grupo();
        todas.setDescricao(GrupoDAO.GRUPO_TODAS);
        todas.setId(Long.valueOf(0));
        //pre teste
        List<Categoria> todasCategorias = ctrlCat.getCategorias(todas);
        assertEquals(2, todasCategorias.size());

        Grupo transporte = new Grupo();
        transporte.setDescricao(GrupoDAO.GRUPO_TRANSPORTE);
        transporte.setId(Long.valueOf(4));
        List<Categoria> categorias = ctrlCat.getCategorias(transporte);
        assertEquals(1, categorias.size());
        assertEquals("Combustível", categorias.get(0).getDescricao());
    }

    @Test
    public void tentaRepetirCategoriaNoMesmoGrupo(){
        Categoria categoriaRepetida = new Categoria();
        categoriaRepetida.setDescricao("Combustível");
        categoriaRepetida.setIdGrupo(Long.valueOf(4));

        try{
            ctrlCat.criarCategoria(categoriaRepetida);
            fail("Deveria Lançar exceção por repetir categoria");
        } catch (CategoriaRepetidaException e){
            assertEquals("Já existe a categoria " + categoriaRepetida.getDescricao() + " no grupo.",
                    e.getMessage());
        }
    }

    @Test
    public void repeteCategoriaEmGrupoDiferente(){
        Categoria categoria = new Categoria();
        categoria.setDescricao("Combustível");
        categoria.setIdGrupo(Long.valueOf(0));

        try{
            ctrlCat.criarCategoria(categoria);
        } catch (CategoriaRepetidaException e){
            fail("Não deveria lançar exceção, pois só a descrição é repetida.");
        }
    }

    @Test
    public void testaBuscaPorDescricaoEGrupo(){
        Grupo g = new Grupo();
        g.setId(Long.valueOf(4));
        Categoria existe = ctrlCat.getCategoria("Combustível", g);
        g.setId(Long.valueOf(0));
        Categoria naoExiste = ctrlCat.getCategoria("Combustível", g);

        assertNotNull(existe);
        assertNull(naoExiste);

    }

    @Test
    public void testaCalculoTotalCategoria(){
        /*
        Testa a adicção de duas contas (uma ativa e outra inativa em uma
        categoria, junto com uma conta ativa em outra categoria

                  JAN   FEV   MAR  ABR
        c1         -     -     -
        c2               -     -
        inat       *     *     *    *
        outra      -     -
        total   100.56 101.1 101.1  0
         */

        Conta conta = new Conta();
        conta.setDescricao("conta1");
        conta.setNumeroDePrestacoes(3);
        conta.setValor(new BigDecimal(100.56));
        conta.setCategoria(ID_CAT_COMBUSTIVEL);

        ControladorConta controlConta = new ControladorConta(InstrumentationRegistry.getTargetContext());
        controlConta.criarConta(conta, false, new LocalDate(2017, 01, 30), true);

        Conta conta2 = new Conta();
        conta2.setDescricao("conta2");
        conta2.setNumeroDePrestacoes(2);
        conta2.setValor(new BigDecimal(0.54));
        conta2.setCategoria(ID_CAT_COMBUSTIVEL);

        controlConta.criarConta(conta2, false, new LocalDate(2017, 02, 28), true);

        Conta contaInativa = new Conta();
        contaInativa.setDescricao("Inativa");
        contaInativa.setNumeroDePrestacoes(4);
        contaInativa.setValor(new BigDecimal(300.5));
        contaInativa.setCategoria(ID_CAT_COMBUSTIVEL);

        controlConta.criarConta(contaInativa, false, new LocalDate(2017, 01, 30), false);

        Conta contaOutraCategoria = new Conta();
        contaOutraCategoria.setDescricao("outra catagoria - nao deve contar");
        contaOutraCategoria.setNumeroDePrestacoes(2);
        contaOutraCategoria.setValor(new BigDecimal(300.5));
        contaOutraCategoria.setCategoria(ID_CAT_SAIDAS);

        controlConta.criarConta(contaOutraCategoria, false, new LocalDate(2017, 01, 30), true);

        Categoria cat1 = new Categoria();
        cat1.setId(ID_CAT_COMBUSTIVEL);
        ControladorCategoria controlCategoria = new ControladorCategoria(InstrumentationRegistry.getTargetContext());

        assertEquals(100.56, controlCategoria.getTotalGastosCategoria(cat1, 1, 2017).doubleValue(), 0.0001);
        assertEquals(101.1, controlCategoria.getTotalGastosCategoria(cat1, 2, 2017).doubleValue(), 0.0001);
        assertEquals(101.1, controlCategoria.getTotalGastosCategoria(cat1, 3, 2017).doubleValue(), 0.0001);
        assertEquals(0, controlCategoria.getTotalGastosCategoria(cat1, 4, 2017).doubleValue(), 0.0001);
    }

    @Test
    public void testaCalculoTotalContasDaCategoria(){
        /*
        Testa a adição de duas contas (uma ativa e outra inativa em uma
        categoria, junto com uma conta ativa em outra categoria

                  JAN   FEV   MAR  ABR
        c1         -     -     -
        c2               -     -
        inat       *     *     *    *
        outra      -     -
        total      2     3    3    1
         */

        Conta conta = new Conta();
        conta.setDescricao("conta1");
        conta.setNumeroDePrestacoes(3);
        conta.setValor(new BigDecimal(100.56));
        conta.setCategoria(ID_CAT_COMBUSTIVEL);

        ControladorConta controlConta = new ControladorConta(InstrumentationRegistry.getTargetContext());
        controlConta.criarConta(conta, false, new LocalDate(2017, 01, 30), true);

        Conta conta2 = new Conta();
        conta2.setDescricao("conta2");
        conta2.setNumeroDePrestacoes(2);
        conta2.setValor(new BigDecimal(0.54));
        conta2.setCategoria(ID_CAT_COMBUSTIVEL);

        controlConta.criarConta(conta2, false, new LocalDate(2017, 02, 28), true);

        Conta contaInativa = new Conta();
        contaInativa.setDescricao("Inativa");
        contaInativa.setNumeroDePrestacoes(4);
        contaInativa.setValor(new BigDecimal(300.5));
        contaInativa.setCategoria(ID_CAT_COMBUSTIVEL);

        controlConta.criarConta(contaInativa, false, new LocalDate(2017, 01, 30), false);

        Conta contaOutraCategoria = new Conta();
        contaOutraCategoria.setDescricao("outra catagoria - nao deve contar");
        contaOutraCategoria.setNumeroDePrestacoes(2);
        contaOutraCategoria.setValor(new BigDecimal(300.5));
        contaOutraCategoria.setCategoria(ID_CAT_SAIDAS);

        controlConta.criarConta(contaOutraCategoria, false, new LocalDate(2017, 01, 30), true);

        Categoria cat1 = new Categoria();
        cat1.setId(ID_CAT_COMBUSTIVEL);
        ControladorCategoria controlCategoria = new ControladorCategoria(InstrumentationRegistry.getTargetContext());

        assertEquals(2, controlCategoria.getTotalDeContas(cat1, 1, 2017));
        assertEquals(3, controlCategoria.getTotalDeContas(cat1, 2, 2017));
        assertEquals(3, controlCategoria.getTotalDeContas(cat1, 3, 2017));
        assertEquals(1, controlCategoria.getTotalDeContas(cat1, 4, 2017));
    }

    @Test
    public void testaBuscarCategoriasComContas(){
        ControladorGrupo controladorGrupo = new ControladorGrupo(
                InstrumentationRegistry.getTargetContext());
        ControladorCategoria controladorCategoria = new ControladorCategoria(
                InstrumentationRegistry.getTargetContext());

        Categoria categoriaComCompras = new Categoria();
        categoriaComCompras.setDescricao("Tem Compra");
        // grupo ja tem uma categora vazia
        Grupo transporte = controladorGrupo.getGrupo(GrupoDAO.GRUPO_SAUDE);
        categoriaComCompras.setIdGrupo(transporte.getId());
        long idCategoria = ctrlCat.criarCategoria(categoriaComCompras);

        Conta conta = new Conta();
        conta.setDescricao("conta1");
        conta.setNumeroDePrestacoes(3);
        conta.setValor(new BigDecimal(100.56));
        conta.setCategoria(idCategoria);

        ControladorConta controladorConta = new ControladorConta(
                InstrumentationRegistry.getTargetContext());
        controladorConta.criarConta(conta, true, new LocalDate(2017, 1, 1), false);

        assertEquals(2, controladorCategoria.getCategorias(transporte).size());
        assertEquals(1, controladorCategoria.getCategoriasComContas(transporte, 1, 2017).size());
    }

    @Test
    public void testaTotalGastosComUmPrestacaoInativa(){
                /*
        Testa a adicção de duas contas (uma ativa e outra inativa em uma
        categoria, junto com uma conta ativa em outra categoria

                  JAN   FEV   MAR  ABR
        c1         -     -     -
        c2               *     -
        total   100.0  100.0 200.0  0
         */
        ControladorConta controlConta = new ControladorConta(InstrumentationRegistry.getTargetContext());
        PrestacoesDAO prestacoesDAO = new PrestacoesDAO(InstrumentationRegistry.getTargetContext());

        Conta conta = new Conta();
        conta.setDescricao("conta1");
        conta.setNumeroDePrestacoes(3);
        conta.setValor(new BigDecimal(100));
        conta.setCategoria(ID_CAT_COMBUSTIVEL);
        controlConta.criarConta(conta, false, new LocalDate(2017, 01, 30), true);

        Conta conta2 = new Conta();
        conta2.setDescricao("conta2");
        conta2.setNumeroDePrestacoes(2);
        conta2.setValor(new BigDecimal(100));
        conta2.setCategoria(ID_CAT_COMBUSTIVEL);
        long conta2ID = controlConta.criarConta(conta2, false, new LocalDate(2017, 02, 28), true);
        conta2.setId(conta2ID);
        Prestacao prestacaoInativa = prestacoesDAO.getPrestacao(conta2, 02, 2017);
        prestacaoInativa.setAtivo(false);
        prestacoesDAO.atualiza(prestacaoInativa);

        ControladorCategoria controlCategoria = new ControladorCategoria(InstrumentationRegistry.getTargetContext());
        Categoria cat1 = new Categoria();
        cat1.setId(ID_CAT_COMBUSTIVEL);

        assertEquals(100.0, controlCategoria.getTotalGastosCategoria(cat1, 1, 2017).doubleValue(), 0.0001);
        assertEquals(1, controlCategoria.getTotalDeContas(cat1, 1, 2017));

        assertEquals(100.0, controlCategoria.getTotalGastosCategoria(cat1, 2, 2017).doubleValue(), 0.0001);
        assertEquals(2, controlCategoria.getTotalDeContas(cat1, 2, 2017));

        assertEquals(200.0, controlCategoria.getTotalGastosCategoria(cat1, 3, 2017).doubleValue(), 0.0001);
        assertEquals(2, controlCategoria.getTotalDeContas(cat1, 3, 2017));

    }
}
