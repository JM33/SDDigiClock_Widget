package com.sd.sddigiclock;

import java.io.FileNotFoundException;
import java.util.Calendar;
import java.io.File;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
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

	private static DigiClockPrefs DCP;
	private Button btsdate;
	private Button btsampm;
	private Button bts24;
	private SeekBar btctsize;
	private SeekBar btdtsize;
	private Button btccolor;
	private Button btdcolor;
	private static PendingIntent service = null;
	private Button btchoosebg;
	private ImageButton btsave;
	private ImageButton btcancel;

	private Button btdtformat;

	static int clocktextsize;
	static int datetextsize;

	static boolean dateshown;
	static boolean ampmshown;
	static boolean show24;

	static int cColor;
	static int dColor;

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


	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		//addPreferencesFromResource(R.xml.dc_prefs);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

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

		cColor = prefs.getInt("cColor"+appWidgetId, -1);
		dColor = prefs.getInt("dColor"+appWidgetId, -1);
		bgColor = prefs.getInt("bgColor"+appWidgetId, Color.BLACK);

		Bg = prefs.getInt("Bg"+appWidgetId, 3);
		Fontfile = prefs.getString("Font"+appWidgetId, "Roboto-Regular.ttf");
		mFont = prefs.getInt("Fontnum"+appWidgetId, 0);
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
		btctsize = (SeekBar)DCP.findViewById(R.id.ClockSizeSB);
		btdtsize = (SeekBar)DCP.findViewById(R.id.DateSizeSB);
		btsave = (ImageButton)DCP.findViewById(R.id.btSave);
		btcancel = (ImageButton)DCP.findViewById(R.id.btCancel);
		btdtformat = (Button)DCP.findViewById(R.id.DateFormat);

        mDateFormatFrameLayout = (FrameLayout)DCP.findViewById(R.id.DateFormatFrameLayout);

		btctsize.setProgress(clocktextsize);
		btdtsize.setProgress(datetextsize);


		TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
		tabHost.setup();

		TabSpec spec1=tabHost.newTabSpec("Tab 1");
		spec1.setContent(R.id.tab1);
		spec1.setIndicator("Clock");

		TabSpec spec2=tabHost.newTabSpec("Tab 2");
		spec2.setIndicator("Date");
		spec2.setContent(R.id.tab2);

		TabSpec spec3=tabHost.newTabSpec("Tab 3");
		spec3.setIndicator("Back Ground");
		spec3.setContent(R.id.tab3);

		TabSpec spec4=tabHost.newTabSpec("Tab 4");
		spec4.setIndicator("Font");
		spec4.setContent(R.id.tab4);

		tabHost.addTab(spec1);
		tabHost.addTab(spec2);
		tabHost.addTab(spec3);
		tabHost.addTab(spec4);

		if(dateshown){
			btsdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkedbox,0);
		}else{
			btsdate.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.checkbox,0);
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
				final CharSequence[] grpname= new CharSequence[] {"MMDDYYYY", "MMDDYY"};
				final int selected = 1;
				alt_bld.setTitle("Select a Group Name");
				alt_bld.setSingleChoiceItems(grpname, selected, new DialogInterface
						.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {

						Toast.makeText(getApplicationContext(),
								"Group Name = "+grpname[item], Toast.LENGTH_SHORT).show();
						dialog.dismiss();// dismiss the alertbox after chose option

					}
				});
				AlertDialog alert = alt_bld.create();
				alert.show();

			}
		});



		btsave.setOnClickListener(new OnClickListener() {
	    	public void onClick(View v) {
	    		//final Context context = DigiClockPrefs.this;

	            // When the button is clicked, save the string in our prefs and return that they
	            // clicked OK.



	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
	            SharedPreferences.Editor edit = prefs.edit();


	    		clocktextsize = btctsize.getProgress();
	    		edit.putInt("ClockTextSize"+appWidgetId, clocktextsize);


	    		datetextsize = btdtsize.getProgress();
	    		edit.putInt("DateTextSize"+appWidgetId, datetextsize);
	    		edit.commit();

	    		final AlarmManager m = (AlarmManager) self.getSystemService(Context.ALARM_SERVICE);

		        final Calendar TIME = Calendar.getInstance();
		        TIME.set(Calendar.MINUTE, 0);
		        TIME.set(Calendar.SECOND, 0);
		        TIME.set(Calendar.MILLISECOND, 0);

		        final Intent intent = new Intent(self, UpdateWidgetService.class);

		        //Bundle extras = intent.getExtras();
	    		//if (extras != null) {
	    		//    appWidgetId = extras.getInt(
	    		//            AppWidgetManager.EXTRA_APPWIDGET_ID,
	    		//            AppWidgetManager.INVALID_APPWIDGET_ID);
	    		//}

		        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

		        //if (service == null)
		        //{
		            service = PendingIntent.getService(self, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		        //}

		        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, service);

		        setResult(RESULT_OK, intent);
		        Toast.makeText(DCP, "Settings Saved", Toast.LENGTH_SHORT);
	            finish();

	    	}
	    });

		btcancel.setOnClickListener(new OnClickListener() {
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
        ImageView fcb13 = (ImageView)DCP.findViewById(R.id.ivFCB13);
        ImageView fcb14 = (ImageView)DCP.findViewById(R.id.ivFCB14);


        checkboxesfonts = new ImageView []{fcb1, fcb2, fcb3, fcb4, fcb5,
        		fcb6, fcb7, fcb8, fcb9, fcb10,
        		fcb11, fcb12, fcb13, fcb14};
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
	    		Fontfile = "BRUSHSTP.TTF";
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
        /*
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
	    		Fontfile = "KOMIKAX_.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
        });
        */
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
	    		Fontfile = "DS-DIGIB.TTF";
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
	    		Fontfile = "weezerfont.ttf";
	    		SharedPreferences prefs = self.getSharedPreferences("prefs", 0);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("Font"+appWidgetId, Fontfile);
                edit.putInt("Fontnum"+appWidgetId, mFont);
                edit.commit();
	    	}
		        });




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
        //txt = (TextView) findViewById(R.id.Font7);
        //font = Typeface.createFromAsset(getAssets(), "BRUSHSTP.TTF");
        //txt.setTypeface(font);
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
        //txt = (TextView) findViewById(R.id.Font12);
        //font = Typeface.createFromAsset(getAssets(), "KOMIKAX_.ttf");
        //txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font13);
        font = Typeface.createFromAsset(getAssets(), "DS-DIGIB.TTF");
        txt.setTypeface(font);
        txt = (TextView) findViewById(R.id.Font14);
        font = Typeface.createFromAsset(getAssets(), "weezerfont.ttf");
        txt.setTypeface(font);
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

        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), 1000 * 60, service);

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
}