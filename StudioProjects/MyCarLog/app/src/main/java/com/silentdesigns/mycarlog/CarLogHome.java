package com.sd.mycarlog;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.plus.Plus;
import com.sd.inappbilling.util.IabHelper;
import com.sd.inappbilling.util.IabResult;
import com.sd.inappbilling.util.Inventory;
import com.sd.inappbilling.util.Purchase;


public class CarLogHome extends FragmentActivity implements ConnectionCallbacks,
OnConnectionFailedListener {  
	/**
	* Request code for auto Google Play Services error resolution.
	*/
	    private static final int REQUEST_CODE_RESOLUTION = 1;


		private static final String TAG = "MyCarLog";

		private static final int RESOLVE_CONNECTION_REQUEST_CODE = 100;

		private static final int COMPLETE_AUTHORIZATION_REQUEST_CODE = 101;


	private final String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmT1WL/pCNooQlqSJSFDp6yeKZ/0hYGNP40haD0FD5RMeKyXKvwNCE98yQV2HClsgtwDk+b4yT0wl6e8C+v+msPUa1eC+lOdMpt9MqpA6+XXOXdqbdW7mEFyPpMFoRVmhPXgvvg9tDhn9FnMp3yRhXZ6HAWTaZ9Y9EosTtcK+fBg9RmlUitOYle1tJc+QOeKTv7BHZ3RvaNAymM+mOF2QOxGVzI3pNmq2tNdUdDHfDzYeolGqwspqXR0i7nDCDDu4FRn+KscY/bsmyFk7NxwOIgSNkkDzSWi8j+jbAkqKlpJVTe1XA8myfbASK9aqOl7snCmss4Fhw3uVkqlNK06RMwIDAQAB";
	
	private static String ITEM_SKU = "Upgrade MyCarLog Pro";
	
	// SKUs for our products: the premium upgrade (non-consumable)
	static final String SKU_PREMIUM = "upgrade.full.version";

	// Does the user have the premium upgrade?
	boolean mIsPremium = false;

	// (arbitrary) request code for the purchase flow
	static final int RC_REQUEST = 105;

	// The helper object
	IabHelper mHelper;
	
	private static Context mContext;
	private static CarLogHome CLH;
	// When requested, this adapter returns a DemoObjectFragment,
    // representing an object in the collection.
    DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
    ViewPager mViewPager;
    Calendar mCal;
	private static GoogleApiClient mGoogleApiClient;
	private GoogleApiClient mApiClient;

	private static String mAccountName = null;

	private static Handler mHandler;
	public static int mMinute;
	public static int mHour;
	public static int mYear;
	public static int mMonth;
	public static int mDay;
    static DatePickerDialog datepick;
	private static Button dateButton;
	protected static SharedPreferences prefs;

	private static String mBackupDriveId;

	private static String mLocalBackupLocation;
	

	//private boolean mBillingServiceReady;

	private static int mCurrentVehicle;
	private static ArrayList<Vehicle> mVehicleList = new ArrayList<Vehicle>();
	private String mAllVehicleInfo;

    public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    mHandler = new Handler();
	
	    
	    setContentView(R.layout.activity_home);
	    setCLH(this);
	    mContext = getCLH().getApplicationContext();
	    
	    
	    prefs = getCLH().getSharedPreferences(
	            "Prefs", MODE_PRIVATE);
	    
	    
	    setmCurrentVehicle(prefs.getInt("CurrentVehicle", 0));
	
	    readLog();
	    
	    //boolean firstrun = prefs.getBoolean("FirstRun", true);
	    //if(firstrun){
	    	
	    	
	    //	SaveBooleanPreferences("FirstRun", false);
	    //}
	    setmBackupDriveId(prefs.getString("BackupFileId", ""));
	    setLocalBackup(prefs.getString("LocalBackupLocation", null));
	    setmAccountName(prefs.getString("AccountName", null));
	    
	    
	    //mHelper = new IabHelper(this, base64EncodedPublicKey);
	    
	    
	    
	    // ViewPager and its adapters use support library
	    // fragments, so use getSupportFragmentManager.
	    mDemoCollectionPagerAdapter =
	            new DemoCollectionPagerAdapter(
	                    getSupportFragmentManager());
	    mViewPager = (ViewPager) findViewById(R.id.pager);
	    mViewPager.setAdapter(mDemoCollectionPagerAdapter);
	    
	    
	    mHelper = new IabHelper(this, base64Key);

	    Log.d(TAG, "Starting setup.");
	    mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
		    public void onIabSetupFinished(IabResult result) {
			    Log.d(TAG, "Setup finished.");
		
			    if (!result.isSuccess()) {
			    // Oh noes, there was a problem.
			    Log.d(TAG, "Problem setting up In-app Billing: " + result);
			    }
			    // Hooray, IAB is fully set up!
			    mHelper.queryInventoryAsync(mGotInventoryListener);
		    }
	    });
	}
    
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
    	public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
    	Log.d(TAG, "Query inventory finished.");
    	if (result.isFailure()) {
    	Log.d(TAG, "Failed to query inventory: " + result);
    	return;
    	}
    	else {
    	Log.d(TAG, "Query inventory was successful.");
    	// does the user have the premium upgrade?
    	mIsPremium = inventory.hasPurchase(SKU_PREMIUM);

    	// update UI accordingly

    	Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
    	}

    	Log.d(TAG, "Initial inventory query finished; enabling main UI.");
    	}
    	};

    	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
    	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
    	if (result.isFailure()) {
    	Log.d(TAG, "Error purchasing: " + result);
    	return;
    	}
    	else if (purchase.getSku().equals(SKU_PREMIUM)) {
    	// give user access to premium content and update the UI
    	}
    	}
    	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  MenuInflater inflater = getMenuInflater();
	  inflater.inflate(R.menu.car_log_home, menu);
	  return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	  case R.id.action_choose_account:
	      chooseAccount();
	      break;
	  case R.id.action_upgrade:
	      Log.i(TAG, "Item SKU = " + ITEM_SKU);
	      
		  purchaseUpgrade();
	      break;
	  case R.id.action_restore:
	      //Toast.makeText(this, "Restore selected", Toast.LENGTH_SHORT).show();
	      restoreCheck();
	      break;
	  case R.id.action_backup:
		  if(mBackupDriveId == null || mBackupDriveId == ""){
	        	 Intent intent = new Intent(mContext, CreateBackupFileActivity.class);
	        	 startActivity(intent);
	         }else{
	        	 //create Backup
	        	 Intent intent = new Intent(mContext, EditBackupFileActivity.class);
	        	 startActivity(intent);
	         }
	      break;
	  case R.id.action_settings:
		 Intent intent = new Intent(mContext, SettingsActivity.class);
	 	 startActivity(intent);
		  
	    break;
	  default:
	    break;
	  }
	
	  return true;
	}

	


	private void purchaseUpgrade() {
		/* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_REQUEST,
                mPurchaseFinishedListener, payload);
	}

	private void restoreCheck() {
		
		//Would you like to restore?
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getCLH());
	
			// set title
			alertDialogBuilder.setTitle("Restore Backup File?");
	
			// set dialog message
			alertDialogBuilder
			.setMessage("Click yes to restore a backup file from Google Drive.  Warning: Existing info will be lost.")
			.setCancelable(false)
			.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					//restoreFileChooser();
					//restoreCurrentBackup();
					Intent i = new Intent(mContext, PickBackupFileWithOpenerActivity.class);
					getCLH().startActivity(i);
				}
			  })
			.setNegativeButton("No",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});
			
	
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
	
			// show it
			alertDialog.show();
		
	}

	public void restoreFileChooser() {
		Intent intent = new Intent(mContext, PickBackupFileWithOpenerActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		
				
		switch (requestCode) {
	        //...
	        case RESOLVE_CONNECTION_REQUEST_CODE:
	            if (resultCode == RESULT_OK) {
	                mApiClient.connect();
	            }
	            break;
	            
	        case COMPLETE_AUTHORIZATION_REQUEST_CODE:
	            if (resultCode == Activity.RESULT_OK) {
	                // App is authorized, you can go back to sending the API request
	            } else {
	                // User denied access, show him the account chooser again
	            }
	            break;
	
	    }
		
		Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
				+ data);

				// Pass on the activity result to the helper for handling
				if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
					super.onActivityResult(requestCode, resultCode, data);
					Log.d(TAG, "onActivityResult handled by super.ActivityResult.");
				} else {
					Log.d(TAG, "onActivityResult handled by IABUtil.");
				}
		
	}

	/** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }
    
		
	

	private String log;

	

	protected static String vcyear;

	protected static String vcname;

	protected  static String vcmake;

	protected static String vcmodel;

	@Override
    public void onConnectionFailed(ConnectionResult result) {
        // Called whenever the API client fails to connect.
        Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
        if (!result.hasResolution()) {
            // show the localized error dialog.
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization
        // dialog is displayed to the user.
        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }
    
    @Override
	public void onConnectionSuspended(int arg0) {
    	Log.i(TAG, "API client suspended.");
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		Log.i(TAG, "API client connected.");
	}

	@Override
    protected void onResume() {
    	super.onResume();
    	if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addApi(Plus.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
        
        refresh();
    }
    /**
    * Called when activity gets invisible. Connection to Drive service needs to
    * be disconnected as soon as an activity is invisible.
    */
        @Override
        protected void onPause() {
            super.onPause();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        }
        
        @Override
        protected void onStop() {
            super.onPause();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        }
         
        @Override
        public void onDestroy() {
        	super.onDestroy();
        	if (mHelper != null) mHelper.dispose();
        		mHelper = null;
        	if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        }
        
    public static void SaveStringPreferences(String key, String value){
        SharedPreferences.Editor pEditor = prefs.edit();
        pEditor.putString(key, value);
		pEditor.commit();
        
    }
    
    public static void SaveBooleanPreferences(String key, Boolean value){
        SharedPreferences.Editor pEditor = prefs.edit();
        pEditor.putBoolean(key, value);
		pEditor.commit();
        
    }
    public static void SaveIntegerPreferences(String key, int value){
        SharedPreferences.Editor pEditor = prefs.edit();
        pEditor.putInt(key, value);
		pEditor.commit();
        
    }
    
 public static void buildLog() {
	String newlog = "";
	for(int i = 0; i<getVehicleList().size(); i++){
		newlog += getVehicleList().get(i).toLog();
		Log.i(TAG, "BuildLog added = " + getVehicleList().get(i).toLog());
		//if(getVehicleList().get(i).getEntries().size() > 0){
		
		//}
	}
	
	//sort entries by date
	for(int i = 0; i<getVehicleList().size(); i++){
		for(int j= 0; j<getVehicleList().get(i).getEntries().size(); j++){
			Collections.sort(getVehicleList().get(i).getEntries(), new Comparator<String>() {
		        @Override
		        public int compare(String s1, String s2) {
		            return s1.compareToIgnoreCase(s2);
		        }
		    });
		}
	}
	
	
	
	for(int i = 0; i<getVehicleList().size(); i++){
		for(int j= 0; j<getVehicleList().get(i).getEntries().size(); j++){
			newlog += getVehicleList().get(i).getEntries().get(j);
			Log.i(TAG, "BuildLog added = " + getVehicleList().get(i).getEntries().get(j));
		}
	}
	
	Log.i(TAG, "Log built = " + newlog);
	SaveStringPreferences("Log", newlog);
		
	}

 static void readLog(){
	String log = prefs.getString("Log", "");
	if(log == ""){
		setVehicleList(new ArrayList<Vehicle>());
		Vehicle def = new Vehicle("Default");
    	def.setMake("Maker");
    	def.setModel("Model");
    	def.setYear("2000");
    	
    	getVehicleList().add(def);
    	setmCurrentVehicle(0);
    	buildLog();
		return;
	}
	
	ArrayList<String> cars = new ArrayList<String>();
	setVehicleList(new ArrayList<Vehicle>());
	String[] entries = log.split(" ; ");
	if(entries.length == 0)
		return;
	//get vehicles
	for(int i = 0; i<entries.length; i++){
		String[] tmp = entries[i].split(" ");
		if(tmp.length > 1){
			if(tmp[1].equalsIgnoreCase("VEHICLE")){
				if(cars.size() == 0){
					//Log.i(TAG, "Added car " + tmp[2]);
					Vehicle v = new Vehicle(tmp[2]);
					v.setYear(tmp[3]);
					v.setMake(tmp[4]);
					v.setModel(tmp[5]);
					
					getVehicleList().add(v);
					//Log.i(TAG, "Added car: " + getVehicleList().get(i).name());
				}else{
					for(int j=0; j<cars.size(); j++){
						if(!tmp[2].equalsIgnoreCase(cars.get(j))){
							
							//Log.i(TAG, "Added car " + tmp[2]);
							Vehicle v = new Vehicle(tmp[2]);
							v.setYear(tmp[3]);
							v.setMake(tmp[4]);
							v.setModel(tmp[5]);
							
							getVehicleList().add(v);
						}
						
					}
				}
			}
		}
	}
	
	for(int i = 0; i<getVehicleList().size(); i++){
		getVehicleList().get(i).setEntries(new ArrayList<String>());
		Log.i(TAG, "ListAllVehicles - " + Integer.toString(i) + ": " + getVehicleList().get(i).name());
	}
	
	//add entries to vehicles
	entries = log.split(" ; ");
	if(entries.length == 0)
		return;
	//get vehicles
	for(int i = 0; i<entries.length; i++){
		//Log.i(TAG, "Adding "  + entries[i]);
			String[] tmp = entries[i].split(" ");
			for(int k = 0; k<tmp.length; k++){
				//Log.i(TAG, "Entries[" + Integer.toString(k) + "] = " + tmp[k]);
			if(tmp.length > 4){
				if(!tmp[1].equalsIgnoreCase("VEHICLE")){
					//Log.i(TAG, "Adding "  + entries[i]);
					if(tmp[k].equalsIgnoreCase("NAME")){
						String entryname = tmp[k+1];
						//Log.i(TAG, "entry to add to vehicle " + entryname);
						for(int j = 0 ; j < getVehicleList().size(); j++){
							//Log.i(TAG, "Does " + getVehicleList().get(j).name() + " = " + entryname + "?");
							
							if(getVehicleList().get(j).name().equalsIgnoreCase(entryname)){
								getVehicleList().get(j).getEntries().add(entries[i]  + " ; ");
								Log.i(TAG, "Added entry " + Integer.toString(i) + " to Vehicle: " + getVehicleList().get(j).name());
								
							}
						}
					}
					
					
					
				}
			}
		//if(tmp.length > 0){
			
		}
	}
	
	//for(int m = 0; m< getVehicleList().get(0).getEntries().size(); m++){
	//	Log.i(TAG, "Entry for " +  getVehicleList().get(0).name() + " = " 
	//			+ getVehicleList().get(mCurrentVehicle).getEntries().get(m));
	//}
	if(getVehicleList().size() >= 1){
		Log.i(TAG, "Log read = " + log);
		SaveStringPreferences("Log", log);
		//Toast.makeText(mContext, "No MyCarLog info.  Please try another file.", Toast.LENGTH_SHORT);
	}else{
		Toast.makeText(mContext, "No MyCarLog info.  Please try another file.", Toast.LENGTH_SHORT);
		
	}
	//buildLog();
}



public static double round(double unrounded, int precision, int roundingMode)
{
    BigDecimal bd = new BigDecimal(unrounded);
    BigDecimal rounded = bd.setScale(precision, roundingMode);
    return rounded.doubleValue();
}



	// Since this is an object collection, use a FragmentStatePagerAdapter,
	// and NOT a FragmentPagerAdapter.
 	public class DemoCollectionPagerAdapter extends FragmentPagerAdapter {
	     public DemoCollectionPagerAdapter(FragmentManager fm) {
	         super(fm);
	     }
	
	     @Override
	     public Fragment getItem(int i) {
	    	 int page = i+1;
	    	 Fragment fragment = new FillPageFragment();
	    	 switch(page){
	    	 case 1:
	    		 fragment = new FillPageFragment();
	             
	    		 break;
	    	 case 2:
	    		 fragment = new HistoryPageFragment();
	    		 break;
	    	 case 3:
	    		 fragment = new VehiclesPageFragment();
	    		 break;
	    	 case 4:
	    		 fragment = new StatsPageFragment();
	    		 break;
	    	 }
	         
	         return fragment;
	     }
	
	     @Override
	     public int getCount() {
	         return 4;
	     }
	
	     @Override
	     public CharSequence getPageTitle(int position) {
	    	 String pagename = "";
	    	 position += 1;
	    	 switch(position){
	    	 case 1:
	    		 pagename = getString(R.string.fill_page_name);
	    		 break;
	    	 case 2:
	    		 pagename = getString(R.string.history_page_name);
	    		 break;
	    	 case 3:
	    		 pagename = getString(R.string.vehicles_page_name);
	    		 break;
	    	 case 4:
	    		 pagename = getString(R.string.stats_page_name);
	    		 break;
	    		 
	    	 }
	         return pagename;
	     }
 	}

 
	public static void saveToDrive(){
		if(mBackupDriveId == null || mBackupDriveId == ""){
	    	//create Backup
			buildLog();
			
	    	 Log.i(TAG, "No Backup file Id - creating new file");
	    	 Intent intent = new Intent(mContext, CreateBackupFileActivity.class);
	    	 getCLH().startActivity(intent);
	    	 //saveCheck();
	     }else{
	    	//edit Backup
	    	 buildLog();
	    	 
	    	 Log.i(TAG, "Editing saved backup file");
	    	 Intent intent = new Intent(mContext, EditBackupFileActivity.class);
	    	 getCLH().startActivity(intent);
	     }
	}
 	// Instances of this class are fragments representing a single
 	// object in our collection.
 	public static class FillPageFragment extends Fragment implements OnClickListener{
	     public static final String ARG_OBJECT = "object";
	     private View rootView;
	     private EditText odo;
	     private EditText gal;
	     private EditText price;
	     private static TextView currVehicleText;
	     private CheckBox tankfullbox;
	     //private EditText date;
	     
	     @Override
	     public View onCreateView(LayoutInflater inflater,
	             ViewGroup container, Bundle savedInstanceState) {
	         // The last two arguments ensure LayoutParams are inflated
	         // properly.
	    	// Assign current Date and Time Values to Variables
             final Calendar c = Calendar.getInstance();
             mYear = c.get(Calendar.YEAR);
             mMonth = c.get(Calendar.MONTH);
             mDay = c.get(Calendar.DAY_OF_MONTH);
             mHour = c.get(Calendar.HOUR_OF_DAY);
             mMinute = c.get(Calendar.MINUTE);
             c.set(mYear, mMonth, mDay);
	    	 rootView = inflater.inflate(
	                 R.layout.fillup_page, container, false);
	         
	    	 currVehicleText = (TextView)rootView.findViewById(R.id.textViewFPcurrentname);
	         currVehicleText.setOnClickListener(this);
	         odo = (EditText)rootView.findViewById(R.id.editTextOdometer);
	         gal = (EditText)rootView.findViewById(R.id.editTextGallons);
	         price = (EditText)rootView.findViewById(R.id.editTextPrice);
	         //date = (EditText)rootView.findViewById(R.id.editTextDate);
             dateButton = (Button)rootView.findViewById(R.id.ButtonDate);
             dateButton.setOnClickListener(this);
             SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
             String formattedDate = sdf.format(c.getTime());
             dateButton.setText(formattedDate);
             
             tankfullbox = (CheckBox)rootView.findViewById(R.id.checkBoxTankFull);
             tankfullbox.setChecked(true);
             //tankfullbox.setOnCheckedChangeListener(listener);
             
	         Button save = (Button)rootView.findViewById(R.id.button_save);
             save.setOnClickListener(this);
	         
             Button cancel = (Button)rootView.findViewById(R.id.buttonFPCancel);
             cancel.setText("");
             cancel.setVisibility(View.VISIBLE);
             Button delete = (Button)rootView.findViewById(R.id.ButtonFPDelete);
             delete.setText("");
             delete.setVisibility(View.VISIBLE);
             
             upDate();
             
	         return rootView;
	     }
	     
	     @Override
	     public void onClick(View v) {
	         switch(v.getId()){
	         	case R.id.textViewFPcurrentname:
	         		SelectVehicleDialog picker = new SelectVehicleDialog();
            	 	picker.show(getFragmentManager(), "vehiclePicker");
            	 	changeVehicle();
	         		break;
	         
	             case R.id.button_save:
	            	 //Toast.makeText(mContext, "Fillup Saved", Toast.LENGTH_SHORT).show();
	    	         //String log  = prefs.getString("Log", "");
					if(dateButton.getText() == null){
						Toast.makeText(mContext, "No Date entered. Try again", Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(odo.getText() == null){
						Toast.makeText(mContext, "No odometer entered. Try again", Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(gal.getText() == null){
						Toast.makeText(mContext, "No gallons entered. Try again", Toast.LENGTH_SHORT).show();
						return;
					}
					
					if(price.getText() == null){
						Toast.makeText(mContext, "No price entered. Try again", Toast.LENGTH_SHORT).show();
						return;
					}
					
					String carname = getVehicleList().get(mCurrentVehicle).name();
					//Toast.makeText(mContext, log, Toast.LENGTH_SHORT).show();
	            	 String fillupentry = "ENTRY FILLUP" 
	            			 			 + " NAME " + carname
	            			 			 + " DATE " + dateButton.getText().toString() 
				            			 + " ODO " + odo.getText().toString() 
				            			 + " GAL " + gal.getText().toString() 
				            			 + " PPG " + price.getText().toString() 
				            			 + " FULL " + String.valueOf(tankfullbox.isChecked())
				            			 + " ; ";
	    	         getVehicleList().get(mCurrentVehicle).getEntries().add(fillupentry);

	    	         buildLog();
	    	         
	    	        
	    	         //backup file
	    	         //Toast.makeText(mContext, "Fill Up Saved", Toast.LENGTH_SHORT).show();
	    	         if(mBackupDriveId == null || mBackupDriveId == ""){
	    	        	//create Backup
	    	        	 Log.i(TAG, "No Backup file Id - creating new file");
	    	        	 Intent intent = new Intent(mContext, CreateBackupFileActivity.class);
	    	        	 startActivity(intent);
	    	        	 //saveCheck();
	    	         }else{
	    	        	//edit Backup
	    	        	 Log.i(TAG, "Editing saved backup file");
	    	        	 Intent intent = new Intent(mContext, EditBackupFileActivity.class);
	    	        	 startActivity(intent);
	    	         }
	    	         HistoryPageFragment.upDate();
	             break;
	             case R.id.ButtonDate:
	            	 DatePickerFragment dpicker = new DatePickerFragment();
	            	 dpicker.show(getFragmentManager(), "datePicker");
	                 break;
	         }   
	     }

		public static void upDate() {
			if(currVehicleText != null){
				currVehicleText.setText(getVehicleList().get(mCurrentVehicle).name());
			}
			
		}
	     
	     
	 }
 	public static class HistoryPageFragment extends Fragment implements OnLongClickListener{
	     public static final String ARG_OBJECT = "object";
	     private static View rootView;
	     public static LinearLayout history;
	     public static TextView historytitle;
	     private static int lastid;
	     static HistoryPageFragment hpf;
	     @Override
	     public View onCreateView(LayoutInflater inflater,
	             ViewGroup container, Bundle savedInstanceState) {
	         // The last two arguments ensure LayoutParams are inflated
	         // properly.
	         hpf = this;
	    	 
	         rootView = inflater.inflate(
	                 R.layout.history_page, container, false);
	         
	         
	         historytitle = (TextView)rootView.findViewById(R.id.textViewHPName);
	         history = (LinearLayout)rootView.findViewById(R.id.linearLayoutHistory);
	         //history.setOnClickListener(this);
	         
	         upDate();
	         
	         return rootView;
	     }
	     
	     
	     
	     public static void upDate(){
	    	 if(history == null){
	    		 return;
	    	 }
	    	 readLog();
	    	 history.removeAllViewsInLayout();
	    	 String[] tmp;
	    	 
	    	 historytitle.setText(getVehicleList().get(mCurrentVehicle).name());
	    	 //String[] entries;
	    	 //String carlog = prefs.getString("Log", "");
	    	 //entries = carlog.split(";");
	    	 //if(getVehicleList().get(mCurrentVehicle).getEntries().size() > 0){
	    		 
	    	 if(getVehicleList().size() > 0 && getVehicleList().get(mCurrentVehicle).getEntries().size() > 0)
	    		 for(int j = 0; j<getVehicleList().get(mCurrentVehicle).getEntries().size(); j++){
	    			 tmp = getVehicleList().get(mCurrentVehicle).getEntries().get(j).split(" ");
	    			 //Log.i(TAG, "History Entry " + Integer.toString(j) + " = " + getVehicleList().get(mCurrentVehicle).getEntries().get(j));
	    			 //if(tmp.length > 0){
	    			 //boolean cancompute;
	    			 String[] last = null;
	    			 if(j > 0){
	    				 last = getVehicleList().get(mCurrentVehicle).getEntries().get(j-1).split(" ");
	    			 }
	    			 
			    	 if(tmp[1].equalsIgnoreCase("FILLUP")){
			    		 //if(tmp[4].equalsIgnoreCase(getVehicleList().get(mCurrentVehicle).name())){
			    			 FrameLayout entry = new FrameLayout(mContext);
			    			 LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 120);
			    			 entry.setLayoutParams(params);
			    			 entry.setId(j);
			    			 entry.setOnLongClickListener(hpf);
			    			 
			    			 String odometer = tmp[7];
			    			 String date = tmp[5];
			    			 String gallons = tmp[9];
			    			 String ppg = tmp[11];
			    			 String full = tmp[13];
			    			 
			    			 TextView edate = new TextView(mContext);
			    			 edate.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
			    			 edate.setText(date);
			    			 edate.setTextColor(Color.BLACK);
			    			 edate.setTextSize(20);
			    			 
			    			 entry.addView(edate);
			    			 
	
			    			 TextView eodo = new TextView(mContext);
			    			 eodo.setGravity(Gravity.RIGHT);
			    			 if(j>0 && Boolean.valueOf(full)){
			    				 Double milesdriven = Double.parseDouble(odometer) - Double.parseDouble(last[7]);
			    				 Double mpg = milesdriven / Double.parseDouble(gallons);
			    				 mpg = (double)Math.round(mpg * 100) / 100;
			    				 eodo.setText(mpg + "mpg");
			    			 }
			    			 else{
			    				 eodo.setText(String.valueOf("--"));
			    			 }
			    			 eodo.setTextColor(Color.BLACK);
			    			 eodo.setTextSize(18);
			    			 
			    			 entry.addView(eodo);
			    			 
			    			 TextView egal = new TextView(mContext);
			    			 egal.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
			    			 egal.setTextColor(Color.BLACK);
			    			 egal.setText(gallons + " gal");
			    			 
			    			 entry.addView(egal);
			    			 
			    			 
			    			 
			    			 TextView eprice = new TextView(mContext);
			    			 eprice.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
			    			 //if(j>0){
			    				 Double totalcost = Double.parseDouble(ppg) * Double.parseDouble(gallons);
			    				 totalcost = (double) (Math.round(totalcost * 100) / 100);
			    				 DecimalFormat df = new DecimalFormat("#.00");
			    				 df.format(totalcost);
			    				 eprice.setText("$"+totalcost);
			    			 //
			    			 //}
			    			 eprice.setTextColor(Color.BLACK);
			    			 
			    			 entry.addView(eprice);
			    			 
			    			 
			    			 history.addView(entry, 0);
			    			 
			    			 FrameLayout line = new FrameLayout(mContext);
			    			 params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 10);
			    			 line.setLayoutParams(params);
			    			 line.setBackgroundColor(Color.GRAY);
			    			 line.setPadding(5, 5, 5, 5);
			    			 history.addView(line, 0);
			    			 
			    			 history.postInvalidate();
			    			 lastid = j;
			    		 }
			    	 }
		    	 //}
	    	 //}
	    	//}
	    	 //rootView.add
	    }




		@Override
		public boolean onLongClick(View v) {
			if(v.getId() < getVehicleList().get(mCurrentVehicle).getEntries().size()){
				Bundle ebundle = new Bundle();
				int index = v.getId();
				ebundle.putInt("Index", index);
				
				EditFillupFragment editor = new EditFillupFragment();
	        	 editor.setArguments(ebundle);
	        	 editor.show(getFragmentManager(), "editFillup");
				
			}
			return false;
		}
	     
	     
	 }
 	
 	public static class StatsPageFragment extends Fragment {
	     public static final String ARG_OBJECT = "object";
		private View rootView;
		public static LinearLayout stats;
	
	     @Override
	     public View onCreateView(LayoutInflater inflater,
	             ViewGroup container, Bundle savedInstanceState) {
	         // The last two arguments ensure LayoutParams are inflated
	         // properly.
	    	 
	         rootView = inflater.inflate(
	                 R.layout.stats_page, container, false);
	         stats = (LinearLayout)rootView.findViewById(R.id.linearLayoutStats);
	    	 
	         
	         
	         upDate();
	         return rootView;
	     }

		public static void upDate() {
			if(stats == null){
	    		 return;
	    	 }
	    	 readLog();
	    	 getVehicleList().get(mCurrentVehicle).calcStats();
	    	 
	    	 stats.removeAllViewsInLayout();
	    	 
	    	 FrameLayout title;
	    	 FrameLayout value;
	    	 
	    	 title = createStatTitle("Average Miles Per Gallon");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getAverageMPG());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Distance");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getAverageDistance());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Total Cost");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getAverageTotalCost());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Total Gallons");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getAverageTotaGallons());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Price Per Gallon");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getAveragePricePerGallon());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 
			 title = createStatTitle("Average Cost Per Mile");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getAveragePricePerGallon());
	    	 stats.addView(title);
			 stats.addView(value);
	    	 
	    	 title = createStatTitle("Total Distance");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getTotalDistance());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Total Fuel Gallons");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getTotalTotalGallons());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Total Time");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getTotalTime());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Total Cost");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getTotalTotalCost());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Maximum Days Between Fillups");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMaximumTime());
	    	 stats.addView(title);
			 stats.addView(value);
	    	 
	    	 title = createStatTitle("Minimum Days Between Fillups");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMinimumTime());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Days Between Fillups");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getAverageTime());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Maximum Distance");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMaximumDistance());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Minimum Distance");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMinimumDistance());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Maximum Total Gallons");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMaximumTotalGallons());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Minimum Total Gallons");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMinimumTotalGallons());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Maximum Price Per Gallon");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getMaximumPricePerGallon());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Minimum Price Per Gallon");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getMinimumPricePerGallon());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Cost Per Day");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getCostperday());
	    	 stats.addView(title);
			 stats.addView(value);
			 title = createStatTitle("Average Cost Per Month");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getCostpermonth());
	    	 stats.addView(title);
			 stats.addView(value);
			 title = createStatTitle("Average Cost Per Year");
	    	 value = createStatLine("$ " + getVehicleList().get(mCurrentVehicle).getCostperyear());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Fuel Per Day");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getGallonsperday());
	    	 stats.addView(title);
			 stats.addView(value);
			 title = createStatTitle("Average Fuel Per Month");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getGallonspermonth());
	    	 stats.addView(title);
			 stats.addView(value);
			 title = createStatTitle("Average Fuel Per Year");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getGallonsperyear());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 title = createStatTitle("Average Miles Per Day");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMilesperday());
	    	 stats.addView(title);
			 stats.addView(value);
			 title = createStatTitle("Average Miles Per Month");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMilespermonth());
	    	 stats.addView(title);
			 stats.addView(value);
			 title = createStatTitle("Average Miles Per Year");
	    	 value = createStatLine(getVehicleList().get(mCurrentVehicle).getMilesperyear());
	    	 stats.addView(title);
			 stats.addView(value);
			 
			 
			 stats.postInvalidate();
			
		}

		private static FrameLayout createStatTitle(String title) {
			FrameLayout line = new FrameLayout(mContext);
	    	 LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			 line.setLayoutParams(params);
			 line.setBackgroundColor(Color.LTGRAY);
			 line.setPadding(5, 5, 5, 5);
				 TextView totaldist = new TextView(mContext);
				 totaldist.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
				 totaldist.setText(title);
				 totaldist.setTextColor(Color.BLACK);
				 totaldist.setTextSize(15);
			 
				 line.addView(totaldist);
			return line;
		}

		private static FrameLayout createStatLine(String stat) {
			FrameLayout entry = new FrameLayout(mContext);
			 LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			 entry.setLayoutParams(params);
			 
			 TextView line = new TextView(mContext);
			 line.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			 line.setText(stat);
			 line.setTextColor(Color.DKGRAY);
			 line.setTextSize(20);
			 
			 entry.addView(line);
			return entry;
		}
	 }
 	
 	public static class VehiclesPageFragment extends Fragment {
	     public static final String ARG_OBJECT = "object";
		private View rootView;
		private Button mNewButton;
		private Button mChangeButton;
		private Button mDeleteButton;
		private static TextView makeText;
		private static TextView nameText;
		private static TextView modelText;
		private static TextView yearText;
	
	     @Override
	     public View onCreateView(LayoutInflater inflater,
	             ViewGroup container, Bundle savedInstanceState) {
	         // The last two arguments ensure LayoutParams are inflated
	         // properly.
	         rootView = inflater.inflate(
	                 R.layout.activity_vehicles_page, container, false);
	         
	         mNewButton = (Button)rootView.findViewById(R.id.ButtonVPNew);
	         mNewButton.setOnClickListener(new OnClickListener(){
	        	 @Override
	        	 public void onClick(View v){
	        		 //Toast.makeText(mContext, "New Vehicle Added", Toast.LENGTH_SHORT).show();
	        		 CreateVehicleFragment picker = new CreateVehicleFragment();
	            	 	picker.show(getFragmentManager(), "createVehicle");
	            	 	
	        		 
	        	 }
	         });
	         mChangeButton = (Button)rootView.findViewById(R.id.ButtonVPChange);
	         mChangeButton.setOnClickListener(new OnClickListener(){
	        	 @Override
	        	 public void onClick(View v){
	        		 //Toast.makeText(mContext, "Changed Current Vehicle", Toast.LENGTH_SHORT).show();
	        		 SelectVehicleDialog picker = new SelectVehicleDialog();
	            	 	picker.show(getFragmentManager(), "vehiclePicker");
	        		 changeVehicle();
	        	 }
	         });
	         mDeleteButton = (Button)rootView.findViewById(R.id.ButtonVPDelete);
	         mDeleteButton.setOnClickListener(new OnClickListener(){
	        	 @Override
	        	 public void onClick(View v){
	        		 Toast.makeText(mContext, "Vehicle Deleted", Toast.LENGTH_SHORT).show();
	 				 deleteVehicle();
	 				saveToDrive();
	        	 }
	         });
	         
	         makeText = (TextView) rootView.findViewById(R.id.textViewVPMake);
	         modelText = (TextView) rootView.findViewById(R.id.textViewVPModel);
	         yearText = (TextView) rootView.findViewById(R.id.textViewVPYEAR);
	         nameText = (TextView) rootView.findViewById(R.id.textViewVPname);
	         upDate();
	         return rootView;
	     }

		public static void upDate() {
			makeText.setText(getVehicleList().get(mCurrentVehicle).getMake());
			modelText.setText(getVehicleList().get(mCurrentVehicle).getModel());
			yearText.setText(getVehicleList().get(mCurrentVehicle).getYear());
			nameText.setText(getVehicleList().get(mCurrentVehicle).name());
		}

		
	 }
 	
 	public static class DatePickerFragment extends DialogFragment 
	implements DatePickerDialog.OnDateSetListener {

 		String mStartDate = null;
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH); 
		
		
		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getCLH(), this, year, month, day);
		}
		
		@Override
		public void onDateSet(DatePicker view, int year, int month, int day) {
			Calendar c = Calendar.getInstance();
			c.set(year, month, day);
			
			SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
			String formattedDate = sdf.format(c.getTime());
			dateButton.setText(formattedDate);
			
		}
 	}

	

	/**
	* Shows a toast message.
	*/
	    public static void showMessage(String message) {
	        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
	    }
	    
	    /**
	    * Getter for the {@code GoogleApiClient}.
	    */
	        public GoogleApiClient getGoogleApiClient() {
	          return mGoogleApiClient;
	        }

		public static void refresh() {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
				//rogressBar.setProgress(value);
					if(HistoryPageFragment.history != null)
						HistoryPageFragment.upDate();
					
					if(FillPageFragment.currVehicleText != null)
						FillPageFragment.upDate();
					
					
				
					if(StatsPageFragment.stats != null)
						StatsPageFragment.upDate();
				}
					
				});
			
			
		}

		public static void restoreFromDriveFile(DriveId driveId) {
			setmBackupDriveId(driveId.toString());
			
			Intent i = new Intent(mContext, RetrieveBackupContentsActivity.class);
			getCLH().startActivity(i);
		}

		public static String getmBackupDriveId() {
			return mBackupDriveId;
		}

		public static void setmBackupDriveId(String id) {
			SaveStringPreferences("BackupFileId", id);
			mBackupDriveId = id;
		}

		public static void setLocalBackup(String filelocation) {
			mLocalBackupLocation = filelocation;
			SaveStringPreferences("LocalBackupLocation", filelocation);
		}
		
		public static String getmLocalBackup() {
			return mBackupDriveId;
		}

		public static String getmAccountName() {
			return mAccountName;
		}

		public static void setmAccountName(String accountName) {
			mAccountName = accountName;
			SaveStringPreferences("AccounwtName", mAccountName);
		}

		public static void chooseAccount() {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
		      mGoogleApiClient.disconnect();
		      mGoogleApiClient.connect();
		      setmBackupDriveId("");
		}

		public static CarLogHome getCLH() {
			return CLH;
		}

		public static void setCLH(CarLogHome cLH) {
			CLH = cLH;
		}

		public static int getmCurrentVehicle() {
			return mCurrentVehicle;
		}

		public static void setmCurrentVehicle(int i) {
			SaveIntegerPreferences("CurrentVehicle", i);
			CarLogHome.mCurrentVehicle = i;
		}

		public static ArrayList<Vehicle> getVehicleList() {
			return mVehicleList;
		}

		public static void setVehicleList(ArrayList<Vehicle> mVehicleList) {
			CarLogHome.mVehicleList = mVehicleList;
		}
		
		public String getmAllVehicleInfo() {
			return mAllVehicleInfo;
		}

		public void setmAllVehicleInfo(String allVehicleInfo) {
			SaveStringPreferences("AllVehicleInfo", allVehicleInfo);
			mAllVehicleInfo = allVehicleInfo;
		}
		
		private static void deleteVehicle(){
			getVehicleList().remove(mCurrentVehicle);
			
			if(getVehicleList().size() < 1){
				Vehicle v = new Vehicle("Default");
				v.setYear("2000");
				v.setMake("Maker");
				v.setModel("Model");
				setVehicleList(new ArrayList<Vehicle>());
				for(int i = 0; i < getVehicleList().size(); i++){
					getVehicleList().get(i).setEntries(new ArrayList<String>());
				}
				mCurrentVehicle = 0;
			}
			buildLog();
			if(mCurrentVehicle >= getVehicleList().size()){
				mCurrentVehicle = 0;
			}
			
			//changeVehicle();
			refreshVehicles();
		}
		
		

		static void newVehicle(Vehicle car){
			// custom dialog
			
			
			boolean isnewcar = true;
			for(int i = 0; i<getVehicleList().size(); i++){
				if(getVehicleList().get(i).name().equalsIgnoreCase(car.name())){
					Toast.makeText(mContext, "Vehicle already exists", Toast.LENGTH_SHORT).show();
					isnewcar = false;
				}
			}
			if(isnewcar){
				
				getVehicleList().add(car);
				buildLog();
				
				
			}
			refreshVehicles();
			saveToDrive();
		}
		private static void changeVehicle(){

			
			refreshVehicles();
		}
		
		public static void refreshVehicles() {
			mHandler.post(new Runnable(){
				@Override
				public void run(){
					if(VehiclesPageFragment.nameText != null
							&& VehiclesPageFragment.modelText != null
							&& VehiclesPageFragment.makeText != null
							&& VehiclesPageFragment.yearText != null){
						//readLog();
						VehiclesPageFragment.upDate();
						
					}
					refresh();
				}
			});
		}
		
		
}


