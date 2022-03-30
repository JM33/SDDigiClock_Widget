package com.sd.sddigiclock;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context mContext;
    private View mView;
    int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutFragment newInstance(String param1, String param2) {
        AboutFragment fragment = new AboutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = container.getContext();
        mView = inflater.inflate(R.layout.fragment_about, container, false);


        Bundle bundle = this.getArguments();
        if (bundle != null) {
            appWidgetId = bundle.getInt("appWidgetID", AppWidgetManager.INVALID_APPWIDGET_ID);
        }


        DialogInterface.OnClickListener aboutListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goHome();
                dialog.dismiss();
            }
        };
        new AlertDialog.Builder(mContext)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.p_about_info))

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string.bgs_ok, aboutListener)
                .setIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.logo, mContext.getTheme()))
                .show();

        return mView;
    }

    private void goHome(){
        // Create new fragment and transaction
        Fragment newFragment = new SettingsHomeFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.nav_host_fragment, newFragment);
        transaction.addToBackStack(null);
        DigiClockPrefs.setTitle("Home");
        // Commit the transaction
        transaction.commit();


    }
}