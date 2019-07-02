package br.com.sbsistemas.minhacarteira.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import br.com.sbsistemas.minhacarteira.modelo.Categoria;

public class ExclusaoCategoriaDialogFragment extends DialogFragment {

    private ExclusaoCategoriaDialogListener mlistener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Categoria categoriaClicada = (Categoria) getArguments().get("categoria");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Excluir Categoria " + categoriaClicada.getDescricao() + "? " +
                "Todas as contas serão enviadas para a categoria Diversas->Outros")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mlistener.onPositiveClick(ExclusaoCategoriaDialogFragment.this,
                                categoriaClicada);
                    }
                })
                .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mlistener.onNegativeClick(ExclusaoCategoriaDialogFragment.this);
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mlistener = (ExclusaoCategoriaDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + " deve implementar" +
                    " ExclusaoCategoriaDialogListener ");
        }
    }

    public interface ExclusaoCategoriaDialogListener {

        public void onPositiveClick(DialogFragment dialog, Categoria categoria);
        public void onNegativeClick(DialogFragment dialog);

    }

}
