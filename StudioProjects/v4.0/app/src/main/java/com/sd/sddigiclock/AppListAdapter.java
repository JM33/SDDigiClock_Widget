package com.sd.sddigiclock;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 1/15/2016. Modified by Brian Kimmel 2/9/2019
 * source: https://www.reddit.com/r/androiddev/comments/413hkm/list_view_with_all_the_installed_apps_and_their/
 */
public class AppListAdapter implements ListAdapter {
    public AppListAdapter(Context context) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mContext = context;
        mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPackageManager = context.getPackageManager();
        mPackageInfos = mPackageManager.getInstalledPackages(0);
        mHandlerThread = new HandlerThread("AppListAdapterHandler");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        for (int i = 0; i < mPackageInfos.size(); i++) {
            mHandler.post(loadPackageInfo(mPackageInfos.get(i)));
        }
    }

    private Runnable loadPackageInfo(final PackageInfo packageInfo) {
        return new Runnable() {
            @Override
            public void run() {
                final String packageName = packageInfo.packageName;

                synchronized (mLock) {
                    if (mExtraData.containsKey(packageName)) {
                        return;
                    }
                }

                final CharSequence appName = packageInfo.applicationInfo.loadLabel(mPackageManager);
                final Drawable drawable = packageInfo.applicationInfo.loadIcon(mPackageManager);

                final ExtraPackageInfo extraPackageInfo = new ExtraPackageInfo(packageName, appName, drawable);

                View pendingView;
                synchronized (mLock) {
                    // We save this package's info,
                    mExtraData.put(packageName, extraPackageInfo);
                    // and we clear this view from the views pending updates.
                    pendingView = mPendingViews.remove(packageName);
                    if (pendingView != null) {
                        mPendingViewPackageNames.remove(pendingView);
                    }
                }

                if (pendingView != null) {
                    // Now that this one is loaded the icon info, and it was pending updates, we can update it.
                    // We have to post this to the view's thread.
                    final View view = pendingView;
                    pendingView.post(new Runnable() {
                        @Override
                        public void run() {
                            updateView(packageInfo, view);
                        }
                    });

                }

                if (mPackageInfos.size() == mExtraData.size()) {
                    // We're done loading all of the package infos...
                    mHandlerThread.quit();
                    // TODO: You probably don't want to kill the handler thread if you're going to have a BroadcastListener that will update this AppListAdapter as new packages are installed.
                }
            }
        };
    }

    private final Context mContext;
    private final PackageManager mPackageManager;
    private final LayoutInflater mLayoutInflater;
    private final List<PackageInfo> mPackageInfos;
    private final HashMap<String, ExtraPackageInfo> mExtraData = new HashMap<>();
    private final HashSet<View> mViewsPendingImages = new HashSet<>();
    private final Object mLock = new Object();
    private final HandlerThread mHandlerThread;
    private final Handler mHandler;
    private final HashMap<String, View> mPendingViews = new HashMap<>();
    private final HashMap<View, String> mPendingViewPackageNames = new HashMap<>();


    private static class ExtraPackageInfo {
        public ExtraPackageInfo(String packageName, CharSequence appName, Drawable drawable) {
            this.packageName = packageName;
            this.appName = appName;
            this.drawable = drawable;
        }
        public final String packageName;
        public final CharSequence appName;
        public final Drawable drawable;
    }



    private View updateView(final PackageInfo packageInfo, View view) {
        final String packageName = packageInfo.packageName;
        final TextView packageTextView = (TextView)view.findViewById(R.id.textView_package_name);
        final TextView appNameTextView = (TextView)view.findViewById(R.id.textView_app_name);
        final ImageView imageView = (ImageView)view.findViewById(R.id.imageView_app_icon);

        ExtraPackageInfo extraPackageInfo;
        synchronized (mLock) {
            extraPackageInfo = mExtraData.get(packageName);
            if (extraPackageInfo == null) {
                // If the extra info isn't loaded yet, we set the package name text,
                // and put loading this particular package at the front of the queue.
                packageTextView.setText(packageName);
                appNameTextView.setText(null);
                imageView.setImageDrawable(null);
                mPendingViews.put(packageName, view);
                mPendingViewPackageNames.put(view, packageName);
                mHandler.postAtFrontOfQueue(loadPackageInfo(packageInfo));

                return view;
            }
        }

        packageTextView.setText(extraPackageInfo.packageName);
        appNameTextView.setText(extraPackageInfo.appName);
        Drawable img = resize(extraPackageInfo.drawable);
        imageView.setImageDrawable(img);
        return view;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }
    @Override
    public boolean isEnabled(int position) {
        return true;
    }
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO: You should add a listener for new apps (un)installed so that you can do something with this observer when a new app is installed and update the ListView as necessary
        // Set a global BroadcastListener in the Manifest with intent-filters for Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, etc.
    }
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO: unregister the data set observer
    }
    @Override
    public int getCount() {
        return mPackageInfos.size();
    }
    @Override
    public Object getItem(int position) {
        return mPackageInfos.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public boolean hasStableIds() {
        return true;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.layout_app_listview, parent, false);
        } else {
            synchronized (mLock) {
                // We make sure that if we're still waiting for the convertView to load the image, we remove it from the queue of pending views,
                // in order to avoid a race condition for the view.
                String oldPackageName = mPendingViewPackageNames.remove(convertView);
                if (oldPackageName != null) {
                    mPendingViews.remove(oldPackageName);
                }
            }
        }

        return updateView(mPackageInfos.get(position), convertView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }
    @Override
    public int getViewTypeCount() {
        return 1;
    }
    @Override
    public boolean isEmpty() {
        return mPackageInfos.isEmpty();
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
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, 96, 96, false);
        return new BitmapDrawable(mContext.getResources(), bitmapResized);
    }
}
