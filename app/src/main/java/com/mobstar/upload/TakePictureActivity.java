package com.mobstar.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobstar.R;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;

//import eu.janmuller.android.simplecropimage.CropImage;

public class TakePictureActivity extends Activity {

	Context mContext;

	private Camera mCamera;
	private CameraPreview mPreview;

	ImageView btnCapture, btnFlash, btnChangeCamera;

	boolean isFlashOn = false;
	int currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
	boolean isFrontCameraAvailable = false;

	private boolean isCapture = false;
	String categoryId;
	static public Uri tempUri;
	private String imagePath = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.screenBrightness = 1.0f;
		this.getWindow().setAttributes(lp);

		setContentView(R.layout.activity_take_picture);

		mContext = TakePictureActivity.this;

		Bundle b = getIntent().getExtras();
		if (b != null) {
			if (b.containsKey("categoryId")) {
				categoryId = b.getString("categoryId");
			}
		}
		// Log.d("mobstar","TakePictureActivity=> categoryid "+categoryId);

		InitControls();

		Utility.SendDataToGA("TakePicture Screen", TakePictureActivity.this);
	}

	void InitControls() {

		if (checkCameraHardware(mContext)) {

			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
					isFrontCameraAvailable = true;
				} else {
					isFrontCameraAvailable = false;
				}
			}

			// Create an instance of Camera
			mCamera = getCameraInstance(currentCameraId);

			// Create our Preview view and set it as the content of our
			// activity.
			mPreview = new CameraPreview(this);
			mPreview.setCamera(mCamera);
			FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
			preview.addView(mPreview);

			btnCapture = (ImageView) findViewById(R.id.btnCapture);
			btnCapture.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// get an image from the camera
					Log.d("log_tag", "isCapture==>click");
					if (!isCapture) {
						Log.d("log_tag", "isCapture==>" + isCapture);
						isCapture = true;
						mPreview.CaptureImage();
					}

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

	public Camera getCameraInstance(int currentCameraId) {
		Camera c = null;
		try {
			c = Camera.open(currentCameraId); // attempt to get a Camera
			Parameters params = c.getParameters();
			params.set("orientation", "portrait");
			params.setRotation(90);
			c.setParameters(params); // instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	void onCameraChange() {
		try {
			if (mCamera != null) {
				mCamera.stopPreview();
				mCamera.release(); // release the camera for other
				// applications
			}

			// swap the id of the camera to be used
			if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
				currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
			} else {
				currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
			}

			// Create an instance of Camera
			mCamera = getCameraInstance(currentCameraId);

			mPreview.setCamera(mCamera);

			try {
				mCamera.setPreviewDisplay(mPreview.mHolder);
				mCamera.startPreview();
				setCameraDisplayOrientation((Activity) mContext, currentCameraId, mCamera);
			} catch (IOException e) {

				// Log.d(Constant.TAG, "Error setting camera preview: " +
				// e.getMessage());
			}

		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
			e.printStackTrace();
		}
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
		private Camera mCamera;

		@SuppressWarnings("deprecation")
		public CameraPreview(Context context) {
			super(context);

		}

		void CaptureImage() {
			mCamera.takePicture(null, null, mPicture);
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
			} catch (IOException e) {
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

	public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {

		CameraInfo info = new android.hardware.Camera.CameraInfo();

		Camera.getCameraInfo(cameraId, info);

		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

		int degrees = 0;

		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
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
		// Log.v(Constant.TAG, "result " + result);
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

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File pictureFile = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_IMAGE, mContext);

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
			Intent intent = new Intent(mContext, ApprovePhotoActivity.class);
			intent.putExtra("categoryId", categoryId);
			intent.putExtra("image_path", pictureFile.getAbsolutePath());
			startActivity(intent);
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			onBackPressed();

			releaseCamera();
			isCapture = false;
		}
	};

	public Bitmap rotate(Bitmap bitmap, int degree) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(degree);

		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
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

//	private void doCrop(Uri mImageCaptureUri) {
//		isCapture = false;
//		// Intent intent = new Intent("com.android.camera.action.CROP");
//		// intent.setType("image/*");
//		// List<ResolveInfo> list =
//		// getPackageManager().queryIntentActivities(intent, 0);
//		// int size = list.size();
//		// if (size == 0) {
//		// Toast.makeText(this, "Can not find image crop application",
//		// Toast.LENGTH_SHORT).show();
//		// } else {
//		// DisplayMetrics metrics = new DisplayMetrics();
//		// getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		// intent.setData(mImageCaptureUri);
//		// intent.putExtra("crop", true);
//		// intent.putExtra("outputX", metrics.widthPixels);
//		// intent.putExtra("outputY", metrics.widthPixels);
//		// intent.putExtra("aspectX", 1);
//		// intent.putExtra("aspectY", 1);
//		// intent.putExtra("scale",true);
//		// Intent i = new Intent(intent);
//		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
//		// Locale.ENGLISH).format(new Date());
//		// ContentValues values = new ContentValues();
//		// values.put(MediaStore.Images.Media.TITLE, "IMG_" + timeStamp +
//		// ".jpg");
//		// tempUri =
//		// getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//		// values);
//		// i.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
//		// ResolveInfo res = list.get(0);
//		// i.setComponent(new ComponentName(res.activityInfo.packageName,
//		// res.activityInfo.name));
//		// startActivityForResult(i, 27);
//		// }
//
//		Intent intent = new Intent(TakePictureActivity.this, CropImage.class);
//
//		// tell CropImage activity to look for image to crop
//		String filePath = mImageCaptureUri.getPath();
//		intent.putExtra(CropImage.IMAGE_PATH, filePath);
//
//		// allow CropImage activity to rescale image
//		intent.putExtra(CropImage.SCALE, true);
//
//		// if the aspect ratio is fixed to ratio 3/2
//		intent.putExtra(CropImage.ASPECT_X, 3);
//		intent.putExtra(CropImage.ASPECT_Y, 2);
//
//		// start activity CropImage with certain request code and listen
//		// for result
//		startActivityForResult(intent, 27);
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {

			if (requestCode == 27) {

				try {

					releaseCamera();

					String capturedImageFilePath = getPath(mContext, tempUri);

					imagePath = capturedImageFilePath;
					Intent intent = new Intent(mContext, ApprovePhotoActivity.class);
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
