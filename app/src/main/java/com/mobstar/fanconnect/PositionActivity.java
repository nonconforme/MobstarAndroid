package com.mobstar.fanconnect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mobstar.ProfileActivity;
import com.mobstar.R;
import com.mobstar.custom.PullToRefreshListView;
import com.mobstar.custom.PullToRefreshListView.OnRefreshListener;
import com.mobstar.pojo.StarPojo;
import com.mobstar.pojo.TopTalentPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class PositionActivity extends Activity {

	Context mContext;
	TextView textNoData;

	PullToRefreshListView listUser;
	TopTalentAdapter topTalentAdapter;

	ArrayList<TopTalentPojo> arrEntryPojosChild = new ArrayList<TopTalentPojo>();
	ArrayList<TopTalentPojo> arrTalentPojos = new ArrayList<TopTalentPojo>();

	SharedPreferences preferences;
	public String sErrorMessage;
	private int rank;

	// pagination
	int mFirstVisibleItem = 0;
	private boolean isRefresh = false;
	private boolean isWebCall = false;
	private int currentPage;
	private boolean isNextPageAvail=false;

	private boolean isScrollUp=false,isSrollDown=false,enablePull=false,isPageToScroll=false;
	private int pageDown;
	private int userPage;

	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_position);

		mContext = PositionActivity.this;

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		Bundle b=getIntent().getExtras();
		if(b!=null){
			rank=b.getInt("rank");
			userPage=rank/50;
			userPage=userPage+1;
			currentPage=userPage;
		}

		InitControls();

		Utility.SendDataToGA("FanConnect Position Screen", PositionActivity.this);

		listUser.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub\
				if (Utility.isNetworkAvailable(mContext)) {
					isSrollDown=false;
					isWebCall = true;
					if(currentPage>1){
						currentPage=currentPage-1;
						isScrollUp=true;
						isRefresh=false;
					}
					else{
						isRefresh = true;
						enablePull=true;
						currentPage=1;	
						userPage=1;
						isScrollUp=false;
						isSrollDown=false;
					}
					new TopTalentCall(currentPage).start();
				} else {
					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				}
			}
		});

		listUser.setOnScrollListener(new OnScrollListener() {

			private int visibleThreshold =1;
			private int previousTotal = 0;
			private boolean loading = true;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (loading) {
					if (totalItemCount >= previousTotal || enablePull) {
						loading = false;
						previousTotal = totalItemCount;
						enablePull=false;

					}
				}

				//				Log.d("mobstar","totalCount >> "+totalItemCount );
				//				Log.d("mobstar","previousTotal  >> "+previousTotal );

				//				Log.d("mobstar","Check... "+"!loading"+loading+"!isWebCall"+isWebCall+"isNextPageAvail"+isNextPageAvail+" "+(totalItemCount - visibleItemCount)+"<="+(firstVisibleItem + visibleThreshold));
				if (!loading && !isWebCall && isNextPageAvail && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
					Utility.ShowProgressDialog(mContext, getString(R.string.loading));
					isWebCall = true;
					userPage=userPage+1;
					if(!isRefresh){
						isSrollDown=true;
						isScrollUp=false;
					}

					if (Utility.isNetworkAvailable(mContext)) {
						new TopTalentCall(userPage).start();
					} else {
						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}
					loading = true;
				}

			}

		});

	}

	void InitControls() {

		TextView textPosition = (TextView) findViewById(R.id.textPosition);
		textPosition.setOnClickListener(new OnClickListener() {

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
		topTalentAdapter = new TopTalentAdapter();
		
		listUser.setAdapter(topTalentAdapter);

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {
			isPageToScroll=true;
			new TopTalentCall(userPage).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

		listUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(mContext, ProfileActivity.class);
				intent.putExtra("UserID",
						arrTalentPojos.get(position).getUserID());
				intent.putExtra("UserName",
						arrTalentPojos.get(position).getUserName());
				intent.putExtra("IsMyStar",arrTalentPojos.get(position).getIsMyStar());
				intent.putExtra("UserPic",
						arrTalentPojos.get(position).getProfileImage());
				intent.putExtra("isProfile",true);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

	class TopTalentCall extends Thread {

		int PageNo;

		public TopTalentCall(int page){
			this.PageNo=page;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_TOP_TALENT +"?page="+PageNo , preferences.getString("token", null));

			Log.v(Constant.TAG, "TopTalentCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					arrEntryPojosChild.clear();

					if (jsonObject.has("talents")) {

						JSONArray jsonArrayTalent = jsonObject.getJSONArray("talents");

						for (int i = 0; i < jsonArrayTalent.length(); i++) {

							JSONObject jsonObjRow = jsonArrayTalent.getJSONObject(i);

							JSONObject jsonObjTalent = jsonObjRow.getJSONObject("talent");

							TopTalentPojo talentPojo = new TopTalentPojo();

							talentPojo.setUserID(jsonObjTalent.getString("id"));
							talentPojo.setUserName(jsonObjTalent.getString("userName"));
							talentPojo.setUserDisplayName(jsonObjTalent.getString("displayName"));
							talentPojo.setProfileImage(jsonObjTalent.getString("profileImage"));
							talentPojo.setRank(jsonObjTalent.getString("rank"));
							if(jsonObjTalent.has("isMyStar")){
								talentPojo.setIsMyStar(String.valueOf(jsonObjTalent.getInt("isMyStar")));
							}
							arrEntryPojosChild.add(talentPojo);

						}
					}

					if (isRefresh) {
						mFirstVisibleItem = 0;
						isRefresh = false;
						arrTalentPojos.clear();
						arrTalentPojos.addAll(arrEntryPojosChild);
						runOnUiThread(new Runnable() {
							public void run() {
								listUser.onRefreshComplete();
								//								listUser.setAdapter(new TopTalentAdapter());
							}
						});

					} else {
						if (arrTalentPojos != null && arrTalentPojos.size() > 0) {
							if(isScrollUp){
								ArrayList<TopTalentPojo> arrTalentPojosTemp = new ArrayList<TopTalentPojo>();
								arrTalentPojosTemp.addAll(arrTalentPojos);
								arrTalentPojos.clear();
								arrTalentPojos.addAll(arrEntryPojosChild);
								mFirstVisibleItem = arrTalentPojos.size();
								arrTalentPojos.addAll(arrTalentPojosTemp);

							}
							else if(isSrollDown){
								ArrayList<TopTalentPojo> arrTalentPojosTemp = new ArrayList<TopTalentPojo>();
								arrTalentPojosTemp.addAll(arrTalentPojos);
								arrTalentPojos.clear();
								arrTalentPojos.addAll(arrTalentPojosTemp);
								mFirstVisibleItem = arrTalentPojos.size();
								arrTalentPojos.addAll(arrEntryPojosChild);

							}
							else{
								mFirstVisibleItem = arrTalentPojos.size();
								arrTalentPojos.addAll(arrEntryPojosChild);
							}


						} else {
							if(!isPageToScroll){
								mFirstVisibleItem = 0;	
							}

							arrTalentPojos.clear();
							arrTalentPojos.addAll(arrEntryPojosChild);
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
						handlerMyStar.sendEmptyMessage(0);
					} else {
						handlerMyStar.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerMyStar.sendEmptyMessage(0);
				}

			} else {

				handlerMyStar.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerMyStar = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			if(isScrollUp){
				listUser.onRefreshComplete();	
			}

			isWebCall = false;
			if (msg.what == 1) {
				topTalentAdapter.notifyDataSetChanged();
				boolean flag=false;
				int pos=0;
				if(isPageToScroll){
					isPageToScroll=false;
					for (int i = 0; i < arrTalentPojos.size(); i++) {
						if(arrTalentPojos.get(i).getRank().equalsIgnoreCase(String.valueOf(rank))){
							pos=i;
							flag=true;
							break;
						}
					}

					if(flag){
						listUser.smoothScrollToPositionFromTop(pos,0,500); 
						mFirstVisibleItem=pos;
					}
					else{
//						mFirstVisibleItem=0;
					}
				}

			} else {
				textNoData.setVisibility(View.VISIBLE);
			}
		}
	};

	public class TopTalentAdapter extends BaseAdapter {

		/* private view holder class */
		private class ViewHolder {
			TextView textName;
			ImageView imgUserPic;
			TextView textPostion;
		}

		@Override
		public int getCount() {
			return arrTalentPojos.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;

			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.row_item_top_talent, null);
				viewHolder = new ViewHolder();

				viewHolder.textName = (TextView) convertView.findViewById(R.id.textName);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.textPostion = (TextView) convertView.findViewById(R.id.textPosition);

				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.textName.setText(arrTalentPojos.get(position).getUserDisplayName());

			if (arrTalentPojos.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrTalentPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);

			}

			//			if (position == 0) {
			//				viewHolder.textPostion.setBackgroundResource(R.drawable.position1_circle_bg);
			//				viewHolder.textPostion.setText(Html.fromHtml("1" + "<sup><small>" + "ST" + "</small></sup>"));
			//				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.white_color)));
			//			} else if (position == 1) {
			//				viewHolder.textPostion.setBackgroundResource(R.drawable.position1_circle_bg);
			//				viewHolder.textPostion.setText(Html.fromHtml("2" + "<sup><small>" + "ND" + "</small></sup>"));
			//				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.white_color)));
			//			} else if (position == 2) {
			//				viewHolder.textPostion.setBackgroundResource(R.drawable.position1_circle_bg);
			//				viewHolder.textPostion.setText(Html.fromHtml("3" + "<sup><small>" + "RD" + "</small></sup>"));
			//				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.white_color)));
			//			} else {
			//				viewHolder.textPostion.setBackgroundResource(android.R.color.transparent);
			//				viewHolder.textPostion.setText(Html.fromHtml(position + 1 + "<sup><small>" + "TH" + "</small></sup>"));
			//				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.main_bg)));
			//			}

			if (Integer.parseInt(arrTalentPojos.get(position).getRank()) == 1) {
				viewHolder.textPostion.setBackgroundResource(R.drawable.position1_circle_bg);
				viewHolder.textPostion.setText(Html.fromHtml("1" + "<sup><small>" + "ST" + "</small></sup>"));
				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.white_color)));
			} else if (Integer.parseInt(arrTalentPojos.get(position).getRank()) == 2) {
				viewHolder.textPostion.setBackgroundResource(R.drawable.position1_circle_bg);
				viewHolder.textPostion.setText(Html.fromHtml("2" + "<sup><small>" + "ND" + "</small></sup>"));
				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.white_color)));
			} else if (Integer.parseInt(arrTalentPojos.get(position).getRank()) == 3) {
				viewHolder.textPostion.setBackgroundResource(R.drawable.position1_circle_bg);
				viewHolder.textPostion.setText(Html.fromHtml("3" + "<sup><small>" + "RD" + "</small></sup>"));
				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.white_color)));
			} else {
				viewHolder.textPostion.setBackgroundResource(android.R.color.transparent);
				viewHolder.textPostion.setText(Html.fromHtml(arrTalentPojos.get(position).getRank() + "<sup><small>" + "TH" + "</small></sup>"));
				viewHolder.textPostion.setTextColor((getResources().getColor(R.color.main_bg)));
			}
			return convertView;
		}
	}
}
