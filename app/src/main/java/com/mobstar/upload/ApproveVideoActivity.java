package com.mobstar.upload;

import java.io.File;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

public class ApproveVideoActivity extends Activity {

	Context mContext;

	String sVideoPath;

	Button btnRetake, btnApprove;

	ImageView btnPausePlay;
	MediaPlayer mediaPlayer;

	ProgressBar progressMediaPlayer;

	Handler handler = new Handler();

	TextureView textureView;

	ImageView imageFrame;

	CustomSurfaceTextureListener surfaceTextureListener;

	private int mVideoHeight;
	private int mVideoWidth;

	Typeface typefaceBtn;
	
	String categoryId,subCat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approve_video);

		mContext = ApproveVideoActivity.this;

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sVideoPath = extras.getString("video_path");
			
			if(extras.containsKey("categoryId")) {
				categoryId=extras.getString("categoryId");
				subCat=extras.getString("subCat");
			}
		}

		calculateVideoSize();

		InitControls();
		
		Utility.SendDataToGA("ApproveVideo Screen", ApproveVideoActivity.this);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

		imageFrame = (ImageView) findViewById(R.id.imageFrame);
		imageFrame.setVisibility(View.GONE);

		// try {
		// MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		//
		// retriever.setDataSource(sVideoPath);
		// int timeInSeconds = 30;
		// Bitmap myBitmap = retriever.getFrameAtTime(timeInSeconds * 1000000,
		// MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
		// Log.v(Constant.TAG, "Bitmap Width" + myBitmap.getWidth() + " Height "
		// + myBitmap.getHeight());
		//
		// myBitmap = Bitmap.createScaledBitmap(myBitmap,
		// Utility.dpToPx(mContext, 300), Utility.dpToPx(mContext, 450), true);
		//
		// Log.v(Constant.TAG, "Bitmap Width" + myBitmap.getWidth() + " Height "
		// + myBitmap.getHeight());
		// imageFrame.setImageBitmap(myBitmap);
		//
		// } catch (Exception ex) {
		// Log.i(Constant.TAG, "MediaMetadataRetriever got exception:" + ex);
		// }

		surfaceTextureListener = new CustomSurfaceTextureListener();

		textureView = (TextureView) findViewById(R.id.textureView);

		textureView.setSurfaceTextureListener(surfaceTextureListener);
		if (textureView.isAvailable()) {
			surfaceTextureListener.onSurfaceTextureAvailable(textureView.getSurfaceTexture(), textureView.getWidth(), textureView.getHeight());
		}
		progressMediaPlayer = (ProgressBar) findViewById(R.id.progressMediaPlayer);
		progressMediaPlayer.setProgress(0);

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

		btnRetake = (Button) findViewById(R.id.btnRetake);
		btnRetake.setTypeface(typefaceBtn);
		btnRetake.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, RecordVideoActivity.class);
				intent.putExtra("categoryId",categoryId);
				if(subCat!=null && subCat.length()>0){
					intent.putExtra("subCat",subCat);
				}
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

				if (mediaPlayer != null) {
					if (mediaPlayer.isPlaying()) {

						mediaPlayer.pause();

						btnPausePlay.setBackgroundResource(R.drawable.btn_play);
						btnPausePlay.setSelected(true);

					}
				}

				Intent intent = new Intent(mContext, UploadFileActivity.class);
				intent.putExtra("file1", sVideoPath);
				intent.putExtra("type", "video");
				intent.putExtra("categoryId",categoryId);
				if(subCat!=null && subCat.length()>0){
					intent.putExtra("subCat",subCat);
				}
				startActivityForResult(intent, 26);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		});
	}

	private void calculateVideoSize() {
		try {

			MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
			metaRetriever.setDataSource(sVideoPath);
			String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
			String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
			mVideoHeight = Integer.parseInt(height);
			mVideoWidth = Integer.parseInt(width);


		} catch (NumberFormatException e) {
			e.printStackTrace();
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

	void PlayVideo(int position, final Surface surface) {

		new Thread() {

			public void run() {

				try {
					if (mediaPlayer != null) {
						mediaPlayer.reset();
					}

					File file = new File(sVideoPath);

					if (file.exists()) {
						mediaPlayer.setDataSource(sVideoPath);
						mediaPlayer.setSurface(surface);
						mediaPlayer.setScreenOnWhilePlaying(true);
						mediaPlayer.prepareAsync();

						// Play video when the media source is ready for
						// playback.
						mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
							@Override
							public void onPrepared(final MediaPlayer mediaPlayer) {

								mediaPlayer.start();

								btnPausePlay.setImageResource(R.drawable.btn_pause);
								btnPausePlay.setSelected(false);

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
			if (resultCode == Activity.RESULT_OK) {
				if (requestCode == 26) {
					onBackPressed();
				}
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
