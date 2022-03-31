package com.sd.sddigiclock;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.provider.FontRequest;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.Fragment;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.os.Handler;
import android.provider.FontsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FontSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FontSettingsFragment extends Fragment {
    private final static String TAG = "FontSettings";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static Context mContext;
    private static View mView;
    private static int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static String Fontfile;
    private static int mFont;

    private String[]fontNames;
    static ArrayList<String> fontsList = new ArrayList<String>();
    static ArrayList<Button> fontButtons = new ArrayList<Button>();

    private static LinearLayout linearLayoutFonts;

    private LinearLayout fontsLoadingLayout;
    private ProgressBar fontsProgressBar;

    private static Handler mHandler;
    private WorkManager workManager;


    public FontSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FontSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FontSettingsFragment newInstance(String param1, String param2) {
        FontSettingsFragment fragment = new FontSettingsFragment();
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
        mView = inflater.inflate(R.layout.fragment_font_settings, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appWidgetId = bundle.getInt("appWidgetID", AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            Log.i(TAG, "INVALID WIDGET ID = "+appWidgetId);
            return null;
        }

        LoadPrefs();

        mHandler = new Handler();



        fontsLoadingLayout = (LinearLayout)mView.findViewById(R.id.FontLoadingScreen);
        fontsProgressBar = (ProgressBar)mView.findViewById(R.id.progressBarFonts);
        showLoadingScreen();
        Log.i(TAG, "Show Loading screen - start font download");
        workManager = WorkManager.getInstance(mContext);

        OneTimeWorkRequest loadRequest = new OneTimeWorkRequest.Builder(DownloadFontsWorker.class).build();
        workManager.enqueue(loadRequest);
        workManager.getWorkInfoByIdLiveData(loadRequest.getId())
                .observe(getViewLifecycleOwner(), workInfo -> {
                    if (workInfo.getState() != null &&
                            workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                        hideLoadingScreen();
                        Log.i(TAG, "Hiding Loading screen");
                    }
                });



        //setButtons();


        return mView;
    }



    private void LoadPrefs() {
        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);

        fontsList = new ArrayList<String>();
        boolean hasfonts = listAssetFiles("");
        if(!hasfonts){
            return;
        }
        fontsList.add(0, getResources().getString(R.string.system_font));
        String defaultFontFile = fontsList.get(0);

        if(!prefs.contains("Font"+appWidgetId)) {
            Log.i("Font", "No font saved");
            for (String file : fontsList) {
                Log.i("Font", "Reading Font: "+file);
                if (file.equals(fontsList.get(mFont))) {
                    Log.i("Font", "Found Font: "+file);
                    defaultFontFile = file;
                    Fontfile = file;
                    mFont = fontsList.indexOf(file);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putString("Font" + appWidgetId, Fontfile);
                    edit.putInt("Fontnum" + appWidgetId, mFont);
                    edit.commit();
                }
            }
        }

        Fontfile = prefs.getString("Font"+appWidgetId, defaultFontFile);
        mFont = prefs.getInt("Fontnum"+appWidgetId, 0);

    }

    private void showLoadingScreen(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                fontsLoadingLayout.setVisibility(View.VISIBLE);
                fontsProgressBar.setIndeterminate(true);
            }
        });

    }
    private void hideLoadingScreen(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                fontsLoadingLayout.setVisibility(View.GONE);
            }
        });
    }

    public static void loadFontButtons(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                setButtons();
            }
        });
    }

    public static void setButtons() {
        Activity activity = (Activity)mView.getContext();
        // Get the primary text color of the theme
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = activity.getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                activity.obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryTextColor = arr.getColor(0, -1);
        arr.recycle();

        //Add fonts from assets
        fontsList = new ArrayList<String>();
        fontsList.add(mContext.getResources().getString(R.string.system_font));
        boolean hasfonts = listAssetFiles("");
        if(!hasfonts){
            return;
        }
        linearLayoutFonts = mView.findViewById(R.id.LinearLayoutFontsList);
        ScrollView fontScrollView = mView.findViewById(R.id.ScrollViewFonts);


        Field[] fontFields = R.font.class.getFields();
        ArrayList<Integer> fontIDs = new ArrayList<>();
        ArrayList<String> fontNames = new ArrayList<>();

        for (Field field : fontFields) {
            try {
                fontIDs.add(field.getInt(field));
                fontNames.add(field.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.i(TAG, "fontsList size = "+fontsList.size());
        Log.i(TAG, "fontButtons size = "+fontButtons.size());


        ArrayList<String> allFonts = new ArrayList<String>();

        allFonts.addAll(fontsList);
        //Add fonts from resources
        if (Build.VERSION.SDK_INT >= 26) {
            allFonts.addAll(fontNames);
        }
        Collections.sort(allFonts, String.CASE_INSENSITIVE_ORDER);

        int systemindex = 0;
        for(String title : allFonts){
            if(title.equals(mView.getResources().getString(R.string.system_font))){
                systemindex = allFonts.indexOf(title);
            }
        }
        allFonts.remove(systemindex);
        allFonts.add(0, mView.getResources().getString(R.string.system_font));

        RadioGroup rg = new RadioGroup(mContext);
        final RadioButton[] rb = new RadioButton[allFonts.size()];
        rg.setOrientation(RadioGroup.VERTICAL);
        rg.setLayoutDirection(RadioGroup.LAYOUT_DIRECTION_RTL);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rg.setLayoutParams(layoutParams);

        View selectedButton;


        for(int i=0; i<allFonts.size(); i++){
            rb[i]  = new RadioButton(mContext);
            rb[i].setTextColor(primaryTextColor);
            rb[i].setTextSize(25);
            rb[i].setPadding(25, 25, 25, 25);
            rb[i].setId(i);
            //rb[i].setLayoutDirection(RadioGroup.LAYOUT_DIRECTION_LTR);
            rb[i].setGravity(Gravity.START|Gravity.CENTER_VERTICAL);
            rb[i].setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            rb[i].setLayoutParams(layoutParams);
            rb[i].setButtonDrawable(null);
            rb[i].setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(mContext, android.R.drawable.btn_radio), null);

            Typeface font = Typeface.DEFAULT;
            if(i!=0 && fontsList.contains(allFonts.get(i))){
                Log.i(TAG, "get asset font: " + allFonts.get(i));
                font = Typeface.createFromAsset(mContext.getAssets(), allFonts.get(i));
            }
            if (Build.VERSION.SDK_INT >= 26) {
                if (fontNames.contains(allFonts.get(i))) {
                    int index = fontNames.indexOf(allFonts.get(i));
                    if(fontIDs.get(index) != null) {
                        try{
                            font = ResourcesCompat.getFont(mContext, fontIDs.get(index));
                        }catch(Resources.NotFoundException e){
                            e.printStackTrace();
                        }
                    }else{
                        continue;
                    }
                }
            }
            String fontname = allFonts.get(i).replaceFirst("[.][^.]+$", "");
            fontname = fontname.replaceAll("[-_]", " ");
            rb[i].setText(fontname);

            rb[i].setTypeface(font);

            if(Build.VERSION.SDK_INT >= 21)
            {
                ColorStateList colorStateList = new ColorStateList(
                        new int[][]
                                {
                                        new int[]{-android.R.attr.state_enabled}, // Disabled
                                        new int[]{android.R.attr.state_enabled}   // Enabled
                                },
                        new int[]
                                {
                                        mContext.getColor(R.color.disabled_text), // disabled
                                        mContext.getColor(R.color.primaryColor)   // enabled
                                }
                );

                //rb[i].setCompoundDrawableTintList(colorStateList); // set the color tint list
                TextViewCompat.setCompoundDrawableTintList(rb[i], colorStateList);
                rb[i].invalidate(); // Could not be necessary
            }

            rg.addView(rb[i]);


        }

        int selectedindex = 0;
        for(String fontTitle:allFonts){
            if(fontTitle.equals(Fontfile)){
                selectedindex = allFonts.indexOf(fontTitle);
                Log.i(TAG, "Fontfile: " +Fontfile + " fontTitle = " + fontTitle);
            }
        }
        rg.check(selectedindex);
        selectedButton = rg.getChildAt(selectedindex);
        Log.i(TAG, "Checked radio button: " +selectedindex + " font = " + allFonts.get(selectedindex));

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int grpindex = group.indexOfChild(group.findViewById(checkedId));
                if(grpindex == 0){
                    mFont = 0;
                }else{
                    if(fontsList.contains(allFonts.get(grpindex))) {
                        mFont = fontsList.indexOf(allFonts.get(grpindex));
                    }else{
                        if(fontNames.contains(allFonts.get(grpindex))) {
                            mFont = fontNames.indexOf(grpindex) + fontsList.size()+1;
                            //Fontfile = allFonts.get(grpindex);
                        }
                    }
                }
                Log.d(TAG, "rg selected mFont = " + mFont);
                Fontfile = allFonts.get(grpindex);
                Log.d(TAG, "rg selected FontFile = " + Fontfile);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
                //Log.d(TAG, "Font selected = " + Fontfile);
            }
        });

        linearLayoutFonts.addView(rg);


        focusOnView(fontScrollView, selectedButton);
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

    private static void focusOnView(ScrollView scrollView, View view){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, view.getTop());
            }
        });
    }

    public static String capitalizeString(String str) {
        String retStr = str;
        try { // We can face index out of bound exception if the string is null
            retStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        }catch (Exception e){}
        return retStr;
    }


}