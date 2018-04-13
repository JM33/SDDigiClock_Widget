package com.sd.sddigiclock;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class UpdateWidgetService extends Service {
	private static final String LOG = "DC SRVC";
	private static Context mContext;
	private String ampm;
	private String shours;
	private String sminutes;
	private String sdate;
	private String month_name;
	
	private int clocktextsize;
	private int datetextsize;
	private int cColor;
	private int dColor;
	//private PackageManager packageManager;
	private Intent alarmClockIntent;
	
	private Intent prefsIntent;
	
	private int day;
	private int year;
	private int Bg;
	boolean ampmshown;
	boolean dateshown;
	
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private boolean fillbg;
	private PackageManager packageManager;
	private boolean show24;
	private int mFont;
	private String Fontfile;
	private int bgColor;
	private RemoteViews view;
	private WindowManager mWindowManager;
	private Display mDisplay;
	private int dateheight;
	private int clockheight;
	private String dateFormat;
	
	 @Override  
	    public void onCreate()  
	    {  
	        super.onCreate(); 
	        
	        mContext = this.getApplicationContext();
	        
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
	        Log.i(LOG, "Service onCreate");
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
		    //Log.i(LOG, "Service Started awId =" + Integer.toString(appWidgetId));
		}
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(
	            "prefs", 0);
		dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
		ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
		show24 = prefs.getBoolean("Show24"+appWidgetId, false);
		fillbg = true;
		clocktextsize = prefs.getInt("ClockTextSize"+appWidgetId, 15);
		datetextsize = prefs.getInt("DateTextSize"+appWidgetId, 12);
		
		cColor = prefs.getInt("cColor"+appWidgetId, -1);
		dColor = prefs.getInt("dColor"+appWidgetId, -1);
		bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);
		
		Bg = prefs.getInt("Bg"+appWidgetId, 3);
		Fontfile = prefs.getString("Font"+appWidgetId, "Roboto-Regular.ttf");
		mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
		dateFormat = prefs.getString("DateFormat", "MMMMDDYYYY");
		//getPrefs();
		setText();
		
		
		
	    view = new RemoteViews(getPackageName(), R.layout.widget_layout);  
	    
	    view.setImageViewBitmap(R.id.clockView, buildClockUpdate(shours + ":" + sminutes));
	    view.setImageViewBitmap(R.id.ampmView, buildAMPMUpdate(ampm));
		view.setImageViewBitmap(R.id.dateView, buildDateUpdate(sdate));
	    //view.setTextViewText(R.id.ClockText, (shours + ":" + sminutes));
		//view.setTextColor(R.id.ClockText, cColor);
		//view.setTextColor(R.id.AMPMText, cColor);
		//view.setTextColor(R.id.DateText, dColor);
		//view.setTextViewText(R.id.DateText, sdate);
		//view.setTextViewText(R.id.AMPMText, ampm);
		
		
		if(Bg == 0){
			//view.setInt(R.id.linearLayout2, "setBackgroundResource", getImage(Bg));
			view.setImageViewBitmap(R.id.BackGround, buildBGUpdate(bgColor));
		}
		
		else{
			if(Bg == 1){
				//view.setInt(R.id.linearLayout2, "setBackgroundResource", getImage(Bg));
				view.setImageViewBitmap(R.id.BackGround, buildBGUpdate(bgColor));
				
			}else{
				if(Bg == 2){
					
					//view.setInt(R.id.linearLayout2, "setBackgroundResource", getImage(0));
					view.setImageViewBitmap(R.id.BackGround, buildBGUpdate(bgColor));
				}
				else{
					if(Bg == 3){
					
						//view.setInt(R.id.linearLayout2, "setBackgroundResource", getImage(0));
						view.setImageViewBitmap(R.id.BackGround, buildBGUpdate(bgColor));
					}
				}
			}
		}
		
		
		
		//float ctsize = clocktextsize*1.5f + 6;
		//float dtsize = datetextsize;
		
		//view.setFloat(R.id.ClockText, "setTextSize", ctsize);
		//view.setFloat(R.id.DateText, "setTextSize", dtsize);
		
		if(dateshown){
			view.setViewVisibility(R.id.dateView, View.VISIBLE);
		}else{
			view.setViewVisibility(R.id.dateView, View.GONE);
		}
		if(ampmshown){
			view.setViewVisibility(R.id.ampmView, View.VISIBLE);
		}else{
			view.setViewVisibility(R.id.ampmView, View.GONE);
		}
		
	    
		
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
					
		PendingIntent pendingIntentD = PendingIntent.getActivity(mContext, 0, prefsIntent, 0);
	    view.setOnClickPendingIntent(R.id.dateView, pendingIntentD);
	    
	    
		String clockImpls[][] = {
		        {"HTC Alarm Clock", "com.htc.android.worldclock", "com.htc.android.worldclock.WorldClockTabControl" },
		        {"Standard Alarm Clock", "com.android.deskclock", "com.android.deskclock.DeskClock"},
		        {"Froyo Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclock.DeskClock"},
		        {"Moto Blur Alarm Clock", "com.motorola.blur.alarmclock",  "com.motorola.blur.alarmclock.AlarmClock"},
		        {"Samsung Galaxy Clock", "com.sec.android.app.clockpackage","com.sec.android.app.clockpackage.ClockPackage"},
		        //{"ICS Nexus Alarm Clock", "com.google.android.deskclock", "com.android.deskclockgoogle.DeskClockGoogle"}
		};
		boolean foundClockImpl = false;
	
		for(int i=0; i<clockImpls.length; i++) {
		    String vendor = clockImpls[i][0];
		    String packageName = clockImpls[i][1];
		    String className = clockImpls[i][2];
		    try {
		        ComponentName cn = new ComponentName(packageName, className);
		        packageManager.getActivityInfo(cn, PackageManager.GET_META_DATA);
		        alarmClockIntent.setComponent(cn);
		        Log.d("SDDC", "Found" +  vendor + " --> " + packageName + "/" + className);
		        foundClockImpl = true;
		    } catch (NameNotFoundException e) {
		        Log.d("SDDC", vendor + " does not exists");
		    }
		}
	
		if (foundClockImpl) {
		    PendingIntent pendingIntentC = PendingIntent.getActivity(mContext, 0, alarmClockIntent, 0);
		    view.setOnClickPendingIntent(R.id.clockView, pendingIntentC);
		}
		else{
			view.setOnClickPendingIntent(R.id.clockView, pendingIntent);
		}
		AppWidgetManager manager = AppWidgetManager.getInstance(this);  
		//ComponentName thisWidget = new ComponentName(this, DigiClockProvider.class);
	    
		manager.updateAppWidget(appWidgetId, view);
	    //manager.updateAppWidget(thisWidget, view);
	    
	    
	    
	    return super.onStartCommand(intent, flags, startId);
	}

		public Bitmap buildClockUpdate(String time){
			// font size
			   float fontSize = clocktextsize*5;
			  
		    Paint paint = new Paint();
			SharedPreferences prefs = getApplicationContext().getSharedPreferences(
					"prefs", 0);
			int mfont = prefs.getInt("mFont"+appWidgetId, 0);
			Typeface font;
		    font = Typeface.createFromAsset(this.getAssets(), Fontfile);

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
		    // create bitmap for text
		    Bitmap bm = Bitmap.createBitmap((textBounds.width()+30), textBounds.height()+2, Bitmap.Config.ARGB_8888);
		    // canvas
		    Canvas canvas = new Canvas(bm);
		    //canvas.drawARGB(255, 0, 255, 0);// for visualization
		    
		    canvas.drawText(time, 10, textBounds.height()-textBounds.bottom, paint);
		    clockheight = textBounds.height()+2;
		    return bm;
		}

		public Bitmap buildAMPMUpdate(String time){
			// font size
			   float fontSize = clocktextsize;
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
		    // create bitmap for text
		    
		    Bitmap bm = Bitmap.createBitmap((textBounds.width()+20), textBounds.height()+2, Bitmap.Config.ARGB_8888);
		    // canvas
		    Canvas canvas = new Canvas(bm);
		    //canvas.drawARGB(255, 0, 255, 0);// for visualization
		    
		    canvas.drawText(time, 10, textBounds.height()-textBounds.bottom, paint);

		    return bm;
		}
		
		public Bitmap buildDateUpdate(String time){
			// font size
			   float fontSize = datetextsize*3;
			   
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
		    Log.i("UWS", Float.toString(textBounds.width()));
		    int maxwidth = displayBounds.width()-100;
		    
		    Log.i("UWS", "Maxwidth =" + Integer.toString(maxwidth) + " Orientation = " + Integer.toString(mDisplay.getRotation()));
			if(textBounds.width()+20 >= maxwidth){
		    	time = (month_name + " " + String.valueOf(day) + ",");
		    	paint.getTextBounds(time, 0, time.length(), textBounds);
		    	bm = Bitmap.createBitmap(textBounds.width()+100, (textBounds.height()+2)*2, Bitmap.Config.ARGB_8888);
		    	Rect textBounds2 = new Rect();
			    paint.getTextBounds(String.valueOf(year), 0, String.valueOf(year).length(), textBounds2);
		    	
		    	Canvas canvas = new Canvas(bm);
		    	canvas.drawText(time, textBounds.width()/2+50, textBounds.height()-textBounds.bottom, paint);
		    	canvas.drawText(String.valueOf(year), textBounds.width()/2+50, textBounds.height()*2, paint);
		    	dateheight = (textBounds.height()+2)*2;
		    }else{
		    // create bitmap for text
		    	bm = Bitmap.createBitmap((textBounds.width()+100), (textBounds.height()+2), Bitmap.Config.ARGB_8888);
		    	Canvas canvas = new Canvas(bm);
		    	canvas.drawText(time, textBounds.width()/2+50, textBounds.height()-textBounds.bottom, paint);
		    	dateheight = (textBounds.height()+2);
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
		    	height = clockheight + dateheight + 100;
		    }else{
		    	height = clockheight + 100;
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
		    Bitmap bm = Bitmap.createBitmap(2000, height, Bitmap.Config.ARGB_8888);
		    
		    
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
			
			
			day = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			
			
			//SimpleDateFormat month_date = new SimpleDateFormat("MMMMM");
			//month_name = month_date.format(cal.getTime());
			DateFormat format = new DateFormat();
			month_name = (String) DateFormat.format("MMMM",  cal); // Jun
			Log.d("SDDC", "CurrentMonth: "+ month_name);

			
			
			sdate = (month_name + " " + String.valueOf(day) + ", " + String.valueOf(year));
			
			
			
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

		@Override  
	    public IBinder onBind(Intent intent)  
	    {  
	        return null;  
	    }
}
