package com.mobstar.stararea;

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
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class MyFeedbackFragment extends Fragment {

	Context mContext;

	TextView textMyFeedback;
	EntryListAdapter entryListAdapter;
	ArrayList<EntryPojo> arrEntryPojos = new ArrayList<EntryPojo>();
	ListView listEntry;
	public String sErrorMessage;
	String UserID;
	SharedPreferences preferences;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_my_feedback, container, false);
		mContext = getActivity();

		preferences = getActivity().getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		UserID = preferences.getString("userid", "");

		Utility.SendDataToGA("MyFeedback Screen", getActivity());

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		textMyFeedback = (TextView) view.findViewById(R.id.textMyFeedback);
		textMyFeedback.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		listEntry = (ListView) view.findViewById(R.id.listEntries);
		listEntry.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CommentActivity.class);
				intent.putExtra("entry_id", arrEntryPojos.get(position).getID());
				getActivity().startActivity(intent);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
		entryListAdapter = new EntryListAdapter();
		listEntry.setAdapter(entryListAdapter);

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new EntryCall(UserID).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	public class EntryListAdapter extends BaseAdapter {

		public EntryListAdapter() {

		}

		/* private view holder class */
		private class ViewHolder {
			TextView textUserName, textDescription, textTime;
			TextView textCommentCount;
			ImageView imgUserPic;
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
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.textCommentCount = (TextView) convertView.findViewById(R.id.textCommentCount);

				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			viewHolder.textCommentCount.setText(arrEntryPojos.get(position).getTotalComments());
			viewHolder.textUserName.setText(arrEntryPojos.get(position).getName());
			viewHolder.textDescription.setText(arrEntryPojos.get(position).getDescription());
			viewHolder.textTime.setText(arrEntryPojos.get(position).getCreated());

			if (arrEntryPojos.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrEntryPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
						.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);
				
				// Ion.with(mContext).load(arrEntryPojos.get(position).getProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).resize(Utility.dpToPx(mContext,
				// 45), Utility.dpToPx(mContext,
				// 45)).centerCrop().asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception exception, Bitmap bitmap) {
				// // TODO Auto-generated method stub
				// if (exception == null) {
				// viewHolder.imgUserPic.setImageBitmap(bitmap);
				// } else {
				// // Log.v(Constant.TAG, "Exception " +
				// // exception.toString());
				// }
				// }
				// });
			}

			return convertView;
		}
	}

	class EntryCall extends Thread {

		String sUserID;

		public EntryCall(String sUserID) {
			// TODO Auto-generated constructor stub
			this.sUserID = sUserID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.ENTRY + "?orderBy=latest&user=" + sUserID, preferences.getString("token", null));

			// Log.v(Constant.TAG, "EntryCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("entries")) {

						arrEntryPojos.clear();

						JSONArray jsonArrayEntries = jsonObject.getJSONArray("entries");

						for (int i = 0; i < jsonArrayEntries.length(); i++) {

							JSONObject jsonObj = jsonArrayEntries.getJSONObject(i);

							JSONObject jsonObjEntry = jsonObj.getJSONObject("entry");

							EntryPojo entryPojo = new EntryPojo();

							if (jsonObjEntry.has("user")) {
								JSONObject jsonObjUser = jsonObjEntry.getJSONObject("user");
								entryPojo.setUserID(jsonObjUser.getString("id"));
								entryPojo.setUserName(jsonObjUser.getString("userName"));
								entryPojo.setUserDisplayName(jsonObjUser.getString("displayName"));
								entryPojo.setProfileImage(jsonObjUser.getString("profileImage"));
								entryPojo.setProfileCover(jsonObjUser.getString("profileCover"));
								entryPojo.setTagline(jsonObjUser.getString("tagLine"));
								if (jsonObjUser.has("isMyStar")) {
									entryPojo.setIsMyStar(jsonObjUser.getString("isMyStar"));
								}
							}
							entryPojo.setID(jsonObjEntry.getString("id"));

							entryPojo.setCategory(jsonObjEntry.getString("category"));
							entryPojo.setType(jsonObjEntry.getString("type"));
							entryPojo.setName(jsonObjEntry.getString("name"));
							entryPojo.setDescription(jsonObjEntry.getString("description"));
							entryPojo.setCreated(jsonObjEntry.getString("created"));
							entryPojo.setModified(jsonObjEntry.getString("modified"));
							entryPojo.setUpVotesCount(jsonObjEntry.getString("upVotes"));
							entryPojo.setDownvotesCount(jsonObjEntry.getString("downVotes"));
							entryPojo.setRank(jsonObjEntry.getString("rank"));
							entryPojo.setLanguage(jsonObjEntry.getString("language"));
							entryPojo.setDeleted(jsonObjEntry.getString("deleted"));
							entryPojo.setTotalComments(jsonObjEntry.getString("totalComments"));

							if (jsonObjEntry.getString("totalComments").trim().equals("0")) {
								continue;
							}

							JSONArray jsonArrayTags = jsonObjEntry.getJSONArray("tags");
							for (int j = 0; j < jsonArrayTags.length(); j++) {
								entryPojo.addTags(jsonArrayTags.getString(j));
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

								arrEntryPojos.add(entryPojo);
							}

						}
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerEntry.sendEmptyMessage(0);
					} else {
						handlerEntry.sendEmptyMessage(1);
					}

				} catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
					handlerEntry.sendEmptyMessage(0);
				}
			}

		}
	}

	Handler handlerEntry = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (msg.what == 1) {
				entryListAdapter.notifyDataSetChanged();
			} else {

				OkayAlertDialog(sErrorMessage);
			}
		}
	};

	void OkayAlertDialog(final String msg) {

		if (getActivity() != null && !getActivity().isFinishing()) {
			getActivity().runOnUiThread(new Runnable() {

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
