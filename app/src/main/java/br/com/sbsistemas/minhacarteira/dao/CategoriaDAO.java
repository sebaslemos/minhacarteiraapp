package br.com.sbsistemas.minhacarteira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.exception.CategoriaRepetidaException;
import br.com.sbsistemas.minhacarteira.modelo.Categoria;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 06/08/2017.
 */

public class CategoriaDAO {

    //Tabela
    public static final String NOME_TABELA = "Categoria";
    private static final String NOME_INDEX = "id_grupo_idx";
    public static final String ID = "id";
    public static final String COLUNA_DESCRICAO = "descricao";
    public static final String COLUNA_GRUPO_ID = "id_grupo";

    public static final String CONSTRAINT_DESCRICAO_UNICA = "descricao_unica";
    //SQLs
    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " (" + ID + " INTEGER PRIMARY KEY " +
            ", " + COLUNA_GRUPO_ID + " INTEGER " +
            ", " + COLUNA_DESCRICAO + " TEXT " +
            ", FOREIGN KEY (" + COLUNA_GRUPO_ID + ") REFERENCES " +
                    GrupoDAO.NOME_TABELA + " (" + GrupoDAO.COLUNA_ID + ")" +
            ", CONSTRAINT " + CONSTRAINT_DESCRICAO_UNICA +
                    " UNIQUE (" + COLUNA_DESCRICAO + "," + COLUNA_GRUPO_ID + ")" +
            ");";

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
        List<Categoria> categorias = cursorToList(cursor);
        cursor.close();

        return categorias;
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

        Categoria categoria = null;
        if(cursor.moveToNext()) {
            categoria = cursorToCategoria(cursor);
        }
        cursor.close();
        return categoria;
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

        List<Categoria> categorias = cursorToList(cursor);
        cursor.close();
        return categorias;
    }

    public static void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(UPDATE_TABLE_V6);
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
        categoria.setId(cursor.getLong(cursor.getColumnIndex(ID)));
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
                " WHERE " + ID + " = ?";
        String[] args = {id.toString()};

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        Categoria categoria = null;
        if(cursor.moveToNext()) {
             categoria = cursorToCategoria(cursor);
        }
        cursor.close();
        return categoria;
    }

    public BigDecimal getTotalGastos(@Nullable Categoria categoria, int mes, int ano){
        String SQL;
        String[] args;

        if(categoria == null){
            //lista todas
            SQL = "SELECT sum(conta." + ContaDAO.VALOR + ") as total " +
                    " FROM " + ContaDAO.NOME_TABELA + " as conta INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.CONTA_ID + " = conta." + ContaDAO.ID + ")" +
                    " WHERE prest." + PrestacoesDAO.DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.DATA + " <= " + "?" +
                    " AND prest." + PrestacoesDAO.ATIVO + " = 1";
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano)};
        } else if(categoria.getId() == null){ //categoria todas
            SQL = "SELECT sum(conta." + ContaDAO.VALOR + ") as total " +
                    " FROM " + ContaDAO.NOME_TABELA + " as conta " +
                    " INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.CONTA_ID + " = conta." + ContaDAO.ID + ")" +
                    " INNER JOIN " + CategoriaDAO.NOME_TABELA + " as cat " +
                    " on cat." + CategoriaDAO.ID + " = conta." + ContaDAO.CATEGORIA_ID +
                    " INNER JOIN " + GrupoDAO.NOME_TABELA + " as grupo " +
                    " on grupo." + GrupoDAO.COLUNA_ID + " = cat." + CategoriaDAO.COLUNA_GRUPO_ID +
                    " WHERE prest." + PrestacoesDAO.DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.DATA + " <= " + "?" +
                    " AND prest." + PrestacoesDAO.ATIVO + " = 1" +
                    " AND grupo." + GrupoDAO.COLUNA_ID + " = ?";
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano),
            categoria.getIdGrupo().toString()};
        } else{
            SQL = "SELECT sum(conta." + ContaDAO.VALOR + ") as total " +
                    " FROM " + ContaDAO.NOME_TABELA + " as conta INNER JOIN " + PrestacoesDAO.NOME_TABELA + " as prest" +
                    " on (prest." + PrestacoesDAO.CONTA_ID + " = conta." + ContaDAO.ID + ")" +
                    " WHERE prest." + PrestacoesDAO.DATA + " >= " + "?" +
                    " AND prest." + PrestacoesDAO.DATA + " <= " + "?" +
                    " AND conta." + ContaDAO.CATEGORIA_ID + " = " + "?" +
                    " AND prest." + PrestacoesDAO.ATIVO + " = 1"
                    ;
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano),
                    categoria.getId().toString()};
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        BigDecimal totalGasto = new BigDecimal(0);
        if(cursor.moveToNext())
            totalGasto = new BigDecimal(cursor.getFloat(cursor.getColumnIndex("total")));

        cursor.close();
        return totalGasto;
    }

    public void delete(Categoria categoria) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(NOME_TABELA, ID + " = ?", new String[]{categoria.getId().toString()});
    }
}
