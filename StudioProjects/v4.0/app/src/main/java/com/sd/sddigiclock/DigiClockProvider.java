package com.sd.sddigiclock;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class DigiClockProvider extends AppWidgetProvider{
	
	private static final String LOG = "DCP";

	private static PendingIntent service = null;

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
    	AppWidgetManager mgr = AppWidgetManager.getInstance(context);
    	ComponentName cn = new ComponentName(context, DigiClockProvider.class);
    	int [] awids = mgr.getAppWidgetIds(cn);
    	onUpdate(context, mgr, awids);
    	
    	for (int i = 0; i < awids.length; i++){
    		updateWidget(context, mgr, awids[i]);
    		Log.i(LOG, "Enabled ID = " + Integer.toString(awids[i]));
    	}
    	
    	PackageManager pm = context.getPackageManager();
    	pm.setComponentEnabledSetting(new ComponentName("com.sd.sddigiclock", ".DigiClockProvider"), 
    									PackageManager.COMPONENT_ENABLED_STATE_ENABLED, 
    									PackageManager.DONT_KILL_APP);
    	Log.i(LOG, "DigiClockProvider onEnabled");
    	 
    }
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		//Log.w(LOG, "onUpdate method called");
		
		final int N = appWidgetIds.length;
		for (int i = 0; i < N; ++i) {
			updateWidget(context, appWidgetManager, appWidgetIds[i]);
			
		} 
	}
	
	 @Override  
	    public void onDisabled(Context context)  
	    {  
	        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
	  
	        m.cancel(service);  
	        
	        Log.d("SDCP","Clearing all preferences for:" + "prefs");
        	SharedPreferences prefs=context.getSharedPreferences("prefs", 0);
        	Log.d("SDCP","Number of preferences:" + prefs.getAll().size());
            SharedPreferences.Editor prefsEdit = prefs.edit(); 
            prefsEdit.clear();
            //finally commit the values
            prefsEdit.commit();
            
	        PackageManager pm = context.getPackageManager();
	    	pm.setComponentEnabledSetting(new ComponentName("com.sd.sddigiclock", ".DigiClockProvider"), 
	    									PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
	    									PackageManager.DONT_KILL_APP);
	        
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
		 Log.i(LOG, "intent = " + intent.toString());
		 
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
			 
	 }
	 
	 static void updateWidget(Context context, AppWidgetManager appwidgetmanager, int appWidgetId){
		 final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);  
		 //Log.i("SDDC", appwidgetmanager.getAppWidgetInfo(appWidgetId).toString());
		 final Calendar TIME = Calendar.getInstance();  
		 TIME.set(Calendar.MINUTE, 0);  
		 TIME.set(Calendar.SECOND, 0);  
		 TIME.set(Calendar.MILLISECOND, 0);  
		 //Log.i(LOG, "OnUpdate awId =" + Integer.toString(appWidgetId));
		 final Intent intent = new Intent(context, UpdateWidgetService.class);  
		 intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		 //intent.putExtra("Cells", AppWidgetManager.);
		 intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

		 //if (service == null)  
	     //   {  
	            service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);  
	     //   }  
	  
		 m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, service);
	 
	 }
}

