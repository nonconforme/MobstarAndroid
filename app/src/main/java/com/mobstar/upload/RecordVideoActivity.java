package com.mobstar.upload;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.utils.Utility;

public class RecordVideoActivity extends Activity {

	Context mContext;

	private Camera mCamera;
	private CameraPreview mPreview;

	ImageView btnRecord, btnFlash, btnChangeCamera;

	boolean isFlashOn = false;
	int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
	boolean isFrontCameraAvailable = false;

	private boolean isRecording = false;

	private MediaRecorder mMediaRecorder;

	LinearLayout layoutCameraOption;
	TextView textRecordSecond;

	int currentCount = 15;
	CountDownTimer recordTimer;

	String sFilepath;

	int desiredwidth = 480;
	int desiredheight = 720;
	
	List<Size> videosizes;

	private final int cMaxRecordDurationInMs = 30099;

	private final long cMaxFileSizeInBytes = 8000000;
	
	private final long cMaxFileSizeInBytesProfile = 52428800; //50MB

	String categoryId,subCat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.screenBrightness = 1.0f;
		this.getWindow().setAttributes(lp);

		setContentView(R.layout.activity_record_video);

		mContext = RecordVideoActivity.this;

		Bundle b=getIntent().getExtras();
		if(b!=null) {
			if(b.containsKey("categoryId")) {
				categoryId=b.getString("categoryId");
				subCat=b.getString("subCat");
			}
		}

		InitControls();

		Utility.SendDataToGA("RecordVideo Screen", RecordVideoActivity.this);

	}

	void InitControls() {

		layoutCameraOption = (LinearLayout) findViewById(R.id.layoutCameraOption);
		layoutCameraOption.setVisibility(View.VISIBLE);

		textRecordSecond = (TextView) findViewById(R.id.textRecordSecond);
		textRecordSecond.setVisibility(View.GONE);

		textRecordSecond.setText(currentCount + "");

		recordTimer = new CountDownTimer(19000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				// TODO Auto-generated method stub

				// Log.v(Constant.TAG, "currentCount " + currentCount);

				if (currentCount == 1) {

					currentCount = 0;

					mPreview.RecordVideo();
					textRecordSecond.setText("0");

				} else if ((millisUntilFinished / 1000) != 18) {

					if (currentCount != 0) {
						currentCount--;
						textRecordSecond.setText(currentCount + "");
					}

				}

			}

			@Override
			public void onFinish() {

			}
		};

		if (checkCameraHardware(mContext)) {

			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
					isFrontCameraAvailable = true;
				}
			}

			if (isFrontCameraAvailable) {
				currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
			}

			// Create an instance of Camera
			mCamera = getCameraInstance(currentCameraId);

			// Create our Preview view and set it as the content of our
			// activity.
			mPreview = new CameraPreview(this);
			mPreview.setCamera(mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);

			btnRecord = (ImageView) findViewById(R.id.btnRecord);
			btnRecord.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an image from the camera
					mPreview.RecordVideo();

				}
			});

			btnChangeCamera = (ImageView) findViewById(R.id.btnChangeCamera);
			btnChangeCamera.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (isFrontCameraAvailable) {
						onCameraChange();
					}
				}
			});

			btnFlash = (ImageView) findViewById(R.id.btnFlash);
			btnFlash.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub

					if (!isFlashOn && currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {

					} else {
						if (isFlashOn) {
							isFlashOn = false;
							btnFlash.setImageResource(R.drawable.flash_off);

						} else {
							isFlashOn = true;
							btnFlash.setImageResource(R.drawable.flash_on);
						}

						mPreview.OnOffFlash(isFlashOn);
					}

				}
			});
		}
	}

	void onCameraChange() {
		try {

			mCamera.stopPreview();
			mCamera.release(); // release the camera for other
			// applications


			// swap the id of the camera to be used
			if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
				currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
			} else {
				currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			}

			// Create an instance of Camera


			mCamera = getCameraInstance(currentCameraId);

			mPreview.setCamera(mCamera);

			mCamera.setPreviewDisplay(mPreview.mHolder);
			mCamera.startPreview();
			setCameraDisplayOrientation((Activity) mContext, currentCameraId, mCamera);



		} catch (Exception e) {
			e.printStackTrace();
			// ignore: tried to stop a non-existent preview
		}
	}

	public Camera getCameraInstance(int currentCameraId) {
		Camera c = null;
		try {
			c = Camera.open(currentCameraId); // attempt to get a Camera
			Parameters params = c.getParameters();

			List<String> flashModes = params.getSupportedFlashModes();

			if (flashModes != null && flashModes.size() > 0) {
				// set the focus mode
				params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			}

			params.set("orientation", "portrait");
			params.setRotation(90);

			videosizes = params.getSupportedVideoSizes();

			c.setParameters(params); // instance

		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
			e.printStackTrace();
		}
		return c; // returns null if camera is unavailable
	}

	private boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			// this device has a camera
			return true;
		} else {
			// no camera on this device
			return false;
		}
	}

	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;

		@SuppressWarnings("deprecation")
		public CameraPreview(Context context) {
			super(context);

		}

		void OnOffFlash(boolean isFlashOn) {

			Camera.Parameters parameters = mCamera.getParameters();

			if (!isFlashOn) {
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
			} else {
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(parameters);
			}

		}

		private boolean prepareVideoRecorder() {

			try {
				mCamera.stopPreview();
				mCamera.setPreviewDisplay(null);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

			mMediaRecorder = new MediaRecorder();

			// Step 1: Unlock and set camera to MediaRecorder
			mCamera.unlock();
			mMediaRecorder.setCamera(mCamera);

			// Step 2: Set sources
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			Size optimalVideoSize = getOptimalPreviewSize(videosizes, desiredwidth, desiredheight);

			// // Step 3: Set a CamcorderProfile (requires API Level 8 or
			// higher)
			CamcorderProfile profile = CamcorderProfile.get(currentCameraId, CamcorderProfile.QUALITY_HIGH);
			profile.videoFrameWidth = optimalVideoSize.width;
			profile.videoFrameHeight = optimalVideoSize.height;
			mMediaRecorder.setProfile(profile);

			// Log.v(Constant.TAG, "optimalVideoSize width " +
			// optimalVideoSize.width + " height " + optimalVideoSize.height);
			// mMediaRecorder.setVideoSize(optimalVideoSize.width,
			// optimalVideoSize.height);

			mMediaRecorder.setVideoEncodingBitRate(1280000);
			mMediaRecorder.setMaxDuration(cMaxRecordDurationInMs);
			if(categoryId.equalsIgnoreCase("7")){
				mMediaRecorder.setMaxFileSize(cMaxFileSizeInBytesProfile);
			}
			else{
				mMediaRecorder.setMaxFileSize(cMaxFileSizeInBytes);	
			}
			

			mMediaRecorder.setVideoFrameRate(30);

			sFilepath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO,mContext).toString();

			// Step 4: Set output file
			mMediaRecorder.setOutputFile(sFilepath);

			// Step 5: Set the preview output
			mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

			if (currentCameraId == CameraInfo.CAMERA_FACING_BACK) {
				mMediaRecorder.setOrientationHint(90);
				//				Log.d("mobstar","setorientation 90");
			} else {
				mMediaRecorder.setOrientationHint(270);
				//				Log.d("mobstar","setorientation 270");
			}

			// Step 6: Prepare configured MediaRecorder
			try {
				mMediaRecorder.prepare();
			} catch (IllegalStateException e) {
				//				Log.d(Constant.TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
				releaseMediaRecorder();
				return false;
			} catch (IOException e) {
				//				Log.d(Constant.TAG, "IOException preparing MediaRecorder: " + e.getMessage());
				releaseMediaRecorder();
				return false;
			}
			return true;
		}

		void RecordVideo() {

			//			Log.v(Constant.TAG, "RecordVideo " + isRecording);

			if (isRecording) {
				// stop recording and release camera
				try{
					mMediaRecorder.stop(); // stop the recording
				}
				catch(Exception e){
					e.printStackTrace();
				}

				releaseMediaRecorder(); // release the MediaRecorder object

				mCamera.lock(); // take camera access back from MediaRecorder

				releaseCamera();

				isRecording = false;

				Intent intent = new Intent(mContext, ApproveVideoActivity.class);
				intent.putExtra("video_path", sFilepath);
				intent.putExtra("categoryId",categoryId);
				if(subCat!=null && subCat.length()>0){
					intent.putExtra("subCat",subCat);	  
				}
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				//onBackPressed();

			} else {
				// initialize video camera
				if (prepareVideoRecorder()) {
					// Camera is available and unlocked, MediaRecorder is
					// prepared,
					// now you can start recording
					mMediaRecorder.start();

					layoutCameraOption.setVisibility(View.GONE);
					textRecordSecond.setVisibility(View.VISIBLE);

					recordTimer.start();

					isRecording = true;
				} else {
					// prepare didn't work, release the camera
					releaseMediaRecorder();
					// inform user
				}
			}
		}

		void setCamera(Camera camera) {
			mCamera = camera;
			// Install a SurfaceHolder.Callback so we get notified when the
			// underlying surface is created and destroyed.
			mHolder = getHolder();
			mHolder.addCallback(this);
			// deprecated setting, but required on Android versions prior to 3.0
			mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}

		public void surfaceCreated(SurfaceHolder holder) {
			// The Surface has been created, now tell the camera where to draw
			// the preview.
			try {
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
				setCameraDisplayOrientation((Activity) mContext, currentCameraId, mCamera);
			} catch (Exception e) {
				//				Log.d(Constant.TAG, "Error setting camera preview: " + e.getMessage());
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// empty. Take care of releasing the Camera preview in your
			// activity.
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

			if (mHolder.getSurface() == null) {
				// preview surface does not exist
				return;
			}

			// stop preview before making changes
			try {
				if(mCamera!=null){
					mCamera.stopPreview();

					Camera.Parameters parameters = mCamera.getParameters();
					Camera.Size size = getBestPreviewSize(width, height, parameters);

					if (size != null) {
						parameters.setPreviewSize(size.width, size.height);
						mCamera.setParameters(parameters);
						mCamera.startPreview();
					}

					Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

					if (display.getRotation() == Surface.ROTATION_270) {
						parameters.setPreviewSize(width, height);
					} else {

					}
					setCameraDisplayOrientation((Activity) mContext, currentCameraId, mCamera);

					mCamera.setParameters(parameters);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
				//				Log.d(Constant.TAG, "Error starting camera preview: " + e.getMessage());
			}
		}
	}

	public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {

		CameraInfo info = new android.hardware.Camera.CameraInfo();

		Camera.getCameraInfo(cameraId, info);

		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

		int degrees = 0;

		switch (rotation) {
		case Surface.ROTATION_0:
			//			Log.d("mobstar","ROTATION_0");
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			//			Log.d("mobstar","ROTATION_90");
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			//			Log.d("mobstar","ROTATION_180");
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			//			Log.d("mobstar","ROTATION_270");
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}

		camera.setDisplayOrientation(result);
	}

	private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {

			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int width, int height) {
		Camera.Size result = null;
		for (Camera.Size size : sizes) {

			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;
					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}

	private void releaseMediaRecorder() {
		if (mMediaRecorder != null) {
			mMediaRecorder.reset(); // clear recorder configuration
			mMediaRecorder.release(); // release the recorder object
			mMediaRecorder = null;
			mCamera.lock(); // lock camera for later use
		}

		if (recordTimer != null) {
			recordTimer.cancel();
		}
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		releaseMediaRecorder();
		releaseCamera();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
}
