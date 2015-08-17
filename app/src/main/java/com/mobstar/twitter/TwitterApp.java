package com.mobstar.twitter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

import com.mobstar.R;

public class TwitterApp {
	private Twitter mTwitter;
	private TwitterSession mSession;
	private AccessToken mAccessToken;
	private CommonsHttpOAuthConsumer mHttpOauthConsumer;
	private OAuthProvider mHttpOauthprovider;
	private String mConsumerKey;
	private String mSecretKey;
	private ProgressDialog mProgressDlg;
	private TwDialogListener mListener;
	private Activity context;

	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String CALLBACK_URL = "twitterapp://connect";
	static String base_link_url = getMapUrl(51.03841, -114.01679, 100, 100);
	private static final String TWITTER_ACCESS_TOKEN_URL = "https://api.twitter.com/oauth/access_token";
	private static final String TWITTER_AUTHORZE_URL = "https://api.twitter.com/oauth/authorize";
	private static final String TWITTER_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String MESSAGE = "Hello Everyone...." + "<a href= " + base_link_url + "</a>";
	User user;

	public static String sOauthToken, sOauthTokenSecret, sOauthVerifier;

	public TwitterApp(Activity context, String consumerKey, String secretKey) {
		this.context = context;

		mTwitter = new TwitterFactory().getInstance();
		mSession = new TwitterSession(context);
		mProgressDlg = new ProgressDialog(context);

		mProgressDlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

		mConsumerKey = consumerKey;
		mSecretKey = secretKey;

		mHttpOauthConsumer = new CommonsHttpOAuthConsumer(mConsumerKey, mSecretKey);

		String request_url = TWITTER_REQUEST_URL;
		String access_token_url = TWITTER_ACCESS_TOKEN_URL;
		String authorize_url = TWITTER_AUTHORZE_URL;

		mHttpOauthprovider = new DefaultOAuthProvider(request_url, access_token_url, authorize_url);
		mAccessToken = mSession.getAccessToken();

		configureToken();
	}

	public void setListener(TwDialogListener listener) {
		mListener = listener;
	}

	private void configureToken() {
		if (mAccessToken != null) {
			mTwitter.setOAuthConsumer(mConsumerKey, mSecretKey);
			mTwitter.setOAuthAccessToken(mAccessToken);
		}
	}

	public boolean hasAccessToken() {
		return (mAccessToken == null) ? false : true;
	}

	public void resetAccessToken() {
		if (mAccessToken != null) {
			mSession.resetAccessToken();

			mAccessToken = null;
		}
	}

	public String getUsername() {
		return mSession.getUsername();
	}

	public void updateStatus(String status) throws Exception {
		try {
			mTwitter.updateStatus(status);
			/*
			 * File f = new File("/mnt/sdcard/74.jpg");
			 * mTwitter.updateProfileImage(f);
			 */
		} catch (TwitterException e) {
			throw e;
		}
	}

	public void uploadPic(File file, String message) throws Exception {
//		String accessToken = mAccessToken.getToken();
//		String accessTokenSecret = mAccessToken.getTokenSecret();
//		Configuration conf = new ConfigurationBuilder()
//	    .setOAuthConsumerKey( mConsumerKey )
//	    .setOAuthConsumerSecret( mSecretKey )
//	    .setOAuthAccessToken(accessToken)
//	    .setOAuthAccessTokenSecret(accessTokenSecret)
//	    .build();
//	Twitter twit = new TwitterFactory(conf).getInstance();
		try {
			StatusUpdate status = new StatusUpdate(message);
			status.setMedia(file);
			mTwitter.updateStatus(status);
		} catch (TwitterException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void authorize() {
		mProgressDlg.setMessage(context.getString(R.string.initializing));
		mProgressDlg.show();

		new Thread() {
			@Override
			public void run() {
				String authUrl = "";
				int what = 1;

				try {
					authUrl = mHttpOauthprovider.retrieveRequestToken(mHttpOauthConsumer, CALLBACK_URL);
					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0, authUrl));
			}
		}.start();
	}

	public void processToken(String callbackUrl) {
		mProgressDlg.setMessage(context.getString(R.string.finalizing));
		mProgressDlg.show();

		final String verifier = getVerifier(callbackUrl);

		sOauthVerifier = verifier;

		new Thread() {
			@Override
			public void run() {
				int what = 1;

				try {

					sOauthToken = mHttpOauthConsumer.getToken();
					sOauthTokenSecret = mHttpOauthConsumer.getTokenSecret();

					mHttpOauthprovider.retrieveAccessToken(mHttpOauthConsumer, verifier);

					mAccessToken = new AccessToken(mHttpOauthConsumer.getToken(), mHttpOauthConsumer.getTokenSecret());

					configureToken();

					user = mTwitter.verifyCredentials();

					mSession.storeAccessToken(mAccessToken, user.getName());

					what = 0;
				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private String getVerifier(String callbackUrl) {
		String verifier = "";

		try {
			callbackUrl = callbackUrl.replace("twitterapp", "http");

			URL url = new URL(callbackUrl);
			String query = url.getQuery();

			String array[] = query.split("&");

			for (String parameter : array) {
				String v[] = parameter.split("=");

				if (URLDecoder.decode(v[0]).equals(oauth.signpost.OAuth.OAUTH_VERIFIER)) {
					verifier = URLDecoder.decode(v[1]);
					break;
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return verifier;
	}

	private void showLoginDialog(String url) {
		final TwDialogListener listener = new TwDialogListener() {

			public void onComplete(String value, User user) {
				processToken(value);
			}

			public void onError(String value) {
				mListener.onError(context.getString(R.string.failed_opening_authorization_page));
			}
		};

		new TwitterDialog(context, url, listener).show();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgressDlg.dismiss();

			if (msg.what == 1) {
				if (msg.arg1 == 1)
					mListener.onError(context.getString(R.string.error_request_token));
				else
					mListener.onError(context.getString(R.string.error_access_token));
			} else {
				if (msg.arg1 == 1)
					showLoginDialog((String) msg.obj);
				else
					mListener.onComplete("", user);
			}
		}
	};

	public interface TwDialogListener {
		public void onComplete(String value, User user);

		public void onError(String value);
	}

	public static String getMapUrl(Double lat, Double lon, int width, int height) {
		final String coordPair = lat + "," + lon;
		return "http://maps.googleapis.com/maps/api/staticmap?" + "&zoom=16" + "&size=" + width + "x" + height + "&maptype=roadmap&sensor=true" + "&center=" + coordPair
				+ "&markers=color:black|" + coordPair;

	}
}
