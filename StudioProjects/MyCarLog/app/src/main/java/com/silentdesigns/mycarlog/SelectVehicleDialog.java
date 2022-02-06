package com.sd.mycarlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SelectVehicleDialog extends DialogFragment{
	private View rootView;

	@Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
		 rootView = inflater.inflate(
                 R.layout.select_vehicle_dialog, container, false);
		 getDialog().setTitle("Select Vehicle");
		 
		 
		 final ListView listview = (ListView) rootView.findViewById(R.id.listViewSelectVehicle);
		    String[] values = new String[CarLogHome.getVehicleList().size()];
		    for(int i =0; i < CarLogHome.getVehicleList().size(); i++){
		    	values[i] = CarLogHome.getVehicleList().get(i).name();
		    }

		    final ArrayList<String> list = new ArrayList<String>();
		    for (int i = 0; i < CarLogHome.getVehicleList().size(); ++i) {
		      list.add( CarLogHome.getVehicleList().get(i).name());
		    }
		    final StableArrayAdapter adapter = new StableArrayAdapter(this.getActivity().getApplicationContext(),
		        android.R.layout.simple_list_item_1, list){ 
		    	@Override
		        public View getView(int position, View convertView,
		                ViewGroup parent) {
		            View view =super.getView(position, convertView, parent);

		            TextView textView=(TextView) view.findViewById(android.R.id.text1);

		            /*YOUR CHOICE OF COLOR*/
		            textView.setTextColor(Color.DKGRAY);

		            return view;
		        };
		    };
		    listview.setAdapter(adapter);

		    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view,
		          final int position, long id) {
		        //final String item = (String) parent.getItemAtPosition(position);
		        view.animate().setDuration(2000).alpha(0);
		            	 
		            	  
		               
		        CarLogHome.setmCurrentVehicle(position);
		        CarLogHome.refreshVehicles();
		        dismiss();
		      }
		    });
		  
		 
		 
		 return rootView;
	}
	
	class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }

}
