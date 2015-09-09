package com.mobstar.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	public static String postRequest(String url, String[] name, String[] value, String token) {

		// Making HTTP request
		int status=0;
		try {
			//khyati  for timeout 
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Constant.TIMEOUTCONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, Constant.TIMEOUTSOCKET);

			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("X-API-KEY", Constant.API_KEY);
			if (token != null) {
				httpPost.addHeader("X-API-TOKEN", token);
			}

			//for timeout param
			httpClient.setParams(httpParameters);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (value != null) {
				for (int i = 0; i < value.length; i++) {
					pairs.add(new BasicNameValuePair(name[i], value[i]));
				}
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
			httpPost.setEntity(entity);

			HttpResponse httpResponse = httpClient.execute(httpPost);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			//get header

//			status=httpResponse.getStatusLine().getStatusCode();


		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			json = sb.toString();
			Log.e("WEB CALL","Response is->"+json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// return JSON String

		// return JSON String

		
			return json;
		


	}

	public static String LikepostRequest(String url, String[] name, String[] value, String token) {

		// Making HTTP request
		int status=0;
		try {

			//khyati  for timeout 
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Constant.TIMEOUTCONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, Constant.TIMEOUTSOCKET);

			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPost httpPost = new HttpPost(url);
			httpPost.addHeader("X-API-KEY", Constant.API_KEY);
			if (token != null) {
				httpPost.addHeader("X-API-TOKEN", token);
			}

			//for timeout param
			httpClient.setParams(httpParameters);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (value != null) {
				for (int i = 0; i < value.length; i++) {
					pairs.add(new BasicNameValuePair(name[i], value[i]));
				}
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
			httpPost.setEntity(entity);

			HttpResponse httpResponse = httpClient.execute(httpPost);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

			//get header

			status=httpResponse.getStatusLine().getStatusCode();
			Log.d("Response Status","status code is=>"+status);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			json = sb.toString();
			Log.e("WEB CALL","Response is->"+json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// return JSON String

		if(status!=200){
			return "error";
		}
		else {
			return json;
		}

	}


	public static String putRequest(String url, String[] name, String[] value, String token) {

		// Making HTTP request
		try {

			//khyati  for timeout 
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Constant.TIMEOUTCONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, Constant.TIMEOUTSOCKET);

			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpPut httpPut = new HttpPut(url);
			httpPut.addHeader("X-API-KEY", Constant.API_KEY);
			if (token != null) {
				httpPut.addHeader("X-API-TOKEN", token);
			}

			//for timeout param
			httpClient.setParams(httpParameters);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (value != null) {
				for (int i = 0; i < value.length; i++) {
					pairs.add(new BasicNameValuePair(name[i], value[i]));
				}
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
			httpPut.setEntity(entity);

			HttpResponse httpResponse = httpClient.execute(httpPut);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// return JSON String
		return json;
	}

	public static String deleteRequest(String url, String[] name, String[] value, String token) {

		// Making HTTP request
		try {

			//khyati  for timeout 
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Constant.TIMEOUTCONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, Constant.TIMEOUTSOCKET);
			// defaultHttpClient
			//			Log.d("mobstar","delete req is=>"+url);

			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpDeleteWithBody httpDeleteWithBody = new HttpDeleteWithBody(url);

			HttpDelete httpDelete = new HttpDelete(url);
			httpDelete.addHeader("X-API-KEY", Constant.API_KEY);
			if (token != null) {
				httpDelete.addHeader("X-API-TOKEN", token);
			}
			//for timeout param
			httpClient.setParams(httpParameters);

			List<NameValuePair> pairs = new ArrayList<NameValuePair>();
			if (value != null) {
				for (int i = 0; i < value.length; i++) {
					pairs.add(new BasicNameValuePair(name[i], value[i]));
				}
			}

			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, HTTP.UTF_8);
			httpDeleteWithBody.setEntity(entity);

			HttpResponse httpResponse = httpClient.execute(httpDelete);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// return JSON String
		return json;
	}

	public static String getRequest(String url, String token) {

		// Log.v(Constant.TAG, "URL " + url);

		// Making HTTP request
		try {
			//khyati  for timeout 
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, Constant.TIMEOUTCONNECTION);
			HttpConnectionParams.setSoTimeout(httpParameters, Constant.TIMEOUTSOCKET);

			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			Log.d(Constant.TAG,"Sending Request=>"+url);
			Log.d(Constant.TAG,"api key is->"+Constant.API_KEY);
			Log.d(Constant.TAG,"Token is->"+token);
			httpGet.addHeader("X-API-KEY", Constant.API_KEY);
			if (token != null) {
				httpGet.addHeader("X-API-TOKEN", token);
			}
			//for timeout param
			httpClient.setParams(httpParameters);

			HttpResponse httpResponse = httpClient.execute(httpGet);

			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// return JSON String
		return json;
	}
}
