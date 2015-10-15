package com.mobstar.home.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.inbox.GroupMessageDetail;
import com.mobstar.inbox.MessageDetail;
import com.mobstar.pojo.NotificationPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

	private Context mContext;
	private TextView textNotification;
	private TextView textNoData;
	private SharedPreferences preferences;
	private ListView listNotification;
	private NotificationListAdapter notificationListAdapter;
	public String sErrorMessage;
	private ArrayList<NotificationPojo> arrNotificationPojos = new ArrayList<NotificationPojo>();
	private ArrayList<String> arrSelectionNotificationID = new ArrayList<String>();
	private String NotificationId="";
	private String MESSAGE_TYPE="Message";	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		View view = inflater.inflate(R.layout.fragment_notifications, container, false);

		mContext = getActivity();
		preferences = getActivity().getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		Utility.SendDataToGA("Notification Screen", getActivity());
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		textNotification = (TextView) view.findViewById(R.id.textNotification);
		textNotification.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				getActivity().onBackPressed();
			}
		});

		textNoData = (TextView) view.findViewById(R.id.textNoData);
		textNoData.setVisibility(View.GONE);

		listNotification = (ListView) view.findViewById(R.id.listNotification);
		listNotification.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listNotification.setMultiChoiceModeListener(new MultiChoiceModeListener() {

			@Override
			public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {
				case R.id.menu_delete:
					DeleteNotification();
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
				final int checkedCount = listNotification.getCheckedItemCount();
				// Set the CAB title according to total checked items
				arg0.setTitle(checkedCount + " " + getString(R.string.selected));

				if (arrSelectionNotificationID.contains(arrNotificationPojos.get(position).getNotificationID() + "")) {
					arrSelectionNotificationID.remove(arrNotificationPojos.get(position).getNotificationID() + "");
				} else {
					arrSelectionNotificationID.add(arrNotificationPojos.get(position).getNotificationID() + "");
				}

			}
		});

		notificationListAdapter = new NotificationListAdapter();
		listNotification.setAdapter(notificationListAdapter);

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {
			new NotificationCall().start();
		} else {
			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}
		
		listNotification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
//				new NotificationMarkRead(arrNotificationPojos.get(position).getNotificationID()).start();
				
				new MessageRead(arrNotificationPojos.get(position).getEntryId()).start();
				
				if(arrNotificationPojos.get(position).getNotificationType().equalsIgnoreCase(MESSAGE_TYPE)){
					if(arrNotificationPojos.get(position).getMessageGroup().equalsIgnoreCase("1")){
						final Intent intent = new Intent(mContext, GroupMessageDetail.class);
						intent.putExtra("threadId",arrNotificationPojos.get(position).getEntryId());
						startActivityForResult(intent, 101);
					}
					else{
						final Intent intent = new Intent(mContext, MessageDetail.class);
						intent.putExtra(MessageDetail.THREAD_ID_KEY,arrNotificationPojos.get(position).getEntryId());
						intent.putExtra("UserName",arrNotificationPojos.get(position).getEntryName());
						startActivityForResult(intent, 101);
					}
				}
				else{
					final Intent intent = new Intent(mContext, NotificationEntryActivity.class);
					final UserProfile userProfile = UserProfile.newBuilder()
							.setEntryId(arrNotificationPojos.get(position).getEntryId())
							.build();
					intent.putExtra(NotificationEntryActivity.USER, userProfile);
					intent.putExtra(NotificationEntryActivity.IS_NOTIFICATION, true);
//					intent.putExtra("EntryId",arrNotificationPojos.get(position).getEntryId());
					startActivityForResult(intent, 101);
				}
				
				
			}
		});

	}

	void DeleteNotification() {

		Utility.ShowProgressDialog(mContext, getString(R.string.loading));

		if (Utility.isNetworkAvailable(mContext)) {

			new DeleteNotificationCall(arrSelectionNotificationID.get(0)).start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
			Utility.HideDialog(mContext);
		}

	}

	class DeleteNotificationCall extends Thread {

		String NotificationID;

		public DeleteNotificationCall(String NotificationID) {
			this.NotificationID = NotificationID;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = { "notification" };
			String[] value = { NotificationID };

			String response = JSONParser.deleteRequest(Constant.SERVER_URL + Constant.DELETE_NOTIFICATION + NotificationID, name, value, preferences.getString("token", null));

//			Log.v(Constant.TAG, "DeleteNotificationCall response " + response + " NotificationID " + NotificationID);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerDeleteNotification.sendEmptyMessage(0);
					} else {
						handlerDeleteNotification.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerDeleteNotification.sendEmptyMessage(0);
				}

			} else {

				handlerDeleteNotification.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerDeleteNotification = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 1) {

				int tempIndex = -1;

				for (int i = 0; i < arrNotificationPojos.size(); i++) {
					if (arrSelectionNotificationID.get(0).equals(arrNotificationPojos.get(i).getNotificationID())) {
						tempIndex = i;
						break;
					}
				}
				arrNotificationPojos.remove(tempIndex);
				notificationListAdapter.notifyDataSetChanged();
				arrSelectionNotificationID.remove(0);

				Intent intent = new Intent("notification_count_changed");
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
				
				if (arrSelectionNotificationID.size() == 0) {
					Utility.HideDialog(mContext);
				} else {
					new DeleteNotificationCall(arrSelectionNotificationID.get(0)).start();
				}

			} else {
				Utility.HideDialog(mContext);
			}
		}
	};

	class NotificationCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_NOTIFICATION, preferences.getString("token", null));

			 Log.v(Constant.TAG, "NotificationCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("notifications")) {
						arrNotificationPojos.clear();

						JSONArray jsonArrayNotification = jsonObject.getJSONArray("notifications");
						NotificationId="";
						for (int i = 0; i < jsonArrayNotification.length(); i++) {

							JSONObject jsonObjNotification = jsonArrayNotification.getJSONObject(i);

							NotificationPojo tempNotificationPojo = new NotificationPojo();
							
							if(jsonObjNotification.getString("notificationId")!=null){
								tempNotificationPojo.setNotificationID(jsonObjNotification.getString("notificationId"));
								NotificationId=NotificationId+jsonObjNotification.getString("notificationId")+",";	
								Log.d("mobstar","Notification=>"+NotificationId);
							}
							tempNotificationPojo.setNotificationContent(Utility.unescape_perl_string(
                                    jsonObjNotification.getString("notificationContent")));
							tempNotificationPojo.setNotificationDate(jsonObjNotification.getString("notificationDate"));
							tempNotificationPojo.setNotificationType(jsonObjNotification.getString("notificationType"));
							tempNotificationPojo.setNotificationIcon(jsonObjNotification.getString("notificationIcon"));
							tempNotificationPojo.setNotificationRead(jsonObjNotification.getString("notificationRead"));
							
							if(jsonObjNotification.has("entry")){
								JSONObject jsonEntryObj=jsonObjNotification.getJSONObject("entry");
								tempNotificationPojo.setEntryId(jsonEntryObj.getString("entry_id"));
								tempNotificationPojo.setEntryName(jsonEntryObj.getString("entry_name"));
							}
							
							if(jsonObjNotification.has("messageGroup")){
								tempNotificationPojo.setMessageGroup(jsonObjNotification.getString("messageGroup"));
							}

							arrNotificationPojos.add(tempNotificationPojo);
						}
						if(NotificationId!=null && NotificationId.length()>0){
							int pos=NotificationId.lastIndexOf(",");
							NotificationId=NotificationId.substring(0,pos);
//							Log.d("mobstar","updated string is=>"+NotificationId);
						}
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerNotification.sendEmptyMessage(0);
					} else {
						handlerNotification.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerNotification.sendEmptyMessage(0);
				}

			} else {

				handlerNotification.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerNotification = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			if (arrNotificationPojos.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
				notificationListAdapter.notifyDataSetChanged();
				Utility.ShowProgressDialog(mContext, getString(R.string.loading));
				if (Utility.isNetworkAvailable(mContext)) {
					new NotificationReadCall().start();
				} else {
					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			} else {

			}
		}
	};
	
	class NotificationReadCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String[] name = {"notificationIds"};
			String[] value = {NotificationId};
			
			
//			Log.d("mobstar","notificationIds"+NotificationId);
			String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.READ_NOTIFICATION, name, value,preferences.getString("token", null));
			
//			 Log.v(Constant.TAG, "NotificationCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

//					if (jsonObject.has("notifications")) {
//
//						JSONArray jsonArrayNotification = jsonObject.getJSONArray("notifications");
//
//						for (int i = 0; i < jsonArrayNotification.length(); i++) {
//
//							JSONObject jsonObjNotification = jsonArrayNotification.getJSONObject(i);
//
//							NotificationPojo tempNotificationPojo = new NotificationPojo();
//
//							tempNotificationPojo.setNotificationID(jsonObjNotification.getString("notificationId"));
//							tempNotificationPojo.setNotificationContent(jsonObjNotification.getString("notificationContent"));
//							tempNotificationPojo.setNotificationDate(jsonObjNotification.getString("notificationDate"));
//							tempNotificationPojo.setNotificationContent(jsonObjNotification.getString("notificationContent"));
//							tempNotificationPojo.setNotificationType(jsonObjNotification.getString("notificationType"));
//
//							arrNotificationPojos.add(tempNotificationPojo);
//						}
//					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerNotificationRead.sendEmptyMessage(0);
					} else {
						handlerNotificationRead.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
					handlerNotificationRead.sendEmptyMessage(0);
				}

			} else {

				handlerNotificationRead.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerNotificationRead = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);
			if (arrNotificationPojos.size() == 0) {
				textNoData.setVisibility(View.VISIBLE);
			} else {
				textNoData.setVisibility(View.GONE);
			}

			if (msg.what == 1) {
//				notificationListAdapter.notifyDataSetChanged();
			} else {

			}
		}
	};

	public class NotificationListAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public NotificationListAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return arrNotificationPojos.size();
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder viewHolder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_notification, null);

				viewHolder = new ViewHolder();
				viewHolder.textNotification = (TextView) convertView.findViewById(R.id.textNotification);
				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
				viewHolder.imgNotificationIcon = (ImageView) convertView.findViewById(R.id.imgNotificationIcon);
				viewHolder.llNotification=(LinearLayout)convertView.findViewById(R.id.llNotification);
				convertView.setTag(viewHolder);

			} else {
				viewHolder = (ViewHolder) convertView.getTag();

			}

//			if (arrNotificationPojos.get(position).getNotificationType().equals("Entry Comment")) {
//				viewHolder.imgNotificationIcon.setImageResource(R.drawable.notify_comment);
//			} else if (arrNotificationPojos.get(position).getNotificationType().equals("Entry Vote")) {
//				viewHolder.imgNotificationIcon.setImageResource(R.drawable.notify_like);
//			}
			
			if(arrNotificationPojos.get(position).getNotificationIcon()!=null && arrNotificationPojos.get(position).getNotificationIcon().length()>0){
//				viewHolder.imgNotificationIcon.setBackgroundResource(R.drawable.ic_pic_small);
				Picasso.with(mContext).load(arrNotificationPojos.get(position).getNotificationIcon()).placeholder(R.drawable.ic_pic_small).into(viewHolder.imgNotificationIcon);
				
			}
			else {
				viewHolder.imgNotificationIcon.setBackgroundResource(R.drawable.ic_pic_small);
			}
			
			if(arrNotificationPojos.get(position).getNotificationRead()!=null && arrNotificationPojos.get(position).getNotificationRead().equalsIgnoreCase("0")){
				viewHolder.llNotification.setBackgroundColor(getResources().getColor(R.color.white_color));
			}
			else{
				viewHolder.llNotification.setBackgroundColor(getResources().getColor(R.color.gray_color));
//				viewHolder.llNotification.setBackground(getResources().getDrawable(R.drawable.comment_list_activated_bg));
			}
			

			viewHolder.textNotification.setText(arrNotificationPojos.get(position).getNotificationContent());
			viewHolder.textTime.setText(arrNotificationPojos.get(position).getNotificationDate());

			return convertView;
		}

		class ViewHolder {

			TextView textNotification, textTime;
			ImageView imgNotificationIcon;
			LinearLayout llNotification;
		}
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Intent intent = new Intent("notification_count_changed");
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
	}
	class NotificationMarkRead extends Thread {

		private String notificationId;

		public NotificationMarkRead(String threadId) {
			this.notificationId=threadId;
		}

		@Override
		public void run() {
			Log.d("mobstar","thread id =="+notificationId);
			String[] name={"notificationIds"};
			String[] value={notificationId};
			String response=JSONParser.postRequest(Constant.SERVER_URL + Constant.NOTIFICATION_MARK_READ, name, value, preferences.getString("token", null));
			Log.v(Constant.TAG, "NotificationMarkRead Call response " + response);

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
						handlerMessageRead.sendEmptyMessage(0);
					} else {
						handlerMessageRead.sendEmptyMessage(1);
					}

				} catch (Exception e) {
					e.printStackTrace();
					handlerMessageRead.sendEmptyMessage(0);
				}

			} else {

				handlerMessageRead.sendEmptyMessage(0);
			}

		}
	}

	Handler handlerMessageRead = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Utility.HideDialog(mContext);


			if (msg.what == 1) {
			} else {
			}
		}
	};
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			Utility.ShowProgressDialog(mContext, getString(R.string.loading));

			if (Utility.isNetworkAvailable(mContext)) {
				new NotificationCall().start();
			} else {
				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
				Utility.HideDialog(mContext);
			}
		}
	};

}
