package com.sd.sddigiclock;

import java.util.Calendar;
import java.util.List;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class DigiClockProvider extends AppWidgetProvider{
	
	private static final String LOG = "DCP";

	private static PendingIntent service = null;


	public static final String ACTION_TICK = "CLOCK_TICK";
	public static final String SETTINGS_CHANGED = "SETTINGS_CHANGED";
	public static final String JOB_TICK = "JOB_CLOCK_TICK";
	private static String clockButtonApp;
	private static String sminutes;
	private static String ampm;
	private static String sdate;
	private static Intent prefsIntent;
	private SharedPreferences preferences;

	private static RemoteViews view;
	static boolean dateshown;
	static boolean ampmshown;
	static boolean show24;
	static boolean fillbg;
	static int clocktextsize;
	static int datetextsize;
	static boolean dateMatchClockColor;
	static int dateFormatIndex;

	static int cColor;
	static int dColor;
	static int bgColor;

	static int Bg;
	String Fontfile;
	static int  mFont;
	


    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    	//called when widgets are deleted
        //see that you get an array of widgetIds which are deleted
        //so handle the delete of multiple widgets in an iteration
    	
    	final int N = appWidgetIds.length;
    	for (int i = 0; i<N; i++){
    		
    		
    	}
	
        super.onDeleted(context, appWidgetIds);
    }

    

    @Override
    public void onEnabled(Context context) {
		//runs when all of the first instance of the widget are placed
		//on the home screen


		/*
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, DigiClockProvider.class);
		int [] awids = mgr.getAppWidgetIds(cn);
		onUpdate(context, mgr, awids);

		for (int i = 0; i < awids.length; i++){
			updateWidget(context, mgr, awids[i]);
			Log.i(LOG, "Enabled ID = " + Integer.toString(awids[i]));
		}

		//PackageManager pm = context.getPackageManager();
		//pm.setComponentEnabledSetting(new ComponentName("com.sd.sddigiclock", ".DigiClockProvider"),
		//		PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		//		PackageManager.DONT_KILL_APP);

		*/
		Log.i(LOG, "DigiClockProvider onEnabled");

		restartAll(context);
    }
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.w(LOG, "onUpdate method called");

		for (int appWidgetId : appWidgetIds) {
			updateAppWidget(context, appWidgetManager, appWidgetId);
		}

	}

	 @Override
	    public void onDisabled(Context context)  
	    {
	    	/*
			//final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			//m.cancel(service);

			Log.d("SDCP","Clearing all preferences for:" + "prefs");
			SharedPreferences prefs=context.getSharedPreferences("prefs", 0);
			Log.d("SDCP","Number of preferences:" + prefs.getAll().size());
			SharedPreferences.Editor prefsEdit = prefs.edit();
			prefsEdit.clear();
			//finally commit the values
			prefsEdit.commit();

			//PackageManager pm = context.getPackageManager();
			//pm.setComponentEnabledSetting(new ComponentName("com.sd.sddigiclock", ".DigiClockProvider"),
			//		PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
			//		PackageManager.DONT_KILL_APP);
			*/

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
				jobScheduler.cancelAll();
			} else {
				// stop alarm
				AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
				appWidgetAlarm.stopAlarm();
			}

			Intent serviceBG = new Intent(context.getApplicationContext(), WidgetBackgroundService.class);
			serviceBG.putExtra("SHUTDOWN", true);
			context.getApplicationContext().startService(serviceBG);
			context.getApplicationContext().stopService(serviceBG);
	    }  
	 @Override
     public void onReceive(Context context, Intent intent) {
		//all the intents get handled by this method
	     //mainly used to handle self created intents, which are not
	     //handled by any other method
		 
	     //the super call delegates the action to the other methods
	    
	     //for example the APPWIDGET_UPDATE intent arrives here first
	     //and the super call executes the onUpdate in this case
	     //so it is even possible to handle the functionality of the
	     //other methods here
	     //or if you don't call super you can overwrite the standard
	     //flow of intent handling 
		 //Log.i(LOG, "intent = " + intent.toString());

		 AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
		 ComponentName thisAppWidget = new ComponentName(context.getPackageName(), DigiClockProvider.class.getName());
		 int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

		 if (intent.getAction().equals(SETTINGS_CHANGED)) {
			 onUpdate(context, appWidgetManager, appWidgetIds);
			 if (appWidgetIds.length > 0) {
				 restartAll(context);
			 }
		 }

		 if (intent.getAction().equals(JOB_TICK) || intent.getAction().equals(ACTION_TICK) ||
				 intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
				 || intent.getAction().equals(Intent.ACTION_DATE_CHANGED)
				 || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)
				 || intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
			 restartAll(context);
			 onUpdate(context, appWidgetManager, appWidgetIds);
		 }
		/*
		goAsync();


		 final String action = intent.getAction();
		 if(AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)){
			 Bundle extras = intent.getExtras();
			 final int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			 if(appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID){
				 this.onDeleted(context, new int[] {appWidgetId});
			 }
		 }else{
			 super.onReceive(context, intent);
		 }
			*/
	 }

	private void restartAll(Context context){
		Intent serviceBG = new Intent(context.getApplicationContext(), WidgetBackgroundService.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// for Android 8 start the service in foreground
			context.startForegroundService(serviceBG);
		} else {
			context.startService(serviceBG);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			scheduleJob(context);
		} else {
			AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(context.getApplicationContext());
			appWidgetAlarm.startAlarm();
		}
	}



	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void scheduleJob(Context context) {
		ComponentName serviceComponent = new ComponentName(context.getPackageName(), RepeatingJob.class.getName());
		JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
		builder.setPersisted(true);
		builder.setPeriodic(600000);
		JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		int jobResult = jobScheduler.schedule(builder.build());
		if (jobResult == JobScheduler.RESULT_SUCCESS){
		}
	}
	 
	 static void updateAppWidget(Context context, AppWidgetManager appwidgetmanager, int appWidgetId){

		 Intent intent = new Intent(context, UpdateWidgetService.class);
		 intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		 intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		 context.startService(intent);
		 //Toast.makeText(context, "Updated widget " + appWidgetId, Toast.LENGTH_SHORT).show();


	 }


}

