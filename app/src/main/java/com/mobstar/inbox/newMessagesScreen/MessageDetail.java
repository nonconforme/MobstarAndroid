package com.mobstar.inbox.newMessagesScreen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.api.Api;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.responce.BaseResponse;
import com.mobstar.api.responce.MessageListResponse;
import com.mobstar.custom.PullToRefreshListView;
import com.mobstar.gcm.GcmIntentService;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.inbox.GroupMember;
import com.mobstar.pojo.MessageThreadPojo;
import com.mobstar.pojo.ParticipantsPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MessageDetail extends Activity implements OnClickListener {


    public static final String THREAD_ID_KEY = "threadId";
    public static final String IS_GROUP = "is group chat";
    private static final String LOG_TAG = MessageDetail.class.getName();
    private static boolean isActive = false;
    Context mContext;
    private SharedPreferences preferences;
    private TextView textFans, textNoData;
    private EditText editMessage;
    private ImageView btnSend;
    private PullToRefreshListView listUser;
    private FrameLayout flImgHeader;
    private ImageView imgUserPic;
    private String threadId, profileimageUrl = "", CoverImg, senderUserId, UserName;
    private String sErrorMessage;
    private ArrayList<MessageThreadPojo> arrMessages = new ArrayList<MessageThreadPojo>();
    private String UserID;
    private MessagesAdapter messageAdapter;
    private Typeface typeface;
    private ArrayList<ParticipantsPojo> arrParticipants;
    private boolean isRefresh = false, FromNotification = false;
    private LinearLayout llAdView;
    private BroadcastReceiver mNewMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            messagesReaded();
            updateMessageList(false);
//            Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//            if (Utility.isNetworkAvailable(mContext)) {
//                new GetMessageThreadCall().start();
//
//            } else {
//                Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//                Utility.HideDialog(mContext);
//            }
        }
    };
    private boolean isGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fans);
        initValues();

        initControlls();

        if (FromNotification) {
            messagesReaded();
//			new MessageRead(threadId).start();
        }


        messageAdapter = new MessagesAdapter(mContext, arrMessages, UserID, isGroup);
        listUser.setAdapter(messageAdapter);
        if (threadId != null && threadId.length() > 0) {
            updateMessageList(true);
//			Utility.ShowProgressDialog(mContext, getString(R.string.loading));
//			if (Utility.isNetworkAvailable(mContext)) {
//				new GetMessageThreadCall().start();
//
//			} else {
//				Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//				Utility.HideDialog(mContext);
//			}

        }
        LocalBroadcastManager.getInstance(MessageDetail.this).sendBroadcast(
                new Intent("msg_read")
                        .putExtra("threadId", threadId)
        );
    }

    private void initValues() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            threadId = bundle.getString(THREAD_ID_KEY);
            isGroup = bundle.getBoolean(IS_GROUP, false);
            if (!isGroup) {
                profileimageUrl = bundle.getString("imageUrl");
                UserName = bundle.getString("UserName");
                senderUserId = bundle.getString("UserId");
                CoverImg = bundle.getString("coverImg");
            }
            if (bundle.containsKey("FromNotification")) {
                FromNotification = bundle.getBoolean("FromNotification");
            }
        }


        mContext = MessageDetail.this;
        preferences = mContext.getSharedPreferences("mobstar_pref", Context.MODE_PRIVATE);
        UserID = preferences.getString("userid", "");

        Utility.SendDataToGA("Message Detail Screen", this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mNewMessageReceiver, new IntentFilter(GcmIntentService.NEW_MESSAGE_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mNewMessageReceiver);
    }

    private void initControlls() {
        textNoData = (TextView) findViewById(R.id.textNoData);
        textNoData.setVisibility(View.INVISIBLE);
        textFans = (TextView) findViewById(R.id.textFans);

        textFans.setOnClickListener(this);

        typeface = Typeface.createFromAsset(mContext.getAssets(), "GOTHAM-LIGHT.TTF");
        editMessage = (EditText) findViewById(R.id.editMessage);
        editMessage.setTypeface(typeface);


        btnSend = (ImageView) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        listUser = (PullToRefreshListView) findViewById(R.id.listUser);
        listUser.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listUser.setStackFromBottom(true);
        listUser.disablePullToRefresh();

        flImgHeader = (FrameLayout) findViewById(R.id.flImgHeader);
        flImgHeader.setVisibility(View.VISIBLE);
        flImgHeader.setOnClickListener(this);

        imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

        llAdView = (LinearLayout) findViewById(R.id.llAdView);
        llAdView.setVisibility(View.GONE);

        if (isGroup) {
            textFans.setText(getString(R.string.group));
            editMessage.setHint(getString(R.string.replay_to_group));
            imgUserPic.setImageResource(R.drawable.icon_group);
        } else {
            textFans.setText(UserName.toUpperCase());
            editMessage.setHint(getString(R.string.replay_to) + " " + UserName);
        }

        if (!isGroup)
            if (profileimageUrl == null || profileimageUrl.equalsIgnoreCase("")) {
                if (Utility.isNetworkAvailable(mContext)) {
                    new GroupMemberCall(threadId).start();
                } else {
                    Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
                    Utility.HideDialog(mContext);
                }
            } else {

                imgUserPic.setImageResource(R.drawable.ic_pic_small);

                Picasso.with(mContext).load(profileimageUrl).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop()
                        .placeholder(R.drawable.ic_pic_small).error(R.drawable.ic_pic_small).into(imgUserPic);
            }

    }

    @Override
    public void onClick(View v) {
        if (textFans.equals(v)) {
            onBackPressed();
        } else if (btnSend.equals(v)) {
            if (!editMessage.getText().toString().trim().equals("")) {

                sErrorMessage = "";

                Utility.ShowProgressDialog(mContext, getString(R.string.loading));
                isRefresh = true;
                String strMsg = editMessage.getText().toString().trim();
                String ContentMsg = strMsg.replace("\"", "");
                Api.sendMessageChat(mContext, threadId, StringEscapeUtils.escapeJava(ContentMsg), new ConnectCallback<BaseResponse>() {
                    @Override
                    public void onSuccess(BaseResponse object) {
                        Log.d(LOG_TAG, "sendMessageChat.onSuccess");
                        Utility.HideDialog(mContext);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);
//                        arrMessages.clear();
                        updateMessageList(false);
                    }

                    @Override
                    public void onFailure(String error) {
                        Log.d(LOG_TAG, "sendMessageChat.onFailure.error=" + error);
                        Utility.HideDialog(mContext);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);
//                        arrMessages.clear();
                        updateMessageList(false);
//                        Utility.HideDialog(mContext);
//                        isRefresh = false;
                    }
                });

//				if (Utility.isNetworkAvailable(mContext)) {
//					isRefresh=true;
//					String strMsg=editMessage.getText().toString().trim();
//					String ContentMsg=strMsg.replace("\"","");
//					new SendMessageCall(threadId,StringEscapeUtils.escapeJava(ContentMsg)).start();
//
//				} else {
//					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
//					Utility.HideDialog(mContext);
//				}
                editMessage.setText("");
            }
        } else if (flImgHeader.equals(v)) {
            if (isGroup) {
                Intent intent = new Intent(mContext, GroupMember.class);
                intent.putExtra("threadId", threadId);
                startActivity(intent);
            } else {
                final Intent intent = new Intent(mContext, NewProfileActivity.class);
                final UserProfile userProfile = UserProfile.newBuilder()
                        .setUserId(senderUserId)
                        .setUserDisplayName(UserName)
                        .setUserPic(profileimageUrl)
                        .setUserCoverImage(CoverImg)
                        .build();
                intent.putExtra(NewProfileActivity.USER, userProfile);
//			intent.putExtra("UserID",senderUserId);
//			intent.putExtra("UserDisplayName",UserName);
//			intent.putExtra("UserPic",profileimageUrl);
//			intent.putExtra("UserCoverImage",CoverImg);
                startActivity(intent);
            }
        }
    }

    private void updateMessageList(boolean isShowProgress) {
        if (isShowProgress) Utility.ShowProgressDialog(mContext, getString(R.string.loading));
        Api.getMessageList(mContext, threadId, new ConnectCallback<MessageListResponse>() {
            @Override
            public void onSuccess(MessageListResponse object) {
                Log.d(LOG_TAG, "updateMessageList.onSuccess");
//                arrMessages.clear();
                arrMessages = object.getArrMessages();
                messageAdapter.setArrMessages(arrMessages);
                listUser.setSelection(arrMessages.size() - 1);
                Utility.HideDialog(mContext);
            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG, "updateMessageList.onFailure.error=" + error);
                Utility.HideDialog(mContext);
            }
        });
    }


    class GroupMemberCall extends Thread {

        private String threadId;

        public GroupMemberCall(String threadId) {
            this.threadId = threadId;
        }

        @Override
        public void run() {

            String[] name = {"thread"};
            //			String[] value = {"307",phoneNumber,country };
            String[] value = {threadId};
            String response = JSONParser.postRequest(Constant.SERVER_URL + Constant.MESSAGE_SHOW_PARTICIPANTS, name, value, preferences.getString("token", null));


            Log.v(Constant.TAG, "ReplayMessage response " + response);

            if (response != null) {
                try {
                    sErrorMessage = "";
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("error")) {
                        sErrorMessage = jsonObject.getString("error");
                    }
                    arrParticipants = new ArrayList<ParticipantsPojo>();

                    JSONObject jsonObjThread = jsonObject.getJSONObject("thread");

                    JSONArray jsonArrayMessages = jsonObjThread.getJSONArray("participants");

                    for (int i = 0; i < jsonArrayMessages.length(); i++) {
                        JSONObject jsonObjMessage = jsonArrayMessages.getJSONObject(i);

                        ParticipantsPojo tempPojo = new ParticipantsPojo();
                        tempPojo.setUserId(jsonObjMessage.getString("userId"));
                        tempPojo.setProfileImage(jsonObjMessage.getString("profileImage"));
                        tempPojo.setProfileCover(jsonObjMessage.getString("profileCover"));
                        tempPojo.setDisplayName(jsonObjMessage.getString("displayName"));
                        arrParticipants.add(tempPojo);
                    }

                    if (sErrorMessage != null && !sErrorMessage.equals("")) {
                        handlerGroup.sendEmptyMessage(0);
                    } else {
                        handlerGroup.sendEmptyMessage(1);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    handlerGroup.sendEmptyMessage(0);
                }

            } else {

                handlerGroup.sendEmptyMessage(0);
            }

        }
    }

    private Handler handlerGroup = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Utility.HideDialog(mContext);
            if (msg.what == 1) {
                profileimageUrl = arrParticipants.get(0).getProfileImage();
                senderUserId = arrParticipants.get(0).getUserId();
                CoverImg = arrParticipants.get(0).getProfileCover();
                UserName = arrParticipants.get(0).getDisplayName();
                if (arrParticipants.get(0).getProfileCover().equals("")) {
                    imgUserPic.setImageResource(R.drawable.ic_pic_small);
                } else {
                    imgUserPic.setImageResource(R.drawable.ic_pic_small);
                    if (arrParticipants.get(0).getProfileImage() != null && !arrParticipants.get(0).getProfileImage().equals(""))
                        Picasso
                                .with(mContext)
                                .load(arrParticipants.get(0).getProfileImage())
                                .resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45))
                                .centerCrop()
                                .placeholder(R.drawable.ic_pic_small)
                                .error(R.drawable.ic_pic_small)
                                .into(imgUserPic);

                }
            } else {

            }
        }
    };

    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("isRefresh", isRefresh);
        setResult(101, intent);
        finish();
    }

    private void messagesReaded() {
        Api.sendRequestMessageThreadReaded(mContext, threadId, new ConnectCallback<BaseResponse>() {
            @Override
            public void onSuccess(BaseResponse object) {
                Log.d(LOG_TAG, "messagesReaded.onSuccess");
            }

            @Override
            public void onFailure(String error) {
                Log.d(LOG_TAG, "messagesReaded.onFailure.error=" + error);
            }
        });
    }

    public static boolean isActive() {
        return isActive;
    }

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
////{"message":"Thread read successfully."}
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
////			Utility.HideDialog(mContext);
//
//
//			if (msg.what == 1) {
//			} else {
//			}
//		}
//	};
//
//    class GetMessageThreadCall extends Thread {
//
//        @Override
//        public void run() {
//            String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.GET_MESSAGE_THREAD+threadId, preferences.getString("token", null));
//            Log.v(Constant.TAG, "Message thread response " + response);
//
//            if (response != null) {
//
//                try {
//
//                    sErrorMessage = "";
//
//                    JSONObject jsonObject = new JSONObject(response);
//
//                    if (jsonObject.has("error")) {
//                        sErrorMessage = jsonObject.getString("error");
//                    }
//
//                    JSONObject jsonObjThread = jsonObject.getJSONObject("thread");
//
//                    JSONArray jsonArrayMessages = jsonObjThread.getJSONArray("messages");
//
//                    for (int j = 0; j < jsonArrayMessages.length(); j++) {
//
//                        JSONObject jsonObjMessage = jsonArrayMessages.getJSONObject(j);
//
//                        MessageThreadPojo tempPojo=new MessageThreadPojo();
//                        tempPojo.setThreadId(jsonObjThread.getString("threadId"));
//
//                        tempPojo.setMessageId(jsonObjMessage.getString("message_id"));
//                        tempPojo.setMessage(jsonObjMessage.getString("message"));
//                        tempPojo.setMessageReceived(jsonObjMessage.getString("messageReceived"));
//                        tempPojo.setMessageRead(jsonObjMessage.getString("messageRead"));
//
//                        JSONObject jsonObjMessageSender = jsonObjMessage.getJSONObject("messageSender");
//                        tempPojo.setSenderId(jsonObjMessageSender.getString("id"));
//                        tempPojo.setSenderprofileImage(jsonObjMessageSender.getString("profileImage"));
//                        tempPojo.setSenderdisplayName(jsonObjMessageSender.getString("displayName"));
//                        tempPojo.setSenderUserName(jsonObjMessageSender.getString("userName"));
//                        arrMessages.add(tempPojo);
//                    }
//
//
//                    if (sErrorMessage != null && !sErrorMessage.equals("")) {
//                        handlerMessage.sendEmptyMessage(0);
//                    } else {
//                        handlerMessage.sendEmptyMessage(1);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    handlerMessage.sendEmptyMessage(0);
//                }
//
//            } else {
//
//                handlerMessage.sendEmptyMessage(0);
//            }
//        }
//    }
//
//    Handler handlerMessage = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            Utility.HideDialog(mContext);
//
//            if (msg.what == 1) {
//                messageAdapter.notifyDataSetChanged();
//                listUser.setSelection(arrMessages.size()-1);
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
//            } else {
//
//            }
//        }
//    };
//
//    class SendMessageCall extends Thread {
//
//        private String threadId,message;
//
//        public SendMessageCall(String threadId,String message){
//            this.threadId=threadId;
//            this.message=message;
//        }
//
//        @Override
//        public void run() {
//
//            Log.d("log_tag","thread id=>"+threadId);
//
//            String[] name = {"thread","message"};
//            //			String[] value = {"307",phoneNumber,country };
//            String[] value = {threadId,message};
//            String response = JSONParser.LikepostRequest(Constant.SERVER_URL + Constant.REPLAY_MESSAGE_THREAD, name, value, preferences.getString("token", null));
//
//
//            Log.v(Constant.TAG, "ReplayMessage response " + response);
//
//            if (response != null) {
//
//                try {
//
//                    sErrorMessage = "";
//
//                    if(response.equalsIgnoreCase("error")){
//                        sErrorMessage=getString(R.string.no_entries_found);
//                    }
//
//                    if (sErrorMessage != null && !sErrorMessage.equals("")) {
//                        handlerReplayMessage.sendEmptyMessage(0);
//                    } else {
//                        AdWordsManager.getInstance().sendMessageSentEvent();
//                        handlerReplayMessage.sendEmptyMessage(1);
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    handlerReplayMessage.sendEmptyMessage(0);
//                }
//
//            } else {
//
//                handlerReplayMessage.sendEmptyMessage(0);
//            }
//
//        }
//    }
//
//    Handler handlerReplayMessage = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            Utility.HideDialog(mContext);
//            editMessage.setText("");
//            if (msg.what == 1) {
//
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(editMessage.getWindowToken(), 0);
//                arrMessages.clear();
//
//
//                updateMessageList(false);
////				Utility.ShowProgressDialog(mContext, getString(R.string.loading));
////				if (Utility.isNetworkAvailable(mContext)) {
////					new GetMessageThreadCall().start();
////
////				} else {
////
////					Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
////					Utility.HideDialog(mContext);
////				}
//            } else {
//
//            }
//        }
//    };


}
