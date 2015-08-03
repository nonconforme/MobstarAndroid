package com.mobstar;

import org.apache.commons.lang3.StringEscapeUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment{

	private Context mContext;
	private SharedPreferences preferences;
	private String UserID,UserBio,UserPic;
	private ImageView imgUser;
	private TextView textBio;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile, container, false);

		mContext = getActivity();
		
		initControlls(view);

		return view;
	}


	private void initControlls(View view) {
		preferences = getActivity().getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		Bundle extras = getArguments();
		if (extras != null) {
			UserID = extras.getString("UserID");
			UserBio=extras.getString("UserBio");
			UserPic=extras.getString("UserPic");
		}
		
		imgUser=(ImageView)view.findViewById(R.id.imgUser);
		textBio=(TextView)view.findViewById(R.id.textBio);
		
		if (UserPic.equals("")) {
			imgUser.setImageResource(R.drawable.profile_pic_new);
		} else {
			imgUser.setImageResource(R.drawable.profile_pic_new);
			
			Picasso.with(mContext).load(UserPic).into(imgUser);

//			Picasso.with(mContext).load(UserPic).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic_new)
//			.error(R.drawable.profile_pic_new).transform(new RoundedTransformation(Utility.dpToPx(mContext, 126), 0)).into(imgUser);
		}
		
		if(UserBio!=null && UserBio.length()>0){
			textBio.setText(Utility.unescape_perl_string(UserBio));
			textBio.setVisibility(View.VISIBLE);
		}
		else {
			textBio.setVisibility(View.GONE);
		}
	}
	
	


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
	}

}
