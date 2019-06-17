package com.sd.sddigiclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextClock;

import java.util.Calendar;

/**
 * Created by Brian on 6/15/2019.
 */

public class CustomTextClock extends TextClock {
    int appWidgetId;

    public CustomTextClock(final Context context) {
        super(context);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                final Calendar TIME = Calendar.getInstance();
                TIME.set(Calendar.MINUTE, 0);
                TIME.set(Calendar.SECOND, 0);
                TIME.set(Calendar.MILLISECOND, 0);

                final Intent intent = new Intent(context, UpdateWidgetService.class);

                Bundle extras = intent.getExtras();
                if (extras != null) {
                    appWidgetId = extras.getInt(
                            AppWidgetManager.EXTRA_APPWIDGET_ID,
                            AppWidgetManager.INVALID_APPWIDGET_ID);
                }

                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

                //if (service == null)
                //{
                PendingIntent service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                //}

                m.setExact(AlarmManager.RTC, TIME.getTime().getTime(), service);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        this.addTextChangedListener(textWatcher);

    }
}
