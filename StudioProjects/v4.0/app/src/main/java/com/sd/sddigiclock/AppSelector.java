package com.sd.sddigiclock;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

public class AppSelector extends AppCompatActivity {

    List<ApplicationInfo> packages;
    String[] allPackageNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        ListView mListView = (ListView)findViewById(R.id.ListViewAppSelect);

        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        allPackageNames = new String[packages.size()];

        String[] allLabels = new String[packages.size()];

        for (ApplicationInfo packageInfo : packages) {
            //Log.d("UWS", "Installed package :" + packageInfo.packageName);
            //Log.d("UWS", "Source dir : " + packageInfo.sourceDir);
            //Log.d("UWS", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));

            int index = packages.indexOf(packageInfo);
            allLabels[index] = pm.getApplicationLabel(packageInfo).toString();
            allPackageNames[index] = packageInfo.packageName;

        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.listview_text, allLabels);

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
}
