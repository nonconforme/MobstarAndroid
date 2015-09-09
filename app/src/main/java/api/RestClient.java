package api;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobstar.utils.Constant;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by lipcha on 08.09.15.
 */
public class RestClient {


    private static RestClient instance;
    private AsyncHttpClient httpClient;
    private SharedPreferences preferences;

    private RestClient(){

    }

    public static RestClient getInstance(final Context _context){
        if (instance == null) {
            instance = new RestClient();
        }
        if (instance.preferences == null)
            instance.preferences = _context.getSharedPreferences(Constant.MOBSTAR_PREF, Activity.MODE_PRIVATE);
        instance.httpClient = new AsyncHttpClient();
        instance.httpClient.setTimeout(Constant.TIMEOUTCONNECTION);
        instance.httpClient.addHeader("Content-Type", "application/json");
        instance.httpClient.addHeader("X-API-KEY", Constant.API_KEY);
        instance.httpClient.addHeader("X-API-TOKEN", instance.preferences.getString("token", null));
        return instance;
    }

    public void getRequest(final String url, RequestParams params, final ConnectCallback callback){
        final String absoluteUrl = Constant.SERVER_URL + url;
        Log.d("http get request: ", absoluteUrl + "?" + params.toString());
        httpClient.get(absoluteUrl, params, new AsyncHttpResponseHandler() {
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
                if (callback != null)
                    callback.onFailure(throwable.toString());
            }
        });
    }

    public void postRequest(final String url, HashMap<String, String> params, final ConnectCallback callback){

        final String absoluteUrl = Constant.SERVER_URL + url;
        final RequestParams requestParams = new RequestParams(params);
        Log.d("http post request: ", absoluteUrl + "?" + requestParams.toString());
        httpClient.removeHeader("Content-Type");
        httpClient.post(null, absoluteUrl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    final JSONObject jsonObject = new JSONObject(new String(bytes, "US-ASCII"));
                    Log.d("tag", "json = " + jsonObject);
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
                if (callback != null)
                    callback.onFailure(throwable.toString());
            }
        });
    }
}
