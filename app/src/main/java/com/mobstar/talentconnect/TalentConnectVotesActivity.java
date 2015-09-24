package com.mobstar.talentconnect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;

import com.mobstar.home.new_home_screen.HomeVideoListBaseFragment;
import com.mobstar.utils.Utility;

public class TalentConnectVotesActivity extends FragmentActivity {

	Context mContext;

	SharedPreferences preferences;

	TextView textMyVotes;
	TextView textAllEntries;

	ImageView imgMyVotes;

	boolean isYesVotes = true;

	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;

	boolean isDataLoaded = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_my_votes);

		mFragmentManager = getSupportFragmentManager();

		mContext = TalentConnectVotesActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		// Ion.getDefault(mContext).configure().setLogging("Ion", Log.DEBUG);

		Utility.SendDataToGA("TalentConnect Vote Screen", TalentConnectVotesActivity.this);

		InitControls();
	}

	void InitControls() {

		textAllEntries = (TextView) findViewById(R.id.textAllEntries);
		textAllEntries.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GetData("all");
			}
		});

		textMyVotes = (TextView) findViewById(R.id.textMyVotes);
		textMyVotes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MyVoteDialog();
			}
		});

		imgMyVotes = (ImageView) findViewById(R.id.imgMyVotes);

		if (!isDataLoaded) {
			GetData("up");
		}

		if (isYesVotes) {
			textMyVotes.setText(getString(R.string.yes_votes));
			imgMyVotes.setImageResource(R.drawable.ic_my_votes_yes);
		} else {
			textMyVotes.setText(getString(R.string.no_votes));
			imgMyVotes.setImageResource(R.drawable.ic_my_votes_no);
		}
	}

	void GetData(String VoteType) {

		HomeVideoListBaseFragment videoListFragment = new HomeVideoListBaseFragment();
		Bundle extras = new Bundle();
		extras.putBoolean("isVoteAPI", true);
		extras.putString("VoteType", VoteType);
		videoListFragment.setArguments(extras);
		replaceFragment(videoListFragment, "VideoListFragment");

		isDataLoaded = true;
	}

	private void replaceFragment(Fragment mFragment, String fragmentName) {

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.childFragmentContent, mFragment, fragmentName);
		mFragmentTransaction.commitAllowingStateLoss();
	}

	void MyVoteDialog() {

		CustomTextviewBold btnYesVotes, btnNotVotes;

		final Dialog dialog = new Dialog(mContext, R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_my_votes);
		dialog.setCancelable(true);
		btnYesVotes = (CustomTextviewBold) dialog.findViewById(R.id.btnYesVotes);
		btnYesVotes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				runOnUiThread(new Runnable() {
					public void run() {
						GetData("up");
						textMyVotes.setText(getString(R.string.yes_votes));
						imgMyVotes.setImageResource(R.drawable.ic_my_votes_yes);
						isYesVotes = true;
					}
				});

				dialog.dismiss();
			}
		});
		btnNotVotes = (CustomTextviewBold) dialog.findViewById(R.id.btnNotVotes);
		btnNotVotes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					public void run() {
						GetData("down");
						isYesVotes = false;
						textMyVotes.setText(getString(R.string.no_votes));
						imgMyVotes.setImageResource(R.drawable.ic_my_votes_no);
					}
				});

				dialog.dismiss();
			}
		});
		dialog.show();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

}
