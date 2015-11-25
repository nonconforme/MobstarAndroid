package com.mobstar.help;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobstar.R;
import com.mobstar.api.ConnectCallback;
import com.mobstar.api.DownloadFileManager;
import com.mobstar.api.new_api_call.AuthCall;
import com.mobstar.api.new_api_model.response.WelcomeVideoResponse;
import com.mobstar.help.take_tour.TakeTourActivity;
import com.mobstar.login.who_to_follow.WhoToFollowActivity;
import com.mobstar.utils.UserPreference;
import com.mobstar.utils.Utility;

import java.io.File;

public class WelcomeVideoActivity extends Activity implements OnClickListener, OnCompletionListener, DownloadFileManager.DownloadCallback {

//	public static final String WELCOME_IS_CHECKED = "welcome_is_checked";

//	private String sErrorMessage;
	private Button btnTakeTour, btnEnterMobstar;
	private Typeface typefaceBtn;
	private CheckBox cbPrivacyPolicy, cbDonTShowAgain;
//	private String videoURL = "";
	private ImageView imageFrame;
	private TextureView textureView;
	private ProgressBar progressbar;
	private MediaPlayer mediaPlayer;
	private Handler handler = new Handler();
	private CustomSurfaceTextureListener surfaceTextureListener;
	private ImageView btnPausePlay;
	private ProgressBar progressMediaPlayer;

	private String videoFilePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_video);
		mediaPlayer = new MediaPlayer();
		surfaceTextureListener = new CustomSurfaceTextureListener();
		initControls();
		getWelcomeVideoUrl();
		Utility.SendDataToGA("WelcomeVideo Screen", WelcomeVideoActivity.this);

	}

	private void findViews(){
		progressMediaPlayer   = (ProgressBar) findViewById(R.id.progressMediaPlayer);
		cbDonTShowAgain       = (CheckBox) findViewById(R.id.cbDontShow);
		btnPausePlay          = (ImageView) findViewById(R.id.btnPausePlay);
		imageFrame            = (ImageView) findViewById(R.id.imageFrame);
		progressbar           = (ProgressBar) findViewById(R.id.progressbar);
		textureView           = (TextureView) findViewById(R.id.textureView);
		cbPrivacyPolicy       = (CheckBox) findViewById(R.id.cbPrivacyPolicy);
		btnTakeTour           = (Button) findViewById(R.id.btnTakeTour);
		btnEnterMobstar       = (Button) findViewById(R.id.btnEnterMobstar);

	}

	private void setListeners(){
		mediaPlayer.setOnCompletionListener(this);
		btnPausePlay.setOnClickListener(this);
		btnTakeTour.setOnClickListener(this);
		btnEnterMobstar.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnTakeTour:
				onTakeTour();
				break;
			case R.id.btnEnterMobstar:
				onEnterMobstar();
				break;
			case R.id.btnPausePlay:
				playPause();
				break;
		}
	}

	private void setTypeface(){
		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");
		cbPrivacyPolicy.setTypeface(typefaceBtn);
		btnTakeTour.setTypeface(typefaceBtn);
		btnEnterMobstar.setTypeface(typefaceBtn);
	}

	private void initControls() {
		findViews();
		setListeners();
		setTypeface();

		progressMediaPlayer.setProgress(0);
		btnPausePlay.setSelected(true);
		cbPrivacyPolicy.setChecked(false);

		String cbText = cbPrivacyPolicy.getText().toString();
		String text= cbText.substring(0,cbText.indexOf("\n")+1);
		Spannable word = new SpannableString(text);
		word.setSpan(new ForegroundColorSpan(Color.WHITE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.setText(word);

		int posNewLine=cbText.indexOf("\n");
		String text1=cbText.substring(posNewLine+1,cbText.indexOf("&"));

		Spannable word1 = new SpannableString(text1); 
		word1.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.append(word1);

		int pos=cbText.indexOf("&");

		String text3=cbText.substring(pos,pos+1);//white

		Spannable word3 = new SpannableString(text3); 
		word3.setSpan(new ForegroundColorSpan(Color.WHITE), 0, word3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.append(word3);

		String text4=cbText.substring(pos+1);
		Spannable word4 = new SpannableString(text4);
		word4.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, word4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.append(word4);

	}


	private void playPause(){
		if (btnPausePlay.isSelected()) {

			btnPausePlay.setImageResource(R.drawable.btn_pause);
			btnPausePlay.setSelected(false);
			if (mediaPlayer != null) {
				mediaPlayer.start();
			}
			mediaPlayerProgressUpdater();
		} else {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
			btnPausePlay.setImageResource(R.drawable.btn_play);
			btnPausePlay.setSelected(true);
		}
	}

	private void onTakeTour(){
		if (!cbPrivacyPolicy.isChecked())
			return;
		UserPreference.setWelcomeChecked(this, !cbDonTShowAgain.isChecked());
		startTakeTourActivity();
	}

	private void onEnterMobstar(){
		if (!cbPrivacyPolicy.isChecked())
			return;
		UserPreference.setWelcomeChecked(this, !cbDonTShowAgain.isChecked());
		startWhoToFollowActivity();

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		btnPausePlay.setImageResource(R.drawable.btn_play);
		btnPausePlay.setSelected(true);
	}

	private void getWelcomeVideoUrl(){
		AuthCall.getWelcomeVideo(this, new ConnectCallback<WelcomeVideoResponse>() {
			@Override
			public void onSuccess(WelcomeVideoResponse object) {
				downloadWelcomeVideo(object.getVideoUrl());
			}

			@Override
			public void onFailure(String error) {

			}

			@Override
			public void onServerError(com.mobstar.api.responce.Error error) {

			}
		});
	}

	private void downloadWelcomeVideo(final String videoUrl){
		if (videoUrl == null)
			return;
		final DownloadFileManager downloadFileManager = new DownloadFileManager(this, this);
		downloadFileManager.downloadFile(videoUrl, 0);
	}

	@Override
	public void onDownload(String filePath, int position) {
		videoFilePath = filePath;
		preparePlayer();
	}

	@Override
	public void onFailed() {

	}

	private void preparePlayer(){

		btnPausePlay.setImageResource(R.drawable.btn_pause);
		btnPausePlay.setSelected(false);
		progressbar.setVisibility(View.GONE);
		imageFrame.setImageResource(R.drawable.video_placeholder);
		textureView.setVisibility(View.GONE);
		imageFrame.setVisibility(View.VISIBLE);

		imageFrame.setImageResource(R.drawable.video_placeholder);
		textureView.setVisibility(View.VISIBLE);

		textureView.setSurfaceTextureListener(surfaceTextureListener);
		if (textureView.isAvailable()) {
			surfaceTextureListener.onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
		}

		imageFrame.setVisibility(View.GONE);
	}

//	class WelcomeCall extends Thread {
//
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//
//			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.WELCOME, preferences.getString("token", null));
//
//			// Log.v(Constant.TAG, "WelcomeCall response " + response);
//
//			if (response != null) {
//
//				try {
//
//					JSONObject jsonObject = new JSONObject(response);
//
//					if (jsonObject.has("error")) {
//						sErrorMessage = jsonObject.getString("error");
//					}
//
//					if (jsonObject.has("url")) {
//						videoURL = jsonObject.getString("url");
//					}
//
//					if (sErrorMessage != null && !sErrorMessage.equals("")) {
//						handlerWelcome.sendEmptyMessage(0);
//					} else {
//						handlerWelcome.sendEmptyMessage(1);
//					}
//
//				} catch (Exception exception) {
//					// TODO: handle exception
//					exception.printStackTrace();
//					handlerWelcome.sendEmptyMessage(0);
//				}
//			}
//
//		}
//	}

//	Handler handlerWelcome = new Handler() {
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//
//			final String sFileName;
//
//			sFileName = Utility.GetFileNameFromURl(videoURL);
//
//			String path = Utility.getCurrentDirectory(WelcomeVideoActivity.this);
//
////			File folder = new File(Environment.getExternalStorageDirectory() + "/.mobstar");
//
//			File folder = new File(path);
//
//			if (!folder.exists()) {
//				folder.mkdir();
//			}
//
//			File file = new File(path+ sFileName);
//
//			if (!file.exists()) {
//				// Log.v(Constant.TAG, "Download video " + videoURL);
//
//				AsyncHttpClient client = new AsyncHttpClient();
//				final int DEFAULT_TIMEOUT = 60 * 1000;
//				client.setTimeout(DEFAULT_TIMEOUT);
//				client.get(videoURL, new FileAsyncHttpResponseHandler(file) {
//
//					@Override
//					public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
//						// TODO Auto-generated method stub
//
//					}
//
//					@Override
//					public void onSuccess(int arg0, Header[] arg1, File file) {
//						// TODO Auto-generated method stub
//						// Log.v(Constant.TAG,
//						// "onSuccess Video File  downloaded");
//
//						btnPausePlay.setImageResource(R.drawable.btn_pause);
//						btnPausePlay.setSelected(false);
//
//						progressbar.setVisibility(View.GONE);
//
//						imageFrame.setImageResource(R.drawable.video_placeholder);
//						textureView.setVisibility(View.GONE);
//						imageFrame.setVisibility(View.VISIBLE);
//
//						imageFrame.setImageResource(R.drawable.video_placeholder);
//						textureView.setVisibility(View.VISIBLE);
//
//						textureView.setSurfaceTextureListener(surfaceTextureListener);
//						if (textureView.isAvailable()) {
//							surfaceTextureListener.onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
//						}
//
//						imageFrame.setVisibility(View.GONE);
//					}
//
//
//				});
//
//			} else {
//
//				btnPausePlay.setImageResource(R.drawable.btn_pause);
//				btnPausePlay.setSelected(false);
//
////				Log.v(Constant.TAG, "Exist " + videoURL);
//
//				progressbar.setVisibility(View.GONE);
//
//				imageFrame.setImageResource(R.drawable.video_placeholder);
//				textureView.setVisibility(View.GONE);
//				imageFrame.setVisibility(View.VISIBLE);
//
//				imageFrame.setImageResource(R.drawable.video_placeholder);
//				textureView.setVisibility(View.VISIBLE);
//
//				textureView.setSurfaceTextureListener(surfaceTextureListener);
//				if (textureView.isAvailable()) {
//					surfaceTextureListener.onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
//				}
//
//				imageFrame.setVisibility(View.GONE);
//			}
//		}
//	};

	private void playVideo(final Surface surface) {

		new Thread() {

			public void run() {

				try {
					if (mediaPlayer != null) {
						mediaPlayer.reset();
					}

//					final String sFileName = Utility.GetFileNameFromURl(videoURL);

					// Log.v(Constant.TAG, "sFileName " +
					// Environment.getExternalStorageDirectory() +
					// "/.mobstar/" + sFileName);

//					String path = Utility.getCurrentDirectory(WelcomeVideoActivity.this);
					
//					File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);


					if (new File(videoFilePath).exists()) {
//						mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);
						
						mediaPlayer.setDataSource(videoFilePath);
						
						mediaPlayer.setSurface(surface);
						// Play video when the media source is ready for
						// playback.
						mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
							@Override
							public void onPrepared(final MediaPlayer mediaPlayer) {
								mediaPlayer.start();
								mediaPlayerProgressUpdater();

								runOnUiThread(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub

										progressMediaPlayer.setMax(mediaPlayer.getDuration() / 1000);

									}
								});
							}
						});
						mediaPlayer.prepareAsync();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}.start();
	}

	private void mediaPlayerProgressUpdater() {

		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				if (mediaPlayer.isPlaying()) {

					progressMediaPlayer.setProgress(mediaPlayer.getCurrentPosition() / 1000);

				}
			}
		});

		if (mediaPlayer.isPlaying()) {

			final Runnable notification = new Runnable() {
				public void run() {
					mediaPlayerProgressUpdater();
				}
			};
			handler.postDelayed(notification, 1000);
		}

	}

	public class CustomSurfaceTextureListener implements SurfaceTextureListener {

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int arg1, int arg2) {

			final Surface surface = new Surface(surfaceTexture);
			playVideo(surface);

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
			return false;
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int arg1, int arg2) {

		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture texture) {

		}

	}

	public void onPause() {
		super.onPause();
		if (mediaPlayer != null) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
				btnPausePlay.setBackgroundResource(R.drawable.btn_play);
				btnPausePlay.setSelected(true);
			}
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}


	private void startTakeTourActivity(){
		final Intent intent = new Intent(this, TakeTourActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}

	private void startWhoToFollowActivity(){
		final Intent intent = new Intent(this, WhoToFollowActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}
}
