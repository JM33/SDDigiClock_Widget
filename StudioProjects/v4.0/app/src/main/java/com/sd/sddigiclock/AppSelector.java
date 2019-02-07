package com.sd.sddigiclock;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

public class AppSelector extends AppCompatActivity {

    List<ApplicationInfo> packages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);

        ListView mListView = (ListView)findViewById(R.id.ListViewAppSelect);

        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo packageInfo : packages) {
            Log.d("UWS", "Installed package :" + packageInfo.packageName);
            Log.d("UWS", "Source dir : " + packageInfo.sourceDir);
            Log.d("UWS", "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }
    }
}
