package br.com.sbsistemas.minhacarteira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.modelo.Conta;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 08/08/2017.
 */

public class PrestacoesDAO {

    //Tabela
    public static final String NOME_TABELA = "Prestacao";
    public static final String NOME_IDX_DATA = "idx_data";
    public static final String COLUNA_ID = "id";
    public static final String CONTA_ID = "id_conta";
    public static final String COLUNA_PAGO = "pago";
    public static final String ATIVO = "ativo";
    public static final String DATA = "data";
    public static final String COLUNA_PRESTACAO_NUMERO = "prestacao_numero";

    //SQLs
    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " (" + COLUNA_ID + " INTEGER PRIMARY KEY " +
            ", " + CONTA_ID + " INTEGER NOT NULL " +
            ", " + COLUNA_PAGO + " BOOLEAN " +
            ", " + ATIVO + " BOOLEAN " +
            ", " + DATA + " DATE NOT NULL " +
            ", " + COLUNA_PRESTACAO_NUMERO + " INTEGER " +
            ", FOREIGN KEY (" + CONTA_ID + ") REFERENCES " +
                    ContaDAO.NOME_TABELA + "(" + ContaDAO.ID + ") ON DELETE CASCADE" +
            ");";

    private static final String UPDATE_TABLE_V5 =
            "CREATE INDEX " + NOME_IDX_DATA +
            " ON " + NOME_TABELA + " (" + DATA + "," + CONTA_ID + ");";

    private static final String UPDATE_TABLE_V6_1 =
            "DROP INDEX IF EXISTS " + NOME_IDX_DATA;

    private static final String UPDATE_TABLE_V6_2 =
            "CREATE INDEX " + NOME_IDX_DATA +
            " ON " + NOME_TABELA + " (" + DATA + ");";
    private MinhaCarteiraDBHelper dbHelper;

    public PrestacoesDAO(Context context){
        dbHelper = MinhaCarteiraDBHelper.getInstance(context);
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(UPDATE_TABLE_V5);
        db.execSQL(UPDATE_TABLE_V6_1);
        db.execSQL(UPDATE_TABLE_V6_2);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 5){
            db.execSQL(UPDATE_TABLE_V5);
        }
        if(oldVersion < 6){
            db.execSQL(UPDATE_TABLE_V6_1);
            db.execSQL(UPDATE_TABLE_V6_2);
        }
    }

    private ContentValues prestacaoToContentValues(Prestacao prestacao) {
        ContentValues values = new ContentValues();
        values.put(ATIVO, prestacao.isAtivo());
        values.put(CONTA_ID, prestacao.getContaId());
        values.put(DATA, prestacao.getData().toString());
        values.put(COLUNA_PAGO, prestacao.isPago());
        values.put(COLUNA_PRESTACAO_NUMERO, prestacao.getNumParcela());

        return values;
    }

    public void close(){
        dbHelper.close();
    }

    public void inserir(Prestacao prestacao){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(NOME_TABELA, null, prestacaoToContentValues(prestacao));
    }

    public Prestacao getPrestacao(Conta conta, int mes, int ano){
        String SQL =
                "SELECT * FROM " + NOME_TABELA +
                " WHERE " + CONTA_ID +  " = ? " +
                    " AND " + DATA + " >= ? " +
                    " AND " + DATA + " <= ? ";
        String[] args = {conta.getId().toString(), LocalDateUtils.getInicioMes(mes, ano),
                LocalDateUtils.getFinalMes(mes, ano)};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        Prestacao prestacao  = null;
        if(cursor.moveToNext()){
            prestacao = cursorToPrestacao(cursor);
        }
        cursor.close();
        return prestacao;
    }

    public List<Prestacao> getPrestacoes(Conta conta){
        String SQL =
                "SELECT * FROM " + NOME_TABELA +
                " WHERE " + CONTA_ID + "  = ?";
        String args[] = {conta.getId().toString()};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        List<Prestacao> prestacoes = cursorToPrestacoes(cursor);
        cursor.close();
        return prestacoes;
    }

    private List<Prestacao> cursorToPrestacoes(Cursor cursor) {
        List<Prestacao> prestacoes = new ArrayList<Prestacao>();

        while (cursor.moveToNext()){
            prestacoes.add(cursorToPrestacao(cursor));
        }
        return prestacoes;
    }

    public void limpaBanco(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NOME_TABELA, null, null);
    }

    private Prestacao cursorToPrestacao(Cursor cursor) {
        int ativo = cursor.getInt(cursor.getColumnIndex(ATIVO));
        int pago = cursor.getInt(cursor.getColumnIndex(COLUNA_PAGO));
        String data = cursor.getString(cursor.getColumnIndex(DATA));

        Prestacao prestacao = new Prestacao();
        prestacao.setPago(pago == 0 ? false : true);
        prestacao.setNumParcela(cursor.getInt(cursor.getColumnIndex(COLUNA_PRESTACAO_NUMERO)));
        prestacao.setData(LocalDate.parse(data));
        prestacao.setContaID(cursor.getLong(cursor.getColumnIndex(CONTA_ID)));
        prestacao.setAtivo(ativo == 0 ? false : true);
        prestacao.setId(cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));

        return prestacao;
    }

    public void remover(Conta conta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] args = {conta.getId().toString()};
        db.delete(NOME_TABELA, CONTA_ID + " = ?", args);
    }

    public void atualiza(Prestacao prestacao) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = COLUNA_ID + " = ?";
        String[] args = {prestacao.getId().toString()};
        db.update(NOME_TABELA, prestacaoToContentValues(prestacao), whereClause, args);
    }

}
