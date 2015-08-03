package com.mobstar.talentconnect;

import org.apache.commons.lang3.StringEscapeUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.fanconnect.FanFeedbackActivity;
import com.mobstar.fanconnect.FansActivity;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class TalentConnectHomeFragment extends Fragment implements OnClickListener {

	Context mContext;

	TextView textDisplayName, textTagline;
	ImageView imgUserPic, imgCoverPage;
	SharedPreferences preferences;

	String UserPicURL = "", UserCoverImageURL = "";

	LinearLayout llTalentPool, llVotes, llComments;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_talent_connect_home, container, false);

		mContext = getActivity();

		preferences = mContext.getSharedPreferences("mobstar_pref", Context.MODE_PRIVATE);

		UserPicURL = preferences.getString("profile_image", "");
		UserCoverImageURL = preferences.getString("cover_image", "");

		Utility.SendDataToGA("TalentConnectHome Screen", getActivity());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		TextView textTalentConnect = (TextView) view.findViewById(R.id.textTalentConnect);
		textTalentConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		textDisplayName = (TextView) view.findViewById(R.id.textDisplayName);
		textDisplayName.setText(preferences.getString("displayName", ""));

		textTagline = (TextView) view.findViewById(R.id.textTagline);
		textTagline.setText(Utility.unescape_perl_string(preferences.getString("tagline", "")));

		imgUserPic = (ImageView) view.findViewById(R.id.imgUserPic);
		if (UserPicURL.equals("")) {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);
		} else {
			imgUserPic.setImageResource(R.drawable.ic_pic_small);

			Picasso.with(mContext).load(UserPicURL).resize(Utility.dpToPx(mContext, 60), Utility.dpToPx(mContext, 60)).centerCrop().placeholder(R.drawable.ic_pic_small)
					.error(R.drawable.ic_pic_small).transform(new RoundedTransformation(Utility.dpToPx(mContext, 60), 0)).into(imgUserPic);

		}

		imgCoverPage = (ImageView) view.findViewById(R.id.imgCoverPage);
		if (UserCoverImageURL.equals("")) {
			imgCoverPage.setBackgroundResource(R.drawable.fan_connect_cover);
		} else {
			imgCoverPage.setBackgroundResource(R.drawable.fan_connect_cover);

			Picasso.with(mContext).load(UserCoverImageURL).fit().centerCrop().placeholder(R.drawable.fan_connect_cover).error(R.drawable.fan_connect_cover).into(imgCoverPage);

		}

		llTalentPool = (LinearLayout) view.findViewById(R.id.llTalentPool);
		llTalentPool.setOnClickListener(this);

		llVotes = (LinearLayout) view.findViewById(R.id.llVotes);
		llVotes.setOnClickListener(this);

		llComments = (LinearLayout) view.findViewById(R.id.llComments);
		llComments.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (llTalentPool.equals(v)) {
			Intent intent = new Intent(mContext, TalentPoolActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else if (llVotes.equals(v)) {
			Intent intent = new Intent(mContext, TalentConnectVotesActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else if (llComments.equals(v)) {
			Intent intent = new Intent(mContext, TalentConnectCommentActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}
}
