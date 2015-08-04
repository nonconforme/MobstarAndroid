package com.mobstar.talentconnect;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mobstar.ProfileActivity;
import com.mobstar.R;
import com.mobstar.custom.PullToRefreshListView;
import com.mobstar.custom.PullToRefreshListView.OnRefreshListener;
import com.mobstar.home.ShareActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.pojo.StarPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class TalentPoolActivity extends Activity {

	Context mContext;

	TextView textTalentPool;
	MyStarAdapter myStarAdapter;
	PullToRefreshListView listUser;

	SharedPreferences preferences;

	public String sErrorMessage;

	String UserID;
	ArrayList<StarPojo> arrEntryPojosChild = new ArrayList<StarPojo>();
	ArrayList<StarPojo> arrStarPojos = new ArrayList<StarPojo>();

	ArrayList<String> arrSelectionStarID = new ArrayList<String>();

	TextView textNoData;

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
		setContentView(R.layout.activity_talent_pool);

		mContext = TalentPoolActivity.this;

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		UserID = preferences.getString("userid", "");

		InitControls();

		listUser.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub\
				if (Utility.isNetworkAvailable(mContext)) {
					isRefresh = true;
					isWebCall = true;
					currentPage=1;
					new MyStarCall(UserID,currentPage).start();
				} else {
					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				}
			}
		});

		myStarAdapter = new MyStarAdapter();
		listUser.setAdapter(myStarAdapter);

		if (Utility.isNetworkAvailable(mContext)) {

			new MyStarCall(UserID,currentPage).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

		listUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				// Intent intent = new Intent(mContext, ProfileActivity.class);
				// intent.putExtra("UserID",
				// arrStarPojos.get(position).getStarID());
				// intent.putExtra("UserName",
				// arrStarPojos.get(position).getStarName());
				// intent.putExtra("IsMyStar", "true");
				// intent.putExtra("UserPic",
				// arrStarPojos.get(position).getProfileImage());
				// intent.putExtra("UserTagline", "");
				//
				// startActivity(intent);
				// overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
						new MyStarCall(UserID,currentPage).start();
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

		textNoData = (TextView) findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);

		listUser = (PullToRefreshListView) findViewById(R.id.listUser);
		listUser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		
		adView = (AdView)findViewById(R.id.adView);

		listUser.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.menu_delete:
					DeleteStar();
					mode.finish(); // Action picked, so close the CAB
					return true;
				default:
					return false;
				}
			}

			@Override
			public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.context_menu, menu);

				return true;
			}

			@Override
			public void onDestroyActionMode(android.view.ActionMode arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean onPrepareActionMode(android.view.ActionMode arg0, Menu arg1) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onItemCheckedStateChanged(android.view.ActionMode arg0, int position, long arg2, boolean arg3) {
				// TODO Auto-generated method stub
				final int checkedCount = listUser.getCheckedItemCount();
				// Set the CAB title according to total checked items
				arg0.setTitle(checkedCount + " " + getString(R.string.selected));

				if (arrSelectionStarID.contains(arrStarPojos.get(position).getStarID() + "")) {
					arrSelectionStarID.remove(arrStarPojos.get(position).getStarID() + "");
				} else {
					arrSelectionStarID.add(arrStarPojos.get(position).getStarID() + "");
				}

			}
		});


		textTalentPool = (TextView) findViewById(R.id.textTalentPool);
		textTalentPool.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));



	}

	void DeleteStar() {

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new DeleteStarCall(arrSelectionStarID.get(0)).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

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

	public class MyStarAdapter extends BaseAdapter {

		public MyStarAdapter() {

		}

		/* private view holder class */
		private class ViewHolder {
			TextView textStarName;
			ImageView imgUserPic;
			ImageView imgStarInfo;
			LinearLayout llTalent,llDefault;
			ImageView imgUserPicTalent;
			TextView textStat;
			ImageView imgShare;
			TextView textRank;
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

				convertView = mInflater.inflate(R.layout.row_item_stars, null);
				viewHolder = new ViewHolder();

				viewHolder.llDefault=(LinearLayout)convertView.findViewById(R.id.llDefault);
				viewHolder.llTalent=(LinearLayout)convertView.findViewById(R.id.llTalent);
				viewHolder.textStarName = (TextView) convertView.findViewById(R.id.textStarName);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.imgStarInfo = (ImageView) convertView.findViewById(R.id.imgStarInfo);

				viewHolder.imgUserPicTalent=(ImageView)convertView.findViewById(R.id.imgUserPicTalent);
				viewHolder.textStat=(TextView)convertView.findViewById(R.id.textStat);
				viewHolder.imgShare=(ImageView)convertView.findViewById(R.id.imgShare);
				viewHolder.textRank=(TextView)convertView.findViewById(R.id.textRank);

				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.textStarName.setText(arrStarPojos.get(position).getStarName());

			viewHolder.textRank.setText("#"+arrStarPojos.get(position).getRank());

			viewHolder.textStat.setText("#"+arrStarPojos.get(position).getStats());


			if(!arrStarPojos.get(position).isChecked){
				if(viewHolder.llTalent.getVisibility() == View.VISIBLE){
					//					TranslateAnimation anim = new TranslateAnimation(0f, 0f, 0f, -100f);  
					//					anim.setDuration(300); 
					//
					//					viewHolder.llTalent.setAnimation(anim);
					viewHolder.llTalent.setVisibility(View.GONE);
				}
			}

			viewHolder.imgShare.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ShareActivity.class);
					intent.putExtra("isTalent",true);
					intent.putExtra("UserName",arrStarPojos.get(position).getStarName());
					intent.putExtra("UserImg",arrStarPojos.get(position).getProfileImage());
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);					
				}
			});

			viewHolder.imgUserPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					//					Log.d("mobstar","Profile img"+arrStarPojos.get(position).getProfileImage());

					Intent intent = new Intent(mContext, ProfileActivity.class);
					intent.putExtra("UserID",
							arrStarPojos.get(position).getStarID());
					intent.putExtra("UserName",
							arrStarPojos.get(position).getStarName());
					intent.putExtra("IsMyStar","1");
					intent.putExtra("UserPic",
							arrStarPojos.get(position).getProfileImage());
					intent.putExtra("UserCoverImage",arrStarPojos.get(position).getProfileCover());
					//					intent.putExtra("isProfile",true);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			viewHolder.imgUserPicTalent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, ProfileActivity.class);
					intent.putExtra("UserID",
							arrStarPojos.get(position).getStarID());
					intent.putExtra("UserName",
							arrStarPojos.get(position).getStarName());
					intent.putExtra("IsMyStar","1");
					intent.putExtra("UserPic",
							arrStarPojos.get(position).getProfileImage());
					intent.putExtra("UserCoverImage",arrStarPojos.get(position).getProfileCover());
					//					intent.putExtra("isProfile",true);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});

			if (arrStarPojos.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
				viewHolder.imgUserPicTalent.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
				viewHolder.imgUserPicTalent.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrStarPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);


				Picasso.with(mContext).load(arrStarPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPicTalent);

				// Ion.with(mContext).load(arrStarPojos.get(position).getProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).resize(Utility.dpToPx(mContext,
				// 45), Utility.dpToPx(mContext,
				// 45)).centerCrop().asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception exception, Bitmap bitmap) {
				// // TODO Auto-generated method stub
				// if (exception == null) {
				//
				// holder.imgUserPic.setImageBitmap(bitmap);
				//
				// } else {
				// // Log.v(Constant.TAG, "Exception " +
				// // exception.toString());
				// }
				// }
				// });
			}

			viewHolder.imgStarInfo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
					//
					//					// set title
					//					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));
					//
					//					// set dialog message
					//					alertDialogBuilder.setMessage(getString(R.string.coming_soon)).setCancelable(false).setNeutralButton("OK", null);
					//
					//					// create alert dialog
					//					AlertDialog alertDialog = alertDialogBuilder.create();
					//
					//					// show it
					//					alertDialog.show();

					arrStarPojos.get(position).isChecked=true;

					for(int i=0;i<arrStarPojos.size();i++){
						if(position!=i){
							arrStarPojos.get(i).isChecked=false;	
						}

					}



					TranslateAnimation anim1 = new TranslateAnimation(0f, 0f, -100f, 0f);  
					anim1.setDuration(300); 

					viewHolder.llTalent.setAnimation(anim1);
					viewHolder.llTalent.setVisibility(View.VISIBLE);

					notifyDataSetChanged();

				}
			});

			viewHolder.llTalent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					TranslateAnimation anim = new TranslateAnimation(0f, 0f, 0f,-100f);  
					anim.setDuration(300);

					viewHolder.llTalent.setAnimation(anim);
					viewHolder.llTalent.setVisibility(View.GONE);

				}
			});


			return convertView;
		}
	}

	//	class MyStarCall extends Thread {
	//
	//		String UserID;
	//		int PageNo;
	//
	//		public MyStarCall(String UserID,int pageNo) {
	//			this.UserID = UserID;
	//			this.PageNo=pageNo;
	//		}
	//
	//		@Override
	//		public void run() {
	//			// TODO Auto-generated method stub
	//
	//			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_STAR + "302"+ "&page=" + PageNo, preferences.getString("token", null));
	//
	//			// Log.v(Constant.TAG, "MyStarCall response " + response);
	//
	//			if (response != null) {
	//
	//				try {
	//
	//					JSONObject jsonObject = new JSONObject(response);
	//
	//					if (jsonObject.has("error")) {
	//						sErrorMessage = jsonObject.getString("error");
	//					}
	//
	//					if (jsonObject.has("users")) {
	//						
	//
	//						JSONArray jsonArrayUsers = jsonObject.getJSONArray("users");
	//
	//						for (int i = 0; i < jsonArrayUsers.length(); i++) {
	//
	//							JSONObject jsonObjUserRow = jsonArrayUsers.getJSONObject(i);
	//
	//							JSONObject jsonObjUser = jsonObjUserRow.getJSONObject("user");
	//
	//							JSONArray jsonArrayStars = jsonObjUser.getJSONArray("stars");
	//
	//							for (int j = 0; j < jsonArrayStars.length(); j++) {
	//
	//								JSONObject jsonObjStar = jsonArrayStars.getJSONObject(j);
	//
	//								StarPojo tempPojo = new StarPojo();
	//								tempPojo.setStarID(jsonObjStar.getString("starId"));
	//								tempPojo.setStarName(jsonObjStar.getString("starName"));
	//								tempPojo.setProfileImage(jsonObjStar.getString("profileImage"));
	//								tempPojo.setProfileCover(jsonObjStar.getString("profileCover"));
	//								tempPojo.setStarredDate(jsonObjStar.getString("starredDate"));
	//								tempPojo.setRank(jsonObjStar.getString("rank"));
	//								tempPojo.setStats(jsonObjStar.getString("stat"));
	//								arrStarPojos.add(tempPojo);
	//							}
	//						}
	//						
	//						
	//					}
	//
	//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
	//						handlerMyStar.sendEmptyMessage(0);
	//					} else {
	//						handlerMyStar.sendEmptyMessage(1);
	//					}
	//
	//				} catch (Exception e) {
	//					// TODO: handle exception
	//					e.printStackTrace();
	//					handlerMyStar.sendEmptyMessage(0);
	//				}
	//
	//			} else {
	//
	//				handlerMyStar.sendEmptyMessage(0);
	//			}
	//
	//		}
	//	}

	class MyStarCall extends Thread {

		String UserID;
		int PageNo;

		public MyStarCall(String UserID,int pageNo) {
			this.UserID = UserID;
			this.PageNo=pageNo;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = {"user", "page"};
			String[] value = {UserID,String.valueOf(PageNo)};

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.GET_FOLLOWING ,name,value, preferences.getString("token", null));

			// Log.v(Constant.TAG, "MyStarCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					JSONArray jsonArrayStars = jsonObject.getJSONArray("stars");
					arrEntryPojosChild.clear();
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
						arrEntryPojosChild.add(tempPojo);
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
						arrStarPojos.addAll(arrEntryPojosChild);

					} else {
						if (arrStarPojos != null && arrStarPojos.size() > 0) {
							mFirstVisibleItem = arrStarPojos.size();
							arrStarPojos.addAll(arrEntryPojosChild);

						} else {
							mFirstVisibleItem = 0;
							arrStarPojos.clear();
							arrStarPojos.addAll(arrEntryPojosChild);
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
			listUser.onRefreshComplete();
			isWebCall = false;
			if (msg.what == 1) {
				myStarAdapter.notifyDataSetChanged();
			} else {
				textNoData.setVisibility(View.VISIBLE);
			}
		}
	};

	class DeleteStarCall extends Thread {

		String UserID;

		public DeleteStarCall(String UserID) {
			this.UserID = UserID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "star" };
			String[] value = { UserID };

			String response = JSONParser.deleteRequest(Constant.SERVER_URL + Constant.DELETE_STAR + UserID, name, value, preferences.getString("token", null));

			//			Log.v(Constant.TAG, "DeleteStarCall response " + response + " UserID " + UserID);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerDeleteStar.sendEmptyMessage(0);
					} else {
						handlerDeleteStar.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerDeleteStar.sendEmptyMessage(0);
				}

			} else {

				handlerDeleteStar.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerDeleteStar = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			if (msg.what == 1) {

				Intent intent = new Intent("star_removed");
				intent.putExtra("UserID", arrSelectionStarID.get(0));
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

				int tempIndex = -1;

				for (int i = 0; i < arrStarPojos.size(); i++) {
					if (arrSelectionStarID.get(0).equals(arrStarPojos.get(i).getStarID())) {
						tempIndex = i;
						break;
					}
				}
				arrStarPojos.remove(tempIndex);
				myStarAdapter.notifyDataSetChanged();
				arrSelectionStarID.remove(0);

				if (arrSelectionStarID.size() == 0) {
					Utility.HideDialog(mContext);
				} else {
					new DeleteStarCall(arrSelectionStarID.get(0)).start();
				}

			} else {
				Utility.HideDialog(mContext);
			}
		}
	};

}
