package com.sd.sddigiclock;

import android.Manifest;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import yuku.ambilwarna.AmbilWarnaDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BackgroundSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BackgroundSettingsFragment extends Fragment {
    public static final String TAG = "BGSettings";

    private static final int DARK_MODE_AUTO = 0;
    private static final int DARK_MODE_LIGHT = 1;
    private static final int DARK_MODE_DARK = 2;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private Context mContext;
    private View mView;
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private int bgColor;
    private int Bg;
    private boolean useHomeColors;

    private LinearLayout bgLayouts;
    private LinearLayout bglayout0;
    private LinearLayout bglayout1;
    private LinearLayout bglayout2;
    private LinearLayout bglayout3;
    private ImageView imgViewBG0;
    private ImageView imgViewBG1;
    private TextView textViewBG2;
    private TextView textViewBG3;
    private ImageView[] checkboxes;
    private LinearLayout bg0;
    private LinearLayout bg1;
    private LinearLayout bg2;
    private LinearLayout bg3;

    private LinearLayout bgHelperLayout;

    private ScrollView BgScrollView;

    private int cornerRadius;
    private int fontcolor;
    private int cColor;
    private int darkMode;

    public BackgroundSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BackgroundSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BackgroundSettingsFragment newInstance(String param1, String param2) {
        BackgroundSettingsFragment fragment = new BackgroundSettingsFragment();
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
        mView = inflater.inflate(R.layout.fragment_background_settings, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appWidgetId = bundle.getInt("appWidgetID", AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.i(TAG, "INVALID WIDGET ID = "+appWidgetId);
            return null;
        }

        LoadPrefs();

        setButtons();


        return mView;
    }

    private void LoadPrefs() {
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
        bgColor = prefs.getInt("bgColor" + appWidgetId, Color.BLACK);
        Bg = prefs.getInt("Bg" + appWidgetId, 3);
        cornerRadius = prefs.getInt("CornerRadius" + appWidgetId, 50);
        cColor = prefs.getInt("cColor" + appWidgetId, -1);
        useHomeColors = prefs.getBoolean("UseHomeColors" + appWidgetId, false);
        darkMode = prefs.getInt("DarkMode"+appWidgetId, 0);
    }

    private void setButtons() {
        BgScrollView = (ScrollView) mView.findViewById(R.id.BGSscrollview);
        bgLayouts = (LinearLayout)mView.findViewById(R.id.linearLayoutBG);
        bgHelperLayout = (LinearLayout)mView.findViewById(R.id.linearLayoutBGHelper);
        //Backgrounds
        bglayout0 = (LinearLayout)mView.findViewById(R.id.LinearLayout01);
        bglayout1 = (LinearLayout)mView.findViewById(R.id.LinearLayout03);
        bglayout2 = (LinearLayout)mView.findViewById(R.id.LinearLayout06);
        bglayout3 = (LinearLayout)mView.findViewById(R.id.LinearLayout04);




        ImageView cb0 = (ImageView)mView.findViewById(R.id.ivCB0);
        ImageView cb1 = (ImageView)mView.findViewById(R.id.ivCB1);
        ImageView cb2 = (ImageView)mView.findViewById(R.id.ivCB2);
        ImageView cb3 = (ImageView)mView.findViewById(R.id.ivCB3);

        imgViewBG0 = (ImageView)mView.findViewById(R.id.imageViewBG0);
        imgViewBG1 = (ImageView)mView.findViewById(R.id.imageViewBG1);
        textViewBG2 = (TextView)mView.findViewById(R.id.textViewBG2);
        textViewBG3 = (TextView)mView.findViewById(R.id.textViewBG3);
        if(useHomeColors) {
            switch(darkMode){
                case DARK_MODE_AUTO:
                    switch (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                        case Configuration.UI_MODE_NIGHT_YES:
                            textViewBG2.setTextColor(mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme()));
                            textViewBG3.setTextColor(mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme()));
                            imgViewBG0.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_100), android.graphics.PorterDuff.Mode.MULTIPLY);
                            imgViewBG1.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_100), android.graphics.PorterDuff.Mode.MULTIPLY);
                            break;
                        case Configuration.UI_MODE_NIGHT_NO:
                            textViewBG2.setTextColor(mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme()));
                            textViewBG3.setTextColor(mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme()));
                            imgViewBG0.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_600), android.graphics.PorterDuff.Mode.MULTIPLY);
                            imgViewBG1.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_600), android.graphics.PorterDuff.Mode.MULTIPLY);
                            break;
                    }
                    break;
                case DARK_MODE_LIGHT:
                    textViewBG2.setTextColor(mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme()));
                    textViewBG3.setTextColor(mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme()));
                    imgViewBG0.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_600), android.graphics.PorterDuff.Mode.MULTIPLY);
                    imgViewBG1.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_600), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
                case DARK_MODE_DARK:
                    textViewBG2.setTextColor(mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme()));
                    textViewBG3.setTextColor(mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme()));
                    imgViewBG0.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_100), android.graphics.PorterDuff.Mode.MULTIPLY);
                    imgViewBG1.setColorFilter(ContextCompat.getColor(mContext, R.color.accent_1_100), android.graphics.PorterDuff.Mode.MULTIPLY);
                    break;
            }

        }else{
            textViewBG2.setTextColor(cColor);
            textViewBG3.setTextColor(cColor);
            imgViewBG0.setColorFilter(cColor, android.graphics.PorterDuff.Mode.MULTIPLY);
            imgViewBG1.setColorFilter(cColor, android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        checkboxes = new ImageView []{cb0, cb1, cb2, cb3};



        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
        Bg = prefs.getInt("Bg"+appWidgetId, 3);


        setCheckboxes(Bg);

        bg0 = (LinearLayout)mView.findViewById(R.id.BGselect0);
        bg0.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bg = 0;
                Log.d("DCP", "BG = " + Bg);

                setCheckboxes(Bg);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
            }
        });

        bg1 = (LinearLayout)mView.findViewById(R.id.BGselect1);
        bg1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bg = 1;
                Log.d("DCP", "BG = " + Bg);

                setCheckboxes(Bg);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
            }
        });

        bg2 = (LinearLayout)mView.findViewById(R.id.BGselect2);
        //bg2.setBackgroundColor(bgColor);
        bg2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bg = 2;
                Log.d("DCP", "BG = " + Bg);
                setCheckboxes(Bg);

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(mContext, bgColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        bgColor = color;
                        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt("bgColor"+appWidgetId, bgColor);
                        edit.commit();
                        setBGs(color);

                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                if(useHomeColors) {
                    Toast.makeText(mContext, R.string.p_use_home_colors_disabled, Toast.LENGTH_LONG).show();
                }else {
                    dialog.show();
                }
                setBGs(bgColor);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("bgColor"+appWidgetId, bgColor);
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();

            }

        });

        bg3 = (LinearLayout)mView.findViewById(R.id.BGSelect3);
        //bg3.setBackgroundColor(bgColor);
        bg3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bg = 3;
                Log.d("DCP", "BG = " + Bg);

                setCheckboxes(Bg);

                AmbilWarnaDialog dialog = new AmbilWarnaDialog(mContext, bgColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        bgColor = color;
                        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt("bgColor"+appWidgetId, bgColor);
                        edit.commit();
                        setBGs(color);

                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                if(useHomeColors) {
                    Toast.makeText(mContext, R.string.p_use_home_colors_disabled, Toast.LENGTH_LONG).show();
                }else {
                    dialog.show();
                }

                setBGs(bgColor);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("bgColor"+appWidgetId, bgColor);
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
            }

        });

        setBGs(bgColor);
    }

    public void setBGs(int color){
        Paint paint = new Paint();
        paint = new Paint();


        if(useHomeColors) {
            if (Build.VERSION.SDK_INT >= 31) {
                switch (darkMode){
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

        //paint.setColor(dColor);
        //min. rect of text
        int height = 200;
        int width = 500;
        Shader shader = null;
        int aw = Color.argb(200, 255, 255, 255);
        int ab = Color.argb(200, 0, 0, 0);
        Bitmap bm;
        Canvas canvas;
        BitmapDrawable d;
        RectF rect = new RectF(0.0f, 0.0f, width, height);
        float newRadius = cornerRadius*2;

        for(int i = 0; i < 4; i++){
            switch(i){
                case 0:
                    shader = new LinearGradient(0, 0, 0, height,
                            new int[]{aw, color, color, ab},
                            new float[]{0,0.45f,.55f,1}, Shader.TileMode.REPEAT);
                    paint.setShader(shader);
                    bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(bm);
                    canvas.drawRoundRect(rect, newRadius, newRadius, paint);
                    //canvas.drawPaint(paint);
                    d = new BitmapDrawable(mContext.getResources(), bm);
                    bglayout0.setBackground(d);
                    break;
                case 1:
                    shader = new LinearGradient(0, 0, 0, height,
                            new int[]{color, color, color, color},
                            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
                    paint.setShader(shader);
                    bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(bm);
                    canvas.drawRoundRect(rect, newRadius, newRadius, paint);
                    //canvas.drawPaint(paint);
                    d = new BitmapDrawable(mContext.getResources(), bm);
                    bglayout1.setBackground(d);
                    break;
                case 2:
                    shader = new LinearGradient(0, 0, 0, height,
                            new int[]{Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT},
                            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
                    paint.setShader(shader);
                    bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(bm);
                    canvas.drawRoundRect(rect, newRadius, newRadius, paint);
                    //canvas.drawPaint(paint);
                    d = new BitmapDrawable(mContext.getResources(), bm);
                    bglayout2.setBackground(d);
                    //bglayout2.postInvalidate();
                    break;
                case 3:
                    shader = new LinearGradient(0, 0, 0, height,
                            new int[]{aw, Color.TRANSPARENT, Color.TRANSPARENT, ab},
                            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
                    paint.setShader(shader);
                    bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    canvas = new Canvas(bm);
                    canvas.drawRoundRect(rect, newRadius, newRadius, paint);
                    //canvas.drawPaint(paint);
                    d = new BitmapDrawable(mContext.getResources(), bm);
                    bglayout3.setBackground(d);
                    //bglayout3.postInvalidate();
                    break;

            }
        }

        //wrap content so checkered background only shows behind buttons
        layoutView(bgHelperLayout);
        int bgTotalHeight = (20) + bgHelperLayout.getHeight();
        ScrollView.LayoutParams layoutParams = new ScrollView.LayoutParams(ScrollView.LayoutParams.MATCH_PARENT, bgTotalHeight);
        bgLayouts.setLayoutParams(layoutParams);
    }

    private void setCheckboxes(int checked){
        for(int i = 0; i < checkboxes.length; i++){
            if (i == checked){
                checkboxes[i].setImageResource(android.R.drawable.radiobutton_on_background);
                checkboxes[i].setColorFilter(ContextCompat.getColor(mContext, R.color.primaryColor), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            else{
                checkboxes[i].setImageResource(android.R.drawable.radiobutton_off_background);
                checkboxes[i].setColorFilter(ContextCompat.getColor(mContext, R.color.disabled_text), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    void layoutView(View view) {
        view.setDrawingCacheEnabled(true);
        int wrapContentSpec =
                View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.measure(wrapContentSpec, wrapContentSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
    }
}