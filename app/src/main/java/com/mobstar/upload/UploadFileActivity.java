package com.mobstar.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.home.split.SplitActivity;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class UploadFileActivity extends Activity {

    private static final String LOG_TAG = UploadFileActivity.class.getName();
    ArrayList<String> arrayTags = new ArrayList<String>();
	Context mContext;

	TagListAdapter tagListAdapter;
	ListView listTags;

	EditText editTitle, editTag,editAge,editHeight;
	TextView textTitleHint,textAgeHint,textHeightHint;
	ImageView btnAddTag;

	Typeface typefaceBtn;
	Button btnBack, btnFinish;

	String sType, sFile1, sFile2;

	SharedPreferences preferences;

	String categoryId,subCat="";
	boolean IsFinish=false;
	LinearLayout llParent;
	boolean isModelType=false;
	private Spinner spAge,spHeight;
	private List<String> list;
	private List<String> listHeight;
	private double cm=2.54;
	int posHeight=0,posAge=0;
    private EntryPojo parentSplitEntry;


    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_upload_file);

		mContext = UploadFileActivity.this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			sType = extras.getString("type");
			sFile1 = extras.getString("file1");
			if (extras.containsKey("file2")) {
				sFile2 = extras.getString("file2");
			}

			if(extras.containsKey("categoryId")) {
				categoryId=extras.getString("categoryId");
			}
			if(extras.containsKey("subCat")){
				subCat=extras.getString("subCat");
			}
            if(extras.containsKey(SplitActivity.ENTRY_SPLIT)){
                parentSplitEntry=(EntryPojo) extras.getSerializable(SplitActivity.ENTRY_SPLIT);
            }
		}

		Log.d("mobstar","upload category type is=>"+categoryId);
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		InitControls();

		Utility.SendDataToGA("UploadFile Screen", UploadFileActivity.this);

	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
		listTags = (ListView) findViewById(R.id.listTags);
		llParent=(LinearLayout)findViewById(R.id.llParent);
		editTitle = (EditText) findViewById(R.id.editTitle);
		editTitle.setTypeface(typefaceBtn);
		textTitleHint = (TextView) findViewById(R.id.textTitleHint);
		textTitleHint.setVisibility(View.INVISIBLE);

		textAgeHint = (TextView) findViewById(R.id.textAgeHint);
		textAgeHint.setVisibility(View.INVISIBLE);
		editAge = (EditText) findViewById(R.id.editAge);
		editAge.setTypeface(typefaceBtn);

		textHeightHint = (TextView) findViewById(R.id.textHeightHint);
		textHeightHint.setVisibility(View.INVISIBLE);
		editHeight = (EditText) findViewById(R.id.editHeight);
		editHeight.setTypeface(typefaceBtn);
		spAge=(Spinner)findViewById(R.id.spAge);
		spHeight=(Spinner)findViewById(R.id.spHeight);

		list = new ArrayList<String>();
		list.add("Select Age");
		for(int i=5;i<=100;i++){
			list.add(i+"");
		}
		ArrayAdapter<String> AgeDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		AgeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spAge.setAdapter(AgeDataAdapter);
		listHeight=new ArrayList<String>();
		listHeight.add("Select Height");
		//for feet
		//		for(int j=3;j<10;j++){
		//			//for inches
		//			for(int i=1;i<=12;i++){
		//				double feetToInch=j*12;
		//				double totalInch=feetToInch+i;
		//				double cm=totalInch*2.54;
		//				String strcm=String.format("%.2f",Double.parseDouble(cm+""));
		//				listHeight.add(strcm+" cm");
		//			}
		//		}
		for (int i = 100; i < 305; i++) {
			listHeight.add(i+" cm");
		}
		ArrayAdapter<String> HeightDataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, listHeight);
		HeightDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spHeight.setAdapter(HeightDataAdapter);

		spHeight.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				posHeight=position;

				editHeight.setText(listHeight.get(position).toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {


			}
		});

		spAge.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				posAge=position;
				editAge.setText(list.get(position).toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		editAge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spAge.performClick();
			}
		});

		editHeight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				spHeight.performClick();
			}
		});

		editTitle.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textTitleHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		editAge.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editAge.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textAgeHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		editHeight.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				editHeight.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				textHeightHint.setVisibility(View.INVISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		editTag = (EditText) findViewById(R.id.editTag);
		editTag.setTypeface(typefaceBtn);
		tagListAdapter = new TagListAdapter();
		listTags.setAdapter(tagListAdapter);

		Log.d("mobstar","category id id=>"+categoryId);
		Log.d("mobstar","Sub Cat id=>"+subCat);

		//if category equals 3
		if(categoryId.equalsIgnoreCase("3") && subCat!=null && subCat.length()>0){
			isModelType=true;
			llParent.setVisibility(View.VISIBLE);

		}
		else {
			isModelType=false;
			llParent.setVisibility(View.GONE);
		}

		btnAddTag = (ImageView) findViewById(R.id.btnAddTag);
		btnAddTag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (editTag.getText().toString().trim().length() != 0) {
					arrayTags.add(0, editTag.getText().toString().trim());
					tagListAdapter.notifyDataSetChanged();
					editTag.setText("");
				}
			}
		});

		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setTypeface(typefaceBtn);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(Activity.RESULT_CANCELED);
				onBackPressed();
			}
		});

		btnFinish = (Button) findViewById(R.id.btnFinish);
		btnFinish.setTypeface(typefaceBtn);
		btnFinish.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!IsFinish){
					IsFinish=true;
//					Log.d("mobstar","IsFinish===>");
					hideKeyboard();

					if (editTitle.getText().toString().trim().length() == 0) {
						editTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
						textTitleHint.setText(getString(R.string.enter_title));
						textTitleHint.setVisibility(View.VISIBLE);

					}
					else if(isModelType){
						if (posAge==0) {
							editAge.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
							textAgeHint.setText(getString(R.string.select_age));
							textAgeHint.setVisibility(View.VISIBLE);

						}
						else if (posHeight==0) {
							editHeight.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.signup_cross, 0);
							textHeightHint.setText(getString(R.string.select_height));
							textHeightHint.setVisibility(View.VISIBLE);
						}
						else {
							Utility.ShowProgressDialog(mContext, getString(R.string.uploading));

							if (Utility.isNetworkAvailable(mContext)) {
								new UploadImage().execute(Constant.SERVER_URL + Constant.ENTRY);
							} else {
								Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
								Utility.HideDialog(mContext);
							}
						}
					}
					else {

						Utility.ShowProgressDialog(mContext, getString(R.string.uploading));

						if (Utility.isNetworkAvailable(mContext)) {
							new UploadImage().execute(Constant.SERVER_URL + Constant.ENTRY);
						} else {

							Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
							Utility.HideDialog(mContext);
						}
					}
				}
				
			}
		});

	}

	private class UploadImage extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... urls) {
			InputStream is = null;
			String json = "";

			// Making HTTP request
			try {
				// defaultHttpClient
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(urls[0]);
				httpPost.addHeader("X-API-KEY", Constant.API_KEY);

				httpPost.addHeader("X-API-TOKEN", preferences.getString("token", null));

				MultipartEntity multipartContent = new MultipartEntity();

				String sMimeType = "";

				if (sType.equals("image")) {
					sMimeType = "image/png";
				} else if (sType.equals("audio")) {
					sMimeType = "audio/mpeg";
					File myFile2 = new File(sFile2);
					FileBody fileBody = new FileBody(myFile2, "image/png");
					multipartContent.addPart("file2", fileBody);
				} else if (sType.equals("video")) {
					sMimeType = "video/mpeg";
				}
				File myFile = new File(sFile1);
				FileBody fileBody = new FileBody(myFile, sMimeType);
				multipartContent.addPart("file1", fileBody);

				multipartContent.addPart("type", new StringBody(sType));
				multipartContent.addPart("category", new StringBody(categoryId));
				//				multipartContent.addPart("category", new StringBody("1"));
				if(isModelType){
					multipartContent.addPart("subCategory", new StringBody(subCat));
					multipartContent.addPart("age", new StringBody(editAge.getText().toString()));
					multipartContent.addPart("height", new StringBody(editHeight.getText().toString()));
					
				Log.d("mobstar","sending post request=> type"+sType+"category="+categoryId+" subCategory= "+subCat +" age="+editAge.getText().toString()+" height="+editHeight.getText().toString()+" language"+"english"+" name"+preferences.getString("username", null)+" description="+editTitle.getText().toString());
				}
				multipartContent.addPart("language", new StringBody("english"));
				multipartContent.addPart("name", new StringBody(preferences.getString("username", null)));
                //parent split video id
                if (parentSplitEntry!=null) {
                    multipartContent.addPart("splitVideoId", new StringBody(parentSplitEntry.getID() + ""));
                    Log.d(LOG_TAG,"add splitVideoId="+parentSplitEntry.getID());
                }

				//remove quote from string
				String strTitle=editTitle.getText().toString().trim();
				String ContentTitle=strTitle.replace("\"","");
				Log.d("mobstar","new title is=>"+ContentTitle);
//				multipartContent.addPart("description", new StringBody(StringEscapeUtils.escapeJava(ContentTitle)));
				multipartContent.addPart("description", new StringBody(ContentTitle));

				//if category is 3 model type need to pass following param
				
				
//				Log.d("mobstar","sending url=>"+Constant.SERVER_URL + Constant.ENTRY);

				String tagList = arrayTags.toString();
				String tags = tagList.substring(1, tagList.length() - 1).replace(", ", ",");

				if (arrayTags.size() > 0) {
					multipartContent.addPart("tags", new StringBody(tags));
				}


				Log.d("mobstar","sending post request=> type"+sType+"category"+categoryId+" language"+"english"+" name"+preferences.getString("username", null)+" description"+editTitle.getText().toString());
                Log.d(LOG_TAG,"multipartContent="+multipartContent.toString());
				httpPost.setEntity(multipartContent);
                Log.d(LOG_TAG, "httpPost=" + httpPost.getURI().toString());

				HttpResponse httpResponse = httpClient.execute(httpPost);

				Log.v(Constant.TAG, "Response code " + httpResponse.getStatusLine());
				Log.v(LOG_TAG, "Response code " + httpResponse.getStatusLine());

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
				is.close();
				json = sb.toString();
			} catch (Exception e) {
				Log.e("Buffer Error", "Error converting result " + e.toString());
			}

			return json;

		}

		@Override
		protected void onPostExecute(String jsonString) {
			Log.v(Constant.TAG, "Upload Response " + jsonString);

			Utility.HideDialog(mContext);

			try {

				Intent intent = new Intent("upload_successful");
				LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

				JSONObject jsonObject = new JSONObject(jsonString);

				if (jsonObject.has("error")) {
					setResult(Activity.RESULT_CANCELED);
					onBackPressed();
				} else {
					setResult(Activity.RESULT_OK);
					onBackPressed();
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
                Log.d(LOG_TAG,"JsonError="+e.toString());

			}

		}

	}

	public class TagListAdapter extends BaseAdapter {

		private LayoutInflater inflater = null;

		public TagListAdapter() {
			inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		public int getCount() {
			return arrayTags.size();

		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			View vi = convertView;
			if (convertView == null)
				vi = inflater.inflate(R.layout.row_tag, null);

			TextView textTag = (TextView) vi.findViewById(R.id.textTag);
			textTag.setText(arrayTags.get(position));

			ImageView btnRemoveTag = (ImageView) vi.findViewById(R.id.btnRemoveTag);
			btnRemoveTag.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					arrayTags.remove(position);
					notifyDataSetChanged();
				}
			});

			return vi;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	private void hideKeyboard() {   
	    // Check if no view has focus:
	    View view = this.getCurrentFocus();
	    if (view != null) {
	        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
}
