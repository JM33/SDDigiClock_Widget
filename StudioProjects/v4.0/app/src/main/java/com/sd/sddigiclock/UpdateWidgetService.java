package com.sd.sddigiclock;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
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
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.text.format.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
	private boolean dateMatchClockColor;
	//private PackageManager packageManager;
	private Intent alarmClockIntent;
	
	private Intent prefsIntent;
	
	private int day;
	private int year;
	private int month;
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
	private int dateFormatIndex;

	//private String dateFormat;

	boolean mIsPortraitOrientation;
	
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
		dateMatchClockColor = prefs.getBoolean("DateMatchClockColor"+appWidgetId, true);
		dateFormatIndex = prefs.getInt("DateFormat" +appWidgetId, 2);

		cColor = prefs.getInt("cColor"+appWidgetId, -1);
		dColor = prefs.getInt("dColor"+appWidgetId, -1);
		bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);
		
		Bg = prefs.getInt("Bg"+appWidgetId, 3);
		Fontfile = prefs.getString("Font"+appWidgetId, "Roboto-Regular.ttf");
		mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
		//dateFormat = prefs.getString("DateFormat", "MMMMDDYYYY");
		//getPrefs();
		setText();
		
		
		
	    view = new RemoteViews(getPackageName(), R.layout.widget_layout);  
	    
	    view.setImageViewBitmap(R.id.BackGround, buildClockUpdate(shours + ":" + sminutes, ampm, sdate, bgColor));

	    //view.setImageViewBitmap(R.id.clockView, buildClockUpdate(shours + ":" + sminutes);
	    //view.setImageViewBitmap(R.id.ampmView, buildAMPMUpdate(ampm));
		//view.setImageViewBitmap(R.id.dateView, buildDateUpdate(sdate));


		//view.setTextViewText(R.id.ClockText, (shours + ":" + sminutes));
		//view.setTextColor(R.id.ClockText, cColor);
		//view.setTextColor(R.id.AMPMText, cColor);
		//view.setTextColor(R.id.DateText, dColor);
		//view.setTextViewText(R.id.DateText, sdate);
		//view.setTextViewText(R.id.AMPMText, ampm);
		
		/*
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
		
		*/
		
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

		//DATE INTENT on click date
		//PendingIntent pendingIntentD = PendingIntent.getActivity(mContext, 0, prefsIntent, 0);
	    //view.setOnClickPendingIntent(R.id.dateView, pendingIntentD);
	    
	    
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
		    view.setOnClickPendingIntent(R.id.BackGround, pendingIntentC);
		}
		else{
			view.setOnClickPendingIntent(R.id.BackGround, pendingIntent);
		}
		AppWidgetManager manager = AppWidgetManager.getInstance(this);  
		//ComponentName thisWidget = new ComponentName(this, DigiClockProvider.class);
	    
		manager.updateAppWidget(appWidgetId, view);
	    //manager.updateAppWidget(thisWidget, view);
	    
	    
	    
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

					Log.d("UWS",
							"appWidgetOptions not null, getting widget sizes...");
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
					Log.d("UWS",
							"No AppWidgetOptions for this widget, using minimal dimensions from provider info!");
				// For some reason I had to set this again here, may be obsolete
				mWidgetLandWidth = providerInfo.minWidth;
				mWidgetPortHeight = providerInfo.minHeight;
				mWidgetPortWidth = providerInfo.minWidth;
				mWidgetLandHeight = providerInfo.minHeight;
			}

			Log.d("UWS", "Dimensions of the Widget in DIP: portWidth =  "
						+ mWidgetPortWidth + ", landWidth = " + mWidgetLandWidth
						+ "; landHeight = " + mWidgetLandHeight
						+ ", portHeight = " + mWidgetPortHeight);

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
			   float fontSize = clocktextsize*10;
			  
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
		    Log.d("UWS", "CLOCK UPDATE");
			Log.d("UWS", "FontSize = " + fontSize);
			Log.d("UWS", "Height = " + height);
			Log.d("UWS", "Width-MeasureText = " + Clockpaint.measureText(time));
			Log.d("UWS", "TextBounds Top= " + textBoundsClock.top);
			Log.d("UWS", "TextBounds Bottom = " + textBoundsClock.bottom);
			Log.d("UWS", "TextBounds Height= " + textBoundsClock.height());

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
			fontSize = datetextsize*6;

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



			Bitmap bm = Bitmap.createBitmap((int)(getScreenWidth()*1.5f), (int)height, Bitmap.Config.ARGB_8888);


			// canvas
			Canvas canvas = new Canvas(bm);
			canvas.drawPaint(BGpaint);

			canvas.drawText(time, canvas.getWidth()*0.5f - (Clockpaint.measureText(time)*0.5f), textBoundsClock.height()-textBoundsClock.bottom+(height*0.1f), Clockpaint);
			if(ampmshown) {
				canvas.drawText(ampm, canvas.getWidth() * 0.5f + (Clockpaint.measureText(time) * 0.5f) + 20, (textBoundsClock.height() * 0.5f) + textBoundsAMPM.height() - textBoundsAMPM.bottom+(height*0.1f), AMPMpaint);
			}


			if(dateshown) {
				// draw text to the Canvas center
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

		    Log.i("UWS", Float.toString(textBounds.width()));
		    int maxwidth = displayBounds.width()-100;
		    
		    Log.i("UWS", "Maxwidth =" + Integer.toString(maxwidth) + " Orientation = " + Integer.toString(mDisplay.getRotation()));
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
            Log.d("UWS", "W = "+ w);
            Log.d("UWS", "getW = "+ getScreenWidth());
            Log.d("UWS", "getH = "+ getScreenHeight());

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

			SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
			String weekDay = dayFormat.format(cal.getTime());

			day = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			
			
			//SimpleDateFormat month_date = new SimpleDateFormat("MMMMM");
			//month_name = month_date.format(cal.getTime());
			DateFormat dateformat = new DateFormat();
			month_name = (String) DateFormat.format("M",  cal); // Jun
			//Log.d("SDDC", "CurrentMonth: "+ month_name);

			SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.US);
			String year_name = yearFormat.format(cal.getTime());
			
			switch(dateFormatIndex) {
				case 0: //0 Tue January 23, 2018
					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMMM",  cal);

					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());

					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
					break;
				case 1:  //1        Tue Jan 23, 2018
					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMM",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
					break;
				case 2:  //2       Tue 1-23-2018

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
					break;
				case 3:  //3       Tue 1/23/2018

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
					break;
				case 4:  //4       Tuesday January 23, 2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMMM",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
					break;
				case 5:  //5		Tuesday Jan 23, 2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMM",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
					break;
				case 6:  //6		Tuesday 1-23-2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
					break;
				case 7:  //7		Tuesday 1/23/2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
					break;
				case 8:  //8		January 23, 2018

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMMM",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
					break;
				case 9:  //9		Jan 23, 2018

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMM",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
					break;
				case 10:  //10		1-23-2018

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
					break;
				case 11:  //11		1/23/2018

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
					break;
				case 12:  //12		1-23-18

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
					break;
				case 13:  //13		1/23/18
//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
					break;
				case 14:  //14		January 23

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMMM",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
					break;
				case 15:  //15		1-23

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "" + year_name);
					break;
				case 16:  //16		1/23

					//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
					break;
				case 17:  //17		Tue January 23

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMMM",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
					break;
				case 18:  //18		Tue Jan 23

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMM",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
					break;
				case 19:  //19		Tue 1-23

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "" + year_name);
					break;
				case 20:  //20		Tue 1/23

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
					break;
				case 21:  //21		Tuesday Jan 23

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("MMM",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
					break;
				case 22:  //22		Tuesday 1/23

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
					break;
				case 23:  //23		Tue 23-1-2018

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "-" + year_name);
					break;
				case 24:  //24		Tue 23/1/2018

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "/" + year_name);
					break;
				case 25:  //25		Tuesday 23-1-2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "-" + year_name);
					break;
				case 26:  //26		Tuesday 23/1/2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "/" + year_name);
					break;
				case 27:  //27		23-1-2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "-" + year_name);
					break;
				case 28:  //28		23/1/2018

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "/" + year_name);
					break;
				case 29:  //29		23-1-18

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "-" + year_name);
					break;
				case 30:  //30		23/1/18

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "/" + year_name);
					break;
				case 31:  //31		23-1

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "" + year_name);
					break;
				case 32:  //32		23/1

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = "";

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "" + year_name);
					break;
				case 33:  //33		Tue 23-1

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "" + year_name);
					break;
				case 34:  //34		Tue 23/1

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "" + year_name);
					break;
				case 35:  //35		Tuesday 23-1

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "" + year_name);
					break;
				case 36:  //36		Tuesday 23/1

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = "";
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "" + year_name);
					break;
				case 37:  //37		Tue 1-23-18

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + month_name + "-" + String.valueOf(day) + "-" + year_name);
					break;
				case 38:  //38		Tue 1/23/18

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + month_name + "/" + String.valueOf(day) + "/" + year_name);
					break;
				case 39:  //39		Tuesday 1-23-18

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + month_name + "-" + String.valueOf(day) + "-" + year_name);
					break;
				case 40:  //40		Tuesday 1/23/18

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + month_name + "/" + String.valueOf(day) + "/" + year_name);
					break;
				case 41:  //41		Tue 23-1-18

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "-" + year_name);
					break;
				case 42:  //42		Tue 23/1/18

					dayFormat = new SimpleDateFormat("E", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "/" + year_name);
					break;
				case 43:  //43		Tuesday 23-1-18

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "-" + month_name + "-" + year_name);
					break;
				case 44:  //44		Tuesday 23/1/18

					dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
					weekDay = dayFormat.format(cal.getTime());

					day = cal.get(Calendar.DAY_OF_MONTH);
					year = cal.get(Calendar.YEAR);

					dateformat = new DateFormat();
					month_name = (String) DateFormat.format("M",  cal);


					yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
					year_name = yearFormat.format(cal.getTime());
					sdate = (weekDay + " "  + String.valueOf(day) + "/" + month_name + "/" + year_name);
					break;
					/*

					 */
			}

			
			
			
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

	@Override
	    public IBinder onBind(Intent intent)  
	    {  
	        return null;  
	    }


}
