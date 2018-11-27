package br.com.sbsistemas.minhacarteira.dao;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

/**
 * Created by sebas on 20/08/2017.
 */

@RunWith(AndroidJUnit4.class)
public class PrestacaoTest {

    private PrestacoesDAO prestacoesDAO;
    long idConta;

    @Before
    public void init(){
        prestacoesDAO = new PrestacoesDAO(InstrumentationRegistry.getTargetContext());
        prestacoesDAO.limpaBanco();

        ContaDAO contaDAO = new ContaDAO(InstrumentationRegistry.getTargetContext());
        Conta conta = new Conta();
        conta.setCategoria(Long.valueOf(0));
        conta.setValor(new BigDecimal(10));
        conta.setDescricao("Nova");
        conta.setNumeroDePrestacoes(3);
        idConta = contaDAO.inserir(conta);

        LocalDate dataInicio = new LocalDate(2017, 1, 31);
        LocalDate dataPrestacao = dataInicio;
        for(int i = 1; i <= conta.getNumeroDePrestacoes(); i++){
            Prestacao prestacao = new Prestacao();
            prestacao.setAtivo(true);
            prestacao.setNumParcela(i);
            prestacao.setContaID(idConta);
            prestacao.setData(dataPrestacao);
            prestacao.setPago(i == 1 ? true :  false);
            prestacoesDAO.inserir(prestacao);
            dataPrestacao = dataInicio.plusMonths(i);
        }
        contaDAO.close();
    }

    @After
    public void finaliza(){
        prestacoesDAO.close();
    }

    @Test
    public void testaBuscarPrestacaoPorContaEmDataErrada(){
        Conta conta = new Conta();
        conta.setId(idConta);
        Prestacao prestacao = prestacoesDAO.getPrestacao(conta, 4, 2017);

        assertNull(prestacao);
    }

    @Test
    public void testaBuscarPrestacaoEmContaErrada(){
        Conta conta = new Conta();
        conta.setId(Long.valueOf(55));
        Prestacao prestacao = prestacoesDAO.getPrestacao(conta, 2, 2017);
        assertNull(prestacao);
    }

    @Test
    public void testaBuscarPrestacao(){
        Conta conta = new Conta();
        conta.setId(idConta);
        Prestacao prestacao = prestacoesDAO.getPrestacao(conta, 1, 2017);

        assertEquals(31, prestacao.getData().getDayOfMonth());
        assertEquals(1, prestacao.getData().getMonthOfYear());
        assertEquals(1, prestacao.getNumParcela().intValue());
        assertEquals(true, prestacao.isPago());

        Prestacao prestacao2 = prestacoesDAO.getPrestacao(conta, 2, 2017);

        assertEquals(28, prestacao2.getData().getDayOfMonth());
        assertEquals(2, prestacao2.getData().getMonthOfYear());
        assertEquals(2, prestacao2.getNumParcela().intValue());
        assertEquals(false, prestacao2.isPago());

        Prestacao prestacao3 = prestacoesDAO.getPrestacao(conta, 3, 2017);

        assertEquals(31, prestacao3.getData().getDayOfMonth());
        assertEquals(3, prestacao3.getData().getMonthOfYear());
        assertEquals(3, prestacao3.getNumParcela().intValue());
        assertEquals(false, prestacao3.isPago());
    }

    @Test
    public void testaBucarPrestacoesDeUmaConta(){

    }
}
