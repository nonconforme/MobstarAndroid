package com.mobstar.fanconnect;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class FanConnectHomeFragment extends Fragment implements OnClickListener {

	Context mContext;

	TextView textDisplayName, textTagline;
	ImageView imgUserPic, imgCoverPage;
	SharedPreferences preferences;

	String UserPicURL = "", UserCoverImageURL = "";

	LinearLayout llFans, llVotes, llPosition, llFeedback;

	public String sErrorMessage;

	int Rank = 0, VoteCount = 0, FansCount = 0;

	TextView textCountFan, textCountVote, textRank;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_fan_connect_home, container, false);

		mContext = getActivity();

		

		

		Utility.SendDataToGA("FanConnectHome Screen", getActivity());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		TextView textFanConnect = (TextView) view.findViewById(R.id.textFanConnect);
		textFanConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});
		preferences = mContext.getSharedPreferences("mobstar_pref", Context.MODE_PRIVATE);
		UserPicURL = preferences.getString("profile_image", "");
		UserCoverImageURL = preferences.getString("cover_image", "");
		textRank = (TextView) view.findViewById(R.id.textRank);
		textCountFan = (TextView) view.findViewById(R.id.textCountFan);
		textCountVote = (TextView) view.findViewById(R.id.textCountVote);

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

		llFans = (LinearLayout) view.findViewById(R.id.llFans);
		llFans.setOnClickListener(this);

		llVotes = (LinearLayout) view.findViewById(R.id.llVotes);
		llVotes.setOnClickListener(this);

		llPosition = (LinearLayout) view.findViewById(R.id.llPosition);
		llPosition.setOnClickListener(this);

		llFeedback = (LinearLayout) view.findViewById(R.id.llFeedback);
		llFeedback.setOnClickListener(this);

		Utility.ShowProgressDialog(mContext, "Loading");

		if (Utility.isNetworkAvailable(mContext)) {

			new FanCall().start();

		} else {

			Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	class FanCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_FAN, preferences.getString("token", null));

			// Log.v(Constant.TAG, "FanCall response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					JSONObject jsonObjUser = jsonObject.getJSONObject("user");

					Rank = jsonObjUser.getInt("rank");
					FansCount = jsonObjUser.getInt("fans");
					VoteCount = jsonObjUser.getInt("votes");

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerFan.sendEmptyMessage(0);
					} else {
						handlerFan.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerFan.sendEmptyMessage(0);
				}

			} else {

				handlerFan.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerFan = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			String suffix = "";
			if (Rank == 0) {
				textRank.setVisibility(View.GONE);
			} else if (Rank == 1) {
				suffix = "ST";
			} else if (Rank == 2) {
				suffix = "ND";
			} else if (Rank == 3) {
				suffix = "RD";
			} else {
				suffix = "TH";
			}

			textRank.setText(Html.fromHtml(Rank + "<sup><small>" + suffix + "</small></sup>"));
			textCountVote.setText(VoteCount + "");
			textCountFan.setText(FansCount + "");

		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (llFans.equals(v)) {
			Intent intent = new Intent(mContext, FansActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else if (llVotes.equals(v)) {
			Intent intent = new Intent(mContext, FanConnectVotesActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else if (llPosition.equals(v)) {
			Intent intent = new Intent(mContext, PositionActivity.class);
			intent.putExtra("rank",Rank);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		} else if (llFeedback.equals(v)) {
			Intent intent = new Intent(mContext, FanFeedbackActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
	}
}
