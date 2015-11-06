//package com.mobstar.inbox;
//
//import java.util.ArrayList;
//
//import org.apache.commons.lang3.StringEscapeUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Typeface;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.content.LocalBroadcastManager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.BaseAdapter;
//import android.widget.EditText;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.mobstar.R;
//import com.mobstar.pojo.MessageThreadPojo;
//import com.mobstar.utils.Constant;
//import com.mobstar.utils.JSONParser;
//import com.mobstar.utils.Utility;
//
//public class GroupMessageDetail extends Activity implements OnClickListener{
//
//	Context mContext;
//	private SharedPreferences preferences;
//	private TextView textFans,textNoData;
//	private EditText editMessage;
//	private ImageView btnSend;
//	private ListView listUser;
//	private FrameLayout flImgHeader;
//	private ImageView imgUserPic;
//	private String threadId;
//	private String sErrorMessage;
//	private ArrayList<MessageThreadPojo> arrMessages=new ArrayList<MessageThreadPojo>();
//	private String UserID;
//	private MessagesAdapter messageAdapter;
//	private Typeface typeface;
//	private ArrayList<String> arrSelectionMsgID = new ArrayList<String>();
//	private boolean isRefresh=false,FromNotification=false;
//	private LinearLayout llAdView;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_fans);
//
//		Bundle bundle=getIntent().getExtras();
//		if(bundle!=null){
//			threadId=bundle.getString("threadId");
////			profileimageUrl=bundle.getString("imageUrl");
////			UserName=bundle.getString("UserName");
//			if(bundle.containsKey("FromNotification")){
//				FromNotification=bundle.getBoolean("FromNotification");
//			}
//		}
//		mContext=GroupMessageDetail.this;
//		preferences = mContext.getSharedPreferences("mobstar_pref", Context.MODE_PRIVATE);
//		UserID = preferences.getString("userid", "");
//
//		Utility.SendDataToGA("Message Detail Screen",this);
//		initControlls();
//
//		if(FromNotification){
//			new MessageRead(threadId).start();
//		}
//		imgUserPic.setImageResource(R.drawable.icon_group);
//
//		messageAdapter=new MessagesAdapter();
//		listUser.setAdapter(messageAdapter);
//		if(threadId!=null && threadId.length()>0){
//			Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//			if (Utility.isNetworkAvailable(mContext)) {
//				new GetMessageThreadCall().start();
//
//			} else {
//				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//				Utility.HideDialog(mContext);
//			}
//
//		}
//		LocalBroadcastManager.getInstance(GroupMessageDetail.this).sendBroadcast(
//	            new Intent("msg_read")
//	            .putExtra("threadId",threadId)
//				);
//
//		flImgHeader.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent=new Intent(mContext,GroupMember.class);
//				intent.putExtra("threadId",threadId);
//				startActivity(intent);
//			}
//		});
//	}
//
//	private void initControlls() {
//		textNoData=(TextView)findViewById(R.id.textNoData);
//		textNoData.setVisibility(View.INVISIBLE);
//		textFans=(TextView)findViewById(R.id.textFans);
//		textFans.setText(getString(R.string.group));
//		textFans.setOnClickListener(this);
//
//		editMessage=(EditText)findViewById(R.id.editMessage);
//		typeface = Typeface.createFromAsset(mContext.getAssets(), "GOTHAM-LIGHT.TTF");
//		editMessage = (EditText) findViewById(R.id.editMessage);
//		editMessage.setTypeface(typeface);
//		editMessage.setHint(getString(R.string.replay_to_group));
//
//		btnSend=(ImageView)findViewById(R.id.btnSend);
//		btnSend.setOnClickListener(this);
//		listUser=(ListView)findViewById(R.id.listUser);
//
//		flImgHeader=(FrameLayout)findViewById(R.id.flImgHeader);
//		flImgHeader.setVisibility(View.VISIBLE);
//
//		imgUserPic=(ImageView)findViewById(R.id.imgUserPic);
//
//		llAdView=(LinearLayout)findViewById(R.id.llAdView);
//		llAdView.setVisibility(View.GONE);
//
//
//	}
//
//	@Override
//	public void onClick(View v) {
//		if(textFans.equals(v)){
//			onBackPressed();
//		}
//		else if(btnSend.equals(v)){
//			if (!editMessage.getText().toString().trim().equals("")) {
//
//				sErrorMessage = "";
//
//				Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//
//				if (Utility.isNetworkAvailable(mContext)) {
//					isRefresh=true;
//					new SendMessageCall(threadId,StringEscapeUtils.escapeJava(editMessage.getText().toString().trim())).start();
//
//				} else {
//					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//					Utility.HideDialog(mContext);
//				}
//				editMessage.setText("");
//			}
//		}
//	}
//
//
//	class GetMessageThreadCall extends Thread {
//
//		@Override
//		public void run() {
//			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_MESSAGE_THREAD+threadId, preferences.getString("token", null));
//			Log.v(Constant.TAG, "Message thread response " + response);
//
//			if (response != null) {
//
//				try {
//
//					sErrorMessage = "";
//
//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("error")) {
//						sErrorMessage = jsonObject.getString("error");
//					}
//
//					JSONObject jsonObjThread = jsonObject.getJSONObject("thread");
//
//					JSONArray jsonArrayMessages = jsonObjThread.getJSONArray("messages");
//
//					for (int j = 0; j < jsonArrayMessages.length(); j++) {
//
//						JSONObject jsonObjMessage = jsonArrayMessages.getJSONObject(j);
//
//						MessageThreadPojo tempPojo=new MessageThreadPojo();
//						tempPojo.setThreadId(jsonObjThread.getString("threadId"));
//
//						tempPojo.setMessageId(jsonObjMessage.getString("message_id"));
//						tempPojo.setMessage(jsonObjMessage.getString("message"));
//						tempPojo.setMessageReceived(jsonObjMessage.getString("messageReceived"));
//						tempPojo.setMessageRead(jsonObjMessage.getString("messageRead"));
//
//						JSONObject jsonObjMessageSender = jsonObjMessage.getJSONObject("messageSender");
//						tempPojo.setSenderId(jsonObjMessageSender.getString("id"));
//						tempPojo.setSenderprofileImage(jsonObjMessageSender.getString("profileImage"));
//						tempPojo.setSenderdisplayName(jsonObjMessageSender.getString("displayName"));
//						tempPojo.setSenderUserName(jsonObjMessageSender.getString("userName"));
//						arrMessages.add(tempPojo);
//					}
//
//
//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
//						handlerMessage.sendEmptyMessage(0);
//					} else {
//						handlerMessage.sendEmptyMessage(1);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					handlerMessage.sendEmptyMessage(0);
//				}
//
//			} else {
//
//				handlerMessage.sendEmptyMessage(0);
//			}
//		}
//	}
//
//	Handler handlerMessage = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			Utility.HideDialog(mContext);
//
//			if (msg.what == 1) {
//				messageAdapter.notifyDataSetChanged();
//				listUser.setSelection(arrMessages.size()-1);
//
////				String name="";
////				if(arrMessages.get(arrMessages.size()-1).getSenderdisplayName().equals("") || arrMessages.get(arrMessages.size()-1).getSenderdisplayName().equals(null)){
////					name=arrMessages.get(arrMessages.size()-1).getSenderUserName();
////				}
////				else {
////					name=arrMessages.get(arrMessages.size()-1).getSenderdisplayName();
////				}
////
////				LocalBroadcastManager.getInstance(MessageDetail.this).sendBroadcast(
////			            new Intent("msg_added")
////			            .putExtra("msg",arrMessages.get(arrMessages.size()-1).getMessage())
////			            .putExtra("threadId",arrMessages.get(arrMessages.size()-1).getThreadId())
////			            .putExtra("profileImage",arrMessages.get(arrMessages.size()-1).getSenderprofileImage())
////			            .putExtra("name",name)
////						);
//			} else {
//
//			}
//		}
//	};
//
//	class SendMessageCall extends Thread {
//
//		private String threadId,message;
//
//		public SendMessageCall(String threadId,String message){
//			this.threadId=threadId;
//			this.message=message;
//		}
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			String[] name = {"thread","message"};
//			//			String[] value = {"307",phoneNumber,country };
//			String[] value = {threadId,message};
//			String response = JSONParser.LikepostRequest(Constant.SERVER_URL + Constant.REPLAY_MESSAGE_THREAD, name, value,preferences.getString("token", null));
//
//
//			Log.v(Constant.TAG, "ReplayMessage response " + response);
//
//			if (response != null) {
//
//				try {
//
//					sErrorMessage = "";
//
//					if(response.equalsIgnoreCase("error")){
//						sErrorMessage=getString(R.string.no_entries_found);
//					}
//
//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
//						handlerReplayMessage.sendEmptyMessage(0);
//					} else {
//						handlerReplayMessage.sendEmptyMessage(1);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//					handlerReplayMessage.sendEmptyMessage(0);
//				}
//
//			} else {
//
//				handlerReplayMessage.sendEmptyMessage(0);
//			}
//
//		}
//	}
//
//	Handler handlerReplayMessage = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			Utility.HideDialog(mContext);
//			editMessage.setText("");
//			if (msg.what == 1) {
//
//				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//				imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);
//				arrMessages.clear();
//
//
//
//				Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//				if (Utility.isNetworkAvailable(mContext)) {
//					new GetMessageThreadCall().start();
//
//				} else {
//
//					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//					Utility.HideDialog(mContext);
//				}
//			} else {
//
//			}
//		}
//	};
//
//
//	public class MessagesAdapter extends BaseAdapter {
//
//		private class ViewHolder {
//			LinearLayout llUserThread,llMyThread;
//			TextView textMsg,textMyMsg,textUsername,textMyname;
//			TextView textTime,textMyTime,textStartTime;
//		}
//
//		@Override
//		public int getCount() {
//			return arrMessages.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			return null;
//		}
//
//		@Override
//		public long getItemId(int arg0) {
//			return 0;
//		}
//
//		public View getView(final int position, View convertView, ViewGroup parent) {
//			final ViewHolder viewHolder;
//
//			LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//			if (convertView == null) {
//
//				convertView = mInflater.inflate(R.layout.row_group_message, null);
//				viewHolder = new ViewHolder();
//				viewHolder.textStartTime= (TextView) convertView.findViewById(R.id.textStartTime);
//				viewHolder.llUserThread = (LinearLayout) convertView.findViewById(R.id.llUserThread);
//				viewHolder.llMyThread = (LinearLayout) convertView.findViewById(R.id.llMyThread);
//				viewHolder.textMsg = (TextView) convertView.findViewById(R.id.textMsg);
//				viewHolder.textMyMsg = (TextView) convertView.findViewById(R.id.textMyMsg);
//				viewHolder.textTime = (TextView) convertView.findViewById(R.id.textTime);
//				viewHolder.textMyTime = (TextView) convertView.findViewById(R.id.textMyTime);
//				viewHolder.textMyname =(TextView) convertView.findViewById(R.id.textMyname);
//				viewHolder.textUsername =(TextView) convertView.findViewById(R.id.textUsername);
//				convertView.setTag(viewHolder);
//			} else
//				viewHolder = (ViewHolder) convertView.getTag();
//
//
//			if(arrMessages.get(position).getMessageReceived()!=null){
//
//				if(position>0){
//					if(!arrMessages.get(position-1).getMessageReceived().equalsIgnoreCase(arrMessages.get(position).getMessageReceived())){
//						viewHolder.textStartTime.setText(arrMessages.get(position).getMessageReceived());
//						viewHolder.textStartTime.setVisibility(View.VISIBLE);
//					}
//					else{
//						viewHolder.textStartTime.setVisibility(View.GONE);
//					}
//				}
//				else{
//					viewHolder.textStartTime.setText(arrMessages.get(position).getMessageReceived());
//					viewHolder.textStartTime.setVisibility(View.VISIBLE);
//				}
//			}
//
//			if(UserID.equalsIgnoreCase(arrMessages.get(position).getSenderId())){
//				//owm message thread
//				viewHolder.llUserThread.setVisibility(View.GONE);
//				viewHolder.llMyThread.setVisibility(View.VISIBLE);
//
//				viewHolder.textMyMsg.setText(Utility.unescape_perl_string(arrMessages.get(position).getMessage()));
//				viewHolder.textMyTime.setText(arrMessages.get(position).getCreated());
//
//				if(arrMessages.get(position).getSenderdisplayName()!=null && arrMessages.get(position).getSenderdisplayName().length()>0){
//					viewHolder.textMyname.setText(arrMessages.get(position).getSenderdisplayName());
//				}
//				else{
//					viewHolder.textMyname.setText(arrMessages.get(position).getSenderUserName());
//				}
//
//
////				if (arrMessages.get(position).getSenderprofileImage().equals("")) {
////					viewHolder.imgMyPic.setImageResource(R.drawable.ic_pic_small);
////				} else {
////					viewHolder.imgMyPic.setImageResource(R.drawable.ic_pic_small);
////
////					Picasso.with(mContext).load(arrMessages.get(position).getSenderprofileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
////					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgMyPic);
////
////				}
//			}
//			else{
//				//other userside thread
//				viewHolder.llMyThread.setVisibility(View.GONE);
//				viewHolder.llUserThread.setVisibility(View.VISIBLE);
//
//				viewHolder.textMsg.setText(Utility.unescape_perl_string(arrMessages.get(position).getMessage()));
//				viewHolder.textTime.setText(arrMessages.get(position).getCreated());
//
//				if(arrMessages.get(position).getSenderdisplayName()!=null && arrMessages.get(position).getSenderdisplayName().length()>0){
//					viewHolder.textUsername.setText(arrMessages.get(position).getSenderdisplayName());
//				}
//				else{
//					viewHolder.textUsername.setText(arrMessages.get(position).getSenderUserName());
//				}
//
////				if (arrMessages.get(position).getSenderprofileImage().equals("")) {
////					viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
////				} else {
////					viewHolder.imgUserPic.setImageResource(R.drawable.ic_pic_small);
////
////					Picasso.with(mContext).load(arrMessages.get(position).getSenderprofileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
////					.placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(viewHolder.imgUserPic);
////
////				}
//			}
//
//			return convertView;
//		}
//	}
//
//	public void onBackPressed() {
//		Intent intent=new Intent();
//		intent.putExtra("isRefresh", isRefresh);
//		setResult(101,intent);
//		finish();
//	};
//
//	class MessageRead extends Thread {
//
//		private String threadId;
//
//		public MessageRead(String threadId) {
//			this.threadId=threadId;
//		}
//
//		@Override
//		public void run() {
//			Log.d("mobstar","thread id =="+threadId);
//			String[] name={"threadId"};
//			String[] value={threadId};
//			String response=JSONParser.postRequest(Constant.SERVER_URL + Constant.MESSAGE_READ, name, value, preferences.getString("token", null));
//			Log.v(Constant.TAG, "MessagesRead Call response " + response);
//
//			if (response != null) {
//
////				try {
////
////					sErrorMessage = "";
////
////					JSONObject jsonObject = new JSONObject(response);
////
////					if (jsonObject.has("error")) {
////						sErrorMessage = jsonObject.getString("error");
////					}
////
////					if (sErrorMessage != null && !sErrorMessage.equals("")) {
////						handlerMessageRead.sendEmptyMessage(0);
////					} else {
////						handlerMessageRead.sendEmptyMessage(1);
////					}
////
////				} catch (Exception e) {
////					e.printStackTrace();
////					handlerMessageRead.sendEmptyMessage(0);
////				}
//
//			} else {
//
//				handlerMessageRead.sendEmptyMessage(0);
//			}
//
//		}
//	}
//
//	Handler handlerMessageRead = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
////			Utility.HideDialog(mContext);
//
//
//			if (msg.what == 1) {
//			} else {
//			}
//		}
//	};
//
//}
