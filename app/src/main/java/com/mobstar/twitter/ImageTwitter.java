package com.mobstar.twitter;

import java.io.File;

import twitter4j.User;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.twitter.TwitterApp.TwDialogListener;
import com.mobstar.utils.Constant;

public class ImageTwitter {
	private TwitterApp mTwitter;
	private static final String CONSUMER_KEY = "AU6O7GQ3y898MK5wW0MubJrS8";
	private static final String CONSUMER_SECRET = "rskJIuRaMPsvODBLx5rup57etMf0edMQVtGA9a3fvT0mO9scbu";

	public OnCompleteListener onCompleteListener;
	

	private enum FROM {
		TWITTER_POST, TWITTER_LOGIN
	};

	private enum MESSAGE {
		SUCCESS, DUPLICATE, FAILED, CANCELLED
	};

	public User mUser;
	Activity activity;
	String postString;
	boolean authOnly = false;
	private File imgFile;
	
	@SuppressLint("NewApi")
	public ImageTwitter(Activity activity, boolean authOnly, String postString,File file) {
		// TODO Auto-generated method stub
		if (android.os.Build.VERSION.SDK_INT >= 10) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		this.postString = postString;
		if(file!=null){
			this.imgFile=file;
		}
		this.activity = activity;
		this.authOnly = authOnly;
		mTwitter = new TwitterApp(activity, CONSUMER_KEY, CONSUMER_SECRET);
		mTwitter.setListener(mTwLoginDialogListener);
	}
	

	public void send() {
		mTwitter.resetAccessToken();
		mTwitter.authorize();
	}

	private void postAsToast(FROM twitterPost, MESSAGE success) {
		switch (twitterPost) {
		case TWITTER_LOGIN:
			switch (success) {
			case SUCCESS:
				Toast.makeText(activity, activity.getString(R.string.login_successful), Toast.LENGTH_LONG).show();
				break;
			case FAILED:
				Toast.makeText(activity, activity.getString(R.string.login_failed), Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		case TWITTER_POST:
			switch (success) {
			case SUCCESS:
				Toast.makeText(activity, activity.getString(R.string.posted_successflly), Toast.LENGTH_LONG).show();
				break;
			case FAILED:
				Toast.makeText(activity, activity.getString(R.string.posting_failed), Toast.LENGTH_LONG).show();
				break;
			case DUPLICATE:
				Toast.makeText(activity, activity.getString(R.string.posting_failed_duplicate), Toast.LENGTH_LONG).show();
			default:
				break;
			}
			break;
		}
	}

	private TwDialogListener mTwLoginDialogListener = new TwDialogListener() {

		public void onError(String value) {
			postAsToast(FROM.TWITTER_LOGIN, MESSAGE.FAILED);
//			Log.e(Constant.TAG, value);
			onCompleteListener.onComplete("Fail");
		}

		public void onComplete(String value, User user) {
			// Log.e(Constant.TAG, user.toString());
			mUser = user;
			try {
				if (!authOnly) {
					if(imgFile!=null){
						postImg(postString, imgFile);
					}
					else {
						post(postString);
					}
					
					postAsToast(FROM.TWITTER_POST, MESSAGE.SUCCESS);
				}

				onCompleteListener.onComplete("Success");
			} catch (Exception e) {
				if (e.getMessage().toString().contains("duplicate")) {
					postAsToast(FROM.TWITTER_POST, MESSAGE.DUPLICATE);
				}
				e.printStackTrace();
				onCompleteListener.onComplete("Fail");
			}
			mTwitter.resetAccessToken();
			// finish();
		}
	};

	public interface OnCompleteListener {
		public void onComplete(String action);
	}

	public void setOnCompleteListener(OnCompleteListener onCompleteListener) {

		this.onCompleteListener = onCompleteListener;
	}

	private void post(String postString) {

		try {
			mTwitter.updateStatus(postString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	private void postImg(String postString,File file) {

		try {
			mTwitter.uploadPic(file, postString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
