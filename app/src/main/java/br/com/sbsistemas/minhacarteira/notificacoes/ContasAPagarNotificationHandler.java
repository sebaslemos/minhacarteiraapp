package br.com.sbsistemas.minhacarteira.notificacoes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.sbsistemas.minhacarteira.ListaGrupos;
import br.com.sbsistemas.minhacarteira.R;
import br.com.sbsistemas.minhacarteira.controlador.ControladorConta;
import br.com.sbsistemas.minhacarteira.modelo.Conta;

public class ContasAPagarNotificationHandler extends Worker {

    private static final String WORKER_TAG = "CONTA_A_PAGAR_NOTIFICACAO";
    private static final String CHANNEL_ID = "MINHA_CARTEIRA_CHANNEL_ID";
    private static final CharSequence CHANNEL_NAME = "MINHA_CARTEIRA_CHANNEL";
    private static final CharSequence NOTIFICATION_TITLE = "Veja suas contas a pagar hoje";

    public ContasAPagarNotificationHandler(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        ControladorConta ctrl = new ControladorConta(getApplicationContext());
        DateTime hoje = DateTime.now();
        List<Conta> contasNaoPagas =
                ctrl.getContasNaoPagas(hoje.getDayOfMonth(), hoje.getMonthOfYear(), hoje.getYear());

        if(!contasNaoPagas.isEmpty()){
            enviarNotificacoes(contasNaoPagas);
        }

        return Result.success();
    }

    private void enviarNotificacoes(List<Conta> contasNaoPagas) {
        //todo criar uma intentlayer com ListaGrupos e ListaContas
        Intent intent = new Intent(getApplicationContext(), ListaGrupos.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, 0);

        NotificationManager notificationManager = (NotificationManager)getApplicationContext().
                getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificacao =
                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(contasNaoPagas.get(0).getDescricao())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true);

        Objects.requireNonNull(notificationManager).notify(contasNaoPagas.get(0).getId().intValue(),
                notificacao.build());
    }

    public static void agendarNotificacao(){
        PeriodicWorkRequest notificationWork = new PeriodicWorkRequest.
                Builder(ContasAPagarNotificationHandler.class, 1, TimeUnit.DAYS)
                .addTag(WORKER_TAG)
                .build();

        WorkManager instance = WorkManager.getInstance();
        instance.enqueueUniquePeriodicWork(WORKER_TAG,
                ExistingPeriodicWorkPolicy.KEEP, notificationWork);
    }
}
