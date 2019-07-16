package br.com.sbsistemas.minhacarteira.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sebas on 05/08/2017.
 */

public class MinhaCarteiraDBHelper extends SQLiteOpenHelper {

    private static final Integer VERSION = 6;
    private static final String DB_NAME = "MINHA_CARTEIRA";

    private static MinhaCarteiraDBHelper instance;

    private MinhaCarteiraDBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    public static synchronized MinhaCarteiraDBHelper getInstance(Context context){
        if(instance == null){
            instance = new MinhaCarteiraDBHelper(context);
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        GrupoDAO.onCreate(db);
        CategoriaDAO.onCreate(db);
        TagDAO.onCreate(db);
        ContaDAO.onCreate(db);
        PrestacoesDAO.onCreate(db);
        ReceitaDAO.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        GrupoDAO.onUpgrade(db, oldVersion, newVersion);
        CategoriaDAO.onUpgrade(db, oldVersion, newVersion);
        TagDAO.onUpgrade(db, oldVersion, newVersion);
        ContaDAO.onUpgrade(db, oldVersion, newVersion);
        PrestacoesDAO.onUpgrade(db, oldVersion, newVersion);
        ReceitaDAO.onUpgrade(db, oldVersion, newVersion);
    }

}
