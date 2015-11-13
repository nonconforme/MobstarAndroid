package com.mobstar.upload;

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
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobstar.R;
import com.mobstar.home.split.SplitActivity;
import com.mobstar.home.youtube.VideoData;
import com.mobstar.pojo.EntryPojo;
import com.mobstar.upload.rewrite.RecordVideoActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

import java.io.File;

public class ApproveVideoActivity extends Activity {

	public static final String APPROVE_SPLIT_VIDEO = "approve split video";


	private Context mContext;
	private String sVideoPath;
	private Button btnRetake, btnApprove;
	private ImageView btnPausePlay;
	private MediaPlayer mediaPlayer;
	private ProgressBar progressMediaPlayer;
	private Handler handler = new Handler();
	private TextureView textureView;
	private ImageView imageFrame;
	private CustomSurfaceTextureListener surfaceTextureListener;
	private int mVideoHeight;
	private int mVideoWidth;
	private Typeface typefaceBtn;
	private EntryPojo entry;
	private String categoryId,subCat;
	private boolean isSplitVideo = false;
	private VideoData youTubeVideoData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_approve_video);

		mContext = ApproveVideoActivity.this;

		getBundleExtra();

		calculateVideoSize();

		InitControls();
		
		Utility.SendDataToGA("ApproveVideo Screen", ApproveVideoActivity.this);
	}

	private void getBundleExtra(){
		final Bundle extras = getIntent().getExtras();
		if (extras != null) {
			sVideoPath = extras.getString("video_path");

			if(extras.containsKey("categoryId")) {
				categoryId = extras.getString("categoryId");
				subCat = extras.getString("subCat");
			}

			if (extras.containsKey(APPROVE_SPLIT_VIDEO))
				isSplitVideo = true;
			if (extras.containsKey(Constant.ENTRY))
				entry = (EntryPojo) extras.getSerializable(Constant.ENTRY);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	void InitControls() {

		typefaceBtn = Typeface.createFromAsset(getAssets(), "GOTHAM-BOLD.TTF");

//		imageFrame = (ImageView) findViewById(R.id.imageFrame);
//		imageFrame.setVisibility(View.GONE);

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
//		if (isSplitVideo){
			final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.texture_view_height));
			textureView.setLayoutParams(layoutParams);
//		}
//		if (isSplitVideo){
//			textureView.setLayoutParams(new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//					, getResources().getDimensionPixelSize(R.dimen.texture_view_height)));
//		}

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
				onClickBtnRetake();
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
                if (isSplitVideo) {
                    intent.putExtra(SplitActivity.ENTRY_SPLIT,entry);
                }

				if(subCat!=null && subCat.length()>0){
					intent.putExtra("subCat",subCat);
				}
				startActivityForResult(intent, 26);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

			}
		});
	}

	private void onClickBtnRetake(){
		removeFile(sVideoPath);
		// TODO Auto-generated method stub
		Intent intent;
		if (isSplitVideo){
			intent = new Intent(this, SplitActivity.class);
			intent.putExtra(SplitActivity.ENTRY_SPLIT, entry);
		}
		else {
			intent = new Intent(mContext, RecordVideoActivity.class);
			intent.putExtra("categoryId", categoryId);
			if (subCat != null && subCat.length() > 0) {
				intent.putExtra("subCat", subCat);
			}
		}
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	private void removeFile(final String filePath){
		final File file = new File(filePath);
		file.delete();
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
			playVideo(position, surface);

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

	private void playVideo(int position, final Surface surface) {

		new Thread() {

			public void run() {

				try {
					if (mediaPlayer != null) {
						mediaPlayer.reset();
					}

					final File file = new File(sVideoPath);

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

										progressMediaPlayer.setMax(mediaPlayer.getDuration() / 50);

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

					progressMediaPlayer.setProgress(mediaPlayer.getCurrentPosition() / 50);

				}
			}
		});

		if (mediaPlayer.isPlaying()) {

			Runnable notification = new Runnable() {
				public void run() {
					mediaPlayerProgressUpdater();
				}
			};
			handler.postDelayed(notification, 50);
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
