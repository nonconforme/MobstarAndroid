package com.mobstar;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class LikesActivity extends Activity {

	Context mContext;

	TextView textNoData;

	ListView listUser;
	LikesAdapter likeAdapter;

	SharedPreferences preferences;
	public String sErrorMessage;
	String entryId="";

	ArrayList<EntryPojo> arrStarPojos = new ArrayList<EntryPojo>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_likes);

		mContext = LikesActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			entryId=extras.getString("EntryId");
		}

		InitControls();

		Utility.SendDataToGA("FanConnect Fans Screen", LikesActivity.this);
	}

	void InitControls() {

		TextView textFans = (TextView) findViewById(R.id.textFans);
		textFans.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		textNoData = (TextView) findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);

		listUser = (ListView) findViewById(R.id.listUser);
		likeAdapter = new LikesAdapter();
		listUser.setAdapter(likeAdapter);

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {
			new LikeCall().start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}
	}

	class LikeCall extends Thread {

		@Override
		public void run() {

			String[] name = {"entry"};
			
			String[] value = {entryId};
			String response = JSONParser.LikepostRequest(Constant.SERVER_URL + Constant.GET_LIKE_LIST, name, value,preferences.getString("token", null));

			if (response != null) {

				try {

					sErrorMessage = "";

//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("error")) {
//						sErrorMessage = jsonObject.getString("error");
//						
//					}
					if(response.equalsIgnoreCase("error")){
						sErrorMessage=getString(R.string.no_entries_found);
					}
					else {
						JSONArray jsonArrayStars = new JSONArray(response);

						for (int j = 0; j < jsonArrayStars.length(); j++) {

							JSONObject jsonObjStar = jsonArrayStars.getJSONObject(j);

							EntryPojo tempPojo = new EntryPojo();
							tempPojo.setUserID(jsonObjStar.getString("userId"));
							tempPojo.setUserDisplayName(jsonObjStar.getString("displayName"));
							tempPojo.setProfileImage(jsonObjStar.getString("profileImage"));
							tempPojo.setProfileCover(jsonObjStar.getString("profileCover"));
							tempPojo.setIsMyStar(jsonObjStar.getString("isMyStar"));
							arrStarPojos.add(tempPojo);

						}
					}
				
					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerFan.sendEmptyMessage(0);
					} else {
						handlerFan.sendEmptyMessage(1);
					}

				} catch (Exception e) {
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

			Utility.HideDialog(mContext);

			if (arrStarPojos.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				likeAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};


	public class LikesAdapter extends BaseAdapter {

		private class ViewHolder {
			TextView textStarName;
			ImageView imgUserPic;
			TextView textTime;
		}

		@Override
		public int getCount() {
			return arrStarPojos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;

			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.row_item_fan, null);
				viewHolder = new ViewHolder();

				viewHolder.textStarName = (TextView) convertView.findViewById(R.id.textStarName);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);

				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.textStarName.setText(arrStarPojos.get(position).getUserDisplayName());
			viewHolder.textTime.setVisibility(View.GONE);

			if (arrStarPojos.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrStarPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);

			}

			viewHolder.imgUserPic.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ProfileActivity.class);
					intent.putExtra("UserID",
							arrStarPojos.get(position).getUserID());
					intent.putExtra("UserName",
							arrStarPojos.get(position).getUserDisplayName());
					intent.putExtra("IsMyStar", arrStarPojos.get(position).getIsMyStar());
					intent.putExtra("UserPic",
							arrStarPojos.get(position).getProfileImage());
					intent.putExtra("UserCoverImage",arrStarPojos.get(position).getProfileCover());
					//					intent.putExtra("isProfile",true);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			return convertView;
		}
	}


}
