package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import yuku.ambilwarna.AmbilWarnaDialog;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClockSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClockSettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int DARK_MODE_AUTO = 0;
    private static final int DARK_MODE_LIGHT = 1;
    private static final int DARK_MODE_DARK = 2;
    private static final String TAG = "ClockSettings";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private View mView;

    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private boolean dateshown;
    private boolean ampmshown;
    private boolean show24;
    private LinearLayout clockStackLayout;
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
    private boolean classicMode;

    private LinearLayout clockTextColorLayout;
    private LinearLayout clockShow24HourLayout;
    private LinearLayout clockShowAMPMLayout;
    private LinearLayout clockClickAppLayout;


    private TextView btccolor;
    private TextView btclockclickapp;
    private SwitchMaterial btsampm;
    private SwitchMaterial bts24;
    private SwitchMaterial btStackClock;

    private SeekBar btctsize;
    private boolean stackClock;
    private int darkMode;


    public ClockSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClockSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClockSettingsFragment newInstance(String param1, String param2) {
        ClockSettingsFragment fragment = new ClockSettingsFragment();
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
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
               //final NavController controller = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
                //controller.navigate(R.id.settingsHomeFragment);

                Fragment fragment = null;
                Class fragmentClass = SettingsHomeFragment.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.nav_host_fragment, fragment).commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = container.getContext();
        mView = inflater.inflate(R.layout.fragment_clock_settings, container, false);

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

        classicMode = prefs.getBoolean("ClassicMode"+appWidgetId, false);
        dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
        ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
        show24 = prefs.getBoolean("Show24"+appWidgetId, false);
        stackClock = prefs.getBoolean("StackClock" + appWidgetId, false);
        useHomeColors = prefs.getBoolean("UseHomeColors"+appWidgetId, false);

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
        //Log.d("SDDC", "clock app = "+ clockapp);
        darkMode = prefs.getInt("DarkMode"+appWidgetId, 0);

    }

    private void setButtons() {
        if(mView == null){
            Log.d("ClockFrag", "mView is null");
        }


        btccolor = (TextView) mView.findViewById(R.id.ClockTextColor);
        clockTextColorLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutClockTextColor);

        bts24 = (SwitchMaterial) mView.findViewById(R.id.TwentyFour);
        clockShow24HourLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutShow24);

        btsampm = (SwitchMaterial) mView.findViewById(R.id.ShowAMPM);
        clockShowAMPMLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutShowAMPM);

        btStackClock = (SwitchMaterial) mView.findViewById(R.id.ShowStackClock);
        clockStackLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutStackClock);

        btclockclickapp = (TextView) mView.findViewById(R.id.ClockClickApp);
        clockClickAppLayout = (LinearLayout) mView.findViewById(R.id.LinearLayoutClockClipApp);

        btctsize = (SeekBar)mView.findViewById(R.id.ClockSizeSB);
        btctsize.setProgress(clocktextsize);

        btctsize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                clocktextsize = progress;
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("ClockTextSize" + appWidgetId, clocktextsize);
                edit.commit();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Get the primary text color of the theme
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        TypedArray arr =
                getActivity().obtainStyledAttributes(typedValue.data, new int[]{
                        android.R.attr.textColorPrimary});
        int primaryColor = arr.getColor(0, -1);
        arr.recycle();

        if (Build.VERSION.SDK_INT >= 31) {
            if(useHomeColors){
                btccolor.setEnabled(false);
                btccolor.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                TextView ctcsum = (TextView)mView.findViewById(R.id.textViewSummaryClockTextColor);
                ctcsum.setTextColor(getResources().getColor(R.color.disabled_text, mContext.getTheme()));
                ctcsum.setText(getResources().getString(R.string.p_use_home_colors_disabled));

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
                setClockColorDrawable(color);
            }else{

                btccolor.setEnabled(true);
                btccolor.setTextColor(primaryColor);
                TextView ctcsum = (TextView)mView.findViewById(R.id.textViewSummaryClockTextColor);
                ctcsum.setTextColor(primaryColor);
                ctcsum.setText(getResources().getString(R.string.p_clock_color_summary));

                setClockColorDrawable(cColor);
            }
        }else{

            btccolor.setEnabled(true);
            btccolor.setTextColor(primaryColor);
            TextView ctcsum = (TextView)mView.findViewById(R.id.textViewSummaryClockTextColor);
            ctcsum.setTextColor(primaryColor);
            ctcsum.setText(getResources().getString(R.string.p_clock_color_summary));

            useHomeColors = false;
            SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putBoolean("UseHomeColors"+appWidgetId, useHomeColors);
            edit.commit();

            setClockColorDrawable(cColor);
        }

        bts24.setChecked(show24);
        btsampm.setChecked(ampmshown);
        btStackClock.setChecked(stackClock);


        setClockTextColorListener(btccolor);
        setClockTextColorListener(clockTextColorLayout);
        setShow24Listener(bts24);
        setShow24Listener(clockShow24HourLayout);
        setShowAMPMListener(btsampm);
        setShowAMPMListener(clockShowAMPMLayout);
        setShowStackClockListener(btStackClock);
        setShowStackClockListener(clockStackLayout);
        setClockClickAppListener(btclockclickapp);
        setClockClickAppListener(clockClickAppLayout);
    }

    private void setClockColorDrawable(int color){
        GradientDrawable gd = new GradientDrawable();
        int size = UpdateWidgetView.dpToPx(25, mContext);
        gd.setSize(size, size);
        gd.setColor(color);
        gd.setCornerRadius(5);
        gd.setStroke(2, Color.DKGRAY);
        btccolor.setCompoundDrawablesWithIntrinsicBounds(null, null, gd, null);
    }

    private void setClockTextColorListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(mContext, cColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        cColor = color;
                        SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                        SharedPreferences.Editor edit = prefs.edit();
                        edit.putInt("cColor"+appWidgetId, cColor);
                        edit.commit();

                        setClockColorDrawable(cColor);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });

                if(!useHomeColors) {
                    dialog.show();
                }
            }
        });
    }

    private void setShow24Listener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show24 = !show24;
                bts24.setChecked(show24);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("Show24"+appWidgetId, show24);
                edit.commit();
            }
        });
    }

    private void setShowAMPMListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ampmshown = !ampmshown;
                btsampm.setChecked(ampmshown);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("ShowAMPM" + appWidgetId, ampmshown);
                edit.commit();
            }
        });
    }
    private void setShowStackClockListener(View view) {
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                stackClock = !stackClock;
                btStackClock.setChecked(stackClock);
                SharedPreferences prefs = mContext.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putBoolean("StackClock" + appWidgetId, stackClock);
                edit.commit();
            }
        });
    }


    private void setClockClickAppListener(View view){
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent appchooserintent=new Intent(mContext, AppSelector.class);
                Bundle bundle  = new Bundle();
                bundle.putInt("AppWidgetId", appWidgetId);
                appchooserintent.putExtras(bundle);
                startActivity(appchooserintent);

            }
        });
    }
}