package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdateWidgetWorker extends Worker {

    private final static String TAG = "UpdateWidgetWorker";
    private Context mContext;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final int[] appWidgetIds;

    public UpdateWidgetWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);


        mContext = context;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
        ComponentName thisAppWidget = new ComponentName(mContext.getPackageName(), DigiClockProvider.class.getName());
        appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
    }

    @Override
    public Result doWork() {

        // Do the work here

        for(int appWidgetId: appWidgetIds){
            UpdateWidgetView.updateView(mContext, appWidgetId);
            Log.i(TAG, "Worker updated widget ID: " +appWidgetId);
        }

        // Indicate whether the work finished successfully with the Result
        return Result.success();
    }
}
