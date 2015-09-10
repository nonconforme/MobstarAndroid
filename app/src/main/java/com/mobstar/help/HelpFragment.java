package com.mobstar.help;

import android.content.Context;
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
import com.mobstar.fanconnect.FanConnectVotesActivity;
import com.mobstar.utils.Utility;

public class HelpFragment extends Fragment {

	Context mContext;

	TextView textHelp;
	private LinearLayout btnFAQS, btnAppTour, btnRequestAbuse, btnLeaveFeedback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_help, container, false);
		mContext = getActivity();

		Utility.SendDataToGA("Help Screen", getActivity());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		textHelp = (TextView) view.findViewById(R.id.textHelp);
		textHelp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		btnFAQS = (LinearLayout) view.findViewById(R.id.btnFAQS);
		btnFAQS.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(mContext, FAQSActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		btnAppTour = (LinearLayout) view.findViewById(R.id.btnAppTour);
		btnAppTour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, WelcomeVideoActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		btnRequestAbuse = (LinearLayout) view.findViewById(R.id.btnRequestAbuse);
		btnRequestAbuse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ReportAbuseActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		btnLeaveFeedback = (LinearLayout) view.findViewById(R.id.btnLeaveFeedback);
		btnLeaveFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, LeaveFeedbackActivity.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

	}

}