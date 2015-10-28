package com.mobstar.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Kesedi on 29.08.2015.
 */
public class TimeUtility {
    private static final String LOG_TAG = TimeUtility.class.getName();
    public static long TIME_LAPSE=0;

    public static long getDiffTime(String arg) {
        Calendar today = Calendar.getInstance();
        return (today.getTimeInMillis() - (getTimeInMillis(arg) + TIME_LAPSE)) / 1000;
    }

    public void requestServerTime() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String response = JSONParser.getRequest(Constant.SERVER_URL+Constant.GET_CURRENT_SERVER_TIME
                        ,null);
                Log.d(LOG_TAG, "TIME_LAPSE=" + TIME_LAPSE);
                Log.d(LOG_TAG, "response=" + response);
                if (!response.isEmpty()) {
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("serverCurrentTime")) {

                            long serverCurrentTime = jsonObject.getLong("serverCurrentTime");
                            Calendar deviceTime = Calendar.getInstance();
                            Calendar serverTime = Calendar.getInstance();
                            serverTime.setTimeInMillis(serverCurrentTime);
                            TIME_LAPSE = (deviceTime.getTimeInMillis() - serverTime.getTimeInMillis())+TimeZone.getDefault().getOffset(System.currentTimeMillis());
                            Log.d(LOG_TAG, "TIME_LAPSE=" + TIME_LAPSE);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d(LOG_TAG, "requestServerTime.error=" + e.toString());
                    }

                }
            }
        }.start();
    }

    public static String getStringTime(long seconds) {

        String TimeInString = "";

        if (seconds <= 0) {
            TimeInString = "just now";
        } else if (seconds < 60) {
            TimeInString = seconds + " s ago";
        } else if (seconds < 3600) {
            TimeInString = seconds / 60 + " m ago";
        } else if (seconds < 86400) {
            TimeInString = seconds / 3600 + " h ago";
        } else if (seconds < 604800) {
            TimeInString = seconds / 86400 + " day ago";
        } else if (seconds < 2592000) {
            TimeInString = seconds / 604800 + " week ago";
        } else if (seconds < 31104000) {
            TimeInString = seconds / 2592000 + " month ago";
        } else if (seconds >= 31104000) {
            TimeInString = seconds / 31104000 + " year ago";
        }

        return TimeInString;
    }

    public static String getDifferenceStringTime(long seconds) {

        String TimeInString = "";


        if (seconds <= 0) {
            //			TimeInString = "just now";
            TimeInString = "Today";
        } else if (seconds < 60) {
            //			TimeInString = seconds + " s ago";
            TimeInString = "Today";
        } else if (seconds < 3600) {
            //			TimeInString = seconds / 60 + " m ago";
            TimeInString = "Today";
        } else if (seconds < 86400) {
            //			TimeInString = seconds / 3600 + " h ago";
            TimeInString = "Today";
        } else if (seconds < 604800) {
            //			TimeInString = seconds / 86400 + " day ago";
            long day = seconds / 86400;
            if (day <= 1) {
                TimeInString = "Yesterday";
            } else {
                TimeInString = "";
            }
        }
        //			else if (seconds < 2592000) {
        //			TimeInString = seconds / 604800 + " week ago";
        //		} else if (seconds < 31104000) {
        //			TimeInString = seconds / 2592000 + " month ago";
        //		} else if (seconds >= 31104000) {
        //			TimeInString = seconds / 31104000 + " year ago";
        //		}

        return TimeInString;
    }

    public static long getTimeInMillis(String timeString) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date gmtTime = null;

        try {
            gmtTime = formatter.parse(timeString);// catch exception

        } catch (Exception e) {
            e.printStackTrace();
        }

        Calendar thatDay = Calendar.getInstance();
        thatDay.setTime(gmtTime);

        return thatDay.getTimeInMillis();
    }
}