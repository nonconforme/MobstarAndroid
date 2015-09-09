package com.mobstar.inbox;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.pojo.MessagePojo;
import com.mobstar.pojo.ParticipantsPojo;
import com.mobstar.upload.MessageActivity;
import com.mobstar.upload.MessageComposeActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class InboxFragment extends Fragment {

	Context mContext;
	private SharedPreferences preferences;
	TextView textNoData;
	ListView listUser;
	MessageAdapter msgAdapter;
	ArrayList<MessagePojo> arrMessage=new ArrayList<MessagePojo>();
	private String sErrorMessage,UserID;
	ArrayList<String> arrSelectionThreadID = new ArrayList<String>();
	private ImageView btnAdd;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_inbox, container, false);

		mContext = getActivity();

		preferences = mContext.getSharedPreferences("mobstar_pref", Context.MODE_PRIVATE);
		UserID=preferences.getString("userid", "0");
		Utility.SendDataToGA("Inbox Screen", getActivity());

		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("msg_added"));

		LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter("msg_read"));

		return view;
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent

			if (intent.getAction().equalsIgnoreCase("msg_added")) {

				String threadId = intent.getExtras().getString("threadId");
				String msg=intent.getExtras().getString("msg");
				String profileImg=intent.getExtras().getString("profileImage");
				String name=intent.getExtras().getString("name");


				for (int i = 0; i < arrMessage.size(); i++) {
					if(arrMessage.get(i).getThreadId().equalsIgnoreCase(threadId)){
						arrMessage.get(i).setMessageContent(msg);
						arrMessage.get(i).setDisplayName(name);
						arrMessage.get(i).setProfileImage(profileImg);
					}

				}

			}else if(intent.getAction().equalsIgnoreCase("msg_read")){
				String threadId = intent.getExtras().getString("threadId");
				for (int i = 0; i < arrMessage.size(); i++) {
					if(arrMessage.get(i).getThreadId().equalsIgnoreCase(threadId)){
						arrMessage.get(i).setRead(1);
					}
				}
			}
			msgAdapter.notifyDataSetChanged();
			listUser.invalidate();
		}
	};

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		TextView textMsg = (TextView) view.findViewById(R.id.textMsg);
		textMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});

		btnAdd = (ImageView) view.findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext,MessageComposeActivity.class);
				intent.putExtra("fromIsMsg",true);
				startActivityForResult(intent,101);
				getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		textNoData = (TextView) view.findViewById(R.id.textNoData);
		textNoData.setText(getString(R.string.you_havent_any_messages_yet));
		textNoData.setVisibility(View.GONE);

		listUser = (ListView) view.findViewById(R.id.listMessage);

		listUser.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

		listUser.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.menu_delete:
					Log.d("mobstar","List size"+arrMessage.size());
					//					do {
					//						arrSelectionCommentedID.remove(0);
					//						Log.d("mobstar","size is"+arrSelectionCommentedID.size());
					////						Log.d("mobstar","delete array is"+arrSelectionCommentedID.get(0));
					//					} while (arrSelectionCommentedID.size()!=0);
					if(arrSelectionThreadID.size()>0){
						DeleteThread();	
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
				final int checkedCount = listUser.getCheckedItemCount();
				// Set the CAB title according to total checked items
				arg0.setTitle(checkedCount + " Selected");
				if(arrSelectionThreadID.contains(arrMessage.get(position).getThreadId()+"")){
					arrSelectionThreadID.remove(arrMessage.get(position).getThreadId() + "");
				} else if(arrMessage.get(position).getUserId().equalsIgnoreCase(UserID)){
					arrSelectionThreadID.add(arrMessage.get(position).getThreadId());
				}

			}
		});

		msgAdapter = new MessageAdapter();
		listUser.setAdapter(msgAdapter);

		Utility.ShowProgressDialog(mContext, "Loading");

		if (Utility.isNetworkAvailable(mContext)) {
			new MessageCall().start();
		} else {

			Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

		listUser.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Log.d("mobstar", "thread ID"+arrMessage.get(position).getThreadId());
				new MessageRead(arrMessage.get(position).getThreadId()).start();

				if(arrMessage.get(position).getMessageGroup() == 1){
					Intent intent=new Intent(mContext,GroupMessageDetail.class);
					intent.putExtra("threadId",arrMessage.get(position).getThreadId());
					startActivityForResult(intent,101);
				}
				else{
					Intent intent=new Intent(mContext,MessageDetail.class);
					intent.putExtra("threadId",arrMessage.get(position).getThreadId());
					intent.putExtra("UserId",arrMessage.get(position).getUserId());
					intent.putExtra("imageUrl",arrMessage.get(position).getProfileImage());
					intent.putExtra("coverImg",arrMessage.get(position).getCoverImage());
					intent.putExtra("UserName",arrMessage.get(position).getDisplayName());
					startActivityForResult(intent,101);	
				}
			}
		});

	}

	void DeleteThread() {
		sErrorMessage = "";
		Utility.ShowProgressDialog(mContext, "Loading");

		if (Utility.isNetworkAvailable(mContext)) {

			Log.d("mobstar", "thread ID"+arrSelectionThreadID.get(0));
			new MessageDeleteCall(arrSelectionThreadID.get(0)).start();

		} else {

			Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	class MessageCall extends Thread {

		@Override
		public void run() {

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_MESSAGE,preferences.getString("token", null));
			Log.v(Constant.TAG, "MessagesCall response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					JSONArray jsonArrayThreads = jsonObject.getJSONArray("threads");

					arrMessage.clear();

					for (int j = 0; j < jsonArrayThreads.length(); j++) {

						JSONObject jsonObjStar = jsonArrayThreads.getJSONObject(j);

						JSONObject jsonObjthread=jsonObjStar.getJSONObject("thread");

						if(jsonObjStar.has("thread")){
							MessagePojo messageObj=new MessagePojo();

							messageObj.setThreadId(jsonObjthread.getString("threadId"));
							messageObj.setRead(Integer.valueOf(jsonObjthread.getString("read")));

							JSONObject jsonObjLastMsg=jsonObjthread.getJSONObject("lastMessage");
							messageObj.setMessageContent(jsonObjLastMsg.getString("messageContent"));
							messageObj.setMessageReceived(jsonObjLastMsg.getString("messageReceived"));
							messageObj.setMessageGroup(jsonObjLastMsg.getInt("messageGroup"));


							JSONObject jsonObjSender=jsonObjLastMsg.getJSONObject("messageSender");
							messageObj.setUserId(jsonObjSender.getString("id"));
							messageObj.setProfileImage(jsonObjSender.getString("profileImage"));
							messageObj.setCoverImage(jsonObjSender.getString("profileCover"));
							messageObj.setDisplayName(jsonObjSender.getString("displayName"));
							messageObj.setUserName(jsonObjSender.getString("userName"));

							if(jsonObjthread.has("participants")){
								JSONArray jsonParticipantsArray = jsonObjthread.getJSONArray("participants");
								ArrayList<ParticipantsPojo> arrPaticipants=new ArrayList<ParticipantsPojo>();
								for (int i = 0; i < jsonParticipantsArray.length(); i++) {
									JSONObject jsonObj = jsonParticipantsArray.getJSONObject(i);
									ParticipantsPojo obj=new ParticipantsPojo();
									obj.setUserId(jsonObj.getString("userId"));
									obj.setDisplayName(jsonObj.getString("displayName"));
									obj.setProfileCover(jsonObj.getString("profileImage"));
									obj.setProfileCover(jsonObj.getString("profileCover"));

									arrPaticipants.add(obj);
								}
								messageObj.setArrParticipants(arrPaticipants);
							}


							arrMessage.add(messageObj);
						}
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

			if (arrMessage.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				//				Collections.reverse(arrMessage);
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				msgAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};

	class MessageDeleteCall extends Thread {

		private String threadId;

		public MessageDeleteCall(String threadId) {
			this.threadId=threadId;
		}

		@Override
		public void run() {
			Log.d("mobstar","thread id =="+threadId);
			String[] name={"threadId"};
			String[] value={threadId};
			String response=JSONParser.postRequest(Constant.SERVER_URL + Constant.DELETE_THREAD, name, value, preferences.getString("token", null));
			Log.v(Constant.TAG, "MessagesDelete Call response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerUserDelete.sendEmptyMessage(0);
					} else {
						handlerUserDelete.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerUserDelete.sendEmptyMessage(0);
				}

			} else {

				handlerUserDelete.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerUserDelete = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			if (arrMessage.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				//				Collections.reverse(arrMessage);
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				int tempIndex = -1;

				for (int i = 0; i < arrMessage.size(); i++) {
					if (arrSelectionThreadID.get(0).equals(arrMessage.get(i).getThreadId())) {
						tempIndex = i;
						break;
					}
				}
				arrMessage.remove(tempIndex);
				msgAdapter.notifyDataSetChanged();
				arrSelectionThreadID.remove(0);

				if (arrSelectionThreadID.size() == 0) {
					Utility.HideDialog(mContext);
				} else {
					new MessageDeleteCall(arrSelectionThreadID.get(0)).start();
				}
			} else {
			}
		}
	};

	class MessageRead extends Thread {

		private String threadId;

		public MessageRead(String threadId) {
			this.threadId=threadId;
		}

		@Override
		public void run() {
			Log.d("mobstar","thread id =="+threadId);
			String[] name={"threadId"};
			String[] value={threadId};
			String response=JSONParser.postRequest(Constant.SERVER_URL + Constant.MESSAGE_READ, name, value, preferences.getString("token", null));
			Log.v(Constant.TAG, "MessagesRead Call response " + response);

			if (response != null) {

				try {

					sErrorMessage = "";

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerRead.sendEmptyMessage(0);
					} else {
						handlerRead.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerRead.sendEmptyMessage(0);
				}

			} else {

				handlerRead.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerRead = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);

			//			if (arrMessage.size() == 0) {
			//				textNoData.setVisibility(View.VISIBLE);
			//			} else {
			//				Collections.reverse(arrMessage);
			//				textNoData.setVisibility(View.GONE);
			//			}

			if (msg.what == 1) {
				//				int tempIndex = -1;
				//
				//				for (int i = 0; i < arrMessage.size(); i++) {
				//					if (arrSelectionThreadID.get(0).equals(arrMessage.get(i).getThreadId())) {
				//						tempIndex = i;
				//						break;
				//					}
				//				}
				//				arrMessage.remove(tempIndex);
				//				msgAdapter.notifyDataSetChanged();
				//				arrSelectionThreadID.remove(0);
				//
				//				if (arrSelectionThreadID.size() == 0) {
				//					Utility.HideDialog(mContext);
				//				} else {
				//					new MessageDeleteCall(arrSelectionThreadID.get(0)).start();
				//				}
			} else {
			}
		}
	};

	public class MessageAdapter extends BaseAdapter {

		private class ViewHolder {
			TextView textUserName,textMsg;
			ImageView imgUserPic,imgUserCircle;
			TextView textTime;
			LinearLayout llMessage;
		}

		@Override
		public int getCount() {
			return arrMessage.size();
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

				convertView = mInflater.inflate(R.layout.row_item_user_message, null);
				viewHolder = new ViewHolder();

				viewHolder.llMessage=(LinearLayout)convertView.findViewById(R.id.llMessage);
				viewHolder.textUserName = (TextView) convertView.findViewById(R.id.textUserName);
				viewHolder.textMsg = (TextView) convertView.findViewById(R.id.textMsg);
				viewHolder.imgUserPic = (ImageView) convertView.findViewById(R.id.imgUserPic);
				viewHolder.imgUserCircle = (ImageView) convertView.findViewById(R.id.imgUserCircle);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);

				convertView.setTag(viewHolder);
			} else
				viewHolder = (ViewHolder) convertView.getTag();

			//			viewHolder.textUserName.setText(arrMessage.get(position).getDisplayName());
			String participants=null;
			if(arrMessage.get(position).getArrParticipants()!=null && arrMessage.get(position).getArrParticipants().size()>0){
				for (int i = 0; i < arrMessage.get(position).getArrParticipants().size(); i++) {
					if(i>0){
						participants= participants+", "+arrMessage.get(position).getArrParticipants().get(i).getDisplayName().trim();
					}
					else{
						participants= arrMessage.get(position).getArrParticipants().get(i).getDisplayName().trim();
					}
					
				}
				viewHolder.textUserName.setText(participants);
			}
			else{
				viewHolder.textUserName.setText(arrMessage.get(position).getDisplayName());
			}

			viewHolder.textMsg.setText(Utility.unescape_perl_string(arrMessage.get(position).getMessageContent()));
			viewHolder.textTime.setText(arrMessage.get(position).getMessageReceived());

			if(arrMessage.get(position).getRead()==0){
				viewHolder.llMessage.setBackgroundColor(getResources().getColor(R.color.white_color));
				Picasso.with(mContext).load(R.drawable.circle).into(viewHolder.imgUserCircle);

			}
			else{
				viewHolder.llMessage.setBackgroundColor(getResources().getColor(R.color.gray_color));
				Picasso.with(mContext).load(R.drawable.circle_gray).into(viewHolder.imgUserCircle);
			}

			if(arrMessage.get(position).getMessageGroup() == 1){
				//group msg
				viewHolder.imgUserPic.setImageResource(R.drawable.icon_group);
			}
			else{
				if (arrMessage.get(position).getProfileImage().equals("")) {
					viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
				} else {
					viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);

					Picasso.with(mContext).load(arrMessage.get(position).getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);

				}
			}



			//			viewHolder.imgUserPic.setOnClickListener(new OnClickListener() {
			//				@Override
			//				public void onClick(View v) {
			//					Intent intent = new Intent(mContext, ProfileActivity.class);
			//					intent.putExtra("UserID",
			//							arrStarPojos.get(position).getStarID());
			//					intent.putExtra("UserName",
			//							arrStarPojos.get(position).getStarName());
			//					intent.putExtra("IsMyStar", arrStarPojos.get(position).getIsMyStar());
			//					intent.putExtra("UserPic",
			//							arrStarPojos.get(position).getProfileImage());
			//					intent.putExtra("UserCoverImage",arrStarPojos.get(position).getProfileCover());
			//					//					intent.putExtra("isProfile",true);
			//					startActivity(intent);
			//					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			//				}
			//			});

			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == 101) {
				if(data!=null && data.getExtras().containsKey("isRefresh")){
					boolean isRefresh=data.getBooleanExtra("isRefresh",false);
					if(isRefresh){
						Utility.ShowProgressDialog(mContext, "Loading");

						if (Utility.isNetworkAvailable(mContext)) {
							new MessageCall().start();
						} else {

							Toast.makeText(mContext, "No, Internet Access!", Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}
					}
				}
				
		}
	}

}
