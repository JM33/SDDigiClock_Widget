package com.sd.sddigiclock;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AppSelector extends AppCompatActivity {

    List<ApplicationInfo> packages;
    String[] allPackageNames;

    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        ListView mListView = (ListView)findViewById(R.id.ListViewAppSelect);

        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        allPackageNames = new String[packages.size()];



        final List<ResolveInfo> apps = installedApps();

        String[] allLabels = new String[apps.size()];
        final Drawable [] allIcons = new Drawable[apps.size()];

        for(ResolveInfo ri : apps) {
            // to get drawable icon -->  ri.loadIcon(package_manager)
            allPackageNames[apps.indexOf(ri)] = ri.activityInfo.applicationInfo.packageName;
            allLabels[apps.indexOf(ri)] = ri.loadLabel(pm).toString();
            allIcons[apps.indexOf(ri)] = ri.loadIcon(pm);
        }

        /*
        for (ApplicationInfo packageInfo : packages) {
            //Log.d("UWS", "Installed package :" + packageInfo.packageName);
            //Log.d("UWS", "Source dir : " + packageInfo.sourceDir);
            //Log.d("UWS", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

            int index = packages.indexOf(packageInfo);
            allLabels[index] = pm.getApplicationLabel(packageInfo).toString();
            allPackageNames[index] = packageInfo.packageName;

                allIcons[index] = ri(packageInfo);
                Log.d("SDDC", "ICON FOUND = " + allIcons[index].toString());
        }
    */

        //ArrayAdapter adapter = new ArrayAdapter<String>(this,
        //        R.layout.listview_text, allLabels);


        // Create an ArrayAdapter from List
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, R.layout.listview_text, allLabels){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(R.id.label);
                Drawable icon = allIcons[position];
                tv.setCompoundDrawables(icon, null, null, null);


                // Generate ListView Item using TextView
                return view;
            }
        };


        mListView.setAdapter(adapter);



        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String pname = allPackageNames[position];
                UpdateWidgetService.setClockButtonApp(pname);
                Log.d("SDDC", "Selected: " + pname);
                finish();
            }

        });
    }

    private List<ResolveInfo> installedApps() {
        final Intent main_intent = new Intent(Intent.ACTION_MAIN, null);
        main_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return pm.queryIntentActivities(main_intent, 0);
    }

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    private Drawable resize(Drawable image) {
        Bitmap b = getBitmapFromDrawable(image);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 64, 64, false);
        return new BitmapDrawable(getResources(), b);
    }
}
