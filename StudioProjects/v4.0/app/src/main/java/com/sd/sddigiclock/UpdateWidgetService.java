package com.sd.sddigiclock;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.text.format.DateFormat;
import android.widget.TextClock;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class UpdateWidgetService extends Service {
	private static final String LOG = "DCProvider";
	private static final int JOB_ID = 101;
	public static Context mContext;
	private String ampm;
	private String shours;
	private String sminutes;
	private String sdate;
	private String month_name;
	
	private int clocktextsize;
	private int datetextsize;
	private int cColor;
	private int dColor;
	private boolean dateMatchClockColor;
	//private PackageManager packageManager;
	public static Intent alarmClockIntent;
	
	private Intent prefsIntent;
	
	private int day;
	private int year;
	private int month;
	private int Bg;
	boolean ampmshown;
	boolean dateshown;
	
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private boolean fillbg;
	private static PackageManager packageManager;
	private boolean show24;
	private int mFont;
	private String Fontfile;
	private int bgColor;
	private static RemoteViews view;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private int dateheight;
	private int clockheight;
	private int dateFormatIndex;

	private static String clockButtonApp;

	static List<ApplicationInfo> packages;

	private static Handler mHandler;
	PendingIntent service;

	public static AlarmManager alarmManager;
	//private String dateFormat;

	boolean mIsPortraitOrientation;
	public static boolean isOversize;

	@Override
	    public void onCreate()  
	    {  


	        mContext = this.getApplicationContext();

	        mHandler = new Handler();
	        
	        mWindowManager =  (WindowManager) getSystemService(WINDOW_SERVICE);
	        mDisplay = mWindowManager.getDefaultDisplay();
	        packageManager = this.getPackageManager();
	        
	        alarmClockIntent = new Intent();
	        prefsIntent = new Intent();
	        /*
	        mContext = this.getApplicationContext();
	        TimeText = new TextView(mContext);
	        TimeText.setId(R.id.update);
	        FrameLayout Flayout = new FrameLayout(mContext);
	        Flayout.setId(R.id.FrameLayout);
	        */
	        //Log.i(LOG, "Service onCreate");

	    }

		@Override  
	public int onStartCommand(Intent intent, int flags, int startId)  
	{



		if(intent == null){
			Log.d(LOG, "No Intent onStartCommand");
			return START_REDELIVER_INTENT;
		}
		if (intent.getExtras() != null) {
			Bundle extras = intent.getExtras();
		    appWidgetId = extras.getInt(
		            AppWidgetManager.EXTRA_APPWIDGET_ID, 
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		    Log.i(LOG, "Service Started awId =" + Integer.toString(appWidgetId));
		}

		//alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		/*
		final AlarmManager m = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

		final Intent intent2 = new Intent(mContext, UpdateWidgetService.class);
		intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		//intent2.putExtra("Cells", AppWidgetManager.);
		intent2.setData(Uri.parse(intent2.toUri(Intent.URI_INTENT_SCHEME)));


		PendingIntent service = PendingIntent.getService(mContext, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		m.set(AlarmManager.RTC, (1000 * 60), service);
		*/


		//	m.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (1000 * 60), service);


		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(
	            "prefs", 0);
		dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
		ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
		show24 = prefs.getBoolean("Show24"+appWidgetId, false);
		fillbg = true;
		clocktextsize = prefs.getInt("ClockTextSize"+appWidgetId, 15);
		datetextsize = prefs.getInt("DateTextSize"+appWidgetId, 12);
		dateMatchClockColor = prefs.getBoolean("DateMatchClockColor"+appWidgetId, true);
		dateFormatIndex = prefs.getInt("DateFormat" +appWidgetId, 2);

		cColor = prefs.getInt("cColor"+appWidgetId, -1);
		if(dateMatchClockColor){
			dColor = cColor;
		}else {
			dColor = prefs.getInt("dColor" + appWidgetId, -1);
		}
		bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);
		
		Bg = prefs.getInt("Bg"+appWidgetId, 3);
		Fontfile = prefs.getString("Font"+appWidgetId, "Roboto-Regular.ttf");
		mFont = prefs.getInt("Fontnum"+appWidgetId, 0);

		clockButtonApp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
        //Log.d("SDDC", "ClockApp saved = " + clockButtonApp);
		setText();
		
		
		
	    view = new RemoteViews(getPackageName(), R.layout.widget_layout);

		//CustomTextClock mTextClock = new CustomTextClock(getApplicationContext());
		//RemoteViews newView = new RemoteViews(getPackageName(), R.id.customTextClockLayout);
		//view.addView(R.id.linearLayout3, newView);
		//view.setImageViewBitmap(R.id.BackGround, buildBGUpdate(bgColor));
		Bitmap updateBitmap = buildClockUpdate(shours + ":" + sminutes, ampm, sdate, bgColor);
		int maxsize = (int)(getScreenWidth() * getScreenHeight() * 4 * 1.5f);
		Log.d("UpdateWidgetService", "SW - " + getScreenWidth() + " x SH - " + getScreenHeight() + " x 4 x 1.5 = " +maxsize);
		Log.d("UpdateWidgetService", "Bitmap = " + updateBitmap.getByteCount());
		isOversize = false;
		if(updateBitmap.getByteCount() > maxsize){
			Toast.makeText(mContext, R.string.oversize, Toast.LENGTH_LONG).show();
			isOversize = true;
		}
		//ByteArrayOutputStream out = new ByteArrayOutputStream();
		//updateBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
		//Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

		view.setImageViewBitmap(R.id.BackGround, updateBitmap);
	    //updateBitmap.recycle();
	    //view.setImageViewBitmap(R.id.clockView, buildClockUpdate(shours + ":" + sminutes);
	    //view.setImageViewBitmap(R.id.ampmView, buildAMPMUpdate(ampm));
		//view.setImageViewBitmap(R.id.dateView, buildDateUpdate(sdate));

		//view.setInt(R.id.ClockButton, "setWeight", 4);

		//int mfont = prefs.getInt("mFont"+appWidgetId, 0);
		//view.setInt(R.id.clockText, "setTypeFace", R.font.weezerfont);
		//view.setTextViewText(R.id.ClockText, (shours + ":" + sminutes));
		//view.setTextColor(R.id.ClockText, Color.TRANSPARENT);
		//int clocksize = clocktextsize * 10;
		//view.setFloat(R.id.ClockText, "setTextSize", clocksize);
		//view.set
		//view.setTextColor(R.id.DateText, dColor);
		//view.setTextViewText(R.id.DateText, sdate);

		//view.setTextColor(R.id.AMPMText, cColor);
		//view.setTextColor(R.id.DateText, Color.TRANSPARENT);
		//view.setTextViewText(R.id.DateText, sdate);
		//int datesize = datetextsize * 6;
		//view.setFloat(R.id.DateText, "setTextSize", datesize);
		//view.setTextViewText(R.id.AMPMText, ampm);
		
		/*
		
		float ctsize = (clocktextsize*1.5f + 6);
		float dtsize = datetextsize;
		
		view.setFloat(R.id.clockText, "setTextSize", ctsize);
		view.setFloat(R.id.DateText, "setTextSize", dtsize);
		
		if(dateshown){
			view.setViewVisibility(R.id.DateText, View.VISIBLE);
		}else{
			view.setViewVisibility(R.id.DateText, View.GONE);
		}
		if(ampmshown){
			//view.setViewVisibility(R.id.ampmView, View.VISIBLE);
			view.setCharSequence(R.id.clockText, "setFormat12Hour", "h:mm a");
		}else{
			view.setCharSequence(R.id.clockText, "setFormat12Hour", "h:mm");
		}
		if(show24){
			view.setCharSequence(R.id.clockText, "setFormat12Hour", "HH:mm");
			view.setCharSequence(R.id.clockText, "setFormat24Hour", "HH:mm");
		}else{
			if(ampmshown){
				//view.setViewVisibility(R.id.ampmView, View.VISIBLE);
				view.setCharSequence(R.id.clockText, "setFormat12Hour", "h:mm a");
				view.setCharSequence(R.id.clockText, "setFormat24Hour", "h:mm a");
			}else{
				view.setCharSequence(R.id.clockText, "setFormat12Hour", "h:mm");
				view.setCharSequence(R.id.clockText, "setFormat24Hour", "h:mm");
			}
		}
	    */
		
		if(fillbg){
			//view.setInt(R.id.linearLayout2, "setHeight", -2);
		}else{
			//view.setInt(R.id.linearLayout2, "setHeight", -1);
		}
	    // Push update for this widget to the home screen
				
		ComponentName cnpref = new ComponentName("com.sd.sddigiclock", "com.sd.sddigiclock.DigiClockPrefs");
		prefsIntent.setComponent(cnpref);
		prefsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		prefsIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	    prefsIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		
		PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
				0, prefsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.SettingsButton, pendingIntent);

		//DATE INTENT on click date
		PendingIntent pendingIntentD = PendingIntent.getActivity(mContext, 0, prefsIntent, 0);
	    //view.setOnClickPendingIntent(R.id.DateButton, pendingIntentD);

		//final PackageManager pm = getPackageManager();
	//get a list of installed apps.
		//packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		/*
		for (ApplicationInfo packageInfo : packages) {
			Log.d("UWS", "Installed package :" + packageInfo.packageName);
			Log.d("UWS", "Source dir : " + packageInfo.sourceDir);
			Log.d("UWS", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
		}
		*/

// the getLaunchIntentForPackage returns an intent that you can use with startActivity()

	    
		String clockImpls[][] = {
		        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
		        {"Standard Alarm Clock", "com.android.deskclock", "com.android.deskclock.DeskClock"},
		        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
		        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
		        //{"Samsung Galaxy Clock","com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage",} //
		        //{"ICS Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclockgoogle.DeskClockGoogle"}
		};
		boolean foundClockImpl = false;

		Intent appchooserintent=new Intent(UpdateWidgetService.this,AppSelector.class);

		Bundle bundle = new Bundle();
		bundle.putInt("AppWidgetId", appWidgetId);
		appchooserintent.putExtras(bundle);
		/*
		for(int i=0; i<clockImpls.length; i++) {
		    String vendor = clockImpls[i][0];
		    String packageName = clockImpls[i][1];
		    String className = clockImpls[i][2];
		    try {
		        ComponentName cn = new ComponentName(packageName, className);
		        packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
		        alarmClockIntent.setComponent(cn);
		        //Log.d("SDDC", "Found" +  vendor + " --> " + packageName + "/" + className);
		        foundClockImpl = true;
		    } catch (NameNotFoundException e) {
		        Log.d("SDDC", vendor + " does not exists");
		    }
		}
		*/


		if(clockButtonApp.equals("NONE")){
			PendingIntent pendingIntentC = PendingIntent.getActivity(mContext, 0, prefsIntent, 0);
			view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
		}else{
			setClockButtonApp(clockButtonApp, appWidgetId);

		}

		Intent refreshIntent = new Intent(mContext, UpdateWidgetService.class);
		refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		refreshIntent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		PendingIntent pendingIntentR = PendingIntent.getService(mContext, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.refreshButton, pendingIntentR);
		/*
		if (foundClockImpl) {
		    PendingIntent pendingIntentC = PendingIntent.getActivity(mContext, 0, alarmClockIntent, 0);
		    view.setOnClickPendingIntent(R.id.BackGround, pendingIntentC);
		}
		else{
			view.setOnClickPendingIntent(R.id.BackGround, pendingIntent);
		}
		*/

		AppWidgetManager manager = AppWidgetManager.getInstance(this);  
		//ComponentName thisWidget = new ComponentName(this, DigiClockProvider.class);
	    if(!isOversize) {
			manager.updateAppWidget(appWidgetId, view);
		}
	    //manager.updateAppWidget(thisWidget, view);
		/*
		final AlarmManager m = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		Log.i("UWS", "UPDATING WIDGET: " + appWidgetId);
		final Calendar TIME = Calendar.getInstance();
		TIME.set(Calendar.MINUTE, 0);
		TIME.set(Calendar.SECOND, 0);
		TIME.set(Calendar.MILLISECOND, 0);
		//Log.i(LOG, "OnUpdate awId =" + Integer.toString(appWidgetId));
		final Intent intent2 = new Intent(mContext, UpdateWidgetService.class);
		intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		//intent2.putExtra("Cells", AppWidgetManager.);
		intent2.setData(Uri.parse(intent2.toUri(Intent.URI_INTENT_SCHEME)));

		if (service == null)
		{
			service = PendingIntent.getService(mContext, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		if(Build.VERSION.SDK_INT <23){
			//Doze???
		}


		//startService(intent);
		//m.set(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis(), service);
		//m.setRepeating(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis(),60L * 1000L, service);
		//final PendingIntent pending = PendingIntent.getService(mContext, 0, intent, 0);
		//m.cancel(pending);

		//m.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000*60, service);
		//m.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis() + 60L * 1000L, service);
		Intent startServiceIntent = new Intent(mContext, ClockJobScheduler.class);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			mContext.startForegroundService(startServiceIntent);
		} else {
			mContext.startService(startServiceIntent);
		}
		*/
		//registerOneTimeAlarm(service, 1000*60, false);
		Log.i(LOG, "UpdateWidgetService Setting Alarm for 1 minute!!!!?X");
		//return START_STICKY;
	    return super.onStartCommand(intent, flags, startId);
	}

	public Bitmap buildClockUpdate(String time, String ampm, String date, int  color){

	 	/* Get Device and Widget orientation.
           This is done by adding a boolean value to
           a port resource directory like values-port/bools.xml */

	 		boolean mIsKeyguard;

			if(getScreenHeight() > getScreenWidth()) {
				mIsPortraitOrientation = true;
			}else{
				mIsPortraitOrientation = false;
			}

			// Get min dimensions from provider info
			AppWidgetProviderInfo providerInfo = AppWidgetManager.getInstance(
					getApplicationContext()).getAppWidgetInfo(appWidgetId);

			if(providerInfo == null){
			    return null;
            }
			// Since min and max is usually the same, just take min
			int mWidgetLandWidth = providerInfo.minWidth;
			int mWidgetPortHeight = providerInfo.minHeight;
			int mWidgetPortWidth = providerInfo.minWidth;
			int mWidgetLandHeight = providerInfo.minHeight;

			// Get current dimensions (in DIP, scaled by DisplayMetrics) of this
			// Widget, if API Level allows to
			AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
			Bundle mAppWidgetOptions = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				mAppWidgetOptions = mAppWidgetManager.getAppWidgetOptions(appWidgetId);

			if (mAppWidgetOptions != null
					&& mAppWidgetOptions
					.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) > 0) {

					//Log.d("UWS", "appWidgetOptions not null, getting widget sizes...");
				// Reduce width by a margin of 8dp (automatically added by
				// Android, can vary with third party launchers)

            /* Actually Min and Max is a bit irritating,
               because it depends on the homescreen orientation
               whether Min or Max should be used: */

				mWidgetPortWidth = mAppWidgetOptions
						.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
				mWidgetLandWidth = mAppWidgetOptions
						.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
				mWidgetLandHeight = mAppWidgetOptions
						.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
				mWidgetPortHeight = mAppWidgetOptions
						.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

				// Get the value of OPTION_APPWIDGET_HOST_CATEGORY
				int category = mAppWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);

				// If the value is WIDGET_CATEGORY_KEYGUARD, it's a lockscreen
				// widget (dumped with Android-L preview :-( ).
				mIsKeyguard = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;

			} else {
				//if (D.DEBUG_SERVICE)
					//Log.d("UWS", "No AppWidgetOptions for this widget, using minimal dimensions from provider info!");
				// For some reason I had to set this again here, may be obsolete
				mWidgetLandWidth = providerInfo.minWidth;
				mWidgetPortHeight = providerInfo.minHeight;
				mWidgetPortWidth = providerInfo.minWidth;
				mWidgetLandHeight = providerInfo.minHeight;
			}

			/*
			Log.d("UWS", "Dimensions of the Widget in DIP: portWidth =  "
						+ mWidgetPortWidth + ", landWidth = " + mWidgetLandWidth
						+ "; landHeight = " + mWidgetLandHeight
						+ ", portHeight = " + mWidgetPortHeight);

			*/

			// If device is in port oriantation, use port sizes
			int mWidgetWidthPerOrientation = mWidgetPortWidth;
			int mWidgetHeightPerOrientation = mWidgetPortHeight;

			if (!mIsPortraitOrientation)
			{
				// Not Portrait, so use landscape sizes
				mWidgetWidthPerOrientation = mWidgetLandWidth;
				mWidgetHeightPerOrientation = mWidgetLandHeight;
			}




			// font size
			float fontSize = clocktextsize*6;
			  
		    Paint Clockpaint = new Paint();
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(
					"prefs", 0);
			int mfont = prefs.getInt("mFont"+appWidgetId, 0);
			Typeface font;
		    font = Typeface.createFromAsset(this.getAssets(), Fontfile);

			Clockpaint.setAntiAlias(true);
			Clockpaint.setSubpixelText(true);
		    if(mFont == 0)
				Clockpaint.setTypeface(Typeface.DEFAULT);
		    else
				Clockpaint.setTypeface(font);
			Clockpaint.setStyle(Paint.Style.FILL);
			Clockpaint.setColor(cColor);
			Clockpaint.setTextSize((int)fontSize);
			Clockpaint.setShadowLayer(3f, 2f, 2f, Color.BLACK);
			Clockpaint.setTextAlign(Align.LEFT);
		    
		 // min. rect of text
		    Rect textBoundsClock = new Rect();
			Clockpaint.getTextBounds(time, 0, time.length(), textBoundsClock);
			Paint.FontMetrics fm = Clockpaint.getFontMetrics();
			float height = fm.descent - fm.ascent;
		    // create bitmap for text
		    //Bitmap bm = Bitmap.createBitmap((int) (Clockpaint.measureText(time)+30), textBoundsClock.height(), Bitmap.Config.ARGB_8888);
		    // canvas
		    //Canvas canvas = new Canvas(bm);
		    //canvas.drawARGB(255, 0, 255, 0);// for visualization

			/*
		    Log.d("UWS", "CLOCK UPDATE");
			Log.d("UWS", "FontSize = " + fontSize);
			Log.d("UWS", "Height = " + height);
			Log.d("UWS", "Width-MeasureText = " + Clockpaint.measureText(time));
			Log.d("UWS", "TextBounds Top= " + textBoundsClock.top);
			Log.d("UWS", "TextBounds Bottom = " + textBoundsClock.bottom);
			Log.d("UWS", "TextBounds Height= " + textBoundsClock.height());
			*/

		    //canvas.drawText(time, 10, textBoundsClock.height()-textBoundsClock.bottom, Clockpaint);
		    clockheight = (int)height+2;

		/////AMPM UPDATE

			// font size
			fontSize = clocktextsize*2;
			//fontSize+=fontSize*0.2f;
			//Bitmap myBitmap = Bitmap.createBitmap(clocktextsize*2, clocktextsize+20, Bitmap.Config.ARGB_4444);
			//Canvas myCanvas = new Canvas(myBitmap);
			Paint AMPMpaint = new Paint();


			//mfont = prefs.getInt("mFont"+appWidgetId, 0);
			//font = Typeface.createFromAsset(this.getAssets(), Fontfile);


			AMPMpaint.setAntiAlias(true);
			AMPMpaint.setSubpixelText(true);
			if(mFont == 0)
				AMPMpaint.setTypeface(Typeface.DEFAULT);
			else
				AMPMpaint.setTypeface(font);
			AMPMpaint.setStyle(Paint.Style.FILL);
			AMPMpaint.setColor(cColor);
			AMPMpaint.setTextSize((int)fontSize);
			AMPMpaint.setShadowLayer(3f, 2f, 2f, Color.BLACK);
			AMPMpaint.setTextAlign(Align.LEFT);


			// min. rect of text
			Rect textBoundsAMPM = new Rect();
			AMPMpaint.getTextBounds(ampm, 0, ampm.length(), textBoundsAMPM);

			fm = AMPMpaint.getFontMetrics();
			height = fm.descent - fm.ascent;
			// create bitmap for text

			//bm = Bitmap.createBitmap((textBoundsAMPM.width()+20), textBoundsAMPM.height(), Bitmap.Config.ARGB_8888);
			// canvas
			//canvas = new Canvas(bm);
			//canvas.drawARGB(255, 0, 255, 0);// for visualization

			//canvas.drawText(ampm, 10, textBounds.height()-textBounds.bottom, AMPMpaint);


		////// DATE UPDATE

			// font size
			fontSize = datetextsize*4;

			//Bitmap myBitmap = Bitmap.createBitmap(clocktextsize*2, clocktextsize+20, Bitmap.Config.ARGB_4444);
			//Canvas myCanvas = new Canvas(myBitmap);
			//Paint Datepaint = new Paint();

			//SharedPreferences prefs = getApplicationContext().getSharedPreferences(
			//		"prefs", 0);
			mfont = prefs.getInt("mFont"+appWidgetId, 0);
			font = Typeface.createFromAsset(this.getAssets(), Fontfile);


			TextPaint Datepaint= new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
			Datepaint.setSubpixelText(true);
			if(mFont == 0)
				Datepaint.setTypeface(Typeface.DEFAULT);
			else
				Datepaint.setTypeface(font);
			Datepaint.setStyle(Paint.Style.FILL);
			if(dateMatchClockColor){
				Datepaint.setColor(cColor);
			}else{
				Datepaint.setColor(dColor);
			}
			//Datepaint.setColor(dColor);
			Datepaint.setTextSize((int)fontSize);
			Datepaint.setShadowLayer(3f, 2f, 2f, Color.BLACK);
			Datepaint.setTextAlign(Align.CENTER);


			//Bitmap bm;
			// min. rect of text
			Rect textBoundsDate = new Rect();
			Rect displayBounds = new Rect();
			mDisplay.getRectSize(displayBounds);
			Datepaint.getTextBounds(time, 0, time.length(), textBoundsDate);

			fm = Datepaint.getFontMetrics();
			height = (int)(fm.descent - fm.ascent);

			//Log.i("UWS", Float.toString(textBoundsDate.width()));
			int maxwidth = displayBounds.width()-50;

			//Log.i("UWS", "Maxwidth =" + Integer.toString(maxwidth) + " Orientation = " + Integer.toString(mDisplay.getRotation()));


			float scale = getResources().getDisplayMetrics().density;
			int textWidth = getScreenWidth() - (int) (16 * scale);
			StaticLayout textLayout = new StaticLayout(
					date, Datepaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

			// get height of multiline text
			int textHeight = textLayout.getHeight();


			float x = textWidth/2;
			float y = (clockheight);
			dateheight = textHeight;

		////// BACKGROUND UPDATE

			//Bitmap myBitmap = Bitmap.createBitmap(clocktextsize*2, clocktextsize+20, Bitmap.Config.ARGB_4444);
			//Canvas myCanvas = new Canvas(myBitmap);
			Paint BGpaint = new Paint();



			new Rect();
			displayBounds = new Rect();
			mDisplay.getRectSize(displayBounds);
			//int height;
			if(dateshown){
				height = (int) ((clockheight + dateheight)*1.1f);
			}else{
				height = (int) ((clockheight)*1.1f);
			}
			Shader shader = null;
			int aw = Color.argb(200, 255, 255, 255);
			int ab = Color.argb(200, 0, 0, 0);
			switch(Bg){
				case 0:

					shader = new LinearGradient(0, 0, 0, height,
							new int[]{Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT},
							new float[]{0,0.45f,.55f,1}, Shader.TileMode.REPEAT);
					break;
				case 1:
					shader = new LinearGradient(0, 0, 0, height,
							new int[]{aw, Color.TRANSPARENT, Color.TRANSPARENT, ab},
							new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
					break;
				case 2:
					shader = new LinearGradient(0, 0, 0, height,
							new int[]{aw, color, color, ab},
							new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
					break;
				case 3:
					shader = new LinearGradient(0, 0, 0, height,
							new int[]{color, color, color, color},
							new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
					break;
			}

			BGpaint.setShader(shader);


			// min. rect of text
			//Rect textBounds = new Rect();
			//paint.getTextBounds(time, 0, time.length(), textBounds);
			// create bitmap for text

			//(int)(mWidgetLandWidth)*5
			int widthPX = dpToPx(mWidgetLandWidth*1.5f, mContext);
			//Bitmap bm = Bitmap.createBitmap((int)(getScreenWidth()*1.5f), (int)height, Bitmap.Config.ARGB_8888);
			Bitmap bm = Bitmap.createBitmap(widthPX, (int)height, Bitmap.Config.ARGB_8888);

			// canvas
			Canvas canvas = new Canvas(bm);
			canvas.drawPaint(BGpaint);

			canvas.drawText(time, canvas.getWidth()*0.5f - (Clockpaint.measureText(time)*0.5f), textBoundsClock.height()-textBoundsClock.bottom+(height*0.1f), Clockpaint);

			if(ampmshown) {
				canvas.drawText(ampm, canvas.getWidth() * 0.5f + (Clockpaint.measureText(time) * 0.5f) + 20, (textBoundsClock.height() * 0.5f) + textBoundsAMPM.height() - textBoundsAMPM.bottom+(height*0.1f), AMPMpaint);
			}


			if(dateshown) {
				// draw text to the Canvas center

				//canvas.drawText(sdate, canvas.getWidth()*0.5f - (Datepaint.measureText(sdate)*0.5f), textBoundsDate.height()-textBoundsDate.bottom+(height*0.1f), Datepaint);

				canvas.save();
				canvas.translate((canvas.getWidth() * 0.5f), y);
				textLayout.draw(canvas);
				canvas.restore();

			}
			//canvas.drawText(date, canvas.getWidth()*0.5f, (textBoundsDate.height()-textBoundsDate.bottom) + clockheight+50, Datepaint);
			// for visualization
			//canvas.drawPaint(paint);
			//canvas.drawText(time, 5, textBounds.height()-textBounds.bottom, paint);

			new BitmapDrawable(mContext.getResources(), bm);
		    return bm;
		}

		public Bitmap buildAMPMUpdate(String time){
			// font size
			   float fontSize = clocktextsize*2;
			   //fontSize+=fontSize*0.2f;
		    //Bitmap myBitmap = Bitmap.createBitmap(clocktextsize*2, clocktextsize+20, Bitmap.Config.ARGB_4444);
		    //Canvas myCanvas = new Canvas(myBitmap);
		    Paint paint = new Paint();

			SharedPreferences prefs = getApplicationContext().getSharedPreferences(
					"prefs", 0);
			int mfont = prefs.getInt("mFont"+appWidgetId, 0);
			Typeface font = Typeface.createFromAsset(this.getAssets(), Fontfile);


		    paint.setAntiAlias(true);
		    paint.setSubpixelText(true);
		    if(mFont == 0)
		    	paint.setTypeface(Typeface.DEFAULT);
		    else
		    	paint.setTypeface(font);
		    paint.setStyle(Paint.Style.FILL);
		    paint.setColor(cColor);
		    paint.setTextSize((int)fontSize);
		    paint.setShadowLayer(3f, 2f, 2f, Color.BLACK);
		    paint.setTextAlign(Align.LEFT);
		    
		    
		 // min. rect of text
		    Rect textBounds = new Rect();
		    paint.getTextBounds(time, 0, time.length(), textBounds);

			Paint.FontMetrics fm = paint.getFontMetrics();
			float height = fm.descent - fm.ascent;
		    // create bitmap for text
		    
		    Bitmap bm = Bitmap.createBitmap((textBounds.width()+20), textBounds.height(), Bitmap.Config.ARGB_8888);
		    // canvas
		    Canvas canvas = new Canvas(bm);
		    //canvas.drawARGB(255, 0, 255, 0);// for visualization
		    
		    canvas.drawText(time, 10, textBounds.height()-textBounds.bottom, paint);

		    return bm;
		}
		
		public Bitmap buildDateUpdate(String time){
			// font size
			   float fontSize = datetextsize*6;
			   
		    //Bitmap myBitmap = Bitmap.createBitmap(clocktextsize*2, clocktextsize+20, Bitmap.Config.ARGB_4444);
		    //Canvas myCanvas = new Canvas(myBitmap);
		    Paint paint = new Paint();

			SharedPreferences prefs = getApplicationContext().getSharedPreferences(
					"prefs", 0);
			int mfont = prefs.getInt("mFont"+appWidgetId, 0);
			Typeface font = Typeface.createFromAsset(this.getAssets(), Fontfile);


			paint.setAntiAlias(true);
		    paint.setSubpixelText(true);
		    if(mFont == 0)
		    	paint.setTypeface(Typeface.DEFAULT);
		    else
		    	paint.setTypeface(font);
		    paint.setStyle(Paint.Style.FILL);
		    paint.setColor(dColor);
		    paint.setTextSize((int)fontSize);
		    paint.setShadowLayer(3f, 2f, 2f, Color.BLACK);
		    paint.setTextAlign(Align.CENTER);
	    	
		    
		    Bitmap bm;
		 // min. rect of text
		    Rect textBounds = new Rect();
		    Rect displayBounds = new Rect();
		    mDisplay.getRectSize(displayBounds);
		    paint.getTextBounds(time, 0, time.length(), textBounds);

			Paint.FontMetrics fm = paint.getFontMetrics();
			int height = (int)(fm.descent - fm.ascent);

		    //Log.i("UWS", Float.toString(textBounds.width()));
		    int maxwidth = displayBounds.width()-100;
		    
		    //Log.i("UWS", "Maxwidth =" + Integer.toString(maxwidth) + " Orientation = " + Integer.toString(mDisplay.getRotation()));
			if(paint.measureText(time)+20 >= maxwidth){
		    	time = (month_name + " " + String.valueOf(day) + ",");
		    	paint.getTextBounds(time, 0, time.length(), textBounds);
		    	bm = Bitmap.createBitmap((int)paint.measureText(time)+100, (height+2)*2, Bitmap.Config.ARGB_8888);
		    	Rect textBounds2 = new Rect();
			    paint.getTextBounds(String.valueOf(year), 0, String.valueOf(year).length(), textBounds2);
		    	
		    	Canvas canvas = new Canvas(bm);
		    	canvas.drawText(time, textBounds.width()/2+50, height-textBounds.bottom, paint);
		    	canvas.drawText(String.valueOf(year), textBounds.width()/2+50, textBounds.height()*2, paint);
		    	dateheight = (height+2)*2;
		    }else{
		    // create bitmap for text
		    	bm = Bitmap.createBitmap(((int)paint.measureText(time)+100), (height+2), Bitmap.Config.ARGB_8888);
		    	Canvas canvas = new Canvas(bm);
		    	canvas.drawText(time, textBounds.width()/2+50, height-textBounds.bottom, paint);
		    	dateheight = (height+2);
		    }
		    // canvas
		    
		    //canvas.drawARGB(255, 0, 255, 0);// for visualization
		    
		    
		    return bm;
		}
		
		private Bitmap buildBGUpdate(int color){
			
			   
		    //Bitmap myBitmap = Bitmap.createBitmap(clocktextsize*2, clocktextsize+20, Bitmap.Config.ARGB_4444);
		    //Canvas myCanvas = new Canvas(myBitmap);
		    Paint paint = new Paint();
		    paint = new Paint();
			
		    
		    
		    new Rect();
		    Rect displayBounds = new Rect();
		    mDisplay.getRectSize(displayBounds);
		    int height;
			if(dateshown){
		    	height = clockheight + dateheight + 25;
		    }else{
		    	height = clockheight + 50;
		    }
		    Shader shader = null;
		    int aw = Color.argb(200, 255, 255, 255);
		    int ab = Color.argb(200, 0, 0, 0);
		    switch(Bg){
			case 0: 
				
				 shader = new LinearGradient(0, 0, 0, height,
				            new int[]{Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT},
				            new float[]{0,0.45f,.55f,1}, Shader.TileMode.REPEAT);
				break;
			case 1: 
				shader = new LinearGradient(0, 0, 0, height,
				            new int[]{aw, Color.TRANSPARENT, Color.TRANSPARENT, ab},
				            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				break;
			case 2: 
				 shader = new LinearGradient(0, 0, 0, height,
				            new int[]{aw, color, color, ab},
				            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				 break;
			case 3: 
				 shader = new LinearGradient(0, 0, 0, height,
				            new int[]{color, color, color, color},
				            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				break;
			}
		   
			paint.setShader(shader);
			
			
		 // min. rect of text
		    //Rect textBounds = new Rect();
		    //paint.getTextBounds(time, 0, time.length(), textBounds);
		    // create bitmap for text
            int w = 0;



            if(getScreenWidth() > getScreenHeight()){
                w=getScreenWidth();
            } else{
                w = getScreenHeight();
            }
            //Log.d("UWS", "W = "+ w);
            //Log.d("UWS", "getW = "+ getScreenWidth());
            //Log.d("UWS", "getH = "+ getScreenHeight());

		    Bitmap bm = Bitmap.createBitmap((int)(w*1.5f), height, Bitmap.Config.ARGB_8888);
		    
		    
		    // canvas
		    Canvas canvas = new Canvas(bm);
		    canvas.drawPaint(paint);
		    // for visualization
		    //canvas.drawPaint(paint);
		    //canvas.drawText(time, 5, textBounds.height()-textBounds.bottom, paint);

		    new BitmapDrawable(mContext.getResources(), bm);
		    
		    return bm;
		}
		
		/*
		private int getImage(int t){
	    	int img = 0;
	    	switch (t){
	    	case 0:
    			img = R.drawable.blank;
    			break;
	    	case 1:
    			img = R.drawable.clearbg;
    			break;
	    	case 2:
    			img = R.drawable.icsblue;
    			break;
	    	case 3:
    			img = R.drawable.stock;
    			break;
	    	
	    	}
	    	
	    	
			return img;
	    	
	    	
	    }
	    */
	    
	    private void setText(){
	    	Calendar cal = Calendar.getInstance();
			sminutes = Integer.toString(cal.get(Calendar.MINUTE));
			int hours = cal.get(Calendar.HOUR_OF_DAY);
			int minutes = cal.get(Calendar.MINUTE);

			sdate = DigiClockPrefs.getFormattedDate(dateFormatIndex);


			
			if(show24){
				ampm = ("");
				shours = Integer.toString(hours);
			}
			else{
				if (hours > 0 && hours < 12){
					ampm = ("AM");
					shours = Integer.toString(hours);
				}else{
					ampm = ("PM");
					shours = Integer.toString(hours-12);
				}
				
				if (hours == 12){
					ampm = ("PM");
					shours = "12";
				}
				
				if (hours == 0 || hours == 24){
					ampm = ("AM");
					shours = "12";
				}
				
				
			}
			
			if(minutes<10){
				sminutes = ("0" + Integer.toString(minutes));
			}else{
				sminutes = (Integer.toString(minutes));
			}
	    }

	public int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}
    public int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static void setClockButtonApp(final String packagename, int appWidgetId){
		//Log.d("SDDC", "Set Clock Button Application " +  " --> " + packagename );
	    		packageManager = mContext.getPackageManager();
				packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

                //Log.d("SDDC", "LOOKING FOR PACKAGE :" + packagename);
				for (ApplicationInfo packageInfo : packages) {
					//Log.d("UWS", "Installed package :" + packageInfo.packageName + " -- looking for: " + packagename);
					//Log.d("UWS", "Source dir : " + packageInfo.sourceDir);
					//Log.d("UWS", "Launch Activity :" + packageManager.getLaunchIntentForPackage(packageInfo.packageName));

					if(packageInfo.packageName.equals(packagename)){
                        //Log.d("SDDC", "Found " +  " --> " + packagename );
						Intent launchActivity = packageManager.getLaunchIntentForPackage(packageInfo.packageName);
                        //Log.d("SDDC", "LaunchActivity = " + launchActivity );
						//try {
							//ComponentName cn = new ComponentName(packageInfo.packageName, launchActivity);
							//packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
							alarmClockIntent = launchActivity;
							alarmClockIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
							PendingIntent pendingIntentC = PendingIntent.getActivity(mContext, 0, alarmClockIntent, 0);

							view.setOnClickPendingIntent(R.id.ClockButton, pendingIntentC);
							SharedPreferences prefs = mContext.getSharedPreferences(
									"prefs", 0);
							SharedPreferences.Editor edit = prefs.edit();
							edit.putString("ClockButtonApp"+appWidgetId, packagename);
							edit.commit();
							clockButtonApp = packagename;
							//Log.d("SDDC", "Found " +  " --> " + packagename + "/" + launchActivity);
                            //Log.d("SDDC", "Prefs clock app = " +  prefs.getString("ClockButtonApp", "NONE"));



							return;
						//} catch (NameNotFoundException e) {
						//	Log.d("SDDC", packageInfo.packageName + " does not exists -- " + e.getMessage());
						//}

					}
				}





	}

	public static void updateAllWidgets(final Context context,
										final int layoutResourceId,
										final Class< ? extends AppWidgetProvider> appWidgetClass)
	{
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutResourceId);

		AppWidgetManager manager = AppWidgetManager.getInstance(context);
		final int[] appWidgetIds = manager.getAppWidgetIds(new ComponentName(context, appWidgetClass));

		for (int i = 0; i < appWidgetIds.length; ++i)
		{
			manager.updateAppWidget(appWidgetIds[i], remoteViews);
		}
	}


	@Override
	    public IBinder onBind(Intent intent)  
	    {  
	        return null;  
	    }



	static void registerOneTimeAlarm(PendingIntent alarmIntent, long delayMillis, boolean triggerNow) {
		int SDK_INT = Build.VERSION.SDK_INT;
		long timeInMillis = (System.currentTimeMillis() + (triggerNow ? 0 : delayMillis));

		if (SDK_INT < Build.VERSION_CODES.KITKAT) {
			alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
		} else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
			alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
		} else if (SDK_INT >= Build.VERSION_CODES.M) {
			alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, alarmIntent);
		}
	}

	public static int dpToPx(float dp, Context context) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}

	private Bitmap overlay(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(bmp2, new Matrix(), null);
		return bmOverlay;
	}
}
