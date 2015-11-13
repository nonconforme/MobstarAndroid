package com.mobstar.api.responce;

import com.mobstar.pojo.MessagePojo;
import com.mobstar.pojo.ParticipantsPojo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alexandr on 19.10.2015.
 */
public class ChatListResponse extends BaseResponse {
    private ArrayList<MessagePojo> arrMessage=new ArrayList<MessagePojo>();

    @Override
    public void configure(JSONObject jsonObject) throws JSONException {
        super.configure(jsonObject);
        JSONArray jsonArrayThreads = jsonObject.getJSONArray("threads");

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
    }

    public ArrayList<MessagePojo> getArrMessage() {
        return arrMessage;
    }
}