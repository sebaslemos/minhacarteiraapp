package br.com.sbsistemas.minhacarteira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.to.ListaContaAdapterTO;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 07/08/2017.
 */

public class ContaDAO {

    private MinhaCarteiraDBHelper dbHelper;

    //Tabela
    public static final String NOME_TABELA = "Conta";
    public static final String NOME_IDX_CATEGORIA = "idx_conta_categoria";
    public static final String ID = "id";
    public static final String CATEGORIA_ID = "id_categoria";
    public static final String COLUNA_TAG_ID = "id_tag";
    public static final String COLUNA_PRESTACOES = "prestacoes";
    public static final String COLUNA_DESCRICAO = "descricao";
    public static final String VALOR = "valor";

    //SQLs
    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " (" + ID + " INTEGER PRIMARY KEY " +
            ", " + CATEGORIA_ID + " INTEGER NOT NULL " +
            ", " + COLUNA_TAG_ID + " INTEGER " +
            ", " + COLUNA_PRESTACOES + " INTEGER NOT NULL " +
            ", " + COLUNA_DESCRICAO + " TEXT " +
            ", " + VALOR + " REAL NOT NULL " +
            ", FOREIGN KEY (" + CATEGORIA_ID + ") REFERENCES " +
                    CategoriaDAO.NOME_TABELA + " (" + CategoriaDAO.ID + ") " +
            ", FOREIGN KEY (" + COLUNA_TAG_ID + ") REFERENCES " +
                    TagDAO.NOME_TABELA + " (" + TagDAO.COLUNA_ID + ")" +
            ");";

    private static final String UPGRADE_TABLE_V5 =
        "CREATE INDEX " + NOME_IDX_CATEGORIA + " ON " + NOME_TABELA + " (" + CATEGORIA_ID + ");";


    public ContaDAO(Context context){
        dbHelper = MinhaCarteiraDBHelper.getInstance(context);
    }


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(UPGRADE_TABLE_V5);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 5){
            db.execSQL(UPGRADE_TABLE_V5);
        }
    }

    private ContentValues contaToContentValues(Conta conta) {
        ContentValues values = new ContentValues();
        values.put(CATEGORIA_ID, conta.getCategoria());
        values.put(COLUNA_DESCRICAO, conta.getDescricao());
        values.put(COLUNA_PRESTACOES, conta.getNumeroDePrestacoes());
        values.put(COLUNA_TAG_ID, conta.getTag());
        values.put(VALOR, conta.getValor().doubleValue());

        return values;
    }

    public void close(){
        dbHelper.close();
    }

    public Long inserir(Conta conta){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.insert(NOME_TABELA, null, contaToContentValues(conta));
    }

    /**
     *
     * @param categoria
     * @param mes 1 a 12
     * @param ano
     * @return
     */
    public List<ListaContaAdapterTO> getContas(@Nullable Categoria categoria, int mes, int ano){
        //TODO validações de datas
        String SQL;
        String[] args;

        if(categoria == null){
            //lista todas as contas
            SQL = "SELECT conta.*, prest.id as prest_id, prest.ativo, prest.data, prest.prestacao_numero, prest.pago, grupo.descricao as grupo_desc " +
                    " FROM " + NOME_TABELA + " as conta INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.CONTA_ID + " = conta." + ContaDAO.ID + ")" +
                    " INNER JOIN Categoria cat on cat.id = conta.id_categoria " +
                    " INNER JOIN Grupo grupo on grupo.id = cat.id_grupo " +
                    " WHERE prest." + PrestacoesDAO.DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.DATA + " <= " + "?";
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano)};
        } else if(categoria.getId() == null){ //categoria todas
            //lista contas de um grupo
            SQL = "SELECT conta.*, prest.id as prest_id, prest.ativo, prest.data, prest.prestacao_numero, prest.pago, grupo.descricao as grupo_desc " +
                    " FROM " + NOME_TABELA + " as conta INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.CONTA_ID + " = conta." + ContaDAO.ID + ")" +
                    " INNER JOIN Categoria cat on cat.id = conta.id_categoria " +
                    " INNER JOIN Grupo grupo on grupo.id = cat.id_grupo " +
                    " WHERE prest." + PrestacoesDAO.DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.DATA + " <= " + "?" +
                    " AND grupo." + GrupoDAO.COLUNA_ID + " = ?";
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano),
                    categoria.getIdGrupo().toString()};
        }
        else{
            //lista contas de uma categoria
            SQL = "SELECT conta.*, prest.id as prest_id, prest.ativo, prest.data, prest.prestacao_numero, prest.pago, grupo.descricao as grupo_desc " +
                    " FROM " + NOME_TABELA + " as conta INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.CONTA_ID + " = conta." + ContaDAO.ID + ")" +
                    " INNER JOIN Categoria cat on cat.id = conta.id_categoria " +
                    " INNER JOIN Grupo grupo on grupo.id = cat.id_grupo " +
                    " WHERE prest." + PrestacoesDAO.DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.DATA + " <= " + "?" +
                    " AND conta." + CATEGORIA_ID + " = " + "?";
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano),
                    categoria.getId().toString()};
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        List<ListaContaAdapterTO> listaContaAdapterTOs = cursorToList(cursor);
        cursor.close();
        return listaContaAdapterTOs;
    }

    public void limpaBanco(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NOME_TABELA, null, null);
    }

    private List<ListaContaAdapterTO> cursorToList(Cursor cursor) {
        List<ListaContaAdapterTO> contas = new ArrayList<ListaContaAdapterTO>();

        while(cursor.moveToNext()){
            contas.add(cursorToContaTO(cursor));
        }
        cursor.close();
        return contas;
    }

    private ListaContaAdapterTO cursorToContaTO(Cursor cursor) {
        Conta conta = new Conta();
        conta.setDescricao(cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO)));
        conta.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        conta.setCategoria(cursor.getLong(cursor.getColumnIndex(CATEGORIA_ID)));
        conta.setNumeroDePrestacoes(cursor.getInt(cursor.getColumnIndex(COLUNA_PRESTACOES)));
        conta.setTag(cursor.getLong(cursor.getColumnIndex(COLUNA_TAG_ID)));
        conta.setValor(new BigDecimal(cursor.getDouble(cursor.getColumnIndex(VALOR))));

        int ativo = cursor.getInt(cursor.getColumnIndex(PrestacoesDAO.ATIVO));
        int pago = cursor.getInt(cursor.getColumnIndex(PrestacoesDAO.COLUNA_PAGO));
        String data = cursor.getString(cursor.getColumnIndex(PrestacoesDAO.DATA));

        Prestacao prestacao = new Prestacao();
        prestacao.setPago(pago == 0 ? false : true);
        prestacao.setNumParcela(cursor.getInt(cursor.getColumnIndex(PrestacoesDAO.COLUNA_PRESTACAO_NUMERO)));
        prestacao.setData(LocalDate.parse(data));
        prestacao.setAtivo(ativo == 0 ? false : true);
        prestacao.setId(cursor.getLong(cursor.getColumnIndex("prest_id")));
        prestacao.setContaID(conta.getId());

        Grupo grupo = new Grupo();
        grupo.setDescricao(cursor.getString(cursor.getColumnIndex("grupo_desc")));

        return new ListaContaAdapterTO(conta, prestacao, grupo);
    }

    public void remover(Conta conta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = {conta.getId().toString()};
        db.delete(NOME_TABELA, ID + " = ?", args);
    }

    public void atualizar(Conta conta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereSQL = ID + " = ?" ;
        String[] args = {conta.getId().toString()};
        db.update(NOME_TABELA, contaToContentValues(conta), whereSQL, args);
    }

    public List<Conta> getContas(Categoria categoria) {
        String SQL =
                "SELECT * FROM " + ContaDAO.NOME_TABELA +
                " WHERE " + ContaDAO.CATEGORIA_ID + " = ?";
        String[] args = new String[] {categoria.getId().toString()};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        List<Conta> contas = new ArrayList<Conta>();
        while(cursor.moveToNext())
            contas.add(cursorToConta(cursor));

        return contas;
    }

    private Conta cursorToConta(Cursor cursor) {
        Conta conta = new Conta();
        conta.setDescricao(cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO)));
        conta.setId(cursor.getLong(cursor.getColumnIndex(ID)));
        conta.setCategoria(cursor.getLong(cursor.getColumnIndex(CATEGORIA_ID)));
        conta.setNumeroDePrestacoes(cursor.getInt(cursor.getColumnIndex(COLUNA_PRESTACOES)));
        conta.setTag(cursor.getLong(cursor.getColumnIndex(COLUNA_TAG_ID)));
        conta.setValor(new BigDecimal(cursor.getDouble(cursor.getColumnIndex(VALOR))));
        return conta;
    }

    public List<Conta> getContasNaoPagas(int dia, int mes, int ano) {
        String SQL =
                "SELECT c.* "
                + " FROM " + PrestacoesDAO.NOME_TABELA + " p "
                + " INNER JOIN " + ContaDAO.NOME_TABELA  + " c "
                +        " on " + "c." + ContaDAO.ID +  " = p." + PrestacoesDAO.CONTA_ID
                + " WHERE p." + PrestacoesDAO.DATA + " = ?"
                + " AND p." + PrestacoesDAO.ATIVO + " = 1"
                + " AND p." + PrestacoesDAO.COLUNA_PAGO + " = 0";
        String[] args = new String[]{LocalDateUtils.getDataStr(dia, mes, ano)};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);
        List<Conta> contas = new ArrayList<Conta>();
        while (cursor.moveToNext()){
            contas.add(cursorToConta(cursor));
        }

        return contas;
    }
}
