package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.WildcardType;
import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class DigiClockBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "DigiClockBroadcastReceiver";

    private long lastUpdateTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent : " + intent.getAction());



        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DigiClockProvider.class.getName());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
        if(appWidgetIds.length > 0) {
            new DigiClockProvider().onUpdate(context, appWidgetManager, appWidgetIds);
            Log.i(TAG, "Updating all widgets via Provider");
        }

        Intent serviceBG = new Intent(context, WidgetBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            try {
                context.startForegroundService(serviceBG);
                Log.d("DigiClockProvider", "Start service android 31+");
            } catch (android.app.ForegroundServiceStartNotAllowedException e) {
                Log.d(TAG, e.getMessage());
            }

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // for Android 8 start the service in foreground
            context.startForegroundService(serviceBG);
            Log.d("DigiClockProvider", "Start service android 26-31");
        } else {
            context.startService(serviceBG);
            Log.d("DigiClockProvider", "Start service android -26");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            DigiClockProvider.scheduleJob(context);
        } else {
            AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context);
            appWidgetAlarm.startAlarm();
        }
        Log.i(TAG, "Start Background Service");
    }
}
