package com.mobstar.login.who_to_follow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.new_api_call.ProfileCall;
import com.mobstar.api.new_api_model.WhoToFollowUser;
import com.mobstar.api.new_api_model.response.SuccessResponse;
import com.mobstar.api.new_api_model.response.WhoToFollowResponse;
import com.mobstar.api.responce.Error;
import com.mobstar.geo_filtering.SelectCurrentRegionActivity;
import com.mobstar.home.HomeActivity;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

import java.util.ArrayList;

public class WhoToFollowActivity extends Activity implements OnClickListener{

	private GridView grid;
	private Button btnSkip,btnFollow;
	private LinearLayout llBottom;
	private WhoToFollowAdapter whoToFollowAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_who_to_follow);
		findViews();
		setListeners();
		getWhoToFollowUserRequest();
		Utility.SendDataToGA("WhoToFollow Screen", WhoToFollowActivity.this);

	}

	private void findViews() {
		grid        = (GridView)findViewById(R.id.gried_whoTOFollow);
		llBottom    = (LinearLayout)findViewById(R.id.llBottom);
		btnFollow   = (Button)findViewById(R.id.btnFollow);
		btnSkip     = (Button)findViewById(R.id.btnSkip);
	}

	private void setListeners(){
		btnFollow.setOnClickListener(this);
		btnSkip.setOnClickListener(this);
	}

	private void getWhoToFollowUserRequest(){
		Utility.ShowProgressDialog(this, getString(R.string.loading));
		ProfileCall.getWhoToFollowUsers(this, new ConnectCallback<WhoToFollowResponse>() {
			@Override
			public void onSuccess(WhoToFollowResponse object) {
				Utility.HideDialog(WhoToFollowActivity.this);
				createWhoToFollowList(object);
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(WhoToFollowActivity.this);
			}

			@Override
			public void onServerError(Error error) {
				Utility.HideDialog(WhoToFollowActivity.this);
			}
		});
	}

	private void createWhoToFollowList(final WhoToFollowResponse whoToFollowResponse){
		whoToFollowAdapter = new WhoToFollowAdapter(this, whoToFollowResponse.getUser());
		grid.setAdapter(whoToFollowAdapter);
	}


	@Override
	public void onClick(View view) {
		whoToFollowAdapter.notifyDataSetChanged();
		switch (view.getId()){
			case R.id.btnFollow:
				postFollowUserRequest(getFollowUsersArr());
				break;
			case R.id.btnSkip:
				verifyUserContinent();
				break;
		}
	}

	private String getFollowUsersArr(){
		String star = "";
		final ArrayList<WhoToFollowUser> whoToFollowUsers = whoToFollowAdapter.getWhoToFollowList();
		for (int i = 0; i < whoToFollowUsers.size(); i++) {
			if(whoToFollowUsers.get(i).isChecked()){
				if(star.equals(null) || star.equalsIgnoreCase("")){
					star = whoToFollowUsers.get(i).getId();
				}
				else {
					star = star + "," + whoToFollowUsers.get(i).getId();
				}
			}
		}
		return star;
	}

	private void postFollowUserRequest(final String usersId){
		Utility.ShowProgressDialog(this, getString(R.string.loading));
		ProfileCall.followUsers(this, usersId, new ConnectCallback<SuccessResponse>() {
			@Override
			public void onSuccess(SuccessResponse object) {
				Utility.HideDialog(WhoToFollowActivity.this);
				verifyUserContinent();
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(WhoToFollowActivity.this);
			}

			@Override
			public void onServerError(Error error) {
				Utility.HideDialog(WhoToFollowActivity.this);
			}
		});
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

	private void startHomeActivity() {
		final Intent intent = new Intent(this, HomeActivity.class);
		intent.putExtra("isHomeInfo",true);
		startActivity(intent);
		finish();
	}
}
