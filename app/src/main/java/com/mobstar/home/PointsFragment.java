//package com.mobstar.home;//				// TODO Auto-generated method stub

//
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.mobstar.R;
//import com.mobstar.utils.Utility;
//
//public class PointsFragment extends android.support.v4.app.Fragment {
//	Context mContext;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.fragment_points, container, false);
//
//		mContext = getActivity();
//
//		Utility.SendDataToGA("Points Screen", getActivity());
//
//		return view;
//	}
//
//	@Override
//	public void onViewCreated(View view, Bundle savedInstanceState) {
//		super.onViewCreated(view, savedInstanceState);
//
//		TextView textPoints = (TextView) view.findViewById(R.id.textPoints);
//		textPoints.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				getActivity().onBackPressed();
//			}
//		});
//	}
//
//}
