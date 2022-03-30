package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

import androidx.core.content.res.ResourcesCompat;

public class WidgetImage {
    private final static String TAG = "WidgetImage";
    private final static int DARK_MODE_AUTO = 0;
    private final static int DARK_MODE_LIGHT = 1;
    private final static int DARK_MODE_DARK = 2;
    private static Context mContext;
    private static int appWidgetId;

    private static WindowManager mWindowManager;
    private static Display mDisplay;

    private static boolean dateshown;
    private static boolean ampmshown;
    private static boolean show24;
    private static boolean fillbg;
    private static int clocktextsize;
    private static int datetextsize;
    private static boolean dateMatchClockColor;
    private static int dateFormatIndex;
    private static int cColor;
    private static int dColor;
    private static int bgColor;
    private static int Bg;
    private static String Fontfile;
    private static int mFont;
    private static boolean useHomeColors;
    private static String clockapp;
    private static boolean usehomecolors;

    private static String sminutes;
    private static String shours;
    private static String sdate;
    private static String ampm;

    private static boolean mIsPortraitOrientation;
    public static int mWidgetWidthPerOrientation;
    public static int mWidgetHeightPerOrientation;
    private static int clockheight;
    private static int dateheight;
    //private static float backgroundRadius = 150;
    private static int cornerRadius;
    private static boolean stackClock;
    private static ArrayList<String> fontsList;
    private static int darkMode;

    private static boolean isPreview = false;

    public static Bitmap buildClockImage(Context context, int widgetId){
        mContext = context;
        appWidgetId = widgetId;


        mWindowManager =  (WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        getWidgetPrefs(mContext, appWidgetId);
        setWidgetText();
        String time = shours + ":" + sminutes;

        Bitmap updateBitmap = buildClockUpdate(time, ampm, sdate, bgColor);
        if(updateBitmap != null) {
            float newWidth = UpdateWidgetView.dpToPx(mWidgetWidthPerOrientation, mContext);
            float newHeight = UpdateWidgetView.dpToPx(mWidgetHeightPerOrientation, mContext);
            float ratioW = updateBitmap.getWidth();
            float ratioH = updateBitmap.getHeight();
            float ratio = ratioH / ratioW;
            //Log.d("UpdateWidgetService", "uBht, newHt, ratio = " + updateBitmap.getWidth() + ", " + newHeight + ", " +ratio);
            updateBitmap = Bitmap.createScaledBitmap(updateBitmap, (int) newWidth, (int) (newWidth * ratio), false);
        }
        return updateBitmap;
    }

    public static Bitmap buildPreviewImage(Context context, int widgetId){
        isPreview = true;
        Bitmap previewBitmap = buildClockImage(context, widgetId);

        float radius = UpdateWidgetView.dpToPx(20, context);

        if(previewBitmap != null) {
            Canvas canvas = new Canvas(previewBitmap);
            Paint cellPaint = new Paint();
            cellPaint.setStyle(Paint.Style.STROKE);
            float strokeWidth = UpdateWidgetView.dpToPx(5, context);
            float halfstroke = strokeWidth * 0.5f;
            cellPaint.setPathEffect(new DashPathEffect(new float[] {50f,30f}, 0f));
            cellPaint.setStrokeWidth(strokeWidth);
            cellPaint.setColor(mContext.getResources().getColor(R.color.primaryColor, mContext.getTheme()));
            int w = UpdateWidgetView.dpToPx(mWidgetWidthPerOrientation, mContext);
            int h = UpdateWidgetView.dpToPx(mWidgetHeightPerOrientation, mContext);

            //canvas.drawRoundRect(halfstroke, halfstroke, w+strokeWidth, h+strokeWidth, radius, radius, cellPaint);


            //new BitmapDrawable(mContext.getResources(), previewBitmap);
            //if(h > previewBitmap.getHeight()){
                int newH = Math.max(previewBitmap.getHeight(), h);
                Bitmap bm = Bitmap.createBitmap((int)(w + strokeWidth),(int)(newH + strokeWidth), Bitmap.Config.ARGB_8888);

                Canvas newCanvas = new Canvas(bm);
                newCanvas.drawBitmap(previewBitmap, halfstroke,halfstroke, new Paint());
                newCanvas.drawRoundRect(halfstroke, halfstroke, w + halfstroke, h + halfstroke, radius, radius, cellPaint);

                new BitmapDrawable(mContext.getResources(), bm);
                previewBitmap = bm;
            //}
        }

        isPreview = false;


        return previewBitmap;
    }

    private static void getWidgetPrefs(Context mContext, int appWidgetId){
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);

        dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
        ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
        show24 = prefs.getBoolean("Show24"+appWidgetId, false);
        stackClock = prefs.getBoolean("StackClock"+appWidgetId, false);
        useHomeColors = prefs.getBoolean("UseHomeColors"+appWidgetId, false);

        clocktextsize = prefs.getInt("ClockTextSize"+appWidgetId, 15);
        datetextsize = prefs.getInt("DateTextSize"+appWidgetId, 12);
        dateFormatIndex = prefs.getInt("DateFormat" +appWidgetId, 2);

        cColor = prefs.getInt("cColor"+appWidgetId, -1);
        dColor = prefs.getInt("dColor"+appWidgetId, -1);
        dateMatchClockColor = prefs.getBoolean("DateMatchClockColor"+appWidgetId, true);
        bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);

        Bg = prefs.getInt("Bg"+appWidgetId, 3);
        Fontfile = prefs.getString("Font"+appWidgetId, mContext.getResources().getString(R.string.system_font));
        mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
        clockapp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
        cornerRadius = prefs.getInt("CornerRadius"+appWidgetId, 25);
        darkMode = prefs.getInt("DarkMode"+appWidgetId, 0);
    }

    private static void setWidgetText(){
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

        if(stackClock){
            if(hours<10){
                shours = ("0" + Integer.toString(hours));
            }
        }

        if(minutes<10){
            sminutes = ("0" + Integer.toString(minutes));
        }else{
            sminutes = (Integer.toString(minutes));
        }
    }

    public static Bitmap buildClockUpdate(String time, String ampm, String date, int  color){

	 	/* Get Device and Widget orientation.
           This is done by adding a boolean value to
           a port resource directory like values-port/bools.xml */

        boolean mIsKeyguard;

        if(useHomeColors){
            if (Build.VERSION.SDK_INT >= 31) {
                switch(darkMode) {
                    case DARK_MODE_AUTO:
                        switch (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                            case Configuration.UI_MODE_NIGHT_YES:
                                color = mContext.getResources().getColor(R.color.neutral_1_800, mContext.getTheme());
                                break;
                            case Configuration.UI_MODE_NIGHT_NO:
                                color = mContext.getResources().getColor(R.color.accent_1_50, mContext.getTheme());
                                break;
                        }
                        break;
                    case DARK_MODE_LIGHT:
                        color = mContext.getResources().getColor(R.color.accent_1_50, mContext.getTheme());
                        break;
                    case DARK_MODE_DARK:
                        color = mContext.getResources().getColor(R.color.neutral_1_800, mContext.getTheme());
                        break;
                }
            }
        }

        if(getScreenHeight() > getScreenWidth()) {
            mIsPortraitOrientation = true;
        }else{
            mIsPortraitOrientation = false;
        }

        // Get min dimensions from provider info
        try {
            AppWidgetProviderInfo providerInfo = AppWidgetManager.getInstance(
                    mContext).getAppWidgetInfo(appWidgetId);


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
            AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(mContext);
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
            mWidgetWidthPerOrientation = mWidgetPortWidth;
            mWidgetHeightPerOrientation = mWidgetPortHeight;

            if (!mIsPortraitOrientation)
            {
                // Not Portrait, so use landscape sizes
                mWidgetWidthPerOrientation = mWidgetLandWidth;
                mWidgetHeightPerOrientation = mWidgetLandHeight;
            }

            //Log.i("UWS", "w,h = "+ mWidgetWidthPerOrientation + ", " + mWidgetHeightPerOrientation);


            // font size
            float fontSize = clocktextsize*12;

            Paint Clockpaint = new Paint();
            SharedPreferences prefs = mContext.getSharedPreferences(
                    "prefs", 0);
            int mfont = prefs.getInt("mFont"+appWidgetId, 0);
            Typeface font = Typeface.DEFAULT;
            if (mFont != 0) {
                //font = Typeface.createFromAsset(mContext.getAssets(), Fontfile);
                font = getFont();
            }


            Clockpaint.setAntiAlias(true);
            Clockpaint.setSubpixelText(true);
            if(mFont == 0)
                Clockpaint.setTypeface(Typeface.DEFAULT);
            else
                Clockpaint.setTypeface(font);
            Clockpaint.setStyle(Paint.Style.FILL);

            if(useHomeColors){
                //if (Build.VERSION.SDK_INT >= 31) {
                switch(darkMode) {
                    case DARK_MODE_AUTO:
                        switch (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                            case Configuration.UI_MODE_NIGHT_YES:
                                cColor = mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme());
                                break;
                            case Configuration.UI_MODE_NIGHT_NO:
                                cColor = mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme());
                                break;
                        }
                        break;
                    case DARK_MODE_LIGHT:
                        cColor = mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme());
                        break;
                    case DARK_MODE_DARK:
                        cColor = mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme());
                        break;
                }

                //}
            }

            Clockpaint.setColor(cColor);

            Clockpaint.setTextSize((int)fontSize);
            Clockpaint.setShadowLayer(3f, 2f, 2f, Color.BLACK);
            Clockpaint.setTextAlign(Paint.Align.LEFT);

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
            if(stackClock){
                clockheight = (int)(clockheight*2);
            }

            /////AMPM UPDATE

            // font size
            fontSize = clocktextsize*2f;
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
            AMPMpaint.setTextAlign(Paint.Align.LEFT);


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
            font = Typeface.DEFAULT;
            if(mFont != 0) {
                //font = Typeface.createFromAsset(mContext.getAssets(), Fontfile);
                font = getFont();
            }

            TextPaint Datepaint= new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
            Datepaint.setSubpixelText(true);
            if(mFont == 0)
                Datepaint.setTypeface(Typeface.DEFAULT);
            else
                Datepaint.setTypeface(font);
            Datepaint.setStyle(Paint.Style.FILL);
            if(dateMatchClockColor || useHomeColors){
                Datepaint.setColor(cColor);
            }else{
                Datepaint.setColor(dColor);
            }
            //Datepaint.setColor(dColor);
            Datepaint.setTextSize((int)fontSize);
            Datepaint.setShadowLayer(3f, 2f, 2f, Color.BLACK);
            Datepaint.setTextAlign(Paint.Align.CENTER);


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


            float scale = mContext.getResources().getDisplayMetrics().density;
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
            float bgwidth = mWidgetWidthPerOrientation * 1.5f;
            int orientation = mContext.getResources().getConfiguration().orientation;
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                bgwidth = mWidgetWidthPerOrientation * 2f;
            }

            int widthPX = UpdateWidgetView.dpToPx(bgwidth, mContext);
            //Bitmap bm = Bitmap.createBitmap((int)(getScreenWidth()*1.5f), (int)height, Bitmap.Config.ARGB_8888);
            Bitmap bm = Bitmap.createBitmap(widthPX, (int)height, Bitmap.Config.ARGB_8888);

            // canvas
            Canvas canvas = new Canvas(bm);
            //canvas.drawPaint(BGpaint); //see rounded rectangle below

            RectF rect = new RectF(0.0f, 0.0f, widthPX, height);

            // rect contains the bounds of the shape
            // radius is the radius in pixels of the rounded corners
            // paint contains the shader that will texture the shape
            float newRadius = cornerRadius*4;
            canvas.drawRoundRect(rect, newRadius, newRadius, BGpaint);
            //Log.i("UpdateWidgetService", "Draw background");

            int dateOffset = 0;
            if(!dateshown){
                dateOffset = (int)((height * 0.1f));
                if(stackClock){
                    dateOffset = (int)((height * 0.1f));
                }
            }
            String hrs = "";
            String mins = "";
            if(stackClock) {
                String[] splitTime = time.split(":");
                hrs = splitTime[0];
                mins = splitTime[1];
                int hoursI;
                try {
                    hoursI = Integer.parseInt(hrs.toString());
                    if (hoursI < 10 && !hrs.startsWith("0")) {
                        hrs = "0" + hrs.toString();
                    }
                } catch (Error e) {
                    Log.i("WidgetImage", e.getMessage());
                }
            }

            if(stackClock){
                canvas.drawText(hrs, canvas.getWidth() * 0.5f - (Clockpaint.measureText(hrs) * 0.5f), textBoundsClock.height() - textBoundsClock.bottom + (height * 0.1f) + dateOffset, Clockpaint);
                canvas.drawText(mins, canvas.getWidth() * 0.5f - (Clockpaint.measureText(mins) * 0.5f), textBoundsClock.height() - textBoundsClock.bottom + (height * 0.1f) + (textBoundsClock.height() + textBoundsClock.height() * 0.1f) + dateOffset, Clockpaint);
            }else{
                canvas.drawText(time, canvas.getWidth() * 0.5f - (Clockpaint.measureText(time) * 0.5f), textBoundsClock.height() - textBoundsClock.bottom + (height * 0.1f) + dateOffset, Clockpaint);
            }

            if(ampmshown) {
                if (stackClock) {
                    float clockW = Clockpaint.measureText(hrs);
                    if(Clockpaint.measureText(mins) > Clockpaint.measureText(hrs)){
                        clockW = Clockpaint.measureText(mins);
                    }
                    canvas.drawText(ampm, canvas.getWidth() * 0.5f + (clockW * 0.5f) + 20, (textBoundsClock.height()) + textBoundsAMPM.height() - textBoundsAMPM.bottom + (height * 0.1f), AMPMpaint);
                } else {
                    canvas.drawText(ampm, canvas.getWidth() * 0.5f + (Clockpaint.measureText(time) * 0.5f) + 20, (textBoundsClock.height() * 0.5f) + textBoundsAMPM.height() - textBoundsAMPM.bottom + (height * 0.1f), AMPMpaint);
                }
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

        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    private static int getScreenWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }
    private static int getScreenHeight() {
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    private static Typeface getFont(){
        Typeface font = Typeface.DEFAULT;
        fontsList = new ArrayList<String>();
        fontsList.add(mContext.getResources().getString(R.string.system_font));
        boolean hasfonts = listAssetFiles("");
        //Log.d(TAG, "mFont= " + mFont + " fontslist size= "+fontsList.size());
        if(hasfonts){
            if(mFont != 0 && mFont < fontsList.size()) { //add 1 to account for 0 as system font
                font = Typeface.createFromAsset(mContext.getAssets(), fontsList.get(mFont));
            }
        }

        if(mFont >= fontsList.size()){ //add 1 to account for 0 as system font
            Field[] fontFields = R.font.class.getFields();
            ArrayList<Integer> fonts = new ArrayList<>();
            ArrayList<String> fontNames = new ArrayList<>();

            for (Field field : fontFields) {
                try {
                    //Log.i(TAG, field.getName());
                    fonts.add(field.getInt(null));
                    fontNames.add(field.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for(String fontName:fontNames){
                if(fontName.equals(Fontfile)){
                    font = ResourcesCompat.getFont(mContext, fonts.get(fontNames.indexOf(fontName)));
                }
            }
        }

        return font;
    }

    private static boolean listAssetFiles(String path) {

        String [] list;
        try {
            list = mContext.getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for (String file : list) {
                    if (!listAssetFiles(path + "/" + file))
                        return false;
                    else {
                        // This is a file
                        if(file.toLowerCase().endsWith("ttf")){
                            //Log.i("FontSettings", "Adding to font list: "+file);
                            fontsList.add(file);
                        }
                    }
                }
            }
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static int[] widgetSize(Context context, int widgetId){
        int w,h;
        w=h=0;
        mContext = context;
        appWidgetId = widgetId;


        mIsPortraitOrientation = getScreenHeight() > getScreenWidth();

        // Get min dimensions from provider info
        AppWidgetProviderInfo providerInfo = AppWidgetManager.getInstance(
                mContext).getAppWidgetInfo(appWidgetId);

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
        AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        Bundle mAppWidgetOptions = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mAppWidgetOptions = mAppWidgetManager.getAppWidgetOptions(appWidgetId);

        boolean mIsKeyguard;
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
        mWidgetWidthPerOrientation = mWidgetPortWidth;
        mWidgetHeightPerOrientation = mWidgetPortHeight;

        if (!mIsPortraitOrientation)
        {
            // Not Portrait, so use landscape sizes
            mWidgetWidthPerOrientation = mWidgetLandWidth;
            mWidgetHeightPerOrientation = mWidgetLandHeight;
        }

        w= WidgetImage.mWidgetWidthPerOrientation;
        h= mWidgetHeightPerOrientation;

        int[] sizes = new int[2];
        sizes[0] = w;
        sizes[1] = h;
        return sizes;
    }
}
