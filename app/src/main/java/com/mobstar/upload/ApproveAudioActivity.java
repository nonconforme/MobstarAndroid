package com.mobstar.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.utils.Utility;

public class ApproveAudioActivity extends Activity {

	Context mContext;

	String sAudioPath;

	Button btnRetake, btnApprove;

	ImageView btnPausePlay;
	MediaPlayer mediaPlayer;

	ProgressBar progressMediaPlayer;

	Handler handler = new Handler();

	ImageView btnBack;

	LinearLayout layoutSelectFile, layoutApproveAudio;

	ImageView btnSelectFile;

	Typeface typefaceBtn;

	String categoryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approve_audio);

		mContext = ApproveAudioActivity.this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sAudioPath = extras.getString("audio_path");

			if(extras.containsKey("categoryId")) {
				categoryId=extras.getString("categoryId");
			}
		}

		InitControls();

		Utility.SendDataToGA("ApproveAudio Screen", ApproveAudioActivity.this);

	}

	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		btnSelectFile = (ImageView) findViewById(R.id.btnSelectFile);
		btnSelectFile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), 26);
			}
		});

		layoutSelectFile = (LinearLayout) findViewById(R.id.layoutSelectFile);
		layoutApproveAudio = (LinearLayout) findViewById(R.id.layoutApproveAudio);

		layoutApproveAudio.setVisibility(View.VISIBLE);
		layoutSelectFile.setVisibility(View.GONE);

		progressMediaPlayer = (ProgressBar) findViewById(R.id.progressMediaPlayer);
		progressMediaPlayer.setProgress(0);

		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutApproveAudio.setVisibility(View.VISIBLE);
				layoutSelectFile.setVisibility(View.GONE);
			}
		});

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub

				if (isFinishing()) {
					try {
						mp.stop();
					} catch (Exception e) {
						// TODO: handle exception
					}

				}
			}
		});
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer arg0) {
				// TODO Auto-generated method stub

				btnPausePlay.setImageResource(R.drawable.btn_play);
				btnPausePlay.setSelected(true);
			}
		});

		try {
			mediaPlayer.setDataSource(sAudioPath);
			mediaPlayer.prepare();
		} catch (Exception e) {
			// TODO: handle exception
		}

		btnPausePlay = (ImageView) findViewById(R.id.btnPausePlay);
		btnPausePlay.setSelected(true);
		btnPausePlay.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				if (btnPausePlay.isSelected()) {

					if (mediaPlayer != null) {
						PlayAudio();
					}
					btnPausePlay.setImageResource(R.drawable.btn_pause);
					btnPausePlay.setSelected(false);

				} else {

					if (mediaPlayer.isPlaying()) {
						mediaPlayer.pause();
					}

					btnPausePlay.setImageResource(R.drawable.btn_play);
					btnPausePlay.setSelected(true);
				}
			}
		});

		btnRetake = (Button) findViewById(R.id.btnRetake);
		btnRetake.setTypeface(typefaceBtn);
		btnRetake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, RecordAudioActivity.class);
				intent.putExtra("categoryId",categoryId);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		btnApprove = (Button) findViewById(R.id.btnApprove);
		btnApprove.setTypeface(typefaceBtn);
		btnApprove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layoutApproveAudio.setVisibility(View.GONE);
				layoutSelectFile.setVisibility(View.VISIBLE);

				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {

						mediaPlayer.pause();

						btnPausePlay.setBackgroundResource(R.drawable.btn_play);
						btnPausePlay.setSelected(true);

					}
				}
			}
		});
	}

	void PlayAudio() {
		new Thread() {

			public void run() {

				try {

					mediaPlayer.start();
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub

							progressMediaPlayer.setMax(mediaPlayer.getDuration() / 1000);

						}
					});

					mediaPlayerProgressUpdater();

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == 26) {
				try {

					String selectedImagePath = null;

					try {
						Uri selectedImageUri = data.getData();

						if (selectedImageUri.toString().contains("///")) {
							selectedImagePath = selectedImageUri.toString();
							String[] array = selectedImagePath.split("///");
							selectedImagePath = array[1];
						} else {
							selectedImagePath = Utility.getPath(mContext, selectedImageUri);
						}
					} catch (Exception e) {
						// TODO: handle exception
					}

					Intent intent = new Intent(mContext, UploadFileActivity.class);
					intent.putExtra("file1", sAudioPath);
					intent.putExtra("file2", selectedImagePath);
					intent.putExtra("categoryId",categoryId);
					intent.putExtra("type", "audio");
					startActivityForResult(intent, 27);
					finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

				} catch (Exception e) {
					Toast.makeText(ApproveAudioActivity.this, "Unknown Error", Toast.LENGTH_SHORT).show();
				}

			}else if (requestCode == 27) {
				onBackPressed();
			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mediaPlayer != null) {
			mediaPlayer.stop();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
