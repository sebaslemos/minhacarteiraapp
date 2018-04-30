package br.com.sbsistemas.minhacarteira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.exception.CategoriaRepetidaException;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;

/**
 * Created by sebas on 06/08/2017.
 */

public class CategoriaDAO {

    //Tabela
    public static final String NOME_TABELA = "Categoria";
    private static final String NOME_INDEX = "id_grupo_idx";
    public static final String COLUNA_ID = "id";
    public static final String COLUNA_DESCRICAO = "descricao";
    public static final String COLUNA_GRUPO_ID = "id_grupo";

    public static final String CONSTRAINT_DESCRICAO_UNICA = "descricao_unica";
    //SQLs
    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " (" + COLUNA_ID + " INTEGER PRIMARY KEY " +
            ", " + COLUNA_GRUPO_ID + " INTEGER " +
            ", " + COLUNA_DESCRICAO + " TEXT " +
            ", FOREIGN KEY (" + COLUNA_GRUPO_ID + ") REFERENCES " +
                    GrupoDAO.NOME_TABELA + " (" + GrupoDAO.COLUNA_ID + ")" +
            ", CONSTRAINT " + CONSTRAINT_DESCRICAO_UNICA +
                    " UNIQUE (" + COLUNA_DESCRICAO + "," + COLUNA_GRUPO_ID + ")" +
            ");";

    private static final String UPDATE_TABLE_DESENV =
            "DROP TABLE IF EXISTS " + NOME_TABELA;
    private static final String UPDATE_TABLE_V6 =
            "CREATE INDEX " + NOME_INDEX + " ON " + NOME_TABELA + "( " + COLUNA_GRUPO_ID + " );";

    private MinhaCarteiraDBHelper dbHelper;


    public CategoriaDAO(Context context){
        dbHelper = MinhaCarteiraDBHelper.getInstance(context);
    }

    /**
     * Adiciona uma categoria
     * @param categoria
     * @throws CategoriaRepetidaException Se já existe uma categoria com a mesma descrição no grupo
     */
    public long inserir(Categoria categoria) throws CategoriaRepetidaException{
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.insert(NOME_TABELA, null, catogoriaToContentValues(categoria));
    }

    public void limpaBanco() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NOME_TABELA, null, null);
    }

    public List<Categoria> recuperaTodas() {
        String SQL_BUSCA_TODAS =
            "SELECT * FROM " + NOME_TABELA;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_BUSCA_TODAS, null);

        return cursorToList(cursor);
    }

    public boolean existe(String descricao, Long idGrupo) {
        return getCategoria(descricao, idGrupo) != null;
    }

    public Categoria getCategoria(String descricao, Long idGrupo){
        String SQLBuscaPorDescricaoEGrupo =
                "SELECT * FROM " + NOME_TABELA +
                " WHERE " + COLUNA_DESCRICAO + " = ? " +
                " AND " + COLUNA_GRUPO_ID + " = ?";
        String[] args = {descricao, idGrupo.toString()};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQLBuscaPorDescricaoEGrupo, args);
        if(cursor.moveToNext()) return cursorToCategoria(cursor);
        return null;
    }

    public List<Categoria> getCategorias(Grupo grupo) {
        if(grupo.getDescricao().equals(GrupoDAO.GRUPO_TODAS))
            return recuperaTodas();

        String SQL_BUSCA_POR_GRUPO =
                        "SELECT * FROM " + NOME_TABELA +
                        " WHERE " + COLUNA_GRUPO_ID + " = ?";
        String[] args = {grupo.getId().toString()};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_BUSCA_POR_GRUPO, args);

        return cursorToList(cursor);
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 6){
            db.execSQL(UPDATE_TABLE_V6);
        }
    }

    public void close() {
        dbHelper.close();
    }

    private List<Categoria> cursorToList(Cursor cursor){
        List<Categoria> categorias = new ArrayList<Categoria>();
        while(cursor.moveToNext()){
            Categoria categoria = cursorToCategoria(cursor);
            categorias.add(categoria);
        }
        return categorias;
    }

    private Categoria cursorToCategoria(Cursor cursor) {
        Categoria categoria = new Categoria();
        categoria.setId(cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
        categoria.setDescricao(cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO)));
        categoria.setIdGrupo(cursor.getLong(cursor.getColumnIndex(COLUNA_GRUPO_ID)));
        return categoria;
    }

    private ContentValues catogoriaToContentValues(Categoria categoria) {
        ContentValues values = new ContentValues();
        values.put(COLUNA_GRUPO_ID, categoria.getIdGrupo());
        values.put(COLUNA_DESCRICAO, categoria.getDescricao());
        return values;
    }

    public Categoria getCategoria(Long id) {
        String SQL =
                "SELECT * FROM " + NOME_TABELA +
                " WHERE " + COLUNA_ID + " = ?";
        String[] args = {id.toString()};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        if(cursor.moveToNext()) return cursorToCategoria(cursor);
        return null;
    }
}
