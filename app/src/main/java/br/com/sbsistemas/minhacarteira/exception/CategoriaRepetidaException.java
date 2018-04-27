package br.com.sbsistemas.minhacarteira.exception;

/**
 * Created by sebas on 07/08/2017.
 */

public class CategoriaRepetidaException extends RuntimeException{

    public CategoriaRepetidaException(String msg){
        super(msg);
    }

    public CategoriaRepetidaException(Throwable e){
        super(e);
    }

}
