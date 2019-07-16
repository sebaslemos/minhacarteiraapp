package br.com.sbsistemas.minhacarteira.controlador;

import android.content.Context;

import androidx.annotation.Nullable;

import org.joda.time.LocalDate;

import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.to.ContaTO;
import br.com.sbsistemas.minhacarteira.dao.ContaDAO;
import br.com.sbsistemas.minhacarteira.dao.PrestacoesDAO;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

/**
 * Created by sebas on 23/08/2017.
 */

public class ControladorConta {

    private Context context;

    public ControladorConta(Context context){
        this.context = context;
    }

    /**
     * Retorna as contas de uma categoria, incluindo as inativas
     * @param categoria
     * @param mes
     * @param ano
     * @return
     */
    public List<ContaTO> getContas(@Nullable Categoria categoria, int mes, int ano){
        ContaDAO contaDAO = new ContaDAO(context);
        List<ContaTO> contas = contaDAO.getContas(categoria, mes, ano);
        contaDAO.close();
        return  contas;
    }

    public List<ContaTO> getContasNaoPagas(int dia, int mes, int ano){
        ContaDAO dao = new ContaDAO(context);
        List<ContaTO> contasNaoPagas = dao.getContasNaoPagas(dia, mes, ano);
        dao.close();
        return contasNaoPagas;
    }

    public long criarConta(Conta conta, boolean pago, LocalDate dataConta, boolean ativo){
        validaDados(conta, dataConta);

        ContaDAO cDAO = new ContaDAO(context);
        PrestacoesDAO pDAO = new PrestacoesDAO(context);

        long contaId = cDAO.inserir(conta);

        LocalDate dataPrestacao = dataConta;
        for(int i = 1; i <= conta.getNumeroDePrestacoes(); i++){
            Prestacao prestacao = new Prestacao();
            prestacao.setNumParcela(i);
            prestacao.setPago(pago);
            prestacao.setData(dataPrestacao);
            prestacao.setContaID(contaId);
            prestacao.setAtivo(ativo);
            dataPrestacao = dataConta.plusMonths(i);
            pDAO.inserir(prestacao);
        }

        cDAO.close();
        pDAO.close();
        return contaId;
    }

    private void validaDados(Conta conta, LocalDate dataConta) {
        if(conta.getNumeroDePrestacoes() == null || conta.getNumeroDePrestacoes() < 1){
            throw new IllegalArgumentException("A conta deve ter uma ou mais prestações");
        }

        if(conta.getDescricao() == null || conta.getDescricao().equals("")){
            throw new IllegalArgumentException("A descrição da conta deve ser preenchida");
        }

        ControladorCategoria controladorCategoria = new ControladorCategoria(context);
        if(controladorCategoria.getCategoria(conta.getCategoria()) == null){
            throw new IllegalArgumentException("Categoria informada não encontrada");
        }

        if(conta.getValor() == null || conta.getValor().doubleValue() < 0){
            throw new IllegalArgumentException("O valor da conta deve ser maior ou igual a 0");
        }

        if(dataConta == null){
            throw new IllegalArgumentException("A data da conta deve ser informada");
        }
    }

    public void deletar(Conta conta) {
        ContaDAO contaDAO = new ContaDAO(context);
        PrestacoesDAO prestacoesDAO = new PrestacoesDAO(context);

        prestacoesDAO.remover(conta);
        contaDAO.remover(conta);

        contaDAO.close();
        prestacoesDAO.close();
    }

    public void atualizaConta(Conta conta) {
        ContaDAO dao = new ContaDAO(context);

        dao.atualizar(conta);

        dao.close();
    }

    public List<Conta> getContas(Categoria categoria) {
        ContaDAO dao = new ContaDAO(context);

        List<Conta> contas = dao.getContas(categoria);
        dao.close();

        return contas;
    }
}
