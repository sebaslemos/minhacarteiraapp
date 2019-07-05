package br.com.sbsistemas.minhacarteira.notificacoes;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ContasAPagarNotification extends Worker {

    public ContasAPagarNotification(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        

        return Result.success();
    }
}
