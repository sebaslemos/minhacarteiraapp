package br.com.sbsistemas.minhacarteira.notificacoes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import br.com.sbsistemas.minhacarteira.ListaContasActivity;
import br.com.sbsistemas.minhacarteira.ListaGrupos;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.adapter.to.ContaTO;
import br.com.sbsistemas.minhacarteira.broadcast.ActionReceiver;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.utils.CorGrupo;

public class ContasAPagarNotificationHandler extends Worker {

    private static final String WORKER_TAG = "CONTA_A_PAGAR_NOTIFICACAO";
    private static final String CHANNEL_ID = "MINHA_CARTEIRA_CHANNEL_ID";
    private static final CharSequence CHANNEL_NAME = "MINHA_CARTEIRA_CHANNEL";
    private static final CharSequence NOTIFICATION_TITLE = "Veja suas contas a pagar hoje";
    private static final String GRUPOS_CONTAS_NAO_PAGAS = "GRUPOS_CONTAS_NAO_PAGAS";
    private static final int SUMMARY_ID = 999999999;

    public ContasAPagarNotificationHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        ControladorConta ctrl = new ControladorConta(getApplicationContext());
        DateTime hoje = DateTime.now();
        List<ContaTO> contasNaoPagas =
                ctrl.getContasNaoPagas(hoje.getDayOfMonth(), hoje.getMonthOfYear(), hoje.getYear());

        if(contasNaoPagas.size() > 1){
            criaGrupoNotificacao();
        }
        for (ContaTO conta: contasNaoPagas) {
            enviarNotificacoes(conta);
        }

        return Result.success();
    }

    private void criaGrupoNotificacao() {
        Notification notificacaoSummary = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText("TESTE")
                .setGroup(GRUPOS_CONTAS_NAO_PAGAS)
                .setGroupSummary(true)
                .setSmallIcon(R.drawable.icon)
                .build();

        NotificationManagerCompat.from(getApplicationContext())
                .notify(SUMMARY_ID, notificacaoSummary);
    }

    private void enviarNotificacoes(ContaTO contaTO) {
        Intent listaGruposIntent = new Intent(getApplicationContext(), ListaGrupos.class);
        Intent listaContasIntent = new Intent(getApplicationContext(), ListaContasActivity.class);
        listaContasIntent.putExtra("data", LocalDate.now());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addNextIntent(listaGruposIntent).addNextIntent(listaContasIntent);
        PendingIntent onclickPendingIntent = stackBuilder.getPendingIntent(
                new Random().nextInt(), PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pagarcontaIntent = new Intent(getApplicationContext(), ActionReceiver.class);
        pagarcontaIntent.putExtra(ActionReceiver.ACTION_KEY, ActionReceiver.ACTION_PAGAR);
        pagarcontaIntent.putExtra(ActionReceiver.DATA_KEY, contaTO);
        PendingIntent pagarPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                new Random().nextInt(), pagarcontaIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent desativarContaIntent = new Intent(getApplicationContext(), ActionReceiver.class);
        desativarContaIntent.putExtra(ActionReceiver.ACTION_KEY, ActionReceiver.ACTION_DESATIVAR);
        desativarContaIntent.putExtra(ActionReceiver.DATA_KEY, contaTO);
        PendingIntent desativarPendindIntent = PendingIntent.getBroadcast(getApplicationContext(),
                new Random().nextInt(), desativarContaIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationManager notificationManager = (NotificationManager)getApplicationContext().
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        Notification notificacao =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Lembre de pagar " + contaTO.getConta().getDescricao())
                .setContentText(getconteudoNotificacao(contaTO))
                .setContentIntent(onclickPendingIntent)
                .setSmallIcon(R.drawable.icon)
                .setLargeIcon(getIconeGrupo(contaTO))
                .addAction(0, "CONTA PAGA", pagarPendingIntent)
                .addAction(0, "CONTA NÃO ATIVA", desativarPendindIntent)
                .setAutoCancel(true)
                .setGroup(GRUPOS_CONTAS_NAO_PAGAS)
                .build();

        NotificationManagerCompat.from(getApplicationContext()).notify(
                contaTO.getConta().getId().intValue(),
                notificacao);
    }

    private Bitmap getIconeGrupo(ContaTO contaTO) {
        return BitmapFactory.decodeResource(getApplicationContext().getResources(),
                CorGrupo.getIconeGrande(contaTO.getGrupo().getDescricao()));
    }

    private CharSequence getconteudoNotificacao(ContaTO contaTO) {
        String msg = "A conta " + contaTO.getConta().getDescricao() + " deve ser paga hoje. Valor: "
                + contaTO.getConta().getValorFormatado();
        return msg;
    }

    public static void agendarNotificacao(){
        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.
                Builder(ContasAPagarNotificationHandler.class, 1, TimeUnit.DAYS)
                .addTag(WORKER_TAG)
                .build();

        WorkManager instance = WorkManager.getInstance();
        instance.enqueueUniquePeriodicWork(WORKER_TAG,
                ExistingPeriodicWorkPolicy.REPLACE, notificationWork);
    }
}
