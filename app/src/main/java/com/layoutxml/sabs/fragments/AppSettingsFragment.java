package com.layoutxml.sabs.fragments;

import android.app.admin.DevicePolicyManager;
import android.arch.lifecycle.LifecycleFragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.blocker.ContentBlocker56;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.dialogfragment.DnsChangeDialogFragment;
import com.layoutxml.sabs.receiver.CustomDeviceAdminReceiver;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

public class AppSettingsFragment extends LifecycleFragment {
    private static final String TAG = AppSettingsFragment.class.getCanonicalName();
    private FragmentManager fragmentManager;
    private ContentBlocker contentBlocker;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings, container, false);
        mContext = this.getActivity().getApplicationContext();
        contentBlocker = DeviceAdminInteractor.getInstance().getContentBlocker();
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        View layout;
        View seperator3;
        layout = view.findViewById(R.id.allowAppsLayout);
        seperator3 = view.findViewById(R.id.seperator3);
        if (!(contentBlocker instanceof ContentBlocker56 || contentBlocker instanceof ContentBlocker57)) { //without !, should be in else
            layout.setVisibility(View.GONE);
            seperator3.setVisibility(View.GONE);

        }

        Button deleteAppButton = view.findViewById(R.id.deleteApp);
        deleteAppButton.setOnClickListener(v -> new AlertDialog.Builder(v.getContext())
                .setTitle(getString(R.string.delete_app_dialog_title))
                .setMessage(getString(R.string.delete_app_dialog_text))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                {
                    contentBlocker.disableBlocker();
                    ComponentName devAdminReceiver = new ComponentName(mContext, CustomDeviceAdminReceiver.class);
                    DevicePolicyManager dpm = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
                    dpm.removeActiveAdmin(devAdminReceiver);
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    //Todo: Make delete function dynamic
                    String packageName = "package:" + BuildConfig.APPLICATION_ID;
                    intent.setData(Uri.parse(packageName));
                    startActivity(intent);
                })
                .setNegativeButton(android.R.string.no, null).show());


        View layout2;
        View seperator2;
        layout2 = view.findViewById(R.id.dnsLayout);
        seperator3 = view.findViewById(R.id.seperator2);
        if (!(contentBlocker instanceof ContentBlocker57)) {
            layout2.setVisibility(View.GONE);
            seperator3.setVisibility(View.GONE);
        }

        EditText knoxKeyEditText = view.findViewById(R.id.knox_key_editText);
        Button knoxKeyButton = view.findViewById(R.id.submit_knox_key_button);
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String knoxKey = sharedPreferences.getString("knox_key", null);
        if (knoxKey!=null) {
            knoxKeyEditText.setText(knoxKey);
        }
        knoxKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("knox_key", knoxKeyEditText.getText().toString());
                editor.commit();
            }
        });

        return view;
    }
}
