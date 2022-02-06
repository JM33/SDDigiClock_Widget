package com.sd.sddigiclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.WildcardType;

public class DigiClockBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceBG = new Intent(context, WidgetBackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.startForegroundService(serviceBG);
            Log.d("DigiClockProvider", "Start service android 12");
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
