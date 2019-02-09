package br.com.sbsistemas.minhacarteira.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.sbsistemas.minhacarteira.adapter.to.QuantidadeValorTO;
import br.com.sbsistemas.minhacarteira.modelo.Grupo;
import br.com.sbsistemas.minhacarteira.utils.LocalDateUtils;

/**
 * Created by sebas on 05/08/2017.
 */

public class GrupoDAO{

    private MinhaCarteiraDBHelper minhaCarteiraDBHelper;
    //Grupos
    public static final String GRUPO_TRANSPORTE = "Transporte";
    public static final String GRUPO_EDU_TRAB = "Educação/Trabalho";
    public static final String GRUPO_DIVERSAS = "Diversas";
    public static final String GRUPO_MORADIA = "Moradia";
    public static final String GRUPO_LAZER =  "Lazer";
    public static final String GRUPO_TODAS = "Todas";
    public static final String GRUPO_SAUDE = "Saúde";

    //tabela
    public static final String NOME_TABELA = "Grupo";
    private static final String IDX_DESCRICAO = "idx_grupo_descricao";
    public static final String COLUNA_ID = "id";
    public static final String COLUNA_DESCRICAO = "descricao";

    //SQLs
    private static final String CREATE_TABLE =
            "CREATE TABLE " + NOME_TABELA +
            " (" + COLUNA_ID + " INTEGER primary key" +
            ", " + COLUNA_DESCRICAO + " TEXT unique" +
            ");";

    private static final String UPGRADE_TABLE_V5 =
            "CREATE INDEX " + IDX_DESCRICAO + " ON " + NOME_TABELA + " (" + COLUNA_DESCRICAO + ");";

    private static final String SQL_RECUPERA_TODOS =
            "SELECT * FROM " + NOME_TABELA;

    public GrupoDAO(Context context){
        minhaCarteiraDBHelper = MinhaCarteiraDBHelper.getInstance(context);
    }

    public void close(){
        minhaCarteiraDBHelper.close();
    }

    public static void onCreate(SQLiteDatabase db) {
        //cria a tabela
        db.execSQL(CREATE_TABLE);

        //insere os grupos padrão
        String[] gruposPadrao = {GRUPO_TODAS, GRUPO_LAZER, GRUPO_MORADIA, GRUPO_SAUDE, GRUPO_TRANSPORTE, GRUPO_DIVERSAS, GRUPO_EDU_TRAB};
        for (String grupoDesc : gruposPadrao) {
            Grupo novoGrupo = new Grupo();
            novoGrupo.setDescricao(grupoDesc);
            db.insert(NOME_TABELA, null, grupoToContentValue(novoGrupo));
        }
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 5){
            db.execSQL(UPGRADE_TABLE_V5);
        }

    }

    public List<Grupo> recuperaTodos() {
        List<Grupo> todos = new ArrayList<Grupo>();
        SQLiteDatabase db = minhaCarteiraDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_RECUPERA_TODOS, null);

        while(cursor.moveToNext()){
            Grupo grupo = obtemGrupoDoCursor(cursor);
            todos.add(grupo);
        }

        return todos;
    }

    public Grupo getGrupo(String descricao) {
        String SQL =
                "SELECT * FROM " + NOME_TABELA +
                " WHERE " + COLUNA_DESCRICAO + " = ?";
        String[] args = {descricao};

        SQLiteDatabase db = minhaCarteiraDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args, null);

        if(cursor.moveToNext()){
            return  obtemGrupoDoCursor(cursor);
        } else{
            return null;
        }
    }

    private Grupo obtemGrupoDoCursor(Cursor cursor) {
        Grupo grupo = new Grupo();
        grupo.setId(cursor.getLong(cursor.getColumnIndex(COLUNA_ID)));
        grupo.setDescricao(cursor.getString(cursor.getColumnIndex(COLUNA_DESCRICAO)));
        return grupo;
    }

    private static ContentValues grupoToContentValue(Grupo novoGrupo) {
        ContentValues dados = new ContentValues();
        dados.put(COLUNA_DESCRICAO, novoGrupo.getDescricao());
        return dados;
    }

    public Grupo getGrupo(Long idGrupo) {
        String SQL =
                "SELECT * FROM " + NOME_TABELA +
                " WHERE " + COLUNA_ID + " = ?";
        String args[] = {idGrupo.toString()};

        SQLiteDatabase db = minhaCarteiraDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);
        if(cursor.moveToNext()) return obtemGrupoDoCursor(cursor);

        return null;
    }

    public QuantidadeValorTO getQuantitadeValor(Grupo grupo, int mes, int ano){
        String SQL =
                "select count( * ) as QUANTIDADE, sum(co.valor) as VALOR" +
                " from " + NOME_TABELA + " g " +
                " inner join " + CategoriaDAO.NOME_TABELA + " ca " +
                        "on ca." + CategoriaDAO.COLUNA_GRUPO_ID + " = g." + COLUNA_ID +
                " inner join " + ContaDAO.NOME_TABELA + " co " +
                        "on co." + ContaDAO.CATEGORIA_ID + " = ca." + CategoriaDAO.ID +
                " inner join " + PrestacoesDAO.NOME_TABELA + " p " +
                        "on p."+PrestacoesDAO.CONTA_ID +" = co."+ ContaDAO.ID +
                " and p." + PrestacoesDAO.DATA + " >= ?" +
                " and p." + PrestacoesDAO.DATA + " <= ?" +
                " and p." + PrestacoesDAO.ATIVO + " = 1";
        String[] args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano)};

        if(!grupo.getDescricao().equals(GRUPO_TODAS)){
            SQL = SQL.concat(" and g." + COLUNA_ID + " = ?");
            args = new String[]{LocalDateUtils.getInicioMes(mes, ano), LocalDateUtils.getFinalMes(mes, ano),
                    grupo.getId().toString()};
        }

        SQLiteDatabase db = minhaCarteiraDBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL, args);

        return cursorToQuantidadeValor(cursor);
    }

    private QuantidadeValorTO cursorToQuantidadeValor(Cursor cursor) {
        QuantidadeValorTO quantidadeValorTO = new QuantidadeValorTO();

        cursor.moveToNext();
        quantidadeValorTO.setQuantidade(cursor.getInt(cursor.getColumnIndex("QUANTIDADE")));
        quantidadeValorTO.setValor(cursor.getFloat(cursor.getColumnIndex("VALOR")));

        return quantidadeValorTO;
    }


}
