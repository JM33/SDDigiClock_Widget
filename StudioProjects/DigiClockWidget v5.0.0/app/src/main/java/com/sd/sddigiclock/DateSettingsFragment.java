package com.sd.sddigiclock;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import yuku.ambilwarna.AmbilWarnaDialog;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateSettingsFragment extends Fragment {
    private static final int DARK_MODE_AUTO = 0;
    private static final int DARK_MODE_LIGHT = 1;
    private static final int DARK_MODE_DARK = 2;
    private static final String TAG = "DateSettings";

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
    private String clockapp;
    private boolean usehomecolors;

    private TextView btdcolor;
    private SwitchMaterial btdatematchcolor;
    private TextView btdtformat;

    private LinearLayout dateMatchClockLayout;
    private LinearLayout dateTextColorLayout;
    private LinearLayout dateFormatLayout;
    private LinearLayout dateShowLayout;

    private SeekBar btdtsize;
    private SwitchMaterial btshowdate;
    private int darkMode;

    public DateSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DateSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DateSettingsFragment newInstance(String param1, String param2) {
        DateSettingsFragment fragment = new DateSettingsFragment();
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
        mView = inflater.inflate(R.layout.fragment_date_settings, container, false);

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

        dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
        ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
        show24 = prefs.getBoolean("Show24"+appWidgetId, false);
        usehomecolors = prefs.getBoolean("UseHomeColors"+appWidgetId, false);

        clocktextsize = prefs.getInt("ClockTextSize"+appWidgetId, 15);
        datetextsize = prefs.getInt("DateTextSize"+appWidgetId, 12);
        dateFormatIndex = prefs.getInt("DateFormat" +appWidgetId, 2);

        cColor = prefs.getInt("cColor"+appWidgetId, -1);
        dColor = prefs.getInt("dColor"+appWidgetId, -1);
        dateMatchClockColor = prefs.getBoolean("DateMatchClockColor"+appWidgetId, true);
        bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);

        Bg = prefs.getInt("Bg"+appWidgetId, 3);
        Fontfile = prefs.getString("Font"+appWidgetId, "Roboto-Regular.ttf");
        mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
        clockapp = prefs.getString("ClockButtonApp"+appWidgetId, "NONE");
        darkMode = prefs.getInt("DarkMode"+appWidgetId, 0);
    }

    private void setButtons() {
        btdtsize = (SeekBar)mView.findViewById(R.id.DateSizeSB);

        btdcolor = (TextView) mView.findViewById(R.id.DateTextColor);
        dateTextColorLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutDateTextColor);

        btdatematchcolor = (SwitchMaterial) mView.findViewById(R.id.matchClockColor);
        dateMatchClockLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutMatchClockColor);

        btshowdate = (SwitchMaterial) mView.findViewById(R.id.ShowDate);
        dateShowLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutShowDate);

        btdtformat = (TextView) mView.findViewById(R.id.DateFormat);
        dateFormatLayout = (LinearLayout)mView.findViewById(R.id.LinearLayoutDateFormat);

        btdtsize.setProgress(datetextsize);

        btshowdate.setChecked(dateshown);

        // Get the primary text color of the theme
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        arr.recycle();

        btdatematchcolor.setChecked(dateMatchClockColor);
        if(dateMatchClockColor){
            btdcolor.setEnabled(false);
            btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
            TextView dcolorsum = (TextView) mView.findViewById(R.id.textViewSummaryDateTextColor);
            dcolorsum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
        }else{
            btdcolor.setEnabled(true);
            btdcolor.setTextColor(primaryColor);
            TextView dcolorsum = (TextView) mView.findViewById(R.id.textViewSummaryDateTextColor);
            dcolorsum.setTextColor(primaryColor);
        }

        if (Build.VERSION.SDK_INT >= 31) {
            if(usehomecolors){
                btdcolor.setEnabled(false);
                btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                TextView dtcsum = (TextView)mView.findViewById(R.id.textViewSummaryDateTextColor);
                dtcsum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                dtcsum.setText(R.string.p_use_home_colors_disabled);

                btdatematchcolor.setEnabled(false);
                btdatematchcolor.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                TextView dmcsum = (TextView)mView.findViewById(R.id.textViewSummaryMatchClockColor);
                dmcsum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                dmcsum.setText(R.string.p_use_home_colors_disabled);

                int color = Color.WHITE;
                switch(darkMode) {
                    case DARK_MODE_AUTO:
                        switch (mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
                            case Configuration.UI_MODE_NIGHT_YES:
                                color = mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme());
                                break;
                            case Configuration.UI_MODE_NIGHT_NO:
                                color = mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme());
                                break;
                        }
                        break;
                    case DARK_MODE_LIGHT:
                        color = mContext.getResources().getColor(R.color.accent_1_600, mContext.getTheme());
                        break;
                    case DARK_MODE_DARK:
                        color = mContext.getResources().getColor(R.color.accent_1_100, mContext.getTheme());
                        break;
                }
                setDateColorDrawable(color);
            }else{
                if(dateMatchClockColor){
                    btdcolor.setEnabled(false);
                    btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                    TextView dtcsum = (TextView) mView.findViewById(R.id.textViewSummaryDateTextColor);
                    dtcsum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                    dtcsum.setText(R.string.p_date_color_summary);

                    setDateColorDrawable(cColor);
                }else {
                    btdcolor.setEnabled(true);
                    btdcolor.setTextColor(primaryColor);
                    TextView dtcsum = (TextView) mView.findViewById(R.id.textViewSummaryDateTextColor);
                    dtcsum.setTextColor(primaryColor);
                    dtcsum.setText(R.string.p_date_color_summary);
                    setDateColorDrawable(dColor);
                }
                btdatematchcolor.setEnabled(true);
                btdatematchcolor.setTextColor(primaryColor);
                TextView dmcsum = (TextView)mView.findViewById(R.id.textViewSummaryMatchClockColor);
                dmcsum.setTextColor(primaryColor);
                dmcsum.setText(R.string.p_date_match_clock_color_summary);
                btdatematchcolor.setChecked(dateMatchClockColor);
            }
        }else{
            btdatematchcolor.setEnabled(true);
            btdatematchcolor.setTextColor(primaryColor);

            btdatematchcolor.setChecked(dateMatchClockColor);
            if(dateMatchClockColor){
                btdcolor.setEnabled(false);
                btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                TextView dtcsum = (TextView)mView.findViewById(R.id.textViewSummaryDateTextColor);
                dtcsum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                dtcsum.setText(R.string.p_date_color_summary);
                setDateColorDrawable(cColor);
            }else{
                btdcolor.setEnabled(true);
                btdcolor.setTextColor(primaryColor);
                TextView dtcsum = (TextView)mView.findViewById(R.id.textViewSummaryDateTextColor);
                dtcsum.setTextColor(primaryColor);
                dtcsum.setText(R.string.p_date_color_summary);
                setDateColorDrawable(dColor);
            }

            usehomecolors = false;
            SharedPreferences prefs =mContext.getSharedPreferences("prefs", 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("UseHomeColors"+appWidgetId, usehomecolors);
            edit.commit();
        }

        setDateTextColorListener(btdcolor);
        setDateTextColorListener(dateTextColorLayout);

        setShowDateListener(btshowdate);
        setShowDateListener(dateShowLayout);

        setMatchClockColorListener(btdatematchcolor);
        setMatchClockColorListener(dateMatchClockLayout);

        setDateFormatListener(btdtformat);
        setDateFormatListener(dateFormatLayout);
    }

    private void setDateTextColorListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(mContext, dColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        dColor = color;
                        setDateColorDrawable(dColor);
                        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt("dColor"+appWidgetId, dColor);
                        edit.commit();
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                if(!usehomecolors && !dateMatchClockColor) {
                    dialog.show();
                }
            }
        });
    }

    private void setDateColorDrawable(int color){
        Drawable newcolor = ResourcesCompat.getDrawable(getResources(), R.drawable.selected_color, mContext.getTheme());
        //Bitmap bitmap = ((BitmapDrawable) newcolor).getBitmap();
        //int size = UpdateWidgetView.dpToPx(15, mContext);
        //Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
        //d.setTint(color);
        //Drawable ldDrawable = newcolor.findDrawableByLayerId(R.id.selected_color_layer);
        GradientDrawable gd = new GradientDrawable();
        int size = UpdateWidgetView.dpToPx(25, mContext);
        gd.setSize(size, size);
        gd.setColor(color);
        gd.setCornerRadius(5);
        gd.setStroke(2, Color.DKGRAY);
        btdcolor.setCompoundDrawablesWithIntrinsicBounds(null, null, gd, null);
        Log.i("DateSettings", "SET date color= " + color + ", darkmode = " + darkMode);
    }

    private void setShowDateListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dateshown = !dateshown;
                btshowdate.setChecked(dateshown);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("ShowDate"+appWidgetId, dateshown);
                edit.commit();
            }
        });
    }

    private void setMatchClockColorListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dateMatchClockColor = !dateMatchClockColor;
                btdatematchcolor.setChecked(dateMatchClockColor);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("DateMatchClockColor" + appWidgetId, dateMatchClockColor);
                edit.commit();

                // Get the primary text color of the theme
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getActivity().getTheme();
                theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
                TypedArray arr =
                        getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                                android.R.attr.textColorPrimary});
                int primaryColor = arr.getColor(0, -1);
                arr.recycle();

                if(!usehomecolors) {
                    if (dateMatchClockColor) {
                        btdcolor.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                        TextView dcolorsum = (TextView) mView.findViewById(R.id.textViewSummaryDateTextColor);
                        dcolorsum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                        dcolorsum.setText(R.string.p_date_color_summary);
                        btdcolor.setEnabled(false);
                        setDateColorDrawable(cColor);
                    } else {
                        btdcolor.setTextColor(primaryColor);
                        TextView dcolorsum = (TextView) mView.findViewById(R.id.textViewSummaryDateTextColor);
                        dcolorsum.setTextColor(primaryColor);
                        btdcolor.setEnabled(true);
                        setDateColorDrawable(dColor);
                    }
                }
            }
        });

        btdtsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                datetextsize = btdtsize.getProgress();
                edit.putInt("DateTextSize" + appWidgetId, datetextsize);
                edit.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setDateFormatListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Dialog dfdialog = new Dialog(mContext);
                dfdialog.setContentView(R.layout.date_format_layout);
                // set the custom dialog components - text, image and button


                TextView dfDialogButton = (TextView) dfdialog.findViewById(R.id.buttonDateFormatOK);
                // if button is clicked, close the custom dialog
                dfDialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dfdialog.dismiss();
                    }
                });

                final String[] formats = mContext.getResources().getStringArray(R.array.date_formats);
                final String[] localFormats = new String[formats.length];
                for(int i = 0; i < formats.length; i++){
                    localFormats[i] = DigiClockPrefs.getFormattedDate(i);
                }

                // Get the primary text color of the theme
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getActivity().getTheme();
                theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
                TypedArray arr =
                        getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                                android.R.attr.textColorPrimary});
                int primaryTextColor = arr.getColor(0, -1);
                arr.recycle();

                RadioGroup rg = (RadioGroup)dfdialog.findViewById(R.id.RadioGroupDateFormats);
                final RadioButton[] rb = new RadioButton[localFormats.length];
                rg.setOrientation(RadioGroup.VERTICAL);
                for(int i=0; i<localFormats.length; i++){
                    rb[i]  = new RadioButton(mContext);
                    rb[i].setText(localFormats[i]);
                    rb[i].setTextColor(primaryTextColor);
                    rb[i].setTextSize(20);
                    rb[i].setPadding(5, 5, 5, 5);
                    rb[i].setId(i);
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

                        rb[i].setButtonTintList(colorStateList); // set the color tint list
                        rb[i].invalidate(); // Could not be necessary
                    }
                    rg.addView(rb[i]);
                }

                rg.check(dateFormatIndex);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        dateFormatIndex = checkedId;
                        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt("DateFormat"+appWidgetId, dateFormatIndex);

                        edit.commit();
                        Toast.makeText(mContext,
                                "Date Format = "+localFormats[checkedId], Toast.LENGTH_SHORT).show();
                        dfdialog.dismiss();
                    }
                });
                dfdialog.show();

                RadioButton selectedButton = (RadioButton) rg.getChildAt(dateFormatIndex);
                ScrollView dateFormatScrollView = (ScrollView)dfdialog.findViewById(R.id.ScrollViewDateFormat);
                focusOnView(dateFormatScrollView, selectedButton);
            }
        });
    }

    private final void focusOnView(ScrollView scrollView, View view){
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.smoothScrollTo(0, view.getTop());
            }
        });
    }
}