package com.mobstar.help;

import java.io.File;

import org.apache.http.Header;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.mobstar.R;
import com.mobstar.login.WhoToFollowActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.JSONParser;
import com.mobstar.utils.Utility;

public class WelcomeVideoActivity extends Activity implements OnClickListener {

	public static final String WELCOME_IS_CHECKED = "welcome_is_checked";

	private String sErrorMessage;
	private Context mContext;
	private SharedPreferences preferences;
	private Button btnTakeTour, btnEnterMobstar;
	private Typeface typefaceBtn;
	private CheckBox cbPrivacyPolicy, cbDonTShowAgain;
	private String videoURL = "";
	private ImageView imageFrame;
	private TextureView textureView;
	private ProgressBar progressbar;
	private MediaPlayer mediaPlayer;
	private Handler handler = new Handler();
	private CustomSurfaceTextureListener surfaceTextureListener;
	private ImageView btnPausePlay;
	private ProgressBar progressMediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome_video);

		mContext = WelcomeVideoActivity.this;

		preferences = getSharedPreferences("mobstar_pref", Activity.MODE_PRIVATE);

		mediaPlayer = new MediaPlayer();

		surfaceTextureListener = new CustomSurfaceTextureListener();

		InitControls();

		Utility.SendDataToGA("WelcomeVideo Screen", WelcomeVideoActivity.this);

	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub

				btnPausePlay.setImageResource(R.drawable.btn_play);
				btnPausePlay.setSelected(true);
			}
		});
		progressMediaPlayer = (ProgressBar) findViewById(R.id.progressMediaPlayer);
		cbDonTShowAgain = (CheckBox) findViewById(R.id.cbDontShow);
		progressMediaPlayer.setProgress(0);
		btnPausePlay = (ImageView) findViewById(R.id.btnPausePlay);
		btnPausePlay.setSelected(true);
		btnPausePlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
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
		});

		imageFrame = (ImageView) findViewById(R.id.imageFrame);
		progressbar = (ProgressBar) findViewById(R.id.progressbar);
		textureView = (TextureView) findViewById(R.id.textureView);

		//added by khyati
		cbPrivacyPolicy = (CheckBox) findViewById(R.id.cbPrivacyPolicy);
		cbPrivacyPolicy.setTypeface(typefaceBtn);

		cbPrivacyPolicy.setChecked(false);

		String cbText = cbPrivacyPolicy.getText().toString();
		String text= cbText.substring(0,cbText.indexOf("\n")+1);
//		Log.d("text", text);
		Spannable word = new SpannableString(text); 
		word.setSpan(new ForegroundColorSpan(Color.WHITE), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.setText(word);

		int posNewLine=cbText.indexOf("\n");
		String text1=cbText.substring(posNewLine+1,cbText.indexOf("&"));
//		Log.d("text1", text1);

		Spannable word1 = new SpannableString(text1); 
		word1.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, word1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.append(word1);

		int pos=cbText.indexOf("&");

		String text3=cbText.substring(pos,pos+1);//white
//		Log.d("text3", text3);

		Spannable word3 = new SpannableString(text3); 
		word3.setSpan(new ForegroundColorSpan(Color.WHITE), 0, word3.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.append(word3);

		String text4=cbText.substring(pos+1);
//		Log.d("text4", text4);
		Spannable word4 = new SpannableString(text4); 
		word4.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, word4.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		cbPrivacyPolicy.append(word4);

		//		****

		btnTakeTour = (Button) findViewById(R.id.btnTakeTour);
		btnTakeTour.setTypeface(typefaceBtn);
		btnEnterMobstar = (Button) findViewById(R.id.btnEnterMobstar);
		btnEnterMobstar.setTypeface(typefaceBtn);

		btnTakeTour.setOnClickListener(this);
		btnEnterMobstar.setOnClickListener(this);

		if (Utility.isNetworkAvailable(mContext)) {

			new WelcomeCall().start();

		} else {

			Toast.makeText(mContext, getString(R.string.no_internet_access), Toast.LENGTH_SHORT).show();

		}
	}

	class WelcomeCall extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String response = JSONParser.getRequest(Constant.SERVER_URL + Constant.WELCOME, preferences.getString("token", null));

			// Log.v(Constant.TAG, "WelcomeCall response " + response);

			if (response != null) {

				try {

					JSONObject jsonObject = new JSONObject(response);

					if (jsonObject.has("error")) {
						sErrorMessage = jsonObject.getString("error");
					}

					if (jsonObject.has("url")) {
						videoURL = jsonObject.getString("url");
					}

					if (sErrorMessage != null && !sErrorMessage.equals("")) {
						handlerWelcome.sendEmptyMessage(0);
					} else {
						handlerWelcome.sendEmptyMessage(1);
					}

				} catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
					handlerWelcome.sendEmptyMessage(0);
				}
			}

		}
	}

	Handler handlerWelcome = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			final String sFileName;

			sFileName = Utility.GetFileNameFromURl(videoURL);

			String path = Environment.getExternalStorageDirectory().getPath()
					+ "/Android/data/" + mContext.getPackageName() +"/";
			
//			File folder = new File(Environment.getExternalStorageDirectory() + "/.mobstar");
			
			File folder = new File(path);
			
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			File file = new File(path+ sFileName);

			if (!file.exists()) {
				// Log.v(Constant.TAG, "Download video " + videoURL);

				AsyncHttpClient client = new AsyncHttpClient();
				final int DEFAULT_TIMEOUT = 60 * 1000;
				client.setTimeout(DEFAULT_TIMEOUT);
				client.get(videoURL, new FileAsyncHttpResponseHandler(file) {

					@Override
					public void onFailure(int arg0, Header[] arg1, Throwable arg2, File file) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1, File file) {
						// TODO Auto-generated method stub
						// Log.v(Constant.TAG,
						// "onSuccess Video File  downloaded");

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
					

				});

				// Ion.with(mContext).load(videoURL).write(file).setCallback(new
				// FutureCallback<File>() {
				// @Override
				// public void onCompleted(Exception e, File file) {
				// if (file != null && e == null) {
				//
				// btnPausePlay.setImageResource(R.drawable.btn_pause);
				// btnPausePlay.setSelected(false);
				//
				// progressbar.setVisibility(View.GONE);
				//
				// imageFrame.setImageResource(R.drawable.video_placeholder);
				// textureView.setVisibility(View.GONE);
				// imageFrame.setVisibility(View.VISIBLE);
				//
				// imageFrame.setImageResource(R.drawable.video_placeholder);
				// textureView.setVisibility(View.VISIBLE);
				//
				// textureView.setSurfaceTextureListener(surfaceTextureListener);
				// if (textureView.isAvailable()) {
				// surfaceTextureListener.onSurfaceTextureAvailable(textureView.getSurfaceTexture(),
				// textureView.getWidth(), textureView.getHeight());
				// }
				//
				// imageFrame.setVisibility(View.GONE);
				// }
				//
				// }
				// });
			} else {

				btnPausePlay.setImageResource(R.drawable.btn_pause);
				btnPausePlay.setSelected(false);

//				Log.v(Constant.TAG, "Exist " + videoURL);

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
		}
	};

	void PlayVideo(int position, final Surface surface) {

		new Thread() {

			public void run() {

				try {
					if (mediaPlayer != null) {
						mediaPlayer.reset();
					}

					final String sFileName = Utility.GetFileNameFromURl(videoURL);

					// Log.v(Constant.TAG, "sFileName " +
					// Environment.getExternalStorageDirectory() +
					// "/.mobstar/" + sFileName);

					String path = Environment.getExternalStorageDirectory().getPath()
							+ "/Android/data/" + mContext.getPackageName() +"/";
					
//					File file = new File(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);
					
					File file = new File(path + sFileName);

					if (file.exists()) {
//						mediaPlayer.setDataSource(Environment.getExternalStorageDirectory() + "/.mobstar/" + sFileName);
						
						mediaPlayer.setDataSource(path + sFileName);
						
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

			Runnable notification = new Runnable() {
				public void run() {
					mediaPlayerProgressUpdater();
				}
			};
			handler.postDelayed(notification, 1000);
		}

	}

	public class CustomSurfaceTextureListener implements SurfaceTextureListener {

		int position;

		@Override
		public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int arg1, int arg2) {
			// TODO Auto-generated method stub

			// Log.v(Constant.TAG, "Play Video onSurfaceTextureAvailable ");
			Surface surface = new Surface(surfaceTexture);
			PlayVideo(position, surface);

		}

		@Override
		public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
			// TODO Auto-generated method stub

			return false;
		}

		@Override
		public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSurfaceTextureUpdated(SurfaceTexture texture) {
			// TODO Auto-generated method stub

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

	@Override
	public void onClick(View v) {
		if (!cbPrivacyPolicy.isChecked())
			return;
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		if (v.equals(btnTakeTour)) {
			intent = new Intent(mContext, TakeTourActivity.class);
		} else if (v.equals(btnEnterMobstar)) {
			intent = new Intent(mContext, WhoToFollowActivity.class);
		}
		preferences.edit().putBoolean(WELCOME_IS_CHECKED, cbDonTShowAgain.isChecked()).apply();
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		finish();
	}
}
