package com.mobstar.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.utils.Utility;

public class SettingsFragment extends Fragment implements OnClickListener {

    private static final String LINK_PRIVACY_POLICE = "http://www.mobstar.com/privacy-policy/";
    private Context mContext;
	private TextView textSettings;
	private SharedPreferences preferences;
	private boolean isSocial;
	private LinearLayout btnPersonDetails, btnChangePassword, btnLinkedAccounts, btnPrivacySettings, btnContinentSetting;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_settings, container, false);

		mContext = getActivity();
		preferences = getActivity().getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		isSocial=preferences.getBoolean("isSocialLogin",false);
		Utility.SendDataToGA("Settings Screen", getActivity());
		
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		textSettings = (TextView) view.findViewById(R.id.textSettings);
		textSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		btnContinentSetting = (LinearLayout) view.findViewById(R.id.btnLocationSetting);
		btnContinentSetting.setOnClickListener(this);

		btnPersonDetails = (LinearLayout) view.findViewById(R.id.btnPersonDetails);
		btnPersonDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				if(!isSocial){
					Intent intent = new Intent(mContext, PersonDetailsActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
				

			}
		});

		btnChangePassword = (LinearLayout) view.findViewById(R.id.btnChangePassword);
		btnChangePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!isSocial){
					Intent intent = new Intent(mContext, ChangePasswordActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);	
				}
			}
		});

		btnLinkedAccounts = (LinearLayout) view.findViewById(R.id.btnLinkedAccounts);
		btnLinkedAccounts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

				// set title
				alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

				// set dialog message
				alertDialogBuilder.setMessage(getString(R.string.coming_soon)).setCancelable(false).setNeutralButton("OK", null);

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
//				Intent intent = new Intent(mContext, LinkedAccountsActivity.class);
//				startActivity(intent);
//				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		});

		btnPrivacySettings = (LinearLayout) view.findViewById(R.id.btnPrivacySettings);
		btnPrivacySettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(mContext, PrivacySettingsActivity.class);
//				startActivity(intent);
//				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LINK_PRIVACY_POLICE));
				startActivity(browserIntent);

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnLocationSetting:
				startLocationSettingActivity();
				break;
		}
	}

	private void startLocationSettingActivity(){
		final Intent intent = new Intent(getActivity(), SelectCurrentRegionActivity.class);
		intent.putExtra(SelectCurrentRegionActivity.START_HOME_ACTIVITY, false);
		getActivity().startActivity(intent);
	}
}
