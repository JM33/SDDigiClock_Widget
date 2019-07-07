package com.sd.sddigiclock;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.File;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

/*
 * Author Brian Kimmel
 * Copyright Silent Designs, all rights reserved
 */
public class DigiClockPrefs extends Activity{

	public static DigiClockPrefs DCP;
	private Button btsdate;
	private Button btsampm;
	private Button bts24;
	private SeekBar btctsize;
	private SeekBar btdtsize;
	private Button btccolor;
	private Button btclockclickapp;
	private Button btdcolor;
	private Button btdatematchcolor;
	private boolean dateMatchClockColor;
	private static PendingIntent service = null;
	private Button btchoosebg;
	private ImageButton btsave;
	private ImageButton btcancel;
	private LinearLayout saveLinearLayout;
	private LinearLayout cancelLinearLayout;

	private Button btdtformat;

	static int clocktextsize;
	static int datetextsize;

	static boolean dateshown;
	static boolean ampmshown;
	static boolean show24;

	static int cColor;
	static int dColor;
	static int dateFormatIndex = 0;

	private TabHost tabhost;

	private Context self = this;
	private View dlgLayout;
	@SuppressWarnings("unused")
		private ScrollView bgcview;

	ImageView [] checkboxes;
	ImageView [] checkboxesfonts;

	private int Bg;
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	private TabHost tabs;
	private LinearLayout tab1;
	private LinearLayout tab2;
	private LinearLayout tab3;
	private LinearLayout tab4;
	//private TextView Font1;
	private TextView Font2;
	private int mFont;
	private String Fontfile;
	private int bgColor;
	private String clockapp;
	private LinearLayout bg0;
	private LinearLayout bg1;
	private LinearLayout bg2;
	private LinearLayout bg3;
	private LinearLayout bglayout0;
	private LinearLayout bglayout1;
	private LinearLayout bglayout2;
	private LinearLayout bglayout3;
    private PopupWindow mPopupWindow;
    private FrameLayout mDateFormatFrameLayout;
	private AlertDialog.Builder builder;
	private LayoutInflater inflater;
	private AlertDialog myDialog;
	private String day;
	private int year;
	private String month_name;

	static  AlarmManager alarmManager;


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		//addPreferencesFromResource(R.xml.dc_prefs);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);

		DCP = this;


		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
		    appWidgetId = extras.getInt(
		            AppWidgetManager.EXTRA_APPWIDGET_ID,
		            AppWidgetManager.INVALID_APPWIDGET_ID);
		}

		// If they gave us an intent without the widget id, just bail.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }


		LoadPrefs();

		setButtons();

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menuitem1:
			Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.menuitem2:
			Toast.makeText(this, "Menu item 2 selected", Toast.LENGTH_SHORT)
					.show();
			break;

		default:
			break;
		}

		return true;
	}

	private void LoadPrefs() {
		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);

		dateshown = prefs.getBoolean("ShowDate"+appWidgetId, true);
		ampmshown = prefs.getBoolean("ShowAMPM"+appWidgetId, true);
		show24 = prefs.getBoolean("Show24"+appWidgetId, false);

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
	}


	private void setButtons() {
		bglayout0 = (LinearLayout)DCP.findViewById(R.id.LinearLayout01);
		bglayout1 = (LinearLayout)DCP.findViewById(R.id.LinearLayout03);
		bglayout2 = (LinearLayout)DCP.findViewById(R.id.LinearLayout06);
		bglayout3 = (LinearLayout)DCP.findViewById(R.id.LinearLayout04);
		setBGs(bgColor);
		//tabs = (TabHost)DCP.findViewById(R.id.tabHost);
		btsdate = (Button)DCP.findViewById(R.id.ShowDate);
		btsampm = (Button)DCP.findViewById(R.id.ShowAMPM);
		bts24 = (Button)DCP.findViewById(R.id.TwentyFour);
		btccolor = (Button)DCP.findViewById(R.id.ClockTextColor);
		btdcolor = (Button)DCP.findViewById(R.id.DateTextColor);
		btdatematchcolor = (Button)DCP.findViewById(R.id.matchClockColor);
		btctsize = (SeekBar)DCP.findViewById(R.id.ClockSizeSB);
		btdtsize = (SeekBar)DCP.findViewById(R.id.DateSizeSB);
		btsave = (ImageButton)DCP.findViewById(R.id.btSave);
		btcancel = (ImageButton)DCP.findViewById(R.id.btCancel);
		saveLinearLayout = (LinearLayout)DCP.findViewById(R.id.saveLinearLayout);
		cancelLinearLayout = (LinearLayout)DCP.findViewById(R.id.cancelLinearLayout);
		btdtformat = (Button)DCP.findViewById(R.id.DateFormat);
		btclockclickapp = (Button)DCP.findViewById(R.id.ClockClickApp);
		//mDateFormatFrameLayout = (FrameLayout)DCP.findViewById(R.id.DateFormatFrameLayout);

		btctsize.setProgress(clocktextsize);
		btdtsize.setProgress(datetextsize);


		final TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
		tabHost.setup();

		TabSpec spec1=tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator(DCP.getResources().getString(R.string.clock));

		TabSpec spec2=tabHost.newTabSpec("Tab 2");
		spec2.setIndicator(DCP.getResources().getString(R.string.date));
		spec2.setContent(R.id.tab2);

		TabSpec spec3=tabHost.newTabSpec("Tab 3");
		spec3.setIndicator(DCP.getResources().getString(R.string.background));
		spec3.setContent(R.id.tab3);

		TabSpec spec4=tabHost.newTabSpec("Tab 4");
		spec4.setIndicator(DCP.getResources().getString(R.string.font));
		spec4.setContent(R.id.tab4);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);

		setTabColor(tabHost);
		tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

			@Override
			public void onTabChanged(String arg0) {

				setTabColor(tabHost);
			}
		});

		if(dateshown){
			btsdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			btsdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
		}

		if(dateMatchClockColor){
			btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
			btdcolor.setEnabled(false);
		}else{
			btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
			btdcolor.setEnabled(true);
		}

		if(ampmshown){
			btsampm.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			btsampm.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
		}

		if(show24){
			bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
		}

		btsdate.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		if(!dateshown){
			    	btsdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
			    	dateshown = true;

			    	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("ShowDate"+appWidgetId, dateshown);
                    edit.commit();

			    }else{
			    	btsdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
			    	dateshown = false;
			    	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("ShowDate"+appWidgetId, dateshown);
                    edit.commit();
			    }
	    	}
	    });

		btdatematchcolor.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(!dateMatchClockColor){
					btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
					dateMatchClockColor = true;
					btdcolor.setEnabled(false);
					SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("DateMatchClockColor"+appWidgetId, dateMatchClockColor);
					edit.commit();

				}else{
					btdatematchcolor.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
					dateMatchClockColor = false;
					btdcolor.setEnabled(true);
					SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
					SharedPreferences.Editor edit = prefs.edit();
					edit.putBoolean("DateMatchClockColor"+appWidgetId, dateMatchClockColor);
					edit.commit();
				}
			}
		});

		btsampm.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		if(!ampmshown){
			    	btsampm.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
			    	ampmshown = true;
			    	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("ShowAMPM"+appWidgetId, ampmshown);
                    edit.commit();
			    }else{
			    	btsampm.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
			    	ampmshown = false;
			    	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("ShowAMPM"+appWidgetId, ampmshown);
                    edit.commit();
			    }
	    	}
	    });

		bts24.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		if(!show24){
			    	bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
			    	show24 = true;
			    	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("Show24"+appWidgetId, show24);
                    edit.commit();
			    }else{
			    	bts24.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
			    	show24 = false;
			    	SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                    SharedPreferences.Editor edit = prefs.edit();
                    edit.putBoolean("Show24"+appWidgetId, show24);
                    edit.commit();
			    }
	    	}
	    });

		btccolor.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, cColor, new OnAmbilWarnaListener() {
	    	        @Override
	    	        public void onOk(AmbilWarnaDialog dialog, int color) {
	    	                cColor = color;
	    	                SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
	    	                SharedPreferences.Editor edit = prefs.edit();
	    	                edit.putInt("cColor"+appWidgetId, cColor);
	    	                edit.commit();
	    	        }

	    	        @Override
	    	        public void onCancel(AmbilWarnaDialog dialog) {
	    	                // cancel was selected by the user
	    	        }
	    	});

	    	dialog.show();
	    	}
	    });

		btclockclickapp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent appchooserintent=new Intent(DigiClockPrefs.this, AppSelector.class);
				Bundle bundle  = new Bundle();
				bundle.putInt("AppWidgetId", appWidgetId);
				appchooserintent.putExtras(bundle);
				startActivity(appchooserintent);

			}
		});

		btdcolor.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, dColor, new OnAmbilWarnaListener() {
	    	        @Override
	    	        public void onOk(AmbilWarnaDialog dialog, int color) {
	    	                dColor = color;
	    	                SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
	    	                SharedPreferences.Editor edit = prefs.edit();
	    	                edit.putInt("dColor"+appWidgetId, dColor);
	    	                edit.commit();
	    	        }

	    	        @Override
	    	        public void onCancel(AmbilWarnaDialog dialog) {
	    	                // cancel was selected by the user
	    	        }
	    	});

	    	dialog.show();
	    	}
	    });

		builder = new AlertDialog.Builder(DCP);


		btdtformat.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(DCP);
				//alt_bld.setIcon(R.drawable.icon);
				final String[] formats = DCP.getResources().getStringArray(R.array.date_formats);
				final String[] localFormats = new String[formats.length];

				for(int i = 0; i < formats.length; i++){
					localFormats[i] = getFormattedDate(i);
				}

				//final CharSequence[] grpname= DCP.getResources().obtainTypedArray(formats);

				final int selected = dateFormatIndex;
				alt_bld.setTitle("Select a Date Format");
				alt_bld.setSingleChoiceItems(localFormats, selected, new DialogInterface
						.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						dateFormatIndex = item;

						Toast.makeText(getApplicationContext(),
								"Date Format = "+localFormats[item], Toast.LENGTH_SHORT).show();
						dialog.dismiss();// dismiss the alertbox after chose option

					}
				});
				AlertDialog alert = alt_bld.create();
				alert.show();

			}
		});


		btsave.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				saveAndExit();
			}
		});

		saveLinearLayout.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		saveAndExit();
	    	}
	    });

		btcancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();

			}
		});

		cancelLinearLayout.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
		        setResult(RESULT_CANCELED);
	            finish();

	    	}
	    });

		//bgcview = (ScrollView) dlgLayout.findViewById(R.id.BGSscrollview);

        ImageView cb0 = (ImageView)DCP.findViewById(R.id.ivCB0);
        ImageView cb1 = (ImageView)DCP.findViewById(R.id.ivCB1);
        ImageView cb2 = (ImageView)DCP.findViewById(R.id.ivCB2);
        ImageView cb3 = (ImageView)DCP.findViewById(R.id.ivCB3);

        checkboxes = new ImageView []{cb0, cb1, cb2, cb3};

        SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
        Bg = prefs.getInt("Bg"+appWidgetId, 3);

        //Log.i("SDC", "Bg = " + Integer.toString(Bg));


        for(int i =0; i<checkboxes.length; i++){
        	//Log.i("SDC", "i = " + Integer.toString(i) + ", Bg = " + Integer.toString(Bg));
			if (i == Bg){
				checkboxes[i].setImageResource(R.drawable.checkedbox);
			}
			else{
				checkboxes[i].setImageResource(R.drawable.checkbox);

			}
			//Log.i("SDC", "i = " + Integer.toString(i) + "Bg = " + Integer.toString(Bg));
		}

        bg0 = (LinearLayout)DCP.findViewById(R.id.BGselect0);
        bg0.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 0;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });

        bg1 = (LinearLayout)DCP.findViewById(R.id.BGselect1);
        bg1.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 1;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });

        bg2 = (LinearLayout)DCP.findViewById(R.id.BGselect2);
        //bg2.setBackgroundColor(bgColor);
        bg2.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 2;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}

	    		AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, bgColor, new OnAmbilWarnaListener() {
	    	        @Override
	    	        public void onOk(AmbilWarnaDialog dialog, int color) {
	    	                bgColor = color;
	    	                SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
	    	                SharedPreferences.Editor edit = prefs.edit();
	    	                edit.putInt("bgColor"+appWidgetId, bgColor);
	    	                edit.commit();

	    	        }

	    	        @Override
	    	        public void onCancel(AmbilWarnaDialog dialog) {
	    	                // cancel was selected by the user
	    	        }
	    		});

	    		dialog.show();
	    		setBGs(bgColor);
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("bgColor"+appWidgetId, bgColor);
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();

	    	}

        });

        bg3 = (LinearLayout)DCP.findViewById(R.id.BGSelect3);
        //bg3.setBackgroundColor(bgColor);
        bg3.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 3;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}

	    		AmbilWarnaDialog dialog = new AmbilWarnaDialog(DCP, bgColor, new OnAmbilWarnaListener() {
	    	        @Override
	    	        public void onOk(AmbilWarnaDialog dialog, int color) {
	    	                bgColor = color;
	    	                SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
	    	                SharedPreferences.Editor edit = prefs.edit();
	    	                edit.putInt("bgColor"+appWidgetId, bgColor);
	    	                edit.commit();

	    	        }

	    	        @Override
	    	        public void onCancel(AmbilWarnaDialog dialog) {
	    	                // cancel was selected by the user
	    	        }
	    		});

	    		dialog.show();

	    		setBGs(bgColor);
	    		v.invalidate();
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("bgColor"+appWidgetId, bgColor);
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();

	    	}

        });

        
        /*
        LinearLayout bg3 = (LinearLayout)DCP.findViewById(R.id.BGSelect3);
        bg3.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 3;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        */


        ImageView fcb1 = (ImageView)DCP.findViewById(R.id.ivFCB1);
        ImageView fcb2 = (ImageView)DCP.findViewById(R.id.ivFCB2);
        ImageView fcb3 = (ImageView)DCP.findViewById(R.id.ivFCB3);
        ImageView fcb4 = (ImageView)DCP.findViewById(R.id.ivFCB4);
        ImageView fcb5 = (ImageView)DCP.findViewById(R.id.ivFCB5);
        ImageView fcb6 = (ImageView)DCP.findViewById(R.id.ivFCB6);
        ImageView fcb7 = (ImageView)DCP.findViewById(R.id.ivFCB7);
        ImageView fcb8 = (ImageView)DCP.findViewById(R.id.ivFCB8);
        ImageView fcb9 = (ImageView)DCP.findViewById(R.id.ivFCB9);
        ImageView fcb10 = (ImageView)DCP.findViewById(R.id.ivFCB10);
        ImageView fcb11 = (ImageView)DCP.findViewById(R.id.ivFCB11);
        ImageView fcb12 = (ImageView)DCP.findViewById(R.id.ivFCB12);
        //ImageView fcb13 = (ImageView)DCP.findViewById(R.id.ivFCB13);
        //ImageView fcb14 = (ImageView)DCP.findViewById(R.id.ivFCB14);


        checkboxesfonts = new ImageView []{fcb1, fcb2, fcb3, fcb4, fcb5,
        		fcb6, fcb7, fcb8, fcb9, fcb10,
        		fcb11, fcb12};
        mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
        for(int i =0; i<checkboxesfonts.length; i++){
        	//Log.i("SDC", "i = " + Integer.toString(i) + ", Bg = " + Integer.toString(Bg));
			if (i == mFont){
				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
			}
			else{
				checkboxesfonts[i].setImageResource(R.drawable.checkbox);

			}
			//Log.i("SDC", "i = " + Integer.toString(i) + "Bg = " + Integer.toString(Bg));
		}



        FrameLayout fontview1 = (FrameLayout)DCP.findViewById(R.id.FontSelect1);
        fontview1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 0;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		//Fontfile = "Roboto-Regular.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
		        });

        FrameLayout fontview2 = (FrameLayout)DCP.findViewById(R.id.FontSelect2);
        fontview2.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 1;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
                Fontfile = "Chantelli_Antiqua.ttf";
                SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview3 = (FrameLayout)DCP.findViewById(R.id.FontSelect3);
        fontview3.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 2;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "Roboto-Regular.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview4 = (FrameLayout)DCP.findViewById(R.id.FontSelect4);
        fontview4.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 3;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DroidSans.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview5 = (FrameLayout)DCP.findViewById(R.id.FontSelect5);
        fontview5.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 4;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DroidSerif-Regular.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview6 = (FrameLayout)DCP.findViewById(R.id.FontSelect6);
        fontview6.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 5;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "256BYTES.TTF";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview7 = (FrameLayout)DCP.findViewById(R.id.FontSelect7);
        fontview7.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 6;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "weezerfont.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview8 = (FrameLayout)DCP.findViewById(R.id.FontSelect8);
        fontview8.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 7;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "CARBONBL.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview9 = (FrameLayout)DCP.findViewById(R.id.FontSelect9);
        fontview9.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 8;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DistantGalaxy.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}


        });


        FrameLayout fontview10 = (FrameLayout)DCP.findViewById(R.id.FontSelect10);
        fontview10.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 9;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "GOODTIME.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
		        });

        FrameLayout fontview11 = (FrameLayout)DCP.findViewById(R.id.FontSelect11);
        fontview11.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 10;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "Jester.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });

        FrameLayout fontview12 = (FrameLayout)DCP.findViewById(R.id.FontSelect12);
        fontview12.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 11;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "DS-DIGIB.TTF";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });
        /*
        FrameLayout fontview13 = (FrameLayout)DCP.findViewById(R.id.FontSelect13);
        fontview13.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 12;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "KOMIKAX.TTF";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}

        });

        FrameLayout fontview14 = (FrameLayout)DCP.findViewById(R.id.FontSelect14);
        fontview14.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
	    		mFont = 13;
	    		for(int i =0; i<checkboxesfonts.length; i++){
	    			if (i == mFont){
	    				checkboxesfonts[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxesfonts[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		Fontfile = "weezerfont.ttf"; //<--moved to 7
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
		        });
		*/



        TextView txt = (TextView) findViewById(R.id.Font1);
        txt.setTypeface(Typeface.DEFAULT);

        txt = (TextView) findViewById(R.id.Font2);
        Typeface font = Typeface.createFromAsset(getAssets(), "Chantelli_Antiqua.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font3);
        font = Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf");
        txt.setTypeface(font);
        txt.setText("Roboto");

        txt = (TextView) findViewById(R.id.Font4);
        font = Typeface.createFromAsset(getAssets(), "DroidSans.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font5);
        font = Typeface.createFromAsset(getAssets(), "DroidSerif-Regular.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font6);
        font = Typeface.createFromAsset(getAssets(), "256BYTES.TTF");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font7);
        font = Typeface.createFromAsset(getAssets(), "weezerfont.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font8);
        font = Typeface.createFromAsset(getAssets(), "CARBONBL.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font9);
        font = Typeface.createFromAsset(getAssets(), "DistantGalaxy.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font10);
        font = Typeface.createFromAsset(getAssets(), "GOODTIME.ttf");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font11);
        font = Typeface.createFromAsset(getAssets(), "Jester.ttf");
        txt.setTypeface(font);

        txt = (TextView) findViewById(R.id.Font12);
        font = Typeface.createFromAsset(getAssets(), "DS-DIGIB.TTF");
        txt.setTypeface(font);
		//txt = (TextView) findViewById(R.id.Font13);
		//font = Typeface.createFromAsset(getAssets(), "KOMIKAX_.ttf");
		//txt.setTypeface(font);
        //txt = (TextView) findViewById(R.id.Font14);
        //font = Typeface.createFromAsset(getAssets(), "weezerfont.ttf");
        //txt.setTypeface(font);
	}

	private void saveAndExit() {
		SharedPreferences prefs = DCP.getSharedPreferences("prefs", 0);
		SharedPreferences.Editor edit = prefs.edit();


		clocktextsize = btctsize.getProgress();
		edit.putInt("ClockTextSize"+appWidgetId, clocktextsize);


		datetextsize = btdtsize.getProgress();
		edit.putInt("DateTextSize"+appWidgetId, datetextsize);

		edit.putInt("DateFormat"+appWidgetId, dateFormatIndex);

		edit.commit();

		final Intent intent = new Intent(self, UpdateWidgetService.class);

		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

		service = PendingIntent.getService(DCP, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		setResult(RESULT_OK, intent);
		DCP.startService(intent);

		finish();
	}


	@Override
	public void onPause(){
		super.onPause();

	}

	/*
	private void sBGchoose() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        dlgLayout = inflater.inflate(R.layout.bgselect, null);
        bgcview = (ScrollView) dlgLayout.findViewById(R.id.BGSscrollview);
        
        ImageView cb0 = (ImageView)dlgLayout.findViewById(R.id.ivCB0);
        ImageView cb1 = (ImageView)dlgLayout.findViewById(R.id.ivCB1);
        ImageView cb2 = (ImageView)dlgLayout.findViewById(R.id.ivCB2);
        ImageView cb3 = (ImageView)dlgLayout.findViewById(R.id.ivCB3);
        
        checkboxes = new ImageView []{cb0, cb1, cb2, cb3};
        
        SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
        Bg = prefs.getInt("Bg"+appWidgetId, 3);
        
        //Log.i("SDC", "Bg = " + Integer.toString(Bg));
        
        
        for(int i =0; i<checkboxes.length; i++){
        	//Log.i("SDC", "i = " + Integer.toString(i) + ", Bg = " + Integer.toString(Bg));
			if (i == Bg){
				checkboxes[i].setImageResource(R.drawable.checkedbox);
			}
			else{
				checkboxes[i].setImageResource(R.drawable.checkbox);
				
			}
			//Log.i("SDC", "i = " + Integer.toString(i) + "Bg = " + Integer.toString(Bg));
		}
        
        LinearLayout bg0 = (LinearLayout)dlgLayout.findViewById(R.id.BGselect0);
        bg0.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 0;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        
        LinearLayout bg1 = (LinearLayout)dlgLayout.findViewById(R.id.BGselect1);
        bg1.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 1;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        
        LinearLayout bg2 = (LinearLayout)dlgLayout.findViewById(R.id.BGselect2);
        bg2.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 2;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        LinearLayout bg3 = (LinearLayout)dlgLayout.findViewById(R.id.BGSelect3);
        bg3.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		Bg = 3;
	    		for(int i =0; i<checkboxes.length; i++){
	    			if (i == Bg){
	    				checkboxes[i].setImageResource(R.drawable.checkedbox);
	    			}
	    			else{
	    				checkboxes[i].setImageResource(R.drawable.checkbox);
	    			}
	    		}
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putInt("Bg"+appWidgetId, Bg);
                edit.commit();
	    	}
        });
        
        builder.setCancelable(false).setView(
                        dlgLayout).setPositiveButton(R.string.bgs_ok,
                        new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                        return;
                                }
                        }).setNegativeButton(null, null).setTitle(R.string.bgs_title).show();

    }
	*/



	public static void updateWidget(Context context, AppWidgetManager manager,
			int appWidgetId) {
		final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        final Calendar TIME = Calendar.getInstance();
        TIME.set(Calendar.MINUTE, 0);
        TIME.set(Calendar.SECOND, 0);
        TIME.set(Calendar.MILLISECOND, 0);

        final Intent intent = new Intent(context, UpdateWidgetService.class);

        //Bundle extras = intent.getExtras();
		//if (extras != null) {
		//    appWidgetId = extras.getInt(
		//            AppWidgetManager.EXTRA_APPWIDGET_ID, 
		//            AppWidgetManager.INVALID_APPWIDGET_ID);
		//}

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        if (service == null)
        {
            service = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        }

		//final PendingIntent pending = PendingIntent.getService(context, 0, intent, 0);
		//m.cancel(pending);

		//if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			//m.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis() + 60L * 1000L, service);
		//}
		//m.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, TIME.getTime().getTime(), 60*5*1000, service);
		//m.setRepeating(AlarmManager.RTC_WAKEUP, TIME.getTimeInMillis(),60L * 1000L, service);
		Log.i("DCPrefs", "DigiClockPrefs----------Setting Alarm for 5 minutes");


	}

	public static void setTabColor(TabHost tabhost) {

		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++) {
			tabhost.getTabWidget().getChildAt(i)
					.setBackgroundResource(R.drawable.grey); // unselected
			TextView tv = (TextView) tabhost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
			tv.setTextColor(Color.DKGRAY);
		}
		tabhost.getTabWidget().setCurrentTab(0);
		tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
				.setBackgroundResource(R.drawable.blank); // selected
		TextView tv = (TextView) tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab()).findViewById(android.R.id.title); //Unselected Tabs
		tv.setTextColor(Color.WHITE);
		// //have
		// to
		// change
	}

	public void setBGs(int color){
		Paint paint = new Paint();
	    paint = new Paint();



	    //paint.setColor(dColor);
	    //min. rect of text
	    int height = 75;
		int width = 400;
	    Shader shader = null;
	    int aw = Color.argb(200, 255, 255, 255);
	    int ab = Color.argb(200, 0, 0, 0);
	    Bitmap bm;
	    Canvas canvas;
	    BitmapDrawable d;
	    for(int i = 0; i < 4; i++){
	    	switch(i){
	    	case 0:
	    		shader = new LinearGradient(0, 0, 0, height,
			            new int[]{aw, color, color, ab},
			            new float[]{0,0.45f,.55f,1}, Shader.TileMode.REPEAT);
	    		paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout0.setBackground(d);
	    	    break;
	    	case 1:
				shader = new LinearGradient(0, 0, 0, height,
			            new int[]{color, color, color, color},
			            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout1.setBackground(d);
	    	    break;
			case 2:
				shader = new LinearGradient(0, 0, 0, height,
			            new int[]{Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT, Color.TRANSPARENT},
			            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout2.setBackground(d);
	    	    break;
			case 3:
				shader = new LinearGradient(0, 0, 0, height,
			            new int[]{aw, Color.TRANSPARENT, Color.TRANSPARENT, ab},
			            new float[]{0.0f,0.45f,.55f,1.0f}, Shader.TileMode.REPEAT);
				paint.setShader(shader);
	    		bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    	    canvas = new Canvas(bm);
	    	    canvas.drawPaint(paint);
	    	    d = new BitmapDrawable(DCP.getResources(), bm);
	    	    bglayout3.setBackground(d);
	    	    break;

	    	}
		}

		
	}

	public static String getFormattedDate(int index){
		String sdate = "";
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
		String weekDay = dayFormat.format(cal.getTime());

		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);


		//SimpleDateFormat month_date = new SimpleDateFormat("MMMMM");
		//month_name = month_date.format(cal.getTime());
		DateFormat dateformat = new DateFormat();
		String month_name = (String) DateFormat.format("M",  cal); // Jun
		//Log.d("SDDC", "CurrentMonth: "+ month_name);

		SimpleDateFormat yearFormat = new SimpleDateFormat("yy", Locale.US);
		String year_name = yearFormat.format(cal.getTime());

		switch(index) {
			case 0: //0 Tue January 23, 2018
				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);

				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 1:  //1        Tue Jan 23, 2018
				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 2:  //2       Tue 1-23-2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 3:  //3       Tue 1/23/2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 4:  //4       Tuesday January 23, 2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 5:  //5		Tuesday Jan 23, 2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 6:  //6		Tuesday 1-23-2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 7:  //7		Tuesday 1/23/2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 8:  //8		January 23, 2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 9:  //9		Jan 23, 2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + ", " + year_name);
				break;
			case 10:  //10		1-23-2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 11:  //11		1/23/2018

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 12:  //12		1-23-18

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 13:  //13		1/23/18
//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 14:  //14		January 23

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 15:  //15		1-23

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "" + year_name);
				break;
			case 16:  //16		1/23

				//dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
				break;
			case 17:  //17		Tue January 23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 18:  //18		Tue Jan 23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 19:  //19		Tue 1-23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "" + year_name);
				break;
			case 20:  //20		Tue 1/23

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
				break;
			case 21:  //21		Tuesday Jan 23

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("MMM", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + " " + String.valueOf(day) + "" + year_name);
				break;
			case 22:  //22		Tuesday 1/23

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				//yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "" + year_name);
				break;
			case 23:  //23		Tue 23-1-2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 24:  //24		Tue 23/1/2018

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 25:  //25		Tuesday 23-1-2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 26:  //26		Tuesday 23/1/2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 27:  //27		23-1-2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 28:  //28		23/1/2018

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 29:  //29		23-1-18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();

				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 30:  //30		23/1/18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();


				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 31:  //31		23-1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "" + year_name);
				break;
			case 32:  //32		23/1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = "";

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				//weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "" + year_name);
				break;
			case 33:  //33		Tue 23-1

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "" + year_name);
				break;
			case 34:  //34		Tue 23/1

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "" + year_name);
				break;
			case 35:  //35		Tuesday 23-1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "" + year_name);
				break;
			case 36:  //36		Tuesday 23/1

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = "";

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "" + year_name);
				break;
			case 37:  //37		Tue 1-23-18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 38:  //38		Tue 1/23/18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 39:  //39		Tuesday 1-23-18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "-" + String.valueOf(day) + "-" + year_name);
				break;
			case 40:  //40		Tuesday 1/23/18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + month_name + "/" + String.valueOf(day) + "/" + year_name);
				break;
			case 41:  //41		Tue 23-1-18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 42:  //42		Tue 23/1/18

				dayFormat = new SimpleDateFormat("E", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
			case 43:  //43		Tuesday 23-1-18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "-" + month_name + "-" + year_name);
				break;
			case 44:  //44		Tuesday 23/1/18

				dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
				weekDay = dayFormat.format(cal.getTime());

				day = cal.get(Calendar.DAY_OF_MONTH);
				year = cal.get(Calendar.YEAR);

				dateformat = new DateFormat();
				month_name = (String) DateFormat.format("M", cal);


				yearFormat = new SimpleDateFormat("yy", Locale.getDefault());
				year_name = yearFormat.format(cal.getTime());

				month_name = month_name.substring(0,1).toUpperCase() + month_name.substring(1).toLowerCase();
				weekDay = weekDay.substring(0,1).toUpperCase() + weekDay.substring(1).toLowerCase();

				sdate = (weekDay + " " + String.valueOf(day) + "/" + month_name + "/" + year_name);
				break;
		}
		return sdate;
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
}