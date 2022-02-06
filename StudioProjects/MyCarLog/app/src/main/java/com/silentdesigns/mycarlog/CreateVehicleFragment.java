package com.sd.mycarlog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CreateVehicleFragment extends DialogFragment{
	private View rootView;
	private EditText nameText;
	private EditText makeText;
	private EditText modelText;
	private EditText yearText;
	public static String vcyear;
	public static String vcmake;
	public static String vcmodel;
	public static String vcname;

	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		 rootView = inflater.inflate(
                 R.layout.vehicle_creator, container, false);
		 getDialog().setTitle("Create New Vehicle");
		 
		 
		 nameText = (EditText)rootView.findViewById(R.id.editTexVCname);
		 makeText = (EditText)rootView.findViewById(R.id.EditTextVCmake);
		 modelText = (EditText)rootView.findViewById(R.id.EditTextVCmodel);
		 yearText = (EditText)rootView.findViewById(R.id.EditTextVCyear);
		 
		 Button okButton = (Button) rootView.findViewById(R.id.buttonCVok);
			// if button is clicked, close the custom dialog
			okButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//dialog.dismiss();
					if(nameText.getText().toString().equalsIgnoreCase("")){
						Toast.makeText(CarLogHome.getCLH(), "Enter Name to Continue", Toast.LENGTH_SHORT).show();
						return;
					}
					if(yearText.getText().toString().equalsIgnoreCase("")){
						Toast.makeText(CarLogHome.getCLH(), "Enter Year to Continue", Toast.LENGTH_SHORT).show();
						return;
					}
					if(makeText.getText().toString().equalsIgnoreCase("")){
						Toast.makeText(CarLogHome.getCLH(), "Enter Make to Continue", Toast.LENGTH_SHORT).show();
						return;
					}
					if(modelText.getText().toString().equalsIgnoreCase("")){
						Toast.makeText(CarLogHome.getCLH(), "Enter Model to Continue", Toast.LENGTH_SHORT).show();
						return;
					}
						
					vcyear = yearText.getText().toString();
					vcmake = makeText.getText().toString();
					vcmodel= modelText.getText().toString();
					vcname = nameText.getText().toString();
					Vehicle car = new Vehicle(vcname);
					car.setMake(vcmake);
					car.setModel(vcmodel);
					car.setYear(vcyear);	
					
					CarLogHome.newVehicle(car);
					
					dismiss();
				}
			});
			
			Button cancelButton = (Button) rootView.findViewById(R.id.buttonCVcancel);
			// if button is clicked, close the custom dialog
			cancelButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});

			
		 
	return rootView;
	}
}
