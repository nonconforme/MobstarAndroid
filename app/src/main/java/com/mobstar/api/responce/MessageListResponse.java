package com.mobstar.api.responce;

import com.mobstar.pojo.MessageThreadPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alexandr on 15.10.2015.
 */
public class MessageListResponse extends BaseResponse {
    private ArrayList<MessageThreadPojo> arrMessages = new ArrayList<MessageThreadPojo>();

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        JSONObject jsonObjThread = jsonObject.getJSONObject("thread");

        JSONArray jsonArrayMessages = jsonObjThread.getJSONArray("messages");

        for (int j = 0; j < jsonArrayMessages.length(); j++) {

            JSONObject jsonObjMessage = jsonArrayMessages.getJSONObject(j);

            MessageThreadPojo tempPojo=new MessageThreadPojo();
            tempPojo.setThreadId(jsonObjThread.getString("threadId"));

            tempPojo.setMessageId(jsonObjMessage.getString("message_id"));
            tempPojo.setMessage(jsonObjMessage.getString("message"));
            tempPojo.setMessageReceived(jsonObjMessage.getString("messageReceived"));
            tempPojo.setMessageRead(jsonObjMessage.getString("messageRead"));

            JSONObject jsonObjMessageSender = jsonObjMessage.getJSONObject("messageSender");
            tempPojo.setSenderId(jsonObjMessageSender.getString("id"));
            tempPojo.setSenderprofileImage(jsonObjMessageSender.getString("profileImage"));
            tempPojo.setSenderdisplayName(jsonObjMessageSender.getString("displayName"));
            tempPojo.setSenderUserName(jsonObjMessageSender.getString("userName"));
            arrMessages.add(tempPojo);
        }
    }

    public ArrayList<MessageThreadPojo> getArrMessages() {
        return arrMessages;
    }
}
