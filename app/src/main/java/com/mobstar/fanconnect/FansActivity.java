package com.mobstar.fanconnect;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mobstar.R;
import com.mobstar.custom.PullToRefreshListView;
import com.mobstar.custom.PullToRefreshListView.OnRefreshListener;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.pojo.StarPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class FansActivity extends Activity {

	Context mContext;

	TextView textNoData;

	PullToRefreshListView listUser;
	FanAdapter fanAdapter;

	SharedPreferences preferences;
	public String sErrorMessage;
	String UserID="";

	ArrayList<StarPojo> arrStarChildPojos = new ArrayList<StarPojo>();
	ArrayList<StarPojo> arrStarPojos = new ArrayList<StarPojo>();

	EditText editMessage;
	Typeface typeface;
	ImageView btnSend;
	private boolean isOtherUser=false;

	// pagination
	int mFirstVisibleItem = 0;
	private boolean isRefresh = false;
	private boolean isWebCall = false;
	private static int currentPage = 1;
	private boolean isNextPageAvail=false;

	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fans);

		mContext = FansActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			UserID=extras.getString("UserId");
			isOtherUser=true;
		}
		else {
			isOtherUser=false;
			UserID = preferences.getString("userid", "");	
		}

		InitControls();

		listUser.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub\
				if (Utility.isNetworkAvailable(mContext)) {
					isRefresh = true;
					isWebCall = true;
					currentPage=1;
					new UsersFanCall(currentPage).start();
				} else {
					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				}
			}
		});

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		Utility.SendDataToGA("FanConnect Fans Screen", FansActivity.this);

		if (Utility.isNetworkAvailable(mContext)) {
			//			if(isOtherUser){
			new UsersFanCall(currentPage).start();
			//			}
			//			else {
			//				new FanCall().start();
			//			}
		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}



		listUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				startProfileActivity(position);
				Intent intent = new Intent(mContext, NewProfileActivity.class);
				final UserProfile userProfile = UserProfile.newBuilder()
						.setUserId(arrStarPojos.get(position).getStarID())
						.setUserName(arrStarPojos.get(position).getStarName())
						.setIsMyStar(arrStarPojos.get(position).getIsMyStar())
						.setUserPic(arrStarPojos.get(position).getProfileImage())
						.setUserCoverImage(arrStarPojos.get(position).getProfileCover())
						.build();
//				intent.putExtra("UserID",
//						arrStarPojos.get(position).getStarID());
//				intent.putExtra("UserName",
//						arrStarPojos.get(position).getStarName());
//				intent.putExtra("IsMyStar",arrStarPojos.get(position).getIsMyStar());
//				intent.putExtra("UserPic",
//						arrStarPojos.get(position).getProfileImage());
//				intent.putExtra("UserCoverImage",arrStarPojos.get(position).getProfileCover());
				//				intent.putExtra("isProfile",true);
				intent.putExtra(NewProfileActivity.USER, userProfile);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		listUser.setOnScrollListener(new OnScrollListener() {

			private int visibleThreshold = 1;
			private int previousTotal = 0;
			private boolean loading = true;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (loading) {
					if (totalItemCount > previousTotal) {
						loading = false;
						previousTotal = totalItemCount;

					}
				}
				if (!loading && !isWebCall && isNextPageAvail && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					Utility.ShowProgressDialog(mContext, getString(R.string.loading));
					isWebCall = true;
					currentPage++;
					if (Utility.isNetworkAvailable(mContext)) {
						new UsersFanCall(currentPage).start();
					} else {
						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}
					loading = true;
				}

			}

		});
	}

	private void startProfileActivity(int position){
		final Intent intent = new Intent(mContext, NewProfileActivity.class);
		final UserProfile userProfile = UserProfile.newBuilder()
				.setUserId(arrStarPojos.get(position).getStarID())
				.setUserName(arrStarPojos.get(position).getStarName())
				.setIsMyStar(arrStarPojos.get(position).getIsMyStar())
				.setUserPic(arrStarPojos.get(position).getProfileImage())
				.setUserCoverImage(arrStarPojos.get(position).getProfileCover())
				.build();
//				intent.putExtra("UserID",
//						arrStarPojos.get(position).getStarID());
//				intent.putExtra("UserName",
//						arrStarPojos.get(position).getStarName());
//				intent.putExtra("IsMyStar",arrStarPojos.get(position).getIsMyStar());
//				intent.putExtra("UserPic",
//						arrStarPojos.get(position).getProfileImage());
//				intent.putExtra("UserCoverImage",arrStarPojos.get(position).getProfileCover());
		//				intent.putExtra("isProfile",true);
		intent.putExtra(NewProfileActivity.USER, userProfile);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	void InitControls() {

		TextView textFans = (TextView) findViewById(R.id.textFans);
		textFans.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		textNoData = (TextView) findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);
		
		adView = (AdView)findViewById(R.id.adView);

		listUser = (PullToRefreshListView) findViewById(R.id.listUser);
		fanAdapter = new FanAdapter();
		listUser.setAdapter(fanAdapter);


		typeface = Typeface.createFromAsset(mContext.getAssets(), "GOTHAM-LIGHT.TTF");
		editMessage = (EditText) findViewById(R.id.editMessage);
		editMessage.setTypeface(typeface);

		btnSend = (ImageView) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

				if (!editMessage.getText().toString().trim().equals("")) {

					sErrorMessage = "";

					Utility.ShowProgressDialog(mContext, getString(R.string.loading));

					if (Utility.isNetworkAvailable(mContext)) {

						new PostBulkMessageCall(StringEscapeUtils.escapeJava(editMessage.getText().toString().trim())).start();

					} else {

						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}
				}

			}
		});




	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(adView!=null){
			// Request for Ads
			AdRequest adRequest = new AdRequest.Builder()
	 
			// Add a test device to show Test Ads
//			 .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//			 .addTestDevice("CC5F2C72DF2B356BBF0DA198")
					.build();
	 
			// Load ads into Banner Ads
			adView.loadAd(adRequest);
		}
	}

	class FanCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_FAN, preferences.getString("token", null));

			Log.v(Constant.TAG, "FanCall response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					JSONObject jsonObjUser = jsonObject.getJSONObject("user");

					JSONArray jsonArrayStars = jsonObjUser.getJSONArray("stars");

					for (int j = 0; j < jsonArrayStars.length(); j++) {

						JSONObject jsonObjStar = jsonArrayStars.getJSONObject(j);

						StarPojo tempPojo = new StarPojo();
						tempPojo.setStarID(jsonObjStar.getString("starId"));
						tempPojo.setStarName(jsonObjStar.getString("starName"));
						tempPojo.setProfileImage(jsonObjStar.getString("profileImage"));
						tempPojo.setProfileCover(jsonObjStar.getString("profileCover"));
						tempPojo.setStarredDate(jsonObjStar.getString("starredDate"));
						tempPojo.setRank(jsonObjStar.getString("rank"));
						tempPojo.setStats(jsonObjStar.getString("stat"));
						arrStarPojos.add(tempPojo);

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
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (arrStarPojos.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				fanAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};

	class UsersFanCall extends Thread {

		int pageNo;

		public UsersFanCall(int pageNo){
			this.pageNo=pageNo;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = {"user","page"};
			//			String[] value = {"307",phoneNumber,country };
			String[] value = {UserID,String.valueOf(pageNo)};
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.GET_USER_FOLLOWER, name, value,preferences.getString("token", null));


			Log.v(Constant.TAG, "UsersFanCall response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					//					JSONObject jsonObjUser = jsonObject.getJSONObject("user");

					JSONArray jsonArrayStars = jsonObject.getJSONArray("starredBy");

					arrStarChildPojos.clear();

					for (int j = 0; j < jsonArrayStars.length(); j++) {

						JSONObject jsonObjStar = jsonArrayStars.getJSONObject(j);

						StarPojo tempPojo = new StarPojo();
						tempPojo.setStarID(jsonObjStar.getString("starId"));
						tempPojo.setStarName(jsonObjStar.getString("starName"));
						tempPojo.setProfileImage(jsonObjStar.getString("profileImage"));
						tempPojo.setProfileCover(jsonObjStar.getString("profileCover"));
						tempPojo.setStarredDate(jsonObjStar.getString("starredDate"));
						tempPojo.setIsMyStar(jsonObjStar.getString("isMyStar"));
						arrStarChildPojos.add(tempPojo);

					}

					if (isRefresh) {
						mFirstVisibleItem = 0;
						runOnUiThread(new Runnable() {
							public void run() {
								listUser.onRefreshComplete();
							}
						});

						isRefresh = false;
						arrStarPojos.clear();
						arrStarPojos.addAll(arrStarChildPojos);

					} else {
						if (arrStarPojos != null && arrStarPojos.size() > 0) {
							mFirstVisibleItem = arrStarPojos.size();
							arrStarPojos.addAll(arrStarChildPojos);

						} else {
							mFirstVisibleItem = 0;
							arrStarPojos.clear();
							arrStarPojos.addAll(arrStarChildPojos);
						}
					}

					if(jsonObject.has("next")){
						String next=jsonObject.getString("next");
						if(next.length()>0){
							isNextPageAvail=true;
						}
					}
					else{
						isNextPageAvail=false;
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerUserFan.sendEmptyMessage(0);
					} else {
						handlerUserFan.sendEmptyMessage(1);
					}



				} catch (Exception e) {
					e.printStackTrace();
					handlerUserFan.sendEmptyMessage(0);
				}

			} else {

				handlerUserFan.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerUserFan = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			listUser.onRefreshComplete();
			isWebCall = false;
			if (arrStarPojos.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				fanAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};

	public class FanAdapter extends BaseAdapter {

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

			viewHolder.textStarName.setText(arrStarPojos.get(position).getStarName());
			viewHolder.textTime.setText(arrStarPojos.get(position).getStarredDate());

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
					startProfileActivity(position);
				}
			});

			return convertView;
		}

	}

	class PostBulkMessageCall extends Thread {

		String Message;

		public PostBulkMessageCall(String Message) {
			this.Message = Message;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "message", "type" };
			String[] value = { Message, "starred" };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.SEND_BULK_MESSAGE, name, value, preferences.getString("token", null));

			//			Log.v(Constant.TAG, "PostBulkMessageCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerPostBulkMessage.sendEmptyMessage(0);
					} else {
						handlerPostBulkMessage.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerPostBulkMessage.sendEmptyMessage(0);
				}

			} else {

				handlerPostBulkMessage.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerPostBulkMessage = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			Utility.HideDialog(mContext);
			if (msg.what == 1) {

				editMessage.setText("");
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);

			}
		}
	};
}
