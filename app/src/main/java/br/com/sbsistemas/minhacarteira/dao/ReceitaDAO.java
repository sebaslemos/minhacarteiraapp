package br.com.sbsistemas.minhacarteira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.modelo.Receita;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 03/09/2017.
 */

public class ReceitaDAO {

    private static final String NOME_TABELA = "Receita";
    private static final String COLUNA_ID = "id";
    private  static final String COLUNA_DESCRICAO = "descricao";
    private static final String COLUNA_VALOR = "valor";
    private static final String COLUNA_DATA = "data";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " ( " + COLUNA_ID + " INTEGER primary key " +
            " , " + COLUNA_DESCRICAO + " TEXT " +
            " , " + COLUNA_VALOR + " REAL " +
            " , " + COLUNA_DATA +  " DATE " +
            ");";

    private MinhaCarteiraDBHelper dbHelper;

    public ReceitaDAO(Context context){
        dbHelper = MinhaCarteiraDBHelper.getInstance(context);
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 4){
            db.execSQL(CREATE_TABLE);
        }
    }

    public Long adiciona(Receita receita){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.insert(NOME_TABELA, null, receitaToContentValues(receita));
    }

    public List<Receita> getReceitas(int mes, int ano){
        String SQL =
                "SELECT * FROM " + NOME_TABELA +
                " WHERE " + COLUNA_DATA + " >= ? AND " +
                COLUNA_DATA + " <= ?";
        String[] args = {LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano)};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        return cursorToReceitas(cursor);

    }

    private List<Receita> cursorToReceitas(Cursor cursor) {
        List<Receita> receitas = new ArrayList<Receita>();
        while (cursor.moveToNext()){
            Receita receita = cursorToReceita(cursor);
            receitas.add(receita);
        }

        return receitas;
    }

    private Receita cursorToReceita(Cursor cursor) {
        Receita receita = new Receita();
        receita.setData(LocalDate.parse(cursor.getString(cursor.getColumnIndex(COLUNA_DATA))));
        receita.setDescricao(cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO)));
        receita.setId(cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
        double valor = cursor.getDouble(cursor.getColumnIndex(COLUNA_VALOR));
        receita.setValor(BigDecimal.valueOf(valor));

        return receita;
    }

    private ContentValues receitaToContentValues(Receita receita) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_DESCRICAO, receita.getDescricao());
        values.put(COLUNA_VALOR, receita.getValor().doubleValue());
        values.put(COLUNA_DATA, receita.getData().toString());

        return values;
    }

    public void close() {
        dbHelper.close();
    }

    public void deletar(Receita receitaSelecionada) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String clausulaWhere = COLUNA_ID + " = ?";
        String[] args = {receitaSelecionada.getId().toString()};
        db.delete(NOME_TABELA, clausulaWhere, args);
    }
}
