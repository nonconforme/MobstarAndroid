package com.mobstar.talentconnect;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.mobstar.R;
import com.mobstar.custom.PullToRefreshListView;
import com.mobstar.custom.PullToRefreshListView.OnRefreshListener;
import com.mobstar.home.CommentActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class TalentConnectCommentActivity extends Activity {

	Context mContext;

	TextView textComments;
	CommentListAdapter commentListAdapter;
	ArrayList<EntryPojo> arrEntryPojosChild = new ArrayList<EntryPojo>();
	ArrayList<EntryPojo> arrEntryPojos = new ArrayList<EntryPojo>();
	PullToRefreshListView listComment;
	public String sErrorMessage;
	String UserID;
	SharedPreferences preferences;

	TextView textNoData;
	
	// pagination
		int mFirstVisibleItem = 0;
		private boolean isRefresh = false;
		private boolean isWebCall = false;
		private static int currentPage = 1;
		private boolean isNextPageAvail=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_comment);

		mContext = TalentConnectCommentActivity.this;

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		UserID = preferences.getString("userid", "");

		Utility.SendDataToGA("TalentConnect Comment Screen", TalentConnectCommentActivity.this);
		
		InitControls();
		
		listComment.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub\
				if (Utility.isNetworkAvailable(mContext)) {
					isRefresh = true;
					isWebCall = true;
					currentPage=1;
					new CommentsCall(UserID,currentPage).start();
				} else {
					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		listComment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CommentActivity.class);
				intent.putExtra("entry_id", arrEntryPojos.get(position).getID());
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
		commentListAdapter = new CommentListAdapter();
		listComment.setAdapter(commentListAdapter);

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new CommentsCall(UserID,currentPage).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

		
		
		
		listComment.setOnScrollListener(new OnScrollListener() {

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
						new CommentsCall(UserID,currentPage).start();
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

		textComments = (TextView) findViewById(R.id.textComments);
		textComments.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		listComment = (PullToRefreshListView) findViewById(R.id.listComment);
		

	}

	public class CommentListAdapter extends BaseAdapter {

		public CommentListAdapter() {

		}

		/* private view holder class */
		private class ViewHolder {
			TextView textUserName, textDescription, textTime;
			TextView textCommentCount;
			ImageView imgEntry;
		}

		@Override
		public int getCount() {
			return arrEntryPojos.size();
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

				convertView = mInflater.inflate(R.layout.row_item_my_feedback, null);
				viewHolder = new ViewHolder();

				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
				viewHolder.textDescription = (TextView) convertView.findViewById(R.id.textDescription);
				viewHolder.imgEntry = (ImageView) convertView.findViewById(R.id.imgEntry);
				viewHolder.textCommentCount = (TextView) convertView.findViewById(R.id.textCommentCount);

				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.textCommentCount.setText(arrEntryPojos.get(position).getTotalComments());
			viewHolder.textUserName.setText(arrEntryPojos.get(position).getName());
			viewHolder.textDescription.setText(Utility.unescape_perl_string(arrEntryPojos.get(position).getDescription()));
			viewHolder.textTime.setText(arrEntryPojos.get(position).getCreated());

			String imageUrl;

			if (arrEntryPojos.get(position).getType().equals("video")) {
				imageUrl = arrEntryPojos.get(position).getVideoThumb();
			} else {
				imageUrl = arrEntryPojos.get(position).getImageLink();
			}

			Picasso.with(mContext).load(imageUrl).resize(Utility.dpToPx(mContext, 80), Utility.dpToPx(mContext, 80)).centerCrop().placeholder(R.drawable.icon_default_placeholder)
					.error(R.drawable.icon_default_placeholder).into(viewHolder.imgEntry);

			return convertView;
		}
	}

	class CommentsCall extends Thread {

		String sUserID;
		int PageNo;
		
		public CommentsCall(String sUserID, int pageNo) {
			this.sUserID = sUserID;
			this.PageNo=pageNo;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_COMMENT_INDEX + "?user=" + sUserID +"&page=" +PageNo, preferences.getString("token", null));

			// Log.v(Constant.TAG, "CommentsCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("comments")) {

						
						 arrEntryPojosChild.clear();
						JSONArray jsonArrayComment = jsonObject.getJSONArray("comments");

						for (int i = 0; i < jsonArrayComment.length(); i++) {

							JSONObject jsonObj = jsonArrayComment.getJSONObject(i);

							JSONObject jsonObjComment = jsonObj.getJSONObject("comment");

							JSONObject jsonObjEntry = jsonObjComment.getJSONObject("entry");

							EntryPojo entryPojo = new EntryPojo();


							entryPojo.setUserName(jsonObjEntry.getString("name"));
							entryPojo.setID(jsonObjEntry.getString("id"));
							entryPojo.setDescription(jsonObjEntry.getString("description"));
							entryPojo.setType(jsonObjEntry.getString("type"));
							entryPojo.setCreated(jsonObjEntry.getString("created"));
							if (jsonObjEntry.has("totalComments")) {
								entryPojo.setTotalComments(jsonObjEntry.getString("totalComments"));
								if (jsonObjEntry.getString("totalComments").trim().equals("0")) {
									continue;
								}
							}

							if (jsonObjEntry.has("videoThumb")) {
								entryPojo.setVideoThumb(jsonObjEntry.getString("videoThumb"));
							}

							

							if (!jsonObjEntry.has("entryFiles")) {
//								Log.v(Constant.TAG, "entryFiles not exist in ID " + entryPojo.getID());
							} else {
								JSONArray jsonArrayFiles = jsonObjEntry.getJSONArray("entryFiles");
								for (int j = 0; j < jsonArrayFiles.length(); j++) {
									JSONObject jsonObjFile = jsonArrayFiles.getJSONObject(j);

									if (entryPojo.getType().equalsIgnoreCase("image")) {
										entryPojo.setImageLink(jsonObjFile.getString("filePath"));
										entryPojo.setFiletype(jsonObjFile.getString("fileType"));
									} else if (entryPojo.getType().equalsIgnoreCase("audio")) {
										if (j == 0) {

											entryPojo.setAudioLink(jsonObjFile.getString("filePath"));
											entryPojo.setFiletype(jsonObjFile.getString("fileType"));
										} else if (j == 1) {

											entryPojo.setImageLink(jsonObjFile.getString("filePath"));
											entryPojo.setFiletype(jsonObjFile.getString("fileType"));
										}
									} else if (entryPojo.getType().equalsIgnoreCase("video")) {
										entryPojo.setVideoLink(jsonObjFile.getString("filePath"));
										entryPojo.setFiletype(jsonObjFile.getString("fileType"));
									}
								}

								arrEntryPojosChild.add(entryPojo);
							}

						}
					}
					
					if (isRefresh) {
						mFirstVisibleItem = 0;
						runOnUiThread(new Runnable() {
							public void run() {
								listComment.onRefreshComplete();
							}
						});

						isRefresh = false;
						arrEntryPojos.clear();
						arrEntryPojos.addAll(arrEntryPojosChild);

					} else {
						if (arrEntryPojos != null && arrEntryPojos.size() > 0) {
							mFirstVisibleItem = arrEntryPojos.size();
							arrEntryPojos.addAll(arrEntryPojosChild);

						} else {
							mFirstVisibleItem = 0;
							arrEntryPojos.clear();
							arrEntryPojos.addAll(arrEntryPojosChild);
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
						handlerComment.sendEmptyMessage(0);
					} else {
						handlerComment.sendEmptyMessage(1);
					}

				} catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
					handlerComment.sendEmptyMessage(0);
				}
			}

		}
	}

	Handler handlerComment = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			listComment.onRefreshComplete();
			isWebCall = false;
			if (arrEntryPojos.size()==0) {
				textNoData.setVisibility(View.VISIBLE);	
			}
			
			
			if (msg.what == 1) {
				commentListAdapter.notifyDataSetChanged();
			} else {
				textNoData.setVisibility(View.VISIBLE);
			}
		}
	};

	void OkayAlertDialog(final String msg) {

		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);

					// set title
					alertDialogBuilder.setTitle(getResources().getString(R.string.app_name));

					// set dialog message
					alertDialogBuilder.setMessage(msg).setCancelable(false).setNeutralButton("OK", null);

					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();

					// show it
					alertDialog.show();
				}
			});
		}

	}
}
