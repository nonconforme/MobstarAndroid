package com.mobstar.home.notification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
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
import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.call.NotificationCall;
import com.mobstar.api.responce.*;
import com.mobstar.api.responce.Error;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.inbox.newMessagesScreen.MessageDetail;
import com.mobstar.pojo.NotificationPojo;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;
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
	private String notificationId ="";
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
					if (arrSelectionNotificationID.size() != 0)
						deleteNotificatinRequest(arrSelectionNotificationID.get(0));
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

		getNotificationRequest();
		
		listNotification.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Utility.clearBadge(mContext.getApplicationContext());

				if (arrNotificationPojos.get(position).getNotificationType().equalsIgnoreCase(MESSAGE_TYPE)) {
					messageReadRequest(arrNotificationPojos.get(position).getEntryId());
					if (arrNotificationPojos.get(position).getMessageGroup().equalsIgnoreCase("1")) {
						final Intent intent = new Intent(mContext, MessageDetail.class);
						intent.putExtra("threadId", arrNotificationPojos.get(position).getEntryId());
						intent.putExtra(MessageDetail.IS_GROUP, true);
						startActivityForResult(intent, 101);
					} else {
						final Intent intent = new Intent(mContext, MessageDetail.class);
						intent.putExtra(MessageDetail.THREAD_ID_KEY, arrNotificationPojos.get(position).getEntryId());
						intent.putExtra("UserName", arrNotificationPojos.get(position).getEntryName());
						startActivityForResult(intent, 101);
					}
				} else if (!arrNotificationPojos.get(position).getNotificationType().equalsIgnoreCase("Follow")) {
					final Intent intent = new Intent(mContext, SingleEntryActivity.class);
					final UserProfile userProfile = UserProfile.newBuilder()
							.setEntryId(arrNotificationPojos.get(position).getEntryId())
							.build();
					intent.putExtra(SingleEntryActivity.USER, userProfile);
					intent.putExtra(SingleEntryActivity.IS_NOTIFICATION, true);
					startActivityForResult(intent, 101);
				} else {
					startProfileActivity(position);
				}


			}
		});

	}

	private void startProfileActivity(int position){
		final Intent intent = new Intent(mContext, NewProfileActivity.class);
		final NotificationPojo notificationPojo = arrNotificationPojos.get(position);
		final UserProfile userProfile = UserProfile.newBuilder()
				.setUserId(notificationPojo.getEntryId())
				.setUserName(notificationPojo.getEntryName())
				.setIsMyStar(true)
				.setUserPic(notificationPojo.getProfileImage())
				.setUserCoverImage(notificationPojo.getProfileCover())
				.build();
		intent.putExtra(NewProfileActivity.USER, userProfile);
		startActivity(intent);
	}

	private void deleteNotificatinRequest(final String notificationId){
		Utility.ShowProgressDialog(mContext, getString(R.string.loading));
		NotificationCall.deleteNotificationRequest(getActivity(), notificationId, new ConnectCallback<NullResponse>() {
			@Override
			public void onSuccess(NullResponse object) {
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
					deleteNotificatinRequest(arrSelectionNotificationID.get(0));
				}
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(NotificationsFragment.this.getActivity());
			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {

			}
		});
	}

	private void getNotificationRequest(){
		Utility.ShowProgressDialog(mContext, getString(R.string.loading));
		com.mobstar.api.call.NotificationCall.notificationRequest(getActivity(), new ConnectCallback<NotificationResponse>() {
			@Override
			public void onSuccess(NotificationResponse object) {
				Utility.HideDialog(getActivity());
				arrNotificationPojos = object.getArrNotificationPojos();
				notificationId = object.getNotificationId();
						prepareNotifivationList();
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(getActivity());
			}

			@Override
			public void onServerError(Error error) {

			}
		});
	}

	private void messageReadRequest(final String threadId){
		NotificationCall.messageReadRequest(getActivity(), threadId, new ConnectCallback<NullResponse>() {
			@Override
			public void onSuccess(NullResponse object) {

			}

			@Override
			public void onFailure(String error) {

			}

			@Override
			public void onServerError(Error error) {

			}
		});
	}

	private void prepareNotifivationList(){
		if (arrNotificationPojos.size() == 0) {
			textNoData.setVisibility(View.VISIBLE);
		} else {
			textNoData.setVisibility(View.GONE);
		}

		notificationListAdapter.notifyDataSetChanged();
		notificationReadRequest(notificationId);

	}

	private void notificationReadRequest(final String notificationIds){
		Utility.ShowProgressDialog(NotificationsFragment.this.getActivity(), getString(R.string.loading));
		NotificationCall.notificationReadRequest(getActivity(), notificationIds, new ConnectCallback<NullResponse>() {
			@Override
			public void onSuccess(NullResponse object) {
				Utility.HideDialog(NotificationsFragment.this.getActivity());
			}

			@Override
			public void onFailure(String error) {
				Utility.HideDialog(NotificationsFragment.this.getActivity());
			}

			@Override
			public void onServerError(Error error) {

			}
		});
	}

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


	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 101) {
			getNotificationRequest();
		}
	};

}
