package com.sd.sddigiclock;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.List;

import static android.content.Context.WINDOW_SERVICE;

public class UpdateWidgetView {
    private static final String TAG = "UpdateWidgetView";
    private static RemoteViews view;
    private static WindowManager mWindowManager;
    private static PackageManager packageManager;

    private static Intent alarmClockIntent;
    private static Intent prefsIntent;


    private static String clockButtonApp;

    private static List<ApplicationInfo> packages;

    public static void updateView(Context context, int appWidgetId){


        mWindowManager =  (WindowManager) context.getSystemService(WINDOW_SERVICE);
        packageManager = context.getPackageManager();

        alarmClockIntent = new Intent();
        prefsIntent = new Intent();

        getPrefs(context, appWidgetId);

        view = new RemoteViews(context.getPackageName(), R.layout.widget_layout);


        view.setViewVisibility(R.id.linearLayoutAdvanced, View.VISIBLE);
        view.setViewVisibility(R.id.BackGround, View.VISIBLE);
        view.setViewVisibility(R.id.linearLayoutClassic, View.GONE);


        Bitmap updateBitmap = WidgetImage.buildClockImage(context, appWidgetId);

        int maxsize = (int)(getScreenWidth() * getScreenHeight() * 4 * 1.5f);
        //Log.d("UpdateWidgetService", "SW - " + getScreenWidth() + " x SH - " + getScreenHeight() + " x 4 x 1.5 = " +maxsize);
        //Log.d("UpdateWidgetService", "Bitmap = " + updateBitmap.getByteCount());
        boolean isOversize = false;
        if(updateBitmap!=null && updateBitmap.getByteCount() > maxsize){
            Toast.makeText(context, context.getResources().getString(R.string.oversize), Toast.LENGTH_LONG).show();
            isOversize = true;
        }

        if(updateBitmap !=null) {
            view.setImageViewBitmap(R.id.BackGround, updateBitmap);
        }




        ComponentName cnpref = new ComponentName("com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs");
        prefsIntent.setComponent(cnpref);
        prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        prefsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        prefsIntent.setData(Uri.parse(prefsIntent.toUri(Intent.URI_INTENT_SCHEME)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, prefsIntent, PendingIntent.FLAG_MUTABLE);
            view.setOnClickPendingIntent(R.id.SettingsButton, pendingIntent);
        }else{
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, prefsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.SettingsButton, pendingIntent);
        }


        Intent appChooserIntent=new Intent(context, AppSelector.class);

        Bundle bundle = new Bundle();
        bundle.putInt("AppWidgetId", appWidgetId);
        appChooserIntent.putExtras(bundle);



        if(clockButtonApp.equals("NONE")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent pendingIntentC = PendingIntent.getActivity(context, 0, prefsIntent, PendingIntent.FLAG_MUTABLE);
                view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
            }else{
                PendingIntent pendingIntentC = PendingIntent.getActivity(context, 0, prefsIntent, 0);
                view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
            }

        }else{
            setClockButtonApp(clockButtonApp, appWidgetId, context);

        }



        Intent refreshIntent = new Intent(context, DigiClockPrefs.class);
        //refreshIntent.setComponent(cnpref);
        refreshIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        refreshIntent.putExtra("Refresh", "Yes");
        //refreshIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        refreshIntent.setData(Uri.withAppendedPath(Uri.parse("myapp://widget/id/#togetituniqie" + appWidgetId), String.valueOf(appWidgetId)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent pendingIntentR = PendingIntent.getActivity(context, 0, refreshIntent, PendingIntent.FLAG_MUTABLE);
            view.setOnClickPendingIntent(R.id.refreshButton, pendingIntentR);
        }else{
            PendingIntent pendingIntentR = PendingIntent.getService(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.refreshButton, pendingIntentR);
        }




        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(appWidgetId, view);
        Log.i(TAG, "Update widget : " +appWidgetId);
    }

    private static void getPrefs(Context context, int appWidgetId){
        SharedPreferences prefs = context.getApplicationContext().getSharedPreferences(
                "prefs", 0);


        clockButtonApp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
    }


    public static int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
    public static int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static void setClockButtonApp(final String packagename, int appWidgetId, Context context){
        //Log.d("SDDC", "Set Clock Button Application " +  " --> " + packagename );
        packageManager = context.getPackageManager();
        packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        //Log.d("SDDC", "LOOKING FOR PACKAGE :" + packagename);
        for (ApplicationInfo packageInfo : packages) {
            //Log.d("UWS", "Installed package :" + packageInfo.packageName + " -- looking for: " + packagename);
            //Log.d("UWS", "Source dir : " + packageInfo.sourceDir);
            //Log.d("UWS", "Launch Activity :" + packageManager.getLaunchIntentForPackage(packageInfo.packageName));
            if(packagename == null){
                return;
            }
            if(packageInfo.packageName.equals(packagename)){
                Log.d(TAG, "Found " +  " --> " + packagename );
                //Log.d("SDDC", "LaunchActivity = " + launchActivity );
                //try {
                //ComponentName cn = new ComponentName(packageInfo.packageName, launchActivity);
                //packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
                alarmClockIntent = packageManager.getLaunchIntentForPackage(packageInfo.packageName);
                if(alarmClockIntent == null){
                    return;
                }
                alarmClockIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent pendingIntentC = PendingIntent.getActivity(context, 0, alarmClockIntent, PendingIntent.FLAG_MUTABLE);
                    if(view !=null)
                        view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
                }else{
                    PendingIntent pendingIntentC = PendingIntent.getActivity(context, 0, alarmClockIntent, 0);

                    if(view !=null)
                        view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
                }
                SharedPreferences prefs = context.getSharedPreferences(
                        "prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("ClockButtonApp"+appWidgetId, packagename);
                edit.apply();
                clockButtonApp = packagename;
                //Log.d("TAG", "Found " +  " --> " + packagename + "/" + launchActivity);
                //Log.d("TAG", "Prefs clock app = " +  prefs.getString("ClockButtonApp", "NONE"));



                return;


            }
        }
    }
}
