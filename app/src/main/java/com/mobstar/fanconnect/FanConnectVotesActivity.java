package com.mobstar.fanconnect;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.mobstar.R;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class FanConnectVotesActivity extends Activity {

	Context mContext;
	SharedPreferences preferences;

	TextView textNoData;

	ListView listUser;
	VoteAdapter voteAdapter;

	protected String sErrorMessage = "";
	ArrayList<EntryPojo> arrEntryPojos = new ArrayList<EntryPojo>();

	EditText editMessage;
	Typeface typeface;
	ImageView btnSend;
	
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fanconnect_votes);

		mContext = FanConnectVotesActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		InitControls();

		Utility.SendDataToGA("FanConnect Votes Screen", FanConnectVotesActivity.this);

	}

	void InitControls() {

		TextView textVotes = (TextView) findViewById(R.id.textVotes);
		textVotes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		textNoData = (TextView) findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);
		
		adView = (AdView)findViewById(R.id.adView);

		listUser = (ListView) findViewById(R.id.listUser);
		voteAdapter = new VoteAdapter();
		listUser.setAdapter(voteAdapter);

		typeface = Typeface.createFromAsset(mContext.getAssets(), "GOTHAM-LIGHT.TTF");
		editMessage = (EditText) findViewById(R.id.editMessage);
		editMessage.setTypeface(typeface);

		btnSend = (ImageView) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

				if (!editMessage.getText().toString().trim().equals("")) {

					if (arrEntryPojos.size() == 0) {
						Toast.makeText(mContext, getString(R.string.you_dont_have_any_voters_yet), Toast.LENGTH_SHORT).show();
					} else {

						sErrorMessage = "";

						Utility.ShowProgressDialog(mContext, getString(R.string.loading));

						if (Utility.isNetworkAvailable(mContext)) {

							new PostBulkMessageCall(editMessage.getText().toString().trim()).start();

						} else {

							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}

					}

				}

			}
		});

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new EntryCall().start();

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

	class EntryCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_FAN_VOTE + "?user=" + preferences.getString("userid", "0"),
					preferences.getString("token", null));

//			Log.v(Constant.TAG, "EntryCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("votes")) {

						arrEntryPojos.clear();

						JSONArray jsonArrayEntries = jsonObject.getJSONArray("votes");

						for (int i = 0; i < jsonArrayEntries.length(); i++) {

							JSONObject jsonObj = jsonArrayEntries.getJSONObject(i);

							JSONObject jsonObjVote = jsonObj.getJSONObject("vote");

							JSONObject jsonObjEntry = jsonObjVote.getJSONObject("entry");

							EntryPojo entryPojo = new EntryPojo();

							if (jsonObjVote.has("user")) {
								JSONObject jsonObjUser = jsonObjVote.getJSONObject("user");
								entryPojo.setUserID(jsonObjUser.getString("id"));
								entryPojo.setUserName(jsonObjUser.getString("userName"));
								entryPojo.setUserDisplayName(jsonObjUser.getString("displayName"));
								entryPojo.setProfileImage(jsonObjUser.getString("profileImage"));
								entryPojo.setProfileCover(jsonObjUser.getString("profileCover"));
								if (jsonObjUser.has("tagLine")) {
									entryPojo.setTagline(jsonObjUser.getString("tagLine"));
								}
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

							if (jsonObjEntry.has("videoThumb")) {
								entryPojo.setVideoThumb(jsonObjEntry.getString("videoThumb"));
							}

							JSONArray jsonArrayTags = jsonObjEntry.getJSONArray("tags");
							for (int j = 0; j < jsonArrayTags.length(); j++) {
								entryPojo.addTags(jsonArrayTags.getString(j));
							}

							if (!jsonObjEntry.has("entryFiles")) {
								// Log.v(Constant.TAG,
								// "entryFiles not exist in ID " +
								// entryPojo.getID());
							} else {
								JSONArray jsonArrayFiles = jsonObjEntry.getJSONArray("entryFiles");
								for (int j = 0; j < jsonArrayFiles.length(); j++) {
									JSONObject jsonObjFile = jsonArrayFiles.getJSONObject(j);

									if (entryPojo.getType().equalsIgnoreCase("image")) {
										entryPojo.setImageLink(jsonObjFile.getString("filePath"));
										entryPojo.setFiletype(jsonObjFile.getString("fileType"));

										// Log.v(Constant.TAG,
										// "Image "+jsonObjFile.getString("filePath"));
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

			if (arrEntryPojos.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				voteAdapter.notifyDataSetChanged();
			} else {

				OkayAlertDialog(sErrorMessage);
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

	public class VoteAdapter extends BaseAdapter {

		/* private view holder class */
		private class ViewHolder {
			TextView textVoterName;
			ImageView imgUserPic;
			ImageView imgEntry;
			TextView textTitle;
			TextView textTime;
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

		public View getView(final int position, View convertView, ViewGroup parent) {
			final ViewHolder viewHolder;

			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {

				convertView = mInflater.inflate(R.layout.row_item_vote, null);
				viewHolder = new ViewHolder();

				viewHolder.textVoterName = (TextView) convertView.findViewById(R.id.textVoterName);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.imgEntry = (ImageView) convertView.findViewById(R.id.imgEntry);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
				viewHolder.textTitle = (TextView) convertView.findViewById(R.id.textTitle);
				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			if (arrEntryPojos.get(position).getProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
				Picasso.with(mContext).load(arrEntryPojos.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
						.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);

				// Ion.with(mContext).load(arrEntryPojos.get(position).getProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small)
				// .resize(Utility.dpToPx(mContext, 60),
				// Utility.dpToPx(mContext,
				// 60)).centerCrop().asBitmap().setCallback(new
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

			viewHolder.imgUserPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					Intent intent = new Intent(mContext, NewProfileActivity.class);
					final UserProfile userProfile = UserProfile.newBuilder()
							.setUserCoverImage(arrEntryPojos.get(position).getProfileCover())
							.setUserId(arrEntryPojos.get(position).getUserID())
							.setUserName(arrEntryPojos.get(position).getUserName())
							.setUserDisplayName(arrEntryPojos.get(position).getUserDisplayName())
							.setUserPic(arrEntryPojos.get(position).getProfileImage())
							.setIsMyStar(arrEntryPojos.get(position).getIsMyStar())
							.setUserTagline(arrEntryPojos.get(position).getTagline())
							.build();
					intent.putExtra(NewProfileActivity.USER, userProfile);
//					intent.putExtra("UserCoverImage", arrEntryPojos.get(position).getProfileCover());
//					intent.putExtra("UserID", arrEntryPojos.get(position).getUserID());
//					intent.putExtra("UserName", arrEntryPojos.get(position).getUserName());
//					intent.putExtra("UserDisplayName", arrEntryPojos.get(position).getUserDisplayName());
//					intent.putExtra("UserPic", arrEntryPojos.get(position).getProfileImage());
//					intent.putExtra("IsMyStar", arrEntryPojos.get(position).getIsMyStar());
//					intent.putExtra("UserTagline", arrEntryPojos.get(position).getTagline());
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				}
			});
			String imageUrl;

			viewHolder.imgEntry.setImageResource(R.drawable.icon_default_placeholder);
			if (arrEntryPojos.get(position).getType().equals("video")) {
				imageUrl = arrEntryPojos.get(position).getVideoThumb();
			} else {
				imageUrl = arrEntryPojos.get(position).getImageLink();
			}

			Picasso.with(mContext).load(imageUrl).resize(Utility.dpToPx(mContext, 80), Utility.dpToPx(mContext, 80)).centerCrop().placeholder(R.drawable.icon_default_placeholder)
					.error(R.drawable.icon_default_placeholder).into(viewHolder.imgEntry);

			// Ion.with(mContext).load(imageUrl).withBitmap().placeholder(R.drawable.icon_default_placeholder).error(R.drawable.icon_default_placeholder)
			// .resize(Utility.dpToPx(mContext, 80), Utility.dpToPx(mContext,
			// 80)).centerCrop().asBitmap().setCallback(new
			// FutureCallback<Bitmap>() {
			//
			// @Override
			// public void onCompleted(Exception exception, Bitmap bitmap) {
			// // TODO Auto-generated method stub
			// if (exception == null) {
			//
			// viewHolder.imgEntry.setImageBitmap(bitmap);
			// } else {
			// // Log.v(Constant.TAG, "Exception " +
			// // exception.toString());
			// }
			// }
			// });

			viewHolder.textVoterName.setText(arrEntryPojos.get(position).getUserDisplayName());
			viewHolder.textTitle.setText(arrEntryPojos.get(position).getName());

			viewHolder.textTime.setText(arrEntryPojos.get(position).getCreated());

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
			String[] value = { Message, "voters" };

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
