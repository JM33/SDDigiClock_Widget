package com.sd.sddigiclock;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class DownloadFontsWorker extends Worker {
    private static final String TAG = "DownloadFontsWorker";
    public DownloadFontsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        FontSettingsFragment.loadFontButtons();
        Log.i(TAG, "Buttons set");
        return Result.success();
    }
}
