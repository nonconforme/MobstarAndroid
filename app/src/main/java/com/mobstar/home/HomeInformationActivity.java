package com.mobstar.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.mobstar.R;
import com.mobstar.api.new_api_model.DefaultNotification;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.utils.AppRater;
import com.mobstar.utils.UserPreference;
import com.squareup.picasso.Picasso;

public class HomeInformationActivity extends Activity implements View.OnClickListener {

	public static final String DEFAULT_NOTIFICATION   = "default_notification";
	private Context mContext;
	private TextView textTitle,textDes;
	private ImageView imgInfo;
	private ImageButton btnClose;
	private DefaultNotification defaultNotification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_home_info);
		mContext=HomeInformationActivity.this;
		getBundleExtra();
		AppRater.app_launched(mContext);
		findViews();
		setListeners();
		setupViews();
	}

	private void getBundleExtra(){
		final Bundle bundle = getIntent().getExtras();
		if(bundle != null) {
			if (bundle.containsKey(DEFAULT_NOTIFICATION))
				defaultNotification = (DefaultNotification) bundle.getSerializable(DEFAULT_NOTIFICATION);
		}
	}

	private void findViews() {
		textTitle   = (TextView)findViewById(R.id.textTitle);
		textDes     = (TextView)findViewById(R.id.textDes);
		imgInfo     = (ImageView)findViewById(R.id.imgInfo);
		btnClose    = (ImageButton) findViewById(R.id.btnClose);
	}

	private void setListeners(){
		btnClose.setOnClickListener(this);
	}

	private void setupViews(){
		textTitle.setText(defaultNotification.getDescription());
		textDes.setText(defaultNotification.getTitle());
		Picasso.with(mContext).load(defaultNotification.getImage()).into(imgInfo);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnClose:
				verifyUserContinent();
				break;
		}
	}

	private void verifyUserContinent(){
		if (UserPreference.existUserContinent(this))
			startHomeActivity();
		else startSelectCurrentRegionActivity();
	}

	private void startSelectCurrentRegionActivity(){
		final Intent intent = new Intent(this, SelectCurrentRegionActivity.class);
		startActivity(intent);
		finish();
	}

	private void startHomeActivity(){
		Intent intent = new Intent(mContext, HomeActivity.class);
		startActivity(intent);
		finish();
	}
	

}
