package com.mobstar.fanconnect;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.home.CommentActivity;
import com.mobstar.pojo.FeedbackPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class FanFeedbackActivity extends Activity {

	Context mContext;

	TextView textComments;
	CommentListAdapter commentListAdapter;
	ArrayList<FeedbackPojo> arrFeedbackPojos = new ArrayList<FeedbackPojo>();
	ListView listComment;
	public String sErrorMessage;
	String UserID;
	SharedPreferences preferences;

	TextView textNoData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fan_feedback);

		mContext = FanFeedbackActivity.this;

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);
		
		UserID = preferences.getString("userid", "");
		
		InitControls();

		Utility.SendDataToGA("FanFeedback Screen", FanFeedbackActivity.this);
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

		listComment = (ListView) findViewById(R.id.listComment);
		listComment.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CommentActivity.class);
				intent.putExtra("entry_id", arrFeedbackPojos.get(position).getFeedbackId());
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
		commentListAdapter = new CommentListAdapter();
		listComment.setAdapter(commentListAdapter);

		Utility.ShowProgressDialog(mContext, "Loading");

		if (Utility.isNetworkAvailable(mContext)) {

			new FeedbackCall(UserID).start();

		} else {

			Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

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
			return arrFeedbackPojos.size();
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

			viewHolder.textCommentCount.setVisibility(View.GONE);
			viewHolder.textUserName.setText(arrFeedbackPojos.get(position).getEntryName());
			viewHolder.textDescription.setText(arrFeedbackPojos.get(position).getEntryName());
			viewHolder.textTime.setText(arrFeedbackPojos.get(position).getLastComment());

			Picasso.with(mContext).load(arrFeedbackPojos.get(position).getThumbnail()).resize(Utility.dpToPx(mContext, 80), Utility.dpToPx(mContext, 80)).centerCrop()
					.placeholder(R.drawable.icon_default_placeholder).error(R.drawable.icon_default_placeholder).into(viewHolder.imgEntry);

			return convertView;
		}
	}

	class FeedbackCall extends Thread {

		String sUserID;

		public FeedbackCall(String sUserID) {
			// TODO Auto-generated constructor stub
			this.sUserID = sUserID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_FEEDBACK + "?period=allTime", preferences.getString("token", null));

			// Log.v(Constant.TAG, "CommentsCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("entries")) {

						arrFeedbackPojos.clear();

						JSONArray jsonArrayEntries = jsonObject.getJSONArray("entries");

						for (int i = 0; i < jsonArrayEntries.length(); i++) {

							JSONObject jsonEntryObj = jsonArrayEntries.getJSONObject(i);

							FeedbackPojo feedbackPojo = new FeedbackPojo();
							feedbackPojo.setEntryName(jsonEntryObj.getString("entryName"));
							feedbackPojo.setFeedbackId(jsonEntryObj.getString("id"));
							feedbackPojo.setThumbnail(jsonEntryObj.getString("thumbnail"));
							feedbackPojo.setLastComment(jsonEntryObj.getString("lastComment"));

							arrFeedbackPojos.add(feedbackPojo);
						}
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerFeedback.sendEmptyMessage(0);
					} else {
						handlerFeedback.sendEmptyMessage(1);
					}

				} catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
					handlerFeedback.sendEmptyMessage(0);
				}
			}

		}
	}

	Handler handlerFeedback = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (arrFeedbackPojos.size() == 0) {
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
