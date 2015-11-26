package com.mobstar.api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.mobstar.R;
import com.mobstar.api.responce.OnFileDownloadCallback;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by lipcha on 08.09.15.
 */
public class RestClient {

    private static final String LOG_TAG = RestClient.class.getName();
    private static RestClient instance;
    private AsyncHttpClient httpClient;
    private SharedPreferences preferences;
    private Context context;
    private Toast toast;

    private RestClient(){

    }

    public static RestClient getInstance(final Context _context){
        if (instance == null) {
            instance = new RestClient();
            instance.httpClient = new AsyncHttpClient();
            addCookieStore(_context);
        }

        if (instance.preferences == null)
            instance.preferences = _context.getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
        instance.context = _context;
        instance.httpClient.setTimeout(Constant.TIMEOUTCONNECTION);
        instance.httpClient.addHeader("Content-Type", "application/json; charset=utf-8");
        return instance;
    }

    private static void addCookieStore(final Context _context){
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(_context);
        instance.httpClient.setCookieStore(myCookieStore);
    }


    public static boolean existCookie(final Context context){
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        final List<Cookie> cookies = myCookieStore.getCookies();

        if (cookies.isEmpty()) {
            return false;
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                if (cookies.get(i).getName().equalsIgnoreCase("mobstar.sid"))
                return true;
            }
        }
        return false;
    }

    public static void clearCookie(final Context context) {
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(context);
        final List<Cookie> cookies = myCookieStore.getCookies();

        if (cookies.isEmpty()) {
            return;
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                if (cookies.get(i).getName().equalsIgnoreCase("mobstar.sid"))
                    myCookieStore.deleteCookie(cookies.get(i));
            }
        }
    }

    public void getRequest(final String url, HashMap<String, String> params, final ConnectCallback callback){
        if (!Utility.isNetworkAvailable(context)) {
            showToastNotification(context.getString(R.string.no_internet_access));
            if (callback == null)
                return;
            callback.onFailure("");
            return;
        }
        final RequestParams requestParams = new RequestParams(params);
        final String absoluteUrl = ApiConstant.BASE_SERVER_URL + url;
        Log.d(LOG_TAG, "http request get: "+ absoluteUrl + "?" + requestParams.toString());
        httpClient.get(absoluteUrl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                final JSONObject jsonObject = getJsonObject(bytes);
                if (jsonObject != null && callback != null) {
                    callback.parse(jsonObject);
                }else if(callback != null){
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                showToastNotification(throwable.getMessage());
                if (callback != null)
                    callback.onFailure(throwable.toString());
            }
        });
    }

    public JSONObject getJsonObject(final byte[] bytes){
        String jsonStr = "{}";
        try {
            jsonStr = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            jsonStr = "{}";
        }
        if (jsonStr.equals("[]"))
            jsonStr = "{}";
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonStr);
        } catch (JSONException e) {
            jsonStr = "{\"jsonarr\":" + jsonStr + "}";
            try {
                jsonObject = new JSONObject(jsonStr);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void postRequest(final String url, HashMap<String, String> params, final ConnectCallback callback){
        if (!Utility.isNetworkAvailable(context)) {
            showToastNotification(context.getString(R.string.no_internet_access));
            if (callback == null)
                return;
            callback.onFailure("");
            return;
        }
        final String absoluteUrl = ApiConstant.BASE_SERVER_URL + url;
        final RequestParams requestParams = new RequestParams(params);
        Log.d(LOG_TAG, "http request post: "+ absoluteUrl + "?" + requestParams.toString());
        httpClient.removeHeader("Content-Type");
        httpClient.post(null, absoluteUrl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    final JSONObject jsonObject = new JSONObject(new String(bytes, "UTF-8"));
                    if (callback != null) {
                        callback.parse(jsonObject);
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onFailure(e.toString());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                showToastNotification(throwable.getMessage());
                if (callback != null)
                    callback.onFailure(throwable.toString());
            }
        });
    }

    public void getFileRequest(final String url, final String filePath, final OnFileDownloadCallback onFileDownloadCallback){
        httpClient.removeHeader("Content-Type");
        httpClient.removeHeader("X-API-KEY");
        httpClient.removeHeader("X-API-TOKEN");
        final File file = new File(filePath);
        FileAsyncHttpResponseHandler asyncHttpResponseHandler = new FileAsyncHttpResponseHandler(file) {

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                if (onFileDownloadCallback != null)
                    onFileDownloadCallback.onDownload(file);
                Log.d(LOG_TAG, "http_get_file download file: " + file.getAbsolutePath());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {

                if (onFileDownloadCallback != null)
                    onFileDownloadCallback.onFailure(throwable.getMessage(), file.getAbsolutePath());
                Log.d(LOG_TAG, "http_get_file error download file: " + file.getAbsolutePath() + throwable.getMessage());
                Log.d(LOG_TAG, "http_get_file error download file: url=" + url);
                file.delete();
            }
        };
        asyncHttpResponseHandler.setTag(url);
        Log.d(LOG_TAG, "http request download file: " + url);
        httpClient.setURLEncodingEnabled(false);
        httpClient.get(url, asyncHttpResponseHandler);
    }

    public void deleteRequest(final String url, HashMap<String, String> params, final ConnectCallback callback){
        if (!Utility.isNetworkAvailable(context)) {
            showToastNotification(context.getString(R.string.no_internet_access));
            callback.onFailure("");
            return;
        }
        final RequestParams requestParams = new RequestParams(params);
        final String absoluteUrl = ApiConstant.BASE_SERVER_URL + url;
        Log.d(LOG_TAG, "http request delete: " + absoluteUrl + "?" + requestParams.toString());
        httpClient.delete(absoluteUrl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    final JSONObject jsonObject = new JSONObject(new String(bytes, "US-ASCII"));
                    if (callback != null) {
                        callback.parse(jsonObject);
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onFailure(e.toString());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                showToastNotification(throwable.getMessage());
                if (callback != null)
                    callback.onFailure(throwable.toString());
            }
        });
    }

    public void putRequest(final String url, HashMap<String, String> params, final ConnectCallback callback){
        if (!Utility.isNetworkAvailable(context)) {
            showToastNotification(context.getString(R.string.no_internet_access));
            if (callback == null)
                return;
            callback.onFailure("");
            return;
        }
        final String absoluteUrl = ApiConstant.BASE_SERVER_URL + url;
        final RequestParams requestParams = new RequestParams(params);
        Log.d(LOG_TAG, "http request post: "+ absoluteUrl + "?" + requestParams.toString());
        httpClient.removeHeader("Content-Type");
        httpClient.put(null, absoluteUrl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    final JSONObject jsonObject = new JSONObject(new String(bytes, "UTF-8"));
                    if (callback != null) {
                        callback.parse(jsonObject);
                    }
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                    if (callback != null)
                        callback.onFailure(e.toString());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                showToastNotification(throwable.getMessage());
                if (callback != null)
                    callback.onFailure(throwable.toString());
            }
        });
    }

    public void cancelRequest(final String url){
        Log.d(LOG_TAG, "http request cancel: "+ url);
        httpClient.cancelRequestsByTAG(url, false);
    }

    private void showToastNotification(final String message){
        if (toast == null && context != null)
            toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        if (toast != null) {
            toast.setText(message);
            toast.show();
        }
    }
}
