package com.mobstar.talentconnect;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.BaseActivity;
import com.mobstar.R;
import com.mobstar.custom.CustomTextviewBold;

import com.mobstar.home.new_home_screen.HomeVideoListBaseFragment;
import com.mobstar.utils.Utility;

public class TalentConnectVotesActivity extends BaseActivity implements OnClickListener {

	private static final String VIDEO_LIST_FRAGMENT = "VideoListFragment";

	private Context mContext;
	private SharedPreferences preferences;
	private TextView textMyVotes;
	private TextView textAllEntries;
	private ImageView imgMyVotes;
	private boolean isYesVotes = true;
	private FragmentManager mFragmentManager;
	private FragmentTransaction mFragmentTransaction;
	boolean isDataLoaded = false;
	private TextView btnBack;

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

		initControls();
	}

	void initControls() {
		btnBack = (TextView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);

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

	void GetData(String voteType) {

		HomeVideoListBaseFragment videoListFragment = getVideoListFragment();
		if (videoListFragment != null){
			videoListFragment.resetBundleExtra();
			videoListFragment.setIsVoteApi(true);
			videoListFragment.setVoteType(voteType);
			videoListFragment.resetAndLoadFirstPage();
		}else {
			videoListFragment = new HomeVideoListBaseFragment();
			final Bundle extras = new Bundle();
			extras.putBoolean(HomeVideoListBaseFragment.IS_VOTE_API, true);
			extras.putString(HomeVideoListBaseFragment.VOTE_TYPE, voteType);
			videoListFragment.setArguments(extras);
			replaceFragment(videoListFragment, VIDEO_LIST_FRAGMENT);

		}
		isDataLoaded = true;
	}

	private void replaceFragment(Fragment mFragment, String fragmentName) {

		mFragmentTransaction = mFragmentManager.beginTransaction();
		mFragmentTransaction.replace(R.id.childFragmentContent, mFragment, fragmentName);
		mFragmentTransaction.commitAllowingStateLoss();
	}

	private HomeVideoListBaseFragment getVideoListFragment(){
		return (HomeVideoListBaseFragment) mFragmentManager.findFragmentByTag(VIDEO_LIST_FRAGMENT);
	}

	void MyVoteDialog() {

		final CustomTextviewBold btnYesVotes, btnNotVotes;
		final ImageButton btnClose;
		final Dialog dialog = new Dialog(mContext, R.style.DialogTheme);
		dialog.setContentView(R.layout.dialog_my_votes);
		dialog.setCancelable(true);
		btnClose = (ImageButton) dialog.findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
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

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnBack:
				onBackPressed();
				break;
		}
	}
}
