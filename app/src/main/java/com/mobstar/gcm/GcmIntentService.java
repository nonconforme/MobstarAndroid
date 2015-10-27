package com.mobstar.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mobstar.R;
import com.mobstar.home.HomeActivity;
import com.mobstar.home.HomeFragment;
import com.mobstar.home.new_home_screen.profile.NewProfileActivity;
import com.mobstar.home.new_home_screen.profile.UserProfile;
import com.mobstar.inbox.GroupMessageDetail;
import com.mobstar.inbox.MessageDetail;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import java.util.ArrayList;

public class GcmIntentService extends IntentService {

    private static final String LOG_TAG = GcmIntentService.class.getName();
    public static final String NEW_ENTRY_PUSH = "new entry push";
    public static int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	SharedPreferences pref;

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {

			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification(getString(R.string.send_error) + " ");
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification(getString(R.string.deleted_messages_on_server) + " ");
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {

				//added khyati temporary commented



				if(extras.containsKey("Type")){
					String badgeCount="";
                    badgeCount=extras.getString("badge","");

                    Log.d(LOG_TAG, "Type=" + extras.getString("Type"));
                    Log.d(LOG_TAG, "extras=" + extras.toString());
                    Log.d(LOG_TAG, "badge=" + badgeCount);

					if(extras.getString("Type").toString().equalsIgnoreCase("Message")){
						String messageGroup=extras.getString("messageGroup");
						String threadId=extras.getString("entry_id");
						String message=extras.getString("message").toString();
						String userName=extras.getString("diaplayname").toString();
						if(messageGroup!=null && threadId!=null && message!=null && userName!=null){
							sendNotification(message,messageGroup,threadId,userName);
						}

					}
					else if(extras.getString("Type").toString().equalsIgnoreCase("Like")){
						String message=extras.getString("message").toString();
						String entryId=extras.getString("entry_id").toString();
						if(entryId!=null){
							sendNotification(message,entryId);
						}
					}
                    else if(extras.getString("Type").equals("newEntry")){
                        if (extras.containsKey("entries")){
                            String jsonArrayEnry = extras.getString("entries");
                            ArrayList<NewEntryPush> newEntryPushs = NewEntryPush.getList(jsonArrayEnry);
                            if (!newEntryPushs.isEmpty()) sendNewEntrys(newEntryPushs);
                        }

                    }
                    else if(extras.getString("Type").toString().equalsIgnoreCase("splitScreen")){
                        if (extras.containsKey("usedEntryName")&&extras.containsKey("creatorName")&&extras.containsKey("createdEntryId")) {
                            String entryName = Utility.unescape_perl_string(extras.getString("usedEntryName"));
                            String userName = extras.getString("creatorName");
                            String entryId = extras.getString("createdEntryId");
                            String message = getResources().getString(R.string.notif_split_screen_1) + " "
                                    + entryName + " "
                                    + getResources().getString(R.string.notif_split_screen_2)
                                    + " "
                                    + userName
                                    + getResources().getString(R.string.notif_split_screen_3);
                            Log.d(LOG_TAG, "splitScreen message=" + message);
                            Log.d(LOG_TAG, "splitScreen entryId=" + entryId);
                            sendNotification(message, entryId);
                        }
                    }
					else{
						if(extras.getString("message")!=null) {
                            Log.d(LOG_TAG,"message="+extras.getString("message"));
							sendNotification(extras.getString("message"));
						}
					}

                    if (!badgeCount.isEmpty()) {
                        Utility.setBadgeSamsung(getApplicationContext(), Integer.parseInt(badgeCount));
                        Utility.setBadgeSony(getApplicationContext(), Integer.parseInt(badgeCount));
                    }
				}

				Log.v(Constant.TAG, "Received: " + extras.toString());
			}
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}

    private void sendNewEntrys(ArrayList<NewEntryPush> newEntryPushs) {
        Intent intent = new Intent(HomeFragment.NEW_ENTY_ACTION);
        intent.putExtra(NEW_ENTRY_PUSH, newEntryPushs);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void sendNotification(String msg) {

		Intent intent = new Intent("GetNotificationCount");
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

		mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HomeActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        sendPush(contentIntent,msg);
	}

	private void sendNotification(String msg,String messageGroup,String threadId,String name) {

		Intent intent = new Intent("GetNotificationCount");
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

		mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = null;

		if(messageGroup.equalsIgnoreCase("0")){
			Intent i=new Intent(this,MessageDetail.class);
			i.putExtra("threadId",threadId);
			i.putExtra("UserName",name);
			i.putExtra("FromNotification",true);
			contentIntent = PendingIntent.getActivity(this, 0,i, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		else{
			Intent i=new Intent(this,GroupMessageDetail.class);
			i.putExtra("threadId",threadId);
			i.putExtra("FromNotification",true);
			contentIntent = PendingIntent.getActivity(this, 0,i, PendingIntent.FLAG_UPDATE_CURRENT);
		}
        sendPush(contentIntent,msg);
	}

	private void sendNotification(String msg,String entryId) {

		Intent intent = new Intent("GetNotificationCount");
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

		mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

		PendingIntent contentIntent = null;
		Intent i=new Intent(this, NewProfileActivity.class);
		final UserProfile userProfile = UserProfile.newBuilder()
				.setEntryId(entryId)
				.build();
//		i.putExtra("EntryId",entryId);
		i.putExtra(NewProfileActivity.USER, userProfile);
        i.putExtra(NewProfileActivity.IS_NOTIFICATION, true);

		contentIntent = PendingIntent.getActivity(this, 0,i, PendingIntent.FLAG_UPDATE_CURRENT);
        sendPush(contentIntent,msg);
	}

    private void sendPush(PendingIntent contentIntent, String msg) {
        NotificationCompat.Builder mBuilder = getNotificationBuilder();
        mBuilder.setContentTitle(getString(R.string.mobstar_notification))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg)).setContentText(msg);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
            builder.setColor(getResources().getColor(R.color.yellow_color));
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
            builder.setSmallIcon(R.drawable.ic_notification_5);
        } else  builder.setSmallIcon(R.drawable.ic_launcher);
        return builder;
    }
	
	
}
