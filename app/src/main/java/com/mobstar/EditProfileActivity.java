package com.mobstar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobstar.upload.UploadFileActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;
import com.squareup.picasso.Picasso;

public class EditProfileActivity extends Activity {

	Context mContext;
	TextView textEditProfile;

	String[] arrayChangePicture = { getString(R.string.take_from_camera), getString(R.string.choose_from_library)};
	Uri tempUri;

	ImageView imgProfilePic, imgCoverImage, imgTagLine,imgAddContent;

	String ProfilePicPath;
	String CoverImagePath;

	boolean isProfilePicClicked = false;
	SharedPreferences preferences;

	int temp = 0;

	int IMG_PICKER_SELECT=29;
	int VIDEO_PICKER_SELECT=31;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		mContext = EditProfileActivity.this;
		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		ProfilePicPath = preferences.getString("profile_image", "");
		CoverImagePath = preferences.getString("cover_image", "");

		InitControls();

		Utility.SendDataToGA("EditProfile Screen", EditProfileActivity.this);
	}

	void InitControls() {

		textEditProfile = (TextView) findViewById(R.id.textEditProfile);
		textEditProfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});

		imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
		imgProfilePic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				isProfilePicClicked = true;
				onProfilePic();
			}
		});

		imgCoverImage = (ImageView) findViewById(R.id.imgCoverImage);
		imgCoverImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				isProfilePicClicked = false;
				onProfilePic();
			}
		});

		imgAddContent = (ImageView) findViewById(R.id.imgAddContent);
		imgAddContent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				Dialog dialog = selectPlatform();
				dialog.show();

			}
		});

		if (ProfilePicPath.equals("")) {
			imgProfilePic.setImageResource(R.drawable.profile_pic);
		} else {
			imgProfilePic.setImageResource(R.drawable.profile_pic);

			Picasso.with(mContext).load(ProfilePicPath).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.profile_pic)
			.error(R.drawable.profile_pic).into(imgProfilePic);

		}

		if (CoverImagePath.equals("")) {
			imgCoverImage.setImageResource(R.drawable.cover_image);
		} else {
			imgCoverImage.setImageResource(R.drawable.cover_image);

			Picasso.with(mContext).load(CoverImagePath).resize(Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126)).centerCrop().placeholder(R.drawable.ic_pic_small)
			.error(R.drawable.ic_pic_small).into(imgCoverImage);

		}

		imgTagLine = (ImageView) findViewById(R.id.imgTagLine);
		imgTagLine.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, EditTaglineActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
	}

	void onProfilePic() {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(getString(R.string.change_picture)).setItems(arrayChangePicture, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, "IMG_" + timeStamp + ".jpg");
					values.put(MediaStore.Images.Media.ORIENTATION, android.os.Build.MANUFACTURER.equalsIgnoreCase("samsung") ? 90 : 0);
					tempUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
					startActivityForResult(intent, 25);
				} else if (which == 1) {

					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), 26);

				}
			}
		});
		builder.create().show();
	}

	public Dialog selectPlatform() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		CharSequence[] array = {getString(R.string.gallary),getString(R.string.camera)};
		builder.setTitle(getString(R.string.select_file_from))
		.setSingleChoiceItems(array,-1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int pos) {
				if(pos==0){
					Dialog dialog1 = selectFromGalleryDialog();
					dialog1.show();
				}
				else {
					//					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
					//					intent.setType("video/*");
					//					startActivityForResult(intent,VIDEO_PICKER_SELECT);
					Intent intent=new Intent(mContext,AddContentTypeActivity.class);
					intent.putExtra("categoryId",Constant.PROFILE_CATEGORYID);
					intent.putExtra("FromProfile", true);
					startActivity(intent);
				}
				dialog.dismiss();
			}
		})


		.setNegativeButton(getString(R.string.cancel_), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		return builder.create();
	}

	public Dialog selectFromGalleryDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		CharSequence[] array = {getString(R.string.image),getString(R.string.video)};
		builder.setTitle(getString(R.string.select_file_type))
		.setSingleChoiceItems(array,-1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int pos) {
				if(pos==0){
					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					startActivityForResult(intent, IMG_PICKER_SELECT);
				}
				else {
					Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
					intent.setType("video/*");
					startActivityForResult(intent,VIDEO_PICKER_SELECT);
				}
				dialog.dismiss();
			}
		})


		.setNegativeButton(getString(R.string.cancel_), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});

		return builder.create();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			/**
			 * From Camera here...
			 * */

			if (requestCode == 25) {
				doCrop(tempUri, data);
			}

			/**
			 * From Gallery here...
			 * */
			else if (requestCode == 26) {

				Uri selectedImageUri = data.getData();
				tempUri = selectedImageUri;
				String selectedPath = getPath(selectedImageUri);
				System.out.println("SELECT_IMG Path : " + selectedPath);
				doCrop(selectedImageUri, data);
			}

			/**
			 * From Crop here...
			 * */
			else if (requestCode == 27) {

				try {
					String capturedImageFilePath = getPath(mContext, tempUri);

					if (isProfilePicClicked) {
						ProfilePicPath = capturedImageFilePath;
					} else {
						CoverImagePath = capturedImageFilePath;
					}

					Bitmap bitmap = BitmapFactory.decodeFile(capturedImageFilePath);

					if (bitmap != null) {
						bitmap = Bitmap.createScaledBitmap(bitmap, Utility.dpToPx(mContext, 126), Utility.dpToPx(mContext, 126), false);
						if (isProfilePicClicked) {
							imgProfilePic.setImageBitmap(bitmap);
							imgProfilePic.invalidate();

							Utility.ShowProgressDialog(mContext, getString(R.string.uploading));

							if (Utility.isNetworkAvailable(mContext)) {



								new UploadImage().execute(Constant.SERVER_URL + Constant.UPLOAD_PROFILE_IMAGE);
							} else {

								Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
								Utility.HideDialog(mContext);
							}
						} else {
							imgCoverImage.setImageBitmap(bitmap);
							imgCoverImage.invalidate();

							Utility.ShowProgressDialog(mContext, getString(R.string.uploading));

							//							Log.d("mobstar","Sending url->"+Constant.SERVER_URL + Constant.UPLOAD_COVER_IMAGE);

							if (Utility.isNetworkAvailable(mContext)) {
								new UploadImage().execute(Constant.SERVER_URL + Constant.UPLOAD_COVER_IMAGE);
							} else {

								Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
								Utility.HideDialog(mContext);
							}
						}
					}

				} catch (Exception e) {
					Toast.makeText(EditProfileActivity.this, getString(R.string.error_retke_photo), Toast.LENGTH_SHORT).show();
				}

			}	
			else if(requestCode==IMG_PICKER_SELECT){
				if(data!=null && data.getData()!=null){
					Uri selectedMediaUri = data.getData();
					String selectedPath = getPath(selectedMediaUri);
					System.out.println("SELECT_IMG Path : " + selectedPath);
					//					Log.d("mobstar","img path=>"+selectedPath);
					//					File sVideoPath=new File(selectedPath);
					//					content://media/external/images/media/6112
					if(selectedPath!=null){
						Intent intent = new Intent(mContext, UploadFileActivity.class);
						intent.putExtra("categoryId",Constant.PROFILE_CATEGORYID);
						intent.putExtra("file1", selectedPath);
						intent.putExtra("type", "image");
						startActivity(intent);
						//					finish();
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}

				}


			}else if(requestCode==VIDEO_PICKER_SELECT){
				if(data!=null && data.getData()!=null){
					Uri selectedMediaUri = data.getData();
					String selectedPath = getPath(selectedMediaUri);
					System.out.println("SELECT_VIDEO Path : " + selectedPath);
					//					Log.d("mobstar","video path=>"+selectedPath);
					//					File sImagePath=new File(selectedPath);

					if(selectedPath!=null){
						Intent intent = new Intent(mContext, UploadFileActivity.class);
						intent.putExtra("file1", selectedPath);
						intent.putExtra("type", "video");
						intent.putExtra("categoryId",Constant.PROFILE_CATEGORYID);
						startActivity(intent);
						//					finish();
						overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					}

				}

			}

		}
	};

	public String getPath(Uri uri) {
		//		String[] projection = { MediaStore.Images.Media.DATA };
		//		Cursor cursor = managedQuery(uri, projection, null, null, null);
		//		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		//		cursor.moveToFirst();
		//		

		//		try {

		Log.d("mobstar","uri is=>"+uri);
		String strURI=uri.toString();
		String imageURI="";
		//if cloud image
		if(strURI.startsWith("content")){ 
			Log.d("mobstar","starts with content uri is=>"+strURI);
			//string is from drive
			imageURI=isGoogleData(strURI);
			if(imageURI!=null && imageURI.length()>0){
				//SaveFileFromURL(imageURI);
			}
			else if (strURI.substring(0,21)=="content://com.android") {
				String[] photo_split=strURI.split("%3A");
				imageURI = "content://media/external/images/media/"+photo_split[1];

			}
			else {
				imageURI=uri.toString();
			}
			Log.d("mobstar","img url is=>"+imageURI);
			Uri newURI=Uri.parse(imageURI);
			String path=getPath(EditProfileActivity.this, newURI);
			Log.d("mobstar","Download img from cloud path is=>"+path);
			return path;
		}
		else { //for phone stored image
			String[] projection = { MediaColumns.DATA };
			@SuppressWarnings("deprecation")
			Cursor cursor = managedQuery(uri, projection, null, null, null);
			if (cursor != null) {
				int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
				cursor.moveToFirst();
				return cursor.getString(column_index);
			} else
				return null;
		}


		//		} catch (Exception e) {
		//			e.printStackTrace();
		//			 return null;
		//		}

	}

	private void doCrop(Uri mImageCaptureUri, Intent data) {

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
		int size = list.size();
		if (size == 0) {
			Toast.makeText(this, getString(R.string.cant_find_image_crop_application), Toast.LENGTH_SHORT).show();
		} else {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			intent.setData(mImageCaptureUri);
			intent.putExtra("crop", true);
			intent.putExtra("outputX", metrics.widthPixels);
			intent.putExtra("outputY", metrics.widthPixels);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			
			intent.putExtra("scale", true);
			Intent i = new Intent(intent);
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
			ContentValues values = new ContentValues();
			values.put(MediaStore.Images.Media.TITLE, "IMG_" + timeStamp + ".jpg");
			tempUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			i.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
			ResolveInfo res = list.get(0);
			i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
			startActivityForResult(i, 27);
		}
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + context.getPackageName() +"/";

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					//					return Environment.getExternalStorageDirectory() + "/" + split[1];
					return path + "/" + split[1];
				}


			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
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

				String path;

				if (isProfilePicClicked) {
					path = ProfilePicPath;
				} else {
					path = CoverImagePath;
				}

				File myFile = new File(path);
				FileBody fileBody = new FileBody(myFile, "image/png");
				if (isProfilePicClicked) {
					multipartContent.addPart("profileImage", fileBody);
				} else {
					multipartContent.addPart("coverImage", fileBody);
				}

				httpPost.setEntity(multipartContent);

				HttpResponse httpResponse = httpClient.execute(httpPost);

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
				//				Log.e("Buffer Error", "Error converting result " + e.toString());
				e.printStackTrace();
			}

			return json;

		}

		@Override
		protected void onPostExecute(String jsonString) {
			//			Log.v(Constant.TAG, "Upload Response " + jsonString);
			Utility.HideDialog(mContext);

			try {

				JSONObject jsonObject = new JSONObject(jsonString);

				JSONObject jsonUserObj = jsonObject.getJSONObject("user");

				//				Log.v(Constant.TAG, "profileCover " + jsonUserObj.getString("profileCover"));

				preferences.edit().putString("profile_image", jsonUserObj.getString("profileImage")).commit();
				preferences.edit().putString("cover_image", jsonUserObj.getString("profileCover")).commit();

			} catch (Exception e) {
				// TODO: handle exception
			}

			Intent intent = new Intent("profile_image_changed");
			LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

		}

	}

	private String isGoogleData(String uri){
		//	content://com.google.android.apps.photos.content/0/https%3A%2F%2Flh6.googleusercontent.com%2FynTHe850CyVzMJb0a4f_0FMCN9JWshCOIJU-vLCv6g0%3Ds0-d
		if(uri.contains("http")){
			String url=uri.substring(uri.indexOf("http"));
			String newURL=null;
			try {
				newURL = URLDecoder.decode(url, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("mobstar","img url from drive is=> "+newURL);
			return newURL;
		}
		return null;
	}

	

}
