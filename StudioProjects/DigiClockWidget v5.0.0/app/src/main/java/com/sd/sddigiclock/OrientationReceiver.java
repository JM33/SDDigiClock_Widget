package com.sd.sddigiclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class OrientationReceiver extends BroadcastReceiver {

    private final static String TAG= "OrientationReceiver";
    private int appWidgetId;
    private PendingIntent service;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received intent");
        if(intent.getAction().equals("android.intent.action.CONFIGURATION_CHANGED")){
            Log.i(TAG, "Configuration changed");
            // We want to make sure the widget is always up to date.
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DigiClockProvider.class.getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);
            if(appWidgetIds.length > 0) {
                new DigiClockProvider().onUpdate(context, appWidgetManager, appWidgetIds);
            }


            Intent serviceBG = new Intent(context, WidgetBackgroundService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.startForegroundService(serviceBG);
                Log.d(TAG, "Start service android 12");
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // for Android 8 start the service in foreground
                context.startForegroundService(serviceBG);
            } else {
                context.startService(serviceBG);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                DigiClockProvider.scheduleJob(context);
            } else {
                AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context);
                appWidgetAlarm.startAlarm();
            }
        }
    }
}
