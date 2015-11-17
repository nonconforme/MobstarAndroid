package com.mobstar.upload;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mobstar.R;
import com.mobstar.utils.CameraUtility;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TakePictureActivity extends Activity implements OnClickListener {

	private Camera mCamera;
	private CameraPreview mCameraPreview;
	private TextView tvFlash;
	private LinearLayout btnFlash;
	private ImageView btnCapture, ivFlash, btnChangeCamera;
	private boolean isFlashOn = false;
	private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
	boolean isFrontCameraAvailable = false;
	private boolean isCapture = false;
	private String categoryId;
	static public Uri tempUri;
	private String imagePath = "";
	private FrameLayout flCameraPreviewContaner;
	private List<Camera.Size> supportedPhotoSizes;
	private Camera.Size optimalPhotoSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.screenBrightness = 1.0f;
		this.getWindow().setAttributes(lp);

		setContentView(R.layout.activity_take_picture);

		getBundleData();
		// Log.d("mobstar","TakePictureActivity=> categoryid "+categoryId);
		findViews();
		setListeners();
		verifyFrontCamera();

		Utility.SendDataToGA("TakePicture Screen", TakePictureActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeCamera();
	}

	@Override
	protected void onPause() {
		super.onPause();
		flCameraPreviewContaner.removeView(mCameraPreview);
		mCameraPreview.getmHolder().removeCallback(mCameraPreview);
		CameraUtility.releaseCamera(mCamera);
	}

	private void getBundleData(){
		final Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("categoryId")) {
				categoryId = b.getString("categoryId");
			}
		}
	}

	private void findViews(){
		btnCapture = (ImageView) findViewById(R.id.btnCapture);
		btnChangeCamera = (ImageView) findViewById(R.id.btnChangeCamera);
		tvFlash = (TextView) findViewById(R.id.tvFlash);
		btnFlash = (LinearLayout) findViewById(R.id.btnFlash);
		ivFlash = (ImageView) findViewById(R.id.ivFlash);
		flCameraPreviewContaner = (FrameLayout) findViewById(R.id.camera_preview);
	}

	private void setListeners(){
		btnCapture.setOnClickListener(this);
		btnChangeCamera.setOnClickListener(this);
		btnFlash.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.btnCapture:
				captureImage();
				break;
			case R.id.btnChangeCamera:
				if (isFrontCameraAvailable) {
					onCameraChange();
				}
				break;
			case R.id.btnFlash:
				startFlash();
				break;
		}
	}

	private void captureImage(){
		// get an image from the camera
		Log.d("log_tag", "isCapture==>click");
		if (!isCapture) {
			Log.d("log_tag", "isCapture==>" + isCapture);
			isCapture = true;
			mCameraPreview.captureImage();
		}
	}

	private void startFlash(){
		if (!isFlashOn && currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {

		} else {
			if (isFlashOn) {
				isFlashOn = false;
				tvFlash.setText(getString(R.string.off));

			} else {
				isFlashOn = true;
				tvFlash.setText(getString(R.string.on));
			}

			mCameraPreview.onOffFlash(isFlashOn);
		}
	}

	private void verifyFrontCamera(){
		if (CameraUtility.checkCameraHardware(this)) {

			final CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
					isFrontCameraAvailable = true;
				} else {
					isFrontCameraAvailable = false;
				}
			}
		}
	}

	private void initializeCamera() {
		if (flCameraPreviewContaner.getMeasuredHeight() == 0) {
			initializeCameraPostDelay();
			return;
		}

		if (CameraUtility.checkCameraHardware(this)) {
			mCamera = CameraUtility.getPhotoCameraInstance(currentCameraId);
			prepareCameraPreview();
			setPreviewSize();
		}
	}

	private void initializeCameraPostDelay(){
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				initializeCamera();
			}
		}, 50);
	}

	private void prepareCameraPreview(){
		mCameraPreview = new CameraPreview(this);
		mCameraPreview.setCamera(mCamera);
		final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		layoutParams.gravity = Gravity.CENTER;
		mCameraPreview.setLayoutParams(layoutParams);
		flCameraPreviewContaner.addView(mCameraPreview);
	}

	private void setPreviewSize() {
		int width = flCameraPreviewContaner.getMeasuredWidth();
		int height = flCameraPreviewContaner.getMeasuredHeight();
		supportedPhotoSizes = mCamera.getParameters().getSupportedPreviewSizes();
		optimalPhotoSize = CameraUtility.getOptimalPreviewSize(supportedPhotoSizes, width, height);
		int previewWidth = width;
		int previewHeight = height;
		if (height / width !=  optimalPhotoSize.height / optimalPhotoSize.width) {
			if (height / width < optimalPhotoSize.height / optimalPhotoSize.width)
				previewWidth = (optimalPhotoSize.height * height) / optimalPhotoSize.width;
			else previewHeight = (optimalPhotoSize.width * width) / optimalPhotoSize.height;
		}

		mCameraPreview.getLayoutParams().height = previewHeight;
		mCameraPreview.getLayoutParams().width = previewWidth;

	}

	private void onCameraChange() {
        flCameraPreviewContaner.removeView(mCameraPreview);
        mCameraPreview.getmHolder().removeCallback(mCameraPreview);
        CameraUtility.releaseCamera(mCamera);
        if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        initializeCamera();
	}

	public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
		private SurfaceHolder mHolder;
		private Camera mCamera;

		@SuppressWarnings("deprecation")
		public CameraPreview(Context context) {
			super(context);

		}

		public void captureImage() {
			mCamera.takePicture(null, null, mPicture);
		}

		public void onOffFlash(boolean isFlashOn) {

			Camera.Parameters parameters = mCamera.getParameters();

			if (!isFlashOn) {
				parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
				mCamera.setParameters(parameters);
			} else {
				parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				mCamera.setParameters(parameters);
			}

		}

		public SurfaceHolder getmHolder(){
			return mHolder;
		}

		public void setCamera(Camera camera) {
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
				CameraUtility.setCameraDisplayOrientation(TakePictureActivity.this, currentCameraId, mCamera);
			} catch (Exception e) {
				// Log.d(Constant.TAG, "Error setting camera preview: " +
				// e.getMessage());
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
				mCamera.stopPreview();
				final Camera.Parameters parameters = mCamera.getParameters();
				final Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
				if (optimalPhotoSize != null) {
					if (optimalPhotoSize.height > optimalPhotoSize.width)
						parameters.setPreviewSize(optimalPhotoSize.height, optimalPhotoSize.width);
					else
						parameters.setPreviewSize(optimalPhotoSize.width, optimalPhotoSize.height);
					mCamera.setParameters(parameters);
					mCamera.startPreview();
				}

				if (display.getRotation() == Surface.ROTATION_270) {
					parameters.setPreviewSize(optimalPhotoSize.width, optimalPhotoSize.height);
				} else {

				}
				CameraUtility.setCameraDisplayOrientation(TakePictureActivity.this, currentCameraId, mCamera);

				mCamera.setParameters(parameters);
			} catch (Exception e) {
				// ignore: tried to stop a non-existent preview
			}

			try {
				mCamera.setPreviewDisplay(mHolder);
				mCamera.startPreview();

			} catch (Exception e) {
				// Log.d(Constant.TAG, "Error starting camera preview: " +
				// e.getMessage());
			}
		}
	}

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File pictureFile = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_IMAGE, TakePictureActivity.this);

			try {

				FileOutputStream fosStream = new FileOutputStream(pictureFile);
				fosStream.write(data, 0, data.length);
				fosStream.close();

				Bitmap realImage = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

				ExifInterface exif = new ExifInterface(pictureFile.getAbsolutePath());

				// Log.d(Constant.TAG, "EXIF value " +
				// exif.getAttribute(ExifInterface.TAG_ORIENTATION));

				if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
					realImage = Utility.rotate(realImage, 90);
				} else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
					realImage = Utility.rotate(realImage, 270);
				} else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
					realImage = Utility.rotate(realImage, 180);
				}

				if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					realImage = Utility.rotate(realImage, 180);
				}

				FileOutputStream fosStream_rotate = new FileOutputStream(pictureFile);
				realImage.compress(CompressFormat.JPEG, 50, fosStream_rotate);
				fosStream_rotate.close();

			} catch (FileNotFoundException e) {
				Log.d(Constant.TAG, "File not found: " + e.getMessage());
			} catch (IOException e) {
				Log.d(Constant.TAG, "Error accessing file: " + e.getMessage());
			}

			tempUri = Uri.fromFile(new File(pictureFile.getAbsolutePath()));
			// doCrop(tempUri);

			// temp comment
			startApprovePhotoActivity(pictureFile.getAbsolutePath());

			releaseCamera();
			isCapture = false;
		}
	};

	private void startApprovePhotoActivity(final String photoPath){
		Intent intent = new Intent(this, ApprovePhotoActivity.class);
		intent.putExtra("categoryId", categoryId);
		intent.putExtra("image_path", photoPath);
		startActivity(intent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		onBackPressed();
	}

	private void releaseCamera() {
		if (mCamera != null) {
			mCamera.release(); // release the camera for other applications
			mCamera = null;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stuf
		releaseCamera();
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == 27) {

				try {

					releaseCamera();

					String capturedImageFilePath = getPath(TakePictureActivity.this, tempUri);

					imagePath = capturedImageFilePath;
					Intent intent = new Intent(TakePictureActivity.this, ApprovePhotoActivity.class);
					intent.putExtra("categoryId", categoryId);
					intent.putExtra("image_path", imagePath);
					startActivity(intent);
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					onBackPressed();

				} catch (Exception e) {
					Toast.makeText(TakePictureActivity.this, getString(R.string.error_retke_photo), Toast.LENGTH_SHORT).show();
				}

			}
		}
	}

	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/" + context.getPackageName() + "/";

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					// return Environment.getExternalStorageDirectory() + "/" +
					// split[1];
					return path + "/" + split[1];
				}

			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {
			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int column_index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(column_index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

}
