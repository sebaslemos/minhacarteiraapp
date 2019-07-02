package br.com.sbsistemas.minhacarteira.controlador;

import android.content.Context;
import android.widget.TextView;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.to.EstatisticaTO;
import br.com.sbsistemas.minhacarteira.dao.CategoriaDAO;
import br.com.sbsistemas.minhacarteira.dao.GrupoDAO;
import br.com.sbsistemas.minhacarteira.exception.CategoriaRepetidaException;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 20/08/2017.
 */

public class ControladorCategoria {

    private static final int MESES_A_CONTAR_ESTATISTICAS = 8;
    private Context context;

    public ControladorCategoria(Context context){
        this.context = context;
    }

    public List<Categoria> getCategoriasComContas(Grupo grupo, int mes, int ano){
        List<Categoria> categoriasComContas = new ArrayList<>();
        ControladorConta controladorConta = new ControladorConta(context);

        List<Categoria> todas = getCategorias(grupo);
        for(Categoria categoria: todas){
            if(controladorConta.getContas(categoria, mes, ano).size() > 0)
                categoriasComContas.add(categoria);
        }

        return categoriasComContas;
    }

    public  List<Categoria> getCategorias(Grupo grupo) {
        CategoriaDAO catDAO = new CategoriaDAO(context);
        List<Categoria> categorias = catDAO.getCategorias(grupo);
        catDAO.close();

        return categorias;
    }


    /**
     * Retorna o total gasde em uma categoria, excluindo as contas inativas
     * @param categoria
     * @param mes
     * @param ano
     * @return
     */
    public BigDecimal getTotalGastosCategoria(Categoria categoria, int mes, int ano){
        CategoriaDAO categoriaDAO = new CategoriaDAO(context);
        BigDecimal totalGastos = categoriaDAO.getTotalGastos(categoria, mes, ano);
        categoriaDAO.close();
        return totalGastos;

    }

    public long criarCategoria(Categoria categoria){
        if(categoria.getDescricao() == null || categoria.getDescricao().equals(""))
            throw new IllegalArgumentException("A descrição da categoria não pode ser vazia.");

        CategoriaDAO catDAO = new CategoriaDAO(context);

        if(catDAO.existe(categoria.getDescricao(), categoria.getIdGrupo())) {
            throw new CategoriaRepetidaException("Já existe a categoria " + categoria.getDescricao()
                    + " no grupo.");
        }

        long id = catDAO.inserir(categoria);
        catDAO.close();

        return id;
    }

    /**
     * Retorna o tatal de contas cadastradas na categoria, incluindo as inativas
     * @param categoria
     * @param mes
     * @param ano
     * @return
     */
    public int getTotalDeContas(Categoria categoria, int mes, int ano) {
        ControladorConta controladorConta = new ControladorConta(context);
        return controladorConta.getContas(categoria, mes, ano).size();
    }

    public Categoria getCategoria(String descricao, Grupo grupo) {
        CategoriaDAO dao = new CategoriaDAO(context);
        Categoria categoria = dao.getCategoria(descricao, grupo.getId());
        dao.close();
        return categoria;
    }

    public Categoria getCategoria(Long id) {
        CategoriaDAO dao = new CategoriaDAO(context);
        Categoria categoria = dao.getCategoria(id);
        dao.close();
        return categoria;
    }

    public EstatisticaTO calculaMenorValorGasto(Categoria categoria, LocalDate data) {
        EstatisticaTO resultado = new EstatisticaTO();

        BigDecimal menorValor = BigDecimal.valueOf(Double.MAX_VALUE);
        LocalDate menorData = data;
        for(int i = 1; i <= MESES_A_CONTAR_ESTATISTICAS; i++){
            data = data.minusMonths(1);
            BigDecimal totalMes = getTotalGastosCategoria(categoria,
                    data.getMonthOfYear(), data.getYear());

            if(!totalMes.equals(new BigDecimal(0)) && totalMes.compareTo(menorValor) <= 0 ){
                menorValor = totalMes;
                menorData = data;
            }
        }

        resultado.setValor(menorValor);
        resultado.setData(menorData);
        return resultado;
    }

    public EstatisticaTO calculaMaiorValorGasto(Categoria categoria, LocalDate data) {
        EstatisticaTO resultado = new EstatisticaTO();

        BigDecimal maiorValor = BigDecimal.valueOf(Double.MIN_VALUE);
        LocalDate maiorData = data;
        for(int i = 1; i <= MESES_A_CONTAR_ESTATISTICAS; i++){
            data = data.minusMonths(1);
            BigDecimal totalMes = getTotalGastosCategoria(categoria,
                    data.getMonthOfYear(), data.getYear());

            if(totalMes.compareTo(maiorValor) >= 0){
                maiorValor = totalMes;
                maiorData = data;
            }
        }

        resultado.setValor(maiorValor);
        resultado.setData(maiorData);
        return resultado;
    }

    public BigDecimal calculaMedia(Categoria categoria, LocalDate data) {
        BigDecimal soma = new BigDecimal(0);
        int mesesComConta = 0;

        for(int i = 1; i <= MESES_A_CONTAR_ESTATISTICAS; i++){
            data = data.minusMonths(1);
            BigDecimal totalGasto = getTotalGastosCategoria(categoria,
                    data.getMonthOfYear(), data.getYear());

            if(!totalGasto.equals(new BigDecimal(0))){
                soma = soma.add(totalGasto);
                mesesComConta++;
            }
        }

        if(mesesComConta > 0)
            return soma.divide(new BigDecimal(mesesComConta), BigDecimal.ROUND_HALF_DOWN);

        return new BigDecimal(0);
    }

    public void excluirCategoria(Categoria deleteda) {
        Categoria catDiversosOutros = getCategoriaDiversasOutras();
        trasnfereContas(deleteda, catDiversosOutros);
        this.delete(deleteda);
    }

    private void delete(Categoria deleteda) {
        CategoriaDAO dao = new CategoriaDAO(context);
        dao.delete(deleteda);
    }

    private void trasnfereContas(Categoria antiga, Categoria nova) {
        ControladorConta controladorConta = new ControladorConta(context);
        List<Conta> contas = controladorConta.getContas(antiga);
        for(Conta conta : contas){
            conta.setCategoria(nova.getId());
            controladorConta.atualizaConta(conta);
        }
    }

    private Categoria getCategoriaDiversasOutras() {
        Grupo grupoDiversas = new ControladorGrupo(context).getGrupo(GrupoDAO.GRUPO_DIVERSAS);
        Categoria outras = getCategoria("Outras", grupoDiversas);

        if(outras != null){
            return outras;
        }

        outras = new Categoria();
        outras.setDescricao("Outras");
        outras.setIdGrupo(grupoDiversas.getId());
        criarCategoria(outras);
        return getCategoria("Outras", grupoDiversas);
    }
}
