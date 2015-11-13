package com.mobstar.api.responce;

import android.util.Log;

import com.mobstar.pojo.NotificationPojo;
import com.mobstar.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by lipcha on 09.11.15.
 */
public class NotificationResponse extends BaseResponse {

    private ArrayList<NotificationPojo> arrNotificationPojos;
    private String notificationId = "";

    public ArrayList<NotificationPojo> getArrNotificationPojos() {
        return arrNotificationPojos;
    }

    public String getNotificationId() {
        return notificationId;
    }

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("notifications")) {
            arrNotificationPojos = new ArrayList<>();

            JSONArray jsonArrayNotification = jsonObject.getJSONArray("notifications");
            notificationId = "";
            for (int i = 0; i < jsonArrayNotification.length(); i++) {

                JSONObject jsonObjNotification = jsonArrayNotification.getJSONObject(i);

                NotificationPojo tempNotificationPojo = new NotificationPojo();

                if(jsonObjNotification.getString("notificationId")!=null){
                    tempNotificationPojo.setNotificationID(jsonObjNotification.getString("notificationId"));
                    notificationId = notificationId + jsonObjNotification.getString("notificationId") + ",";
                    Log.d("mobstar", "Notification=>" + notificationId);
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
                    if (jsonEntryObj.has("profileImage"))
                        tempNotificationPojo.setProfileImage(jsonEntryObj.getString("profileImage"));
                    if (jsonEntryObj.has("profileCover"))
                        tempNotificationPojo.setProfileCover(jsonEntryObj.getString("profileCover"));
                }

                if(jsonObjNotification.has("messageGroup")){
                    tempNotificationPojo.setMessageGroup(jsonObjNotification.getString("messageGroup"));
                }

                arrNotificationPojos.add(tempNotificationPojo);
            }
            if(notificationId != null && notificationId.length() > 0){
                int pos = notificationId.lastIndexOf(",");
                notificationId = notificationId.substring(0, pos);
            }
        }
    }
}
