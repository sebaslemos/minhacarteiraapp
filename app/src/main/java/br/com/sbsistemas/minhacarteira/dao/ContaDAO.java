package br.com.sbsistemas.minhacarteira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 07/08/2017.
 */

public class ContaDAO {

    private MinhaCarteiraDBHelper dbHelper;

    //Tabela
    public static final String NOME_TABELA = "Conta";
    public static final String NOME_IDX_CATEGORIA = "idx_conta_categoria";
    public static final String COLUNA_ID = "id";
    public static final String COLUNA_CATEGORIA_ID = "id_categoria";
    public static final String COLUNA_TAG_ID = "id_tag";
    public static final String COLUNA_PRESTACOES = "prestacoes";
    public static final String COLUNA_DESCRICAO = "descricao";
    public static final String COLUNA_VALOR = "valor";

    //SQLs
    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " (" + COLUNA_ID + " INTEGER PRIMARY KEY " +
            ", " + COLUNA_CATEGORIA_ID + " INTEGER NOT NULL " +
            ", " + COLUNA_TAG_ID + " INTEGER " +
            ", " + COLUNA_PRESTACOES + " INTEGER NOT NULL " +
            ", " + COLUNA_DESCRICAO + " TEXT " +
            ", " + COLUNA_VALOR + " REAL NOT NULL " +
            ", FOREIGN KEY (" + COLUNA_CATEGORIA_ID + ") REFERENCES " +
                    CategoriaDAO.NOME_TABELA + " (" + CategoriaDAO.COLUNA_ID + ") " +
            ", FOREIGN KEY (" + COLUNA_TAG_ID + ") REFERENCES " +
                    TagDAO.NOME_TABELA + " (" + TagDAO.COLUNA_ID + ")" +
            ");";

    private static final String UPGRADE_TABLE_V5 =
        "CREATE INDEX " + NOME_IDX_CATEGORIA + " ON " + NOME_TABELA + " (" + COLUNA_CATEGORIA_ID + ");";


    public ContaDAO(Context context){
        dbHelper = MinhaCarteiraDBHelper.getInstance(context);
    }


    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 5){
            db.execSQL(UPGRADE_TABLE_V5);
        }
    }

    private ContentValues contaToContentValues(Conta conta) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_CATEGORIA_ID, conta.getCategoria());
        values.put(COLUNA_DESCRICAO, conta.getDescricao());
        values.put(COLUNA_PRESTACOES, conta.getNumeroDePrestacoes());
        values.put(COLUNA_TAG_ID, conta.getTag());
        values.put(COLUNA_VALOR, conta.getValor().doubleValue());

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
    public List<Conta> getContas(@Nullable Categoria categoria, int mes, int ano){
        //TODO validações de datas
        String SQL;
        String[] args;

        if(categoria == null){
            //lista todas
            SQL = "SELECT conta.* " +
                    " FROM " + NOME_TABELA + " as conta INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.COLUNA_CONTA_ID + " = conta." + ContaDAO.COLUNA_ID + ")" +
                    " WHERE prest." + PrestacoesDAO.COLUNA_DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.COLUNA_DATA + " <= " + "?";
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano)};
        } else{
            SQL = "SELECT conta.* " +
                    " FROM " + NOME_TABELA + " as conta INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.COLUNA_CONTA_ID + " = conta." + ContaDAO.COLUNA_ID + ")" +
                    " WHERE prest." + PrestacoesDAO.COLUNA_DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.COLUNA_DATA + " <= " + "?" +
                    " AND conta." + COLUNA_CATEGORIA_ID + " = " + "?";
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano),
                    categoria.getId().toString()};
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        return cursorToList(cursor);
    }

    public List<Conta> getContas(){
        String SQL = "SELECT * FROM " + NOME_TABELA;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, null, null);
        return cursorToList(cursor);
    }

    public void limpaBanco(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NOME_TABELA, null, null);
    }

    private List<Conta> cursorToList(Cursor cursor) {
        List<Conta> contas = new ArrayList<Conta>();

        while(cursor.moveToNext()){
            contas.add(cursorToConta(cursor));
        }
        return contas;
    }

    private Conta cursorToConta(Cursor cursor) {
        Conta conta = new Conta();
        conta.setDescricao(cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO)));
        conta.setId(cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
        conta.setCategoria(cursor.getLong(cursor.getColumnIndex(COLUNA_CATEGORIA_ID)));
        conta.setNumeroDePrestacoes(cursor.getInt(cursor.getColumnIndex(COLUNA_PRESTACOES)));
        conta.setTag(cursor.getLong(cursor.getColumnIndex(COLUNA_TAG_ID)));
        conta.setValor(new BigDecimal(cursor.getDouble(cursor.getColumnIndex(COLUNA_VALOR))));
        return conta;
    }

    public void remover(Conta conta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] args = {conta.getId().toString()};
        db.delete(NOME_TABELA, COLUNA_ID + " = ?", args);
    }

    public void atualizar(Conta conta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereSQL = COLUNA_ID + " = ?" ;
        String[] args = {conta.getId().toString()};
        db.update(NOME_TABELA, contaToContentValues(conta), whereSQL, args);
    }
}
