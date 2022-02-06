package com.sd.mycarlog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.sd.mycarlog.AlertDialogFragment.ConfirmAlertDialog;
import com.sd.mycarlog.CarLogHome.DatePickerFragment;
import com.sd.mycarlog.CarLogHome.HistoryPageFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditFillupFragment extends DialogFragment implements OnClickListener{

	private static final String TAG = "EditFillupFragment";
	private int mYear;
	private int mMonth;
	private int mDay;
	private int mHour;
	private int mMinute;
	private View rootView;
	private TextView currVehicleText;
	private EditText odo;
	private EditText price;
	private static Button dateButton2;
	private EditText gal;
	int mIndex;
	String mEntry;
	private CheckBox tankfullbox;
	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		
		getDialog().setTitle("Edit Entry");
		
        // The last two arguments ensure LayoutParams are inflated
        // properly.
   	// Assign current Date and Time Values to Variables
		mIndex = getArguments().getInt("Index");
		mEntry = CarLogHome.getVehicleList().get(CarLogHome.getmCurrentVehicle()).getEntries().get(mIndex);
        
		String[] tmp = mEntry.split(" ");
		String datetxt = tmp[5];
		String odotxt = tmp[7];
		String galtxt = tmp[9];
		String ppgtxt = tmp[11];
		String full = tmp[13];
		
		
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        c.set(mYear, mMonth, mDay);
   	 rootView = inflater.inflate(
                R.layout.fillup_page, container, false);
        
   	 TextView curvehtitle = (TextView)rootView.findViewById(R.id.textViewFPName);
   	 curvehtitle.setVisibility(View.GONE);
   	 LinearLayout mainlayout = (LinearLayout)rootView.findViewById(R.id.linearLayoutFillupPageMain);
   	 
   	 currVehicleText = (TextView)rootView.findViewById(R.id.textViewFPcurrentname);
        currVehicleText.setVisibility(View.GONE);
        odo = (EditText)rootView.findViewById(R.id.editTextOdometer);
        odo.setText(odotxt);
        gal = (EditText)rootView.findViewById(R.id.editTextGallons);
        gal.setText(galtxt);
        price = (EditText)rootView.findViewById(R.id.editTextPrice);
        price.setText(ppgtxt);
        //date = (EditText)rootView.findViewById(R.id.editTextDate);
        dateButton2 = (Button)rootView.findViewById(R.id.ButtonDate);
        dateButton2.setOnClickListener(this);
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        //String formattedDate = sdf.format(c.getTime());
        dateButton2.setText(datetxt);
        
        tankfullbox = (CheckBox)rootView.findViewById(R.id.checkBoxTankFull);
        tankfullbox.setChecked(Boolean.parseBoolean(full));
        
        
        Button save = (Button)rootView.findViewById(R.id.button_save);
        save.setOnClickListener(this);
        
        Button cancel = (Button)rootView.findViewById(R.id.buttonFPCancel);
        cancel.setVisibility(View.VISIBLE);
        cancel.setOnClickListener(this);

        Button delete = (Button)rootView.findViewById(R.id.ButtonFPDelete);
        delete.setVisibility(View.VISIBLE);
        delete.setOnClickListener(this);
        
        return rootView;
    }
	

	@Override
	public void onClick(View v) {
		switch (v.getId()){
		case R.id.button_save:
       	 //Toast.makeText(mContext, "Fillup Saved", Toast.LENGTH_SHORT).show();
	         //String log  = prefs.getString("Log", "");
			if(dateButton2.getText() == null){
				Toast.makeText(CarLogHome.getCLH(), "No Date entered. Try again", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(odo.getText() == null){
				Toast.makeText(CarLogHome.getCLH(), "No odometer entered. Try again", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(gal.getText() == null){
				Toast.makeText(CarLogHome.getCLH(), "No gallons entered. Try again", Toast.LENGTH_SHORT).show();
				return;
			}
			
			if(price.getText() == null){
				Toast.makeText(CarLogHome.getCLH(), "No price entered. Try again", Toast.LENGTH_SHORT).show();
				return;
			}
			
			String carname = CarLogHome.getVehicleList().get(CarLogHome.getmCurrentVehicle()).name();
			//Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
       	 String fillupentry = "ENTRY FILLUP" 
       			 			 + " NAME " + carname
       			 			 + " DATE " + dateButton2.getText().toString() 
		            			 + " ODO " + odo.getText().toString() 
		            			 + " GAL " + gal.getText().toString() 
		            			 + " PPG " + price.getText().toString() 
		            			 + " FULL " + String.valueOf(tankfullbox.isChecked())
		            			 + " ; ";
       	CarLogHome.getVehicleList().get(CarLogHome.getmCurrentVehicle()).getEntries().set(mIndex, fillupentry);

       	CarLogHome.buildLog();
	         
	        
	         //backup file
	         //Toast.makeText(mContext, "Fill Up Saved", Toast.LENGTH_SHORT).show();
	         if(CarLogHome.getmBackupDriveId() == null || CarLogHome.getmBackupDriveId() == ""){
	        	//create Backup
	        	 Log.i(TAG, "No Backup file Id - creating new file");
	        	 Intent intent = new Intent(CarLogHome.getCLH(), CreateBackupFileActivity.class);
	        	 startActivity(intent);
	        	 //saveCheck();
	         }else{
	        	//edit Backup
	        	 Log.i(TAG, "Editing saved backup file");
	        	 Intent intent = new Intent(CarLogHome.getCLH(), EditBackupFileActivity.class);
	        	 startActivity(intent);
	         }
	         CarLogHome.refresh();
	         dismiss();
	         break;
		case R.id.buttonFPCancel:
			dismiss();
			break;
		case R.id.ButtonFPDelete:
			DialogInterface.OnClickListener quitListener = new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int i) {
	                switch (i) {
	                    case DialogInterface.BUTTON_POSITIVE: // delete
	                    	CarLogHome.getVehicleList().get(CarLogHome.getmCurrentVehicle()).getEntries().remove(mIndex);
	            			CarLogHome.saveToDrive();
	            			dismiss();
	                        break;
	                    case DialogInterface.BUTTON_NEGATIVE: // cancel
	                        
	                        dialog.dismiss();
	                        break;
	                }
	            }
	        };
			AlertDialog dlg = new AlertDialog.Builder(getActivity())
            .setIcon(android.R.drawable.ic_dialog_info)
            .setTitle("Delete Entry")
            .setMessage("Entry will be permanently deleted from the log")
            .setCancelable(false)
            .setPositiveButton(getString(R.string.efu_delete), quitListener)
            .setNegativeButton(getString(R.string.cancel), quitListener)
            .create();
    dlg.setCanceledOnTouchOutside(false);
			dlg.show();
			break;
		 case R.id.ButtonDate:
			 Bundle b = new Bundle();
			 b.putString("StartDate", dateButton2.getText().toString());
        	 DatePickerFragment2 dpicker = new DatePickerFragment2();
        	 dpicker.setArguments(b);
        	 dpicker.show(getFragmentManager(), "datePicker");
             break;
		}
		
		
	}

	
	public static class DatePickerFragment2 extends DialogFragment 
	implements DatePickerDialog.OnDateSetListener {

 		String mStartDate = null;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH); 
		
		//if( != null){
			mStartDate = getArguments().getString("StartDate");
		//}
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(CarLogHome.getCLH(), this, year, month, day);
		}
		
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			String formattedDate = sdf.format(c.getTime());
			dateButton2.setText(formattedDate);
			
		}
 	}
}
