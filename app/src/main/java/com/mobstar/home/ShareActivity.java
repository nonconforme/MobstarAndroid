package com.mobstar.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ShareCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.google.android.gms.plus.PlusShare;
import com.mobstar.AdWordsManager;
import com.mobstar.R;
import com.mobstar.custom.RoundedTransformation;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.twitter.ImageTwitter;
import com.mobstar.twitter.ImageTwitter.OnCompleteListener;
import com.mobstar.utils.ImageDownloader;
import com.mobstar.utils.ImageDownloader.ImageLoaderListener;
import com.mobstar.utils.Utility;
import com.rosaloves.bitlyj.Url;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.rosaloves.bitlyj.Bitly.as;
import static com.rosaloves.bitlyj.Bitly.shorten;
public class ShareActivity extends Activity {

	Context mContext;

	EntryPojo entryPojo;

	TextView textUserName, textTime, textDescription;
	ImageView imgUserPic;

	TextView btnTweet, btnSendToFriend, btnAddToGPlus, btnFBPost;
	File picFile;
	Uri pngUri;
	private ImageDownloader mDownloader;
	private static Bitmap bitmap;
	private FileOutputStream fos;

	String ShareText = "Hey, check out this entry on MobStar:";
	String ShortURL="";
	String TwitterShareText="Hey, check out this entry on @officialmobstar:";
	private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private boolean isTalent=false;
	private String UserName,UserImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);

		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder().permitAll().build();
		// StrictMode.setThreadPolicy(policy);

		mContext = ShareActivity.this;

		Bundle bundle=getIntent().getExtras();
		if(bundle!=null){
			isTalent=bundle.getBoolean("isTalent");
			if(isTalent){
				UserName=bundle.getString("UserName");
				UserImg=bundle.getString("UserImg");
				if(UserImg!=null && UserImg.length()>0){
					 mDownloader = new ImageDownloader(UserImg
			                    , getParent(), bitmap, new ImageLoaderListener() {
			                @Override
			                public void onImageDownloaded(Bitmap bmp) {
			                    bitmap = bmp;
			       
			                    if(Utility.isNetworkAvailable(mContext)) {
			                    	 pngUri =saveImageToSD();
			                    }
			                    else {
			                    	Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();
								}
			                  
			                }
			                });

			            /*--- we need to call execute() since nothing will happen otherwise ---*/
			            mDownloader.execute();
				}
				
			}
			else {
				entryPojo = (EntryPojo) getIntent().getSerializableExtra("entry");
			}
		}



		Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

		Session session = Session.getActiveSession();

		if (session == null) {
			if (savedInstanceState != null) {
				session = Session.restoreSession(mContext, null, statusCallback, savedInstanceState);
			}
			if (session == null) {
				session = new Session(mContext);
			}
			Session.setActiveSession(session);
		}

		InitControls();

		Utility.SendDataToGA("Share Screen", ShareActivity.this);
	}

	void InitControls() {

		textUserName = (TextView) findViewById(R.id.textUserName);
		textTime = (TextView) findViewById(R.id.textTime);
		textDescription = (TextView) findViewById(R.id.textDescription);

		//		ShareText += "\n" + "http://www.mobstar.com/android";

		if(!isTalent){
			Utility.ShowProgressDialog(mContext, getString(R.string.generating_shorten_link));

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Url url = null;
					//		 if (entryPojo.getType().equals("image")) {
					//		 url = as("niravspaceo",
					//		 "R_5e9eb981a6e34baea49713adbff50779").call(shorten(entryPojo.getImageLink()));
					//		 } else if (entryPojo.getType().equals("audio")) {
					//		 url = as("niravspaceo",
					//		 "R_5e9eb981a6e34baea49713adbff50779").call(shorten(entryPojo.getAudioLink()));
					//		 } else if (entryPojo.getType().equals("video")) {

					//		 http://dev.spaceotechnologies.com/projects/redirect/iphone/
					//		 http://www.mobstar.com/android
					url = as("niravspaceo",
							"R_5e9eb981a6e34baea49713adbff50779").call(shorten("http://share.mobstar.com/info.php?id="+entryPojo.getID()));
					//		 }

					ShortURL= url.getShortUrl();
					ShareText=ShareText+ShortURL;
					Utility.HideDialog(mContext);
				}
			}).start();

			textUserName.setText(entryPojo.getName());
			textDescription.setText(Utility.unescape_perl_string(entryPojo.getDescription()));
			textTime.setText(entryPojo.getCreated());

			imgUserPic = (ImageView) findViewById(R.id.imgUserPic);

			if (entryPojo.getProfileImage().equals("")) {
				imgUserPic.setImageResource(R.drawable.ic_pic_small);
			} else {
				imgUserPic.setImageResource(R.drawable.ic_pic_small);

				Picasso.with(mContext).load(entryPojo.getProfileImage()).resize(Utility.dpToPx(mContext, 45), Utility.dpToPx(mContext, 45)).centerCrop().placeholder(R.drawable.ic_pic_small)
				.error(R.drawable.ic_pic_small).transform(new RoundedTransformation(Utility.dpToPx(mContext, 45), 0)).into(imgUserPic);

			}
		}
		else {
			ShortURL=UserName;
			ShareText="#mobstar "+UserName;
			textUserName.setText(UserName);
			textTime.setVisibility(View.GONE);
			textDescription.setVisibility(View.GONE);
		}


		btnTweet = (TextView) findViewById(R.id.btnTweet);
		btnTweet.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				TwitterShareText=TwitterShareText+ShortURL;
				if(isTalent && picFile!=null){
					
					Utility.ShowProgressDialog(mContext, getString(R.string.loading));

					boolean authOnly = false;
					ImageTwitter mTweet = new ImageTwitter(ShareActivity.this, authOnly,TwitterShareText,picFile);
					mTweet.setOnCompleteListener(new OnCompleteListener() {

						@Override
						public void onComplete(final String action) {

							if (action.equals("Success")) {

								Utility.HideDialog(mContext);
                                AdWordsManager.getInstance().sendSharedEntryEvent();

							} else {
								Utility.HideDialog(mContext);
							}

						}
					});
					mTweet.send();
				}
				else {
					Utility.ShowProgressDialog(mContext, getString(R.string.loading));

					boolean authOnly = false;
					ImageTwitter mTweet = new ImageTwitter(ShareActivity.this, authOnly, TwitterShareText,null);
					mTweet.setOnCompleteListener(new OnCompleteListener() {

						@Override
						public void onComplete(final String action) {
							// TODO Auto-generated method stub

							if (action.equals("Success")) {

								Utility.HideDialog(mContext);
                                AdWordsManager.getInstance().sendSharedEntryEvent();

							} else {
								Utility.HideDialog(mContext);
							}

						}
					});
					mTweet.send();	
				}
				
			}
		});

		btnSendToFriend = (TextView) findViewById(R.id.btnSendToFriend);
		btnSendToFriend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isTalent){
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_SUBJECT, "Mobstar");
					i.putExtra(Intent.EXTRA_TEXT, ShareText);
					if(pngUri!=null){
						i.putExtra(Intent.EXTRA_STREAM, pngUri);
					}
					try {
						startActivity(Intent.createChooser(i, "Send mail..."));
                        AdWordsManager.getInstance().sendSharedEntryEvent();
					} catch (android.content.ActivityNotFoundException ex) {
						Toast.makeText(ShareActivity.this, getString(R.string.there_are_no_email_clients_installed), Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Intent i = new Intent(Intent.ACTION_SEND);
					i.setType("message/rfc822");
					i.putExtra(Intent.EXTRA_SUBJECT, "Mobstar");
					i.putExtra(Intent.EXTRA_TEXT, ShareText);
					try {
						startActivity(Intent.createChooser(i, "Send mail..."));
                        AdWordsManager.getInstance().sendSharedEntryEvent();
					} catch (android.content.ActivityNotFoundException ex) {
						Toast.makeText(ShareActivity.this,  getString(R.string.there_are_no_email_clients_installed), Toast.LENGTH_SHORT).show();
					}

				}

			}
		});

		btnAddToGPlus = (TextView) findViewById(R.id.btnAddToGPlus);
		btnAddToGPlus.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isTalent && pngUri!=null){
					Intent shareIntent = ShareCompat.IntentBuilder.from(ShareActivity.this)
					         .setText(ShareText)
					         .setType("image/jpeg")
					         .setStream(pngUri)
					         .getIntent()
					         .setPackage("com.google.android.apps.plus");
					startActivityForResult(shareIntent, 0);
                    AdWordsManager.getInstance().sendSharedEntryEvent();
				}
				else {
					Intent shareIntent = new PlusShare.Builder(ShareActivity.this).setType("text/plain")
							.setText(ShareText + "\n#mobstar").getIntent();
					startActivityForResult(shareIntent, 0);
                    AdWordsManager.getInstance().sendSharedEntryEvent();
				}
				
				
				
			}
		});

		btnFBPost = (TextView) findViewById(R.id.btnFBPost);
		btnFBPost.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Utility.ShowProgressDialog(mContext,  getString(R.string.uploading_your_post) + "...");

				onClickLogin();
			}
		});
	}

	private void onClickLogin() {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
		} else {
			Session.openActiveSession(this, true, statusCallback);
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {
		@Override
		public void call(Session session, SessionState state, Exception exception) {

			if (exception != null) {

				exception.printStackTrace();
				new AlertDialog.Builder(mContext).setTitle(R.string.app_name).setMessage(exception.getMessage()).setPositiveButton("OK", null).show();

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Utility.HideDialog(mContext);
						return;
					}
				});
			} else {

				if (session.isOpened()) {

					List<String> permissions = session.getPermissions();
					if (!isSubsetOf(PERMISSIONS, permissions)) {

						Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest((Activity) mContext, PERMISSIONS);
						session.requestNewPublishPermissions(newPermissionsRequest);
						return;
					}

					if (session.getPermissions().contains("publish_actions")) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								Bitmap bitmap=null;	
								if(isTalent){
									bitmap=Utility.getBitmapFromURL(UserImg);
								}
								else {
									if(entryPojo.getType().equalsIgnoreCase("image")){
										bitmap=Utility.getBitmapFromURL(entryPojo.getImageLink());
									}
									else if(entryPojo.getType().equalsIgnoreCase("audio")){
										bitmap=Utility.getBitmapFromURL(entryPojo.getImageLink());
									}
									else if(entryPojo.getType().equalsIgnoreCase("video")){
										bitmap=Utility.getBitmapFromURL(entryPojo.getVideoThumb());
									}
								}
								
								if(bitmap!=null) {
									Request request = Request.newUploadPhotoRequest(Session.getActiveSession(),bitmap, new Request.Callback() {

										@Override
										public void onCompleted(final Response response) {
											runOnUiThread(new Runnable() {

												@Override
												public void run() {
													Utility.HideDialog(mContext);
												}
											});

											FacebookRequestError error = response.getError();
											if (error != null) {

												new AlertDialog.Builder(mContext).setTitle(R.string.app_name).setMessage(error.getErrorMessage()).setPositiveButton("OK", null).show();
											} else {

												new AlertDialog.Builder(mContext).setTitle(R.string.app_name)
														.setMessage(getString(R.string.post_successfully_shared_on_your_wall)).setPositiveButton("OK", null).show();
                                                AdWordsManager.getInstance().sendSharedEntryEvent();
											}

										}
									});
									Bundle parameters = request.getParameters(); // <-- THIS IS IMPORTANT
									parameters.putString("name",ShareText);//also try key message
									request.setParameters(parameters);

									request.executeAsync();
								}
							}
						});

					}
				}
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onStop() {
		super.onStop();

		Session.getActiveSession().removeCallback(statusCallback);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		super.onDestroy();

		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
	}


	public void showToast(final String message) {
		if (!isFinishing()) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public void showError(final String message) {

		if (!isFinishing()) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
					Utility.HideDialog(mContext);
				}
			});
		}

	}

	
	private Uri saveImageToSD() {
	    /*--- this method will save your downloaded image to SD card ---*/

	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	    /*--- you can select your preferred CompressFormat and quality. 
	     * I'm going to use JPEG and 100% quality ---*/
	    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
	    /*--- create a new file on SD card ---*/
	    picFile = new File(Environment.getExternalStorageDirectory()
	            + File.separator + "myDownloadedImage.png");
//	    infoLog("file path"+file.getAbsolutePath().toString());
	    try {
	    	picFile.createNewFile();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    /*--- create a new FileOutputStream and write bytes to file ---*/
	    try {
	        fos = new FileOutputStream(picFile);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    try {
	        fos.write(bytes.toByteArray());
	        fos.close();
//	        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
	        if (!picFile.exists())
	        	picFile.mkdirs();
           
//           File pngFile = new File(file, "jetsam.png");
         //Save file encoded as PNG
          pngUri = Uri.fromFile(picFile);
	        return pngUri;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return null;

	}
}
