package com.mobstar.home;

import java.util.ArrayList;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.pojo.CommentPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class CommentActivity extends Activity {

	Context mContext;

	TextView textComment;
	TextView textNoData;

	EditText editComment;
	Typeface typeface;
	ImageView btnSend;

	SharedPreferences preferences;

	ListView listComment;
	CommentListAdapter commentListAdapter;

	String EntryID,UserID;

	ArrayList<CommentPojo> arrCommentpPojos = new ArrayList<CommentPojo>();
	ArrayList<String> arrSelectionCommentedID = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_comment);
		mContext = CommentActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			EntryID = extras.getString("entry_id");
		}
		
		UserID=preferences.getString("userid", "0");
		InitControls();

		Utility.SendDataToGA("Comment Screen", CommentActivity.this);

	}

	void InitControls() {

		textComment = (TextView) findViewById(R.id.textComment);
		textComment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		textNoData = (TextView) findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);

		typeface = Typeface.createFromAsset(mContext.getAssets(), "GOTHAM-LIGHT.TTF");
		editComment = (EditText) findViewById(R.id.editComment);
		editComment.setTypeface(typeface);

		btnSend = (ImageView) findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub

				if (!editComment.getText().toString().trim().equals("")) {

					sErrorMessage = "";

					Utility.ShowProgressDialog(mContext, getString(R.string.loading));

					if (Utility.isNetworkAvailable(mContext)) {

						String strMsg=editComment.getText().toString().trim();
						String ContentMsg=strMsg.replace("\"","");
						new PostCommentCall(StringEscapeUtils.escapeJava(ContentMsg)).start();

					} else {

						Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
						Utility.HideDialog(mContext);
					}
				}

			}
		});

		listComment = (ListView) findViewById(R.id.listComment);
		
		listComment.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		listComment.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.menu_delete:
//					Log.d("mobstar","List size"+arrSelectionCommentedID.size());
//					do {
//						arrSelectionCommentedID.remove(0);
//						Log.d("mobstar","size is"+arrSelectionCommentedID.size());
////						Log.d("mobstar","delete array is"+arrSelectionCommentedID.get(0));
//					} while (arrSelectionCommentedID.size()!=0);
					if(arrSelectionCommentedID.size()>0){
						DeleteComment();	
					}
					
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
				final int checkedCount = listComment.getCheckedItemCount();
				// Set the CAB title according to total checked items
				arg0.setTitle(checkedCount + " " + getString(R.string.selected));

				if (arrSelectionCommentedID.contains(arrCommentpPojos.get(position).getCommentID() + "")) {
					arrSelectionCommentedID.remove(arrCommentpPojos.get(position).getCommentID() + "");
				} else if(arrCommentpPojos.get(position).getUserID().equalsIgnoreCase(UserID)){ //user can delete own comments only
					arrSelectionCommentedID.add(arrCommentpPojos.get(position).getCommentID() + "");
				}

			}
		});
		commentListAdapter = new CommentListAdapter();
		listComment.setAdapter(commentListAdapter);

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new CommentCall(EntryID).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	void DeleteComment() {
		sErrorMessage = "";
		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new DeleteCommentCall(arrSelectionCommentedID.get(0)).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	public class CommentListAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public CommentListAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return arrCommentpPojos.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {

			final ViewHolder viewHolder;

			if (convertView == null) {

				convertView = inflater.inflate(R.layout.row_comment, null);

				viewHolder = new ViewHolder();
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.imgUserGroup= (ImageView) convertView.findViewById(R.id.imgUserGroup);
				viewHolder.imgPin= (ImageView) convertView.findViewById(R.id.imgPin);
				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
				viewHolder.textDescription = (TextView) convertView.findViewById(R.id.textDescription);

				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.textTime.setText(arrCommentpPojos.get(position).getCommentDate());

			viewHolder.textDescription.setText(Utility.unescape_perl_string(arrCommentpPojos.get(position).getCommentText()));

			viewHolder.textUserName.setText(arrCommentpPojos.get(position).getUserDisplayName());

			//khyati
			if(arrCommentpPojos.get(position).getUserGroup().equalsIgnoreCase("1")) {
				//admin
				viewHolder.imgPin.setVisibility(View.GONE);
				viewHolder.imgUserGroup.setVisibility(View.GONE);
			}
			else if(arrCommentpPojos.get(position).getUserGroup().equalsIgnoreCase("2")) {
				//moderator
				viewHolder.imgPin.setVisibility(View.GONE);
				viewHolder.imgUserGroup.setVisibility(View.GONE);
			}
			else if(arrCommentpPojos.get(position).getUserGroup().equalsIgnoreCase("3")) {
				//user
				viewHolder.imgPin.setVisibility(View.GONE);
				viewHolder.imgUserGroup.setVisibility(View.GONE);
			}
			else if(arrCommentpPojos.get(position).getUserGroup().equalsIgnoreCase("4")) {
				//team
				viewHolder.imgPin.setVisibility(View.VISIBLE);
				viewHolder.imgUserGroup.setImageResource(R.drawable.profile_logo);
				viewHolder.imgUserGroup.setVisibility(View.VISIBLE);
			}
			else if(arrCommentpPojos.get(position).getUserGroup().equalsIgnoreCase("5")) {
				//team
				viewHolder.imgPin.setVisibility(View.VISIBLE);
				viewHolder.imgUserGroup.setImageResource(R.drawable.profile_logo);
				viewHolder.imgUserGroup.setVisibility(View.VISIBLE);
			}

			if (arrCommentpPojos.get(position).getUserProfileImage().equals("")) {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(arrCommentpPojos.get(position).getUserProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
				.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);

				// Ion.with(mContext).load(arrCommentpPojos.get(position).getUserProfileImage()).withBitmap().placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small)
				// .resize(Utility.dpToPx(mContext, 45),
				// Utility.dpToPx(mContext,
				// 45)).centerCrop().asBitmap().setCallback(new
				// FutureCallback<Bitmap>() {
				//
				// @Override
				// public void onCompleted(Exception exception, Bitmap bitmap) {
				// // TODO Auto-generated method stub
				// if (exception == null) {
				//
				// viewHolder.imgUserPic.setImageBitmap(bitmap);
				//
				// } else {
				// // Log.v(Constant.TAG, "Exception " +
				// // exception.toString());
				// }
				// }
				// });
			}

			return convertView;
		}

		class ViewHolder {
			TextView textUserName, textDescription, textTime;
			ImageView imgUserPic,imgUserGroup,imgPin;

		}
	}

	class CommentCall extends Thread {

		String EntryID;

		public CommentCall(String EntryID) {
			this.EntryID = EntryID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_COMMENT + "?entry=" + EntryID, preferences.getString("token", null));

			//Log.v(Constant.TAG, "CommentCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("comments")) {

						JSONArray jsonArrayUsers = jsonObject.getJSONArray("comments");

						for (int i = 0; i < jsonArrayUsers.length(); i++) {

							JSONObject jsonObjCommentRow = jsonArrayUsers.getJSONObject(i);

							JSONObject jsonObjComment = jsonObjCommentRow.getJSONObject("comment");

							JSONObject jsonObjUser = jsonObjComment.getJSONObject("user");

							if (jsonObjComment.getString("commentDeleted").equals("false")) {

								CommentPojo tempCommentPojo = new CommentPojo();
								tempCommentPojo.setCommentID(jsonObjComment.getString("commentId"));
								tempCommentPojo.setCommentText(jsonObjComment.getString("comment"));
								tempCommentPojo.setCommentDate(jsonObjComment.getString("commentDate"));
								tempCommentPojo.setUserID(jsonObjUser.getString("id"));

								if (jsonObjUser.has("userName")) {
									tempCommentPojo.setUserName(jsonObjUser.getString("userName"));
								}
								tempCommentPojo.setUserDisplayName(jsonObjUser.getString("displayName"));
								tempCommentPojo.setUserFullName(jsonObjUser.getString("fullName"));
								tempCommentPojo.setUserProfileImage(jsonObjUser.getString("profileImage"));
								tempCommentPojo.setUserGroup(jsonObjUser.getString("usergroup"));

								arrCommentpPojos.add(tempCommentPojo);
							}

						}
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerComment.sendEmptyMessage(0);
					} else {
						handlerComment.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerComment.sendEmptyMessage(0);
				}

			} else {

				handlerComment.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerComment = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			if (arrCommentpPojos.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				commentListAdapter.notifyDataSetChanged();

			} else {

			}
		}
	};

	public String sErrorMessage;

	class DeleteCommentCall extends Thread {

		String CommentID;

		public DeleteCommentCall(String CommentID) {
			this.CommentID = CommentID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "comment" };
			String[] value = { CommentID };

			String response = JSONParser.deleteRequest(Constant.SERVER_URL + Constant.DELETE_COMMENT + CommentID, name, value, preferences.getString("token", null));

			//Log.v(Constant.TAG, "DeleteCommentCall response " + response + " UserID " + CommentID);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerDeleteComment.sendEmptyMessage(0);
					} else {
						handlerDeleteComment.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerDeleteComment.sendEmptyMessage(0);
				}

			} else {

				handlerDeleteComment.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerDeleteComment = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			if (msg.what == 1) {

				int tempIndex = -1;

				for (int i = 0; i < arrCommentpPojos.size(); i++) {
					if (arrSelectionCommentedID.get(0).equals(arrCommentpPojos.get(i).getCommentID())) {
						tempIndex = i;
						break;
					}
				}
				arrCommentpPojos.remove(tempIndex);
				commentListAdapter.notifyDataSetChanged();
				arrSelectionCommentedID.remove(0);

				if (arrSelectionCommentedID.size() == 0) {
					Utility.HideDialog(mContext);
				} else {
					new DeleteCommentCall(arrSelectionCommentedID.get(0)).start();
				}

			} else {
				Utility.HideDialog(mContext);
			}
		}
	};

	class PostCommentCall extends Thread {

		String Comment;

		public PostCommentCall(String Comment) {
			this.Comment = Comment;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			
			String[] name = { "comment", "entryId" };
			String[] value = { Comment, EntryID };

			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.GET_COMMENT + EntryID, name, value, preferences.getString("token", null));

			//Log.v(Constant.TAG, "PostCommentCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerPostComment.sendEmptyMessage(0);
					} else {
						handlerPostComment.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerPostComment.sendEmptyMessage(0);
				}

			} else {

				handlerPostComment.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerPostComment = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			if (msg.what == 1) {
				if (Utility.isNetworkAvailable(mContext)) {

					editComment.setText("");
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(editComment.getWindowToken(), 0);
					arrCommentpPojos.clear();

					new CommentCall(EntryID).start();

				} else {

					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}

			} else {
				Utility.HideDialog(mContext);
			}
		}
	};
}
