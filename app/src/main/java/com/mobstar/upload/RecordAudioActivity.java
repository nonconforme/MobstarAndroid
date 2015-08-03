package com.mobstar.upload;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

public class RecordAudioActivity extends Activity implements OnClickListener {

	Context mContext;

	int currentCount = 3;

	LinearLayout layoutMain, layoutGetReady;
	boolean isgetReadyDone = false;
	TextView textCount, textRecordSecond;
	ImageView btnRecord;

	ImageView imageFillRecord;

	CountDownTimer recordTimer, animationTimer;

	private MediaRecorder recorder = null;

	String sFilepath,categoryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_audio);

		mContext = RecordAudioActivity.this;
		
		Bundle b=getIntent().getExtras();
		if(b!=null) {
			if(b.containsKey("categoryId")) {
				categoryId=b.getString("categoryId");
			}
		}

		InitControls();
		
		Utility.SendDataToGA("RecordAudio Screen", RecordAudioActivity.this);

	}

	void InitControls() {
		layoutMain = (LinearLayout) findViewById(R.id.layoutMain);
		layoutMain.setVisibility(View.VISIBLE);

		layoutGetReady = (LinearLayout) findViewById(R.id.layoutGetReady);
		layoutGetReady.setVisibility(View.GONE);

		btnRecord = (ImageView) findViewById(R.id.btnRecord);
		btnRecord.setOnClickListener(this);

		textRecordSecond = (TextView) findViewById(R.id.textRecordSecond);
		textRecordSecond.setVisibility(View.INVISIBLE);

		textCount = (TextView) findViewById(R.id.textCount);

		imageFillRecord = (ImageView) findViewById(R.id.imageFillRecord);
		ClipDrawable drawable = (ClipDrawable) imageFillRecord.getBackground();
		drawable.setLevel(0);

		recordTimer = new CountDownTimer(19000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

//				Log.v(Constant.TAG, "currentCount " + currentCount);

				if (currentCount == 1) {

					ClipDrawable drawable = (ClipDrawable) imageFillRecord.getBackground();
					drawable.setLevel(10000);

					textRecordSecond.setText("0");

					stopRecording();

					Intent intent = new Intent(mContext, ApproveAudioActivity.class);
					intent.putExtra("audio_path", sFilepath);
					intent.putExtra("categoryId",categoryId);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//					onBackPressed();

				} else if ((millisUntilFinished / 1000) != 18) {

					currentCount--;

					textRecordSecond.setText(currentCount + "");
				}

			}

			@Override
			public void onFinish() {

			}
		};

		animationTimer = new CountDownTimer(30000, 10) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub
				ClipDrawable drawable = (ClipDrawable) imageFillRecord.getBackground();
				if (currentCount == 0 || drawable.getLevel() > 9980) {

				} else {
					drawable.setLevel(drawable.getLevel() + 12);
				}

				// Log.v(Constant.TAG, "Level " + drawable.getLevel());
			}

			@Override
			public void onFinish() {

			}
		};
	}

	private void startRecording() {

		recorder = new MediaRecorder();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		sFilepath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_AUDIO,mContext).getAbsolutePath();
		recorder.setOutputFile(sFilepath);

		try {
			recorder.prepare();
			recorder.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {

		if (recordTimer != null) {
			recordTimer.cancel();
		}

		if (animationTimer != null) {
			animationTimer.cancel();
		}

		if (null != recorder) {
			recorder.stop();
			recorder.reset();
			recorder.release();

			recorder = null;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		stopRecording();
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub

		if (view.equals(btnRecord)) {

			if (!isgetReadyDone) {

				currentCount = 3;

				layoutGetReady.setVisibility(View.VISIBLE);
				layoutMain.setVisibility(View.GONE);

				isgetReadyDone = true;
				textCount.setText(currentCount + "");

				CountDownTimer countDownTimer = new CountDownTimer(5000, 1000) {

					@Override
					public void onTick(long millisUntilFinished) {
						// TODO Auto-generated method stub

						// Log.v(Constant.TAG, "currentCount " + currentCount);

						if (currentCount == 1) {

							layoutGetReady.setVisibility(View.GONE);
							layoutMain.setVisibility(View.VISIBLE);
							textRecordSecond.setVisibility(View.VISIBLE);
							textRecordSecond.setText("15");

							currentCount = 15;

							cancel();

							startRecording();
							animationTimer.start();
							recordTimer.start();

						} else if ((millisUntilFinished / 1000) != 4) {
							currentCount--;

							textCount.setText(currentCount + "");
						}

					}

					@Override
					public void onFinish() {

					}
				};
				countDownTimer.start();

			} else {

				stopRecording();

				Intent intent = new Intent(mContext, ApproveAudioActivity.class);
				intent.putExtra("audio_path", sFilepath);
				intent.putExtra("categoryId",categoryId);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//				onBackPressed();

			}
		}
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
