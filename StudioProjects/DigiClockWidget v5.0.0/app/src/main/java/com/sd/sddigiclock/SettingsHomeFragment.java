package com.sd.sddigiclock;

import android.app.ActivityManager;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsHomeFragment extends Fragment {
    private final String TAG = "SettingsHome";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private View mView;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public static SeekBar homeClockSeekBar;
    public static SeekBar homeDateSeekBar;
    private SeekBar homeCornerSeekBar;
    public static boolean isBGServiceRunning;
    private LinearLayout serviceRunningLinearLayout;
    private SwitchMaterial serviceRunningButton;
    private TextView serviceRunningSummary;
    private ImageView previewImage;

    private LinearLayout useHomeColorsLayout;
    private SwitchMaterial btusehomecolors;

    private LinearLayout darkThemeLayout;
    private TextView btdarktheme;

    //private TextView previewTitleText;

    private boolean dateshown;
    private boolean ampmshown;
    private boolean show24;
    private boolean fillbg;
    private int clocktextsize;
    private int datetextsize;
    private boolean dateMatchClockColor;
    private int dateFormatIndex;
    private int cColor;
    private int dColor;
    private int bgColor;
    private int Bg;
    private String Fontfile;
    private int mFont;
    private boolean useHomeColors;
    private WindowManager mWindowManager;
    private boolean mIsPortraitOrientation;
    private int mWidgetWidthPerOrientation;
    private int mWidgetHeightPerOrientation;
    private int clockheight;
    private int dateheight;
    private Display mDisplay;
    private String shours;
    private String sminutes;
    private String sdate;
    private String ampm;
    private int cornerRadius;
    private int darkMode;

    private LinearLayout linearLayoutSettingsHome;
    private LinearLayout previewBG;
    private Dialog darkDialog;

    public SettingsHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsHomeFragment newInstance(String param1, String param2) {
        SettingsHomeFragment fragment = new SettingsHomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = container.getContext();

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appWidgetId = bundle.getInt("appWidgetID", AppWidgetManager.INVALID_APPWIDGET_ID);
            //Log.i(TAG, "WIDGET ID = "+appWidgetId);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.i(TAG, "INVALID WIDGET ID = "+appWidgetId);
            return null;
        }
        Log.i(TAG, "HAS VALID WIDGET ID = "+appWidgetId);

        mView = inflater.inflate(R.layout.fragment_settings_home, container, false);

        mWindowManager =  (WindowManager) getActivity().getSystemService(mContext.WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();



        if(darkDialog !=null)
            darkDialog.dismiss();

        LoadPrefs();

        setButtons();

        return mView;
    }



    private void LoadPrefs() {
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);

        //classicMode = prefs.getBoolean("ClassicMode"+appWidgetId, true);
        dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
        ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
        //classicMode = ampmshown;
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
        useHomeColors = prefs.getBoolean("UseHomeColors"+appWidgetId, false);
        cornerRadius = prefs.getInt("CornerRadius"+appWidgetId, 25);
        darkMode = prefs.getInt("DarkMode"+appWidgetId, 0);
    }

    private void setButtons() {
        linearLayoutSettingsHome = (LinearLayout)mView.findViewById(R.id.LinearLayoutSettingsHome);
        //previewTitleText = (TextView)mView.findViewById(R.id.textViewPreview);
        //String previewText = getResources().getString(R.string.p_preview_title) +" : " +appWidgetId;
        //previewTitleText.setText(previewText);

        homeClockSeekBar = (SeekBar)mView.findViewById(R.id.HomeClockSizeSB);
        homeDateSeekBar = (SeekBar)mView.findViewById(R.id.HomeDateSizeSB);
        homeCornerSeekBar = (SeekBar)mView.findViewById(R.id.HomeCornerRadiusSB);
        previewBG = (LinearLayout) mView.findViewById(R.id.LinearLayoutPreviewImageBG);
        previewImage = (ImageView) mView.findViewById(R.id.WidgetPreview);
        serviceRunningLinearLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutBGService);
        serviceRunningButton = (SwitchMaterial) mView.findViewById(R.id.buttonStartBGService);
        serviceRunningSummary = (TextView)mView.findViewById(R.id.textViewSummaryServiceRunning);
        useHomeColorsLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutUseHomeColors);
        btusehomecolors = (SwitchMaterial)mView.findViewById(R.id.UseHomeColors);
        darkThemeLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutDarkTheme);
        btdarktheme = (TextView)mView.findViewById(R.id.buttonDarkTheme);

        if(isMyServiceRunning(WidgetBackgroundService.class)){
            serviceRunningSummary.setText(R.string.p_bg_service_running_summary);
            serviceRunningButton.setChecked(true);
        }else{
            serviceRunningSummary.setText(R.string.p_bg_service_stopped_summary);
            serviceRunningButton.setChecked(false);
        }

        setServiceRunningListener(serviceRunningButton);
        setServiceRunningListener(serviceRunningLinearLayout);

        homeClockSeekBar.setProgress(clocktextsize);
        homeClockSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                clocktextsize = progress;
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("ClockTextSize" + appWidgetId, clocktextsize);
                edit.commit();
                refreshPreviewImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        homeDateSeekBar.setProgress(datetextsize);
        homeDateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                datetextsize = progress;
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("DateTextSize" + appWidgetId, datetextsize);
                edit.commit();
                refreshPreviewImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        homeCornerSeekBar.setProgress(cornerRadius);
        homeCornerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cornerRadius = progress;
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("CornerRadius" + appWidgetId, cornerRadius);
                edit.commit();
                refreshPreviewImage();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        if (Build.VERSION.SDK_INT >= 31) {
            btusehomecolors.setChecked(useHomeColors);
            setHomeColorsOnClickListener(btusehomecolors);
            setHomeColorsOnClickListener(useHomeColorsLayout);

            setDarkThemeOnClickListener(darkThemeLayout);
            setDarkThemeOnClickListener(btdarktheme);

            if(!useHomeColors){
                btdarktheme.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                TextView darksum = (TextView) mView.findViewById(R.id.textViewSummaryDarkTheme);
                darksum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                darksum.setText(getResources().getString(R.string.p_dark_theme_disabled));
            }else{
                // Get the primary text color of the theme
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getActivity().getTheme();
                theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
                TypedArray arr =
                        getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                                android.R.attr.textColorPrimary});
                int primaryColor = arr.getColor(0, -1);
                arr.recycle();

                btdarktheme.setTextColor(primaryColor);
                TextView darksum = (TextView) mView.findViewById(R.id.textViewSummaryDarkTheme);
                darksum.setTextColor(primaryColor);
                darksum.setText(getResources().getString(R.string.p_dark_theme_summary));
            }
        }else{
            useHomeColorsLayout.setVisibility(View.GONE);
            darkThemeLayout.setVisibility(View.GONE);
            useHomeColors = false;
            SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("UseHomeColors"+appWidgetId, useHomeColors);
            edit.commit();


        }

        refreshPreviewImage();
    }

    private void refreshPreviewImage(){

        //Log.i(TAG, "Refresh appwidgetID= " +appWidgetId);
        previewImage.setImageBitmap(WidgetImage.buildPreviewImage(mContext, appWidgetId));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, previewImage.getDrawable().getIntrinsicHeight()+20);
        previewBG.setLayoutParams(layoutParams);
        layoutView(previewBG);

        linearLayoutSettingsHome.invalidate();
        //Log.i(TAG, "previewBG H = " + previewBG.getHeight());
        //Log.i(TAG, "previewWidget H = " + previewImage.getHeight());
    }

    void layoutView(View view) {
        view.setDrawingCacheEnabled(true);
        int wrapContentSpec =
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.measure(wrapContentSpec, wrapContentSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

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

    private void setServiceRunningListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("HomeSettings", "View clicked = "+view.getId());
                if(isMyServiceRunning(WidgetBackgroundService.class)){
                    //stop service
                    serviceRunningSummary.setText(R.string.p_bg_service_stopped_summary);
                    serviceRunningButton.setChecked(false);
                    DigiClockPrefs.DCP.stopService(new Intent(DigiClockPrefs.DCP,
                            WidgetBackgroundService.class));
                    //Toast.makeText(mView.getApplicationContext(), R.string.p_toast_bg_service_stopped, Toast.LENGTH_LONG).show();
                }else {
                    //run service
                    serviceRunningSummary.setText(R.string.p_bg_service_running_summary);
                    serviceRunningButton.setChecked(true);
                    Intent serviceBG = new Intent(getActivity().getApplicationContext(), WidgetBackgroundService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        getActivity().startForegroundService(serviceBG);
                        Log.d("DigiClockProvider", "Start service android 12");
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // for Android 8 start the service in foreground
                        getActivity().startForegroundService(serviceBG);
                    } else {
                        getActivity().startService(serviceBG);
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        DigiClockProvider.scheduleJob(getActivity().getApplicationContext());
                    } else {
                        AppWidgetAlarm appWidgetAlarm = new AppWidgetAlarm(getActivity().getApplicationContext());
                        appWidgetAlarm.startAlarm();
                    }
                    Log.i("DigiClockPrefs", "Start BG Service");
                }
            }
        });
    }

    private void setHomeColorsOnClickListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                useHomeColors = !useHomeColors;
                btusehomecolors.setChecked(useHomeColors);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("UseHomeColors"+appWidgetId, useHomeColors);
                edit.commit();
                refreshPreviewImage();

                if(!useHomeColors){
                    btdarktheme.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                    TextView darksum = (TextView) mView.findViewById(R.id.textViewSummaryDarkTheme);
                    darksum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                    darksum.setText(getResources().getString(R.string.p_dark_theme_disabled));
                }else{
                    // Get the primary text color of the theme
                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = getActivity().getTheme();
                    theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
                    TypedArray arr =
                            getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                                    android.R.attr.textColorPrimary});
                    int primaryColor = arr.getColor(0, -1);
                    arr.recycle();

                    btdarktheme.setTextColor(primaryColor);
                    TextView darksum = (TextView) mView.findViewById(R.id.textViewSummaryDarkTheme);
                    darksum.setTextColor(primaryColor);
                    darksum.setText(getResources().getString(R.string.p_dark_theme_summary));
                }
            }
        });
    }

    private void setDarkThemeOnClickListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                darkDialog = new Dialog(mContext);
                darkDialog.setContentView(R.layout.dark_dialog);
                Button darkDialogButton = (Button) darkDialog.findViewById(R.id.buttonDarkOK);
                // if button is clicked, close the custom dialog
                darkDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        darkDialog.dismiss();
                        refreshPreviewImage();
                    }
                });

                RadioGroup rg = (RadioGroup)darkDialog.findViewById(R.id.RadioGroupDarkTheme);

                switch(darkMode){
                    case 0:
                        rg.check(R.id.radioButtonAuto);
                        break;
                    case 1:
                        rg.check(R.id.radioButtonLight);
                        break;
                    case 2:
                        rg.check(R.id.radioButtonDark);
                        break;
                }

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        Log.i(TAG, "checked id: "+ checkedId);
                        switch(checkedId){
                            case R.id.radioButtonAuto:
                                darkMode = 0;
                                Log.i(TAG, "checked0: "+ darkMode);
                                break;
                            case R.id.radioButtonLight:
                                darkMode = 1;
                                Log.i(TAG, "checked1: "+ darkMode);
                                break;
                            case R.id.radioButtonDark:
                                darkMode = 2;
                                Log.i(TAG, "checked2: "+ darkMode);
                                break;
                        }
                        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt("DarkMode"+appWidgetId, darkMode);
                        edit.commit();
                    }
                });


                darkDialog.show();
            }
        });
    }

    public Bitmap buildClockUpdate(String time, String ampm, String date, int  color){

	 	/* Get Device and Widget orientation.
           This is done by adding a boolean value to
           a port resource directory like values-port/bools.xml */

        boolean mIsKeyguard;

        if(useHomeColors){
            if (Build.VERSION.SDK_INT >= 31) {
                switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                    case Configuration.UI_MODE_NIGHT_YES:
                        color = getResources().getColor(R.color.neutral_1_800, mContext.getTheme());
                        break;
                    case Configuration.UI_MODE_NIGHT_NO:
                        color = getResources().getColor(R.color.accent_1_50, mContext.getTheme());
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

        Log.i("UWS", "w,h = "+ mWidgetWidthPerOrientation + ", " + mWidgetHeightPerOrientation);


        // font size
        float fontSize = clocktextsize*12;

        Paint Clockpaint = new Paint();
        SharedPreferences prefs = mContext.getSharedPreferences(
                "prefs", 0);
        int mfont = prefs.getInt("mFont"+appWidgetId, 0);
        Typeface font;
        font = Typeface.createFromAsset(mContext.getAssets(), Fontfile);

        Clockpaint.setAntiAlias(true);
        Clockpaint.setSubpixelText(true);
        if(mFont == 0)
            Clockpaint.setTypeface(Typeface.DEFAULT);
        else
            Clockpaint.setTypeface(font);
        Clockpaint.setStyle(Paint.Style.FILL);

        if(useHomeColors){
            //if (Build.VERSION.SDK_INT >= 31) {
            switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                case Configuration.UI_MODE_NIGHT_YES:
                    cColor = getResources().getColor(R.color.accent_1_100, mContext.getTheme());
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    cColor = getResources().getColor(R.color.accent_1_600, mContext.getTheme());
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
        fontSize = datetextsize*4;

        //Bitmap myBitmap = Bitmap.createBitmap(clocktextsize*2, clocktextsize+20, Bitmap.Config.ARGB_4444);
        //Canvas myCanvas = new Canvas(myBitmap);
        //Paint Datepaint = new Paint();

        //SharedPreferences prefs = getApplicationContext().getSharedPreferences(
        //		"prefs", 0);
        mfont = prefs.getInt("mFont"+appWidgetId, 0);
        font = Typeface.createFromAsset(mContext.getAssets(), Fontfile);


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
        float bgwidth = mWidgetWidthPerOrientation * 1.5f;
        int orientation = this.getResources().getConfiguration().orientation;
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
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, BGpaint);
        Log.i("UpdateWidgetService", "Draw background");


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

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}