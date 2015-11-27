package com.mobstar.talentconnect;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.mobstar.R;
import com.mobstar.api.new_api_model.Profile;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class TalentConnectHomeFragment extends Fragment implements OnClickListener {

	private TextView textDisplayName, textTagline, textTalentConnect;
	private ImageView imgUserPic, imgCoverPage;
	private LinearLayout llTalentPool, llVotes, llComments;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_talent_connect_home, container, false);
		findViews(view);
		setListeners();
		setupViews();

		Utility.SendDataToGA("TalentConnectHome Screen", getActivity());

		return view;
	}

	private void findViews(final View view){
		textTalentConnect   = (TextView) view.findViewById(R.id.textTalentConnect);
		textDisplayName     = (TextView) view.findViewById(R.id.textDisplayName);
		textTagline         = (TextView) view.findViewById(R.id.textTagline);
		imgUserPic          = (ImageView) view.findViewById(R.id.imgUserPic);
		imgCoverPage        = (ImageView) view.findViewById(R.id.imgCoverPage);
		llTalentPool        = (LinearLayout) view.findViewById(R.id.llTalentPool);
		llVotes             = (LinearLayout) view.findViewById(R.id.llVotes);
		llComments          = (LinearLayout) view.findViewById(R.id.llComments);
	}

	private void setListeners(){
		llTalentPool.setOnClickListener(this);
		llVotes.setOnClickListener(this);
		llComments.setOnClickListener(this);
		textTalentConnect.setOnClickListener(this);
	}

	private void setupViews(){
		final Profile userProfile = UserPreference.getUserProfile(getActivity());
		textDisplayName.setText(userProfile.getDisplayName());
		textTagline.setText(userProfile.getTagline());

		if (!userProfile.getProfileImage().equals(""))
			Picasso.with(getActivity())
					.load(userProfile.getProfileImage())
					.resize(Utility.dpToPx(getActivity(), 60), Utility.dpToPx(getActivity(), 60))
					.centerCrop()
					.placeholder(R.drawable.ic_pic_small)
					.error(R.drawable.ic_pic_small)
					.transform(new RoundedTransformation(Utility.dpToPx(getActivity(), 60), 0))
					.into(imgUserPic);

		if (!userProfile.getCoverImage().equals(""))
			Picasso.with(getActivity())
					.load(userProfile.getCoverImage())
					.fit()
					.centerCrop()
					.placeholder(R.drawable.fan_connect_cover)
					.error(R.drawable.fan_connect_cover)
					.into(imgCoverPage);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.llTalentPool:
				startTalentPoolActivity();
				break;
			case R.id.llVotes:
				startTalentConnectVotesActivity();
				break;
			case R.id.llComments:
				startTalentConnectCommentActivity();
				break;
			case R.id.textTalentConnect:
				getActivity().onBackPressed();
				break;


		}
	}

	private void startTalentPoolActivity(){
		final Intent intent = new Intent(getActivity(), TalentPoolActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void startTalentConnectVotesActivity(){
		final Intent intent = new Intent(getActivity(), TalentConnectVotesActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void startTalentConnectCommentActivity(){
		final Intent intent = new Intent(getActivity(), TalentConnectCommentActivity.class);
		startActivity(intent);
		getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
