package br.com.sbsistemas.minhacarteira.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sebas on 07/08/2017.
 */

public class TagDAO {

    private MinhaCarteiraDBHelper dbHelper;

    //Tabela
    public static final String NOME_TABELA = "Tag";
    public static final String COLUNA_ID = "id";
    public static final String COLUNA_DESCRICAO = "descricao";

    //SQLs
    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " (" + COLUNA_ID + " INTEGER PRIMARY KEY "+
            " ," + COLUNA_DESCRICAO + " TEXT);";

    public TagDAO(Context context){
        dbHelper = MinhaCarteiraDBHelper.getInstance(context);
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void close(){
        dbHelper.close();
    }
}
