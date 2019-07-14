package br.com.sbsistemas.minhacarteira.broadcast;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import br.com.sbsistemas.minhacarteira.adapter.to.ContaTO;
import br.com.sbsistemas.minhacarteira.controlador.ControladorPrestacao;
import br.com.sbsistemas.minhacarteira.modelo.Prestacao;

public class ActionReceiver  extends BroadcastReceiver {

    public static final String ACTION_KEY = "action";
    public static final String ACTION_PAGAR = "pagar";
    public static final String ACTION_DESATIVAR = "desativar";
    public static final String DATA_KEY = "conta";

    @Override
    public void onReceive(Context context, Intent intent) {
        ContaTO contaTO = (ContaTO) intent.getSerializableExtra(DATA_KEY);
        ControladorPrestacao ctr = new ControladorPrestacao(context);

        Prestacao prestacao = contaTO.getPrestacao();
        if(intent.getStringExtra(ACTION_KEY).equals(ACTION_PAGAR)){
            Log.d("TAG", "Conta ID: " + contaTO.getConta().getDescricao() + " ContaID em prestação: "
             + prestacao.getContaId() + " pestação ID: " + prestacao.getId());
            prestacao.setPago(true);
        } else if(intent.getStringExtra(ACTION_KEY).equals(ACTION_DESATIVAR)){
            prestacao.setAtivo(false);
        }
        ctr.atualiza(prestacao);

        NotificationManager notificationManager = (NotificationManager)context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(contaTO.getConta().getId().intValue());
    }
}
