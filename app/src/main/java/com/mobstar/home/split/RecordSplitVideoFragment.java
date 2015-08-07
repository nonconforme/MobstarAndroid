package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.upload.ApproveVideoActivity;
import com.mobstar.upload.RecordVideoActivity;
import com.mobstar.utils.Utility;

import java.io.IOException;
import java.util.List;

/**
 * Created by vasia on 06.08.15.
 */
public class RecordSplitVideoFragment extends Fragment {

//    private ImageView btnRecord;
//    private TextView textRecordSecond;
//    private int currentCount = 15;
//    List<Camera.Size> videosizes;
//    private int currentCameraId;
//    private Camera mCamera;
//    private CameraPreview mPreview;
//    private MediaRecorder mMediaRecorder;



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

    List<Camera.Size> videosizes;

    private final int cMaxRecordDurationInMs = 30099;

    private final long cMaxFileSizeInBytes = 8000000;

    private final long cMaxFileSizeInBytesProfile = 52428800; //50MB

    String categoryId="7";
    String subCat;
    private TextureView textureView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_record_video_split, container, false);
        mContext=getActivity();
        findView(inflatedView);
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
        return inflatedView;
    }

    private void findView(View inflatedView) {
        btnRecord = (ImageView) inflatedView.findViewById(R.id.btnRecord);
        textRecordSecond = (TextView) inflatedView.findViewById(R.id.textRecordSecond);
        textureView = (TextureView) inflatedView.findViewById(R.id.textureView);
//        FrameLayout preview = (FrameLayout) inflatedView.findViewById(R.id.camera_preview);
        textRecordSecond.setVisibility(View.GONE);

        textRecordSecond.setText(currentCount + "");

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get an image from the camera
                mPreview.RecordVideo();

            }
        });

        if (checkCameraHardware(getActivity())) {

            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
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
            mPreview = new CameraPreview(getActivity());
            mPreview.setCamera(mCamera);
            FrameLayout preview = (FrameLayout) inflatedView.findViewById(R.id.camera_preview);
            preview.addView(mPreview);

            btnRecord = (ImageView) inflatedView.findViewById(R.id.btnRecord);
            btnRecord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get an image from the camera
                    mPreview.RecordVideo();

                }
            });
        }

    }



    //todo move to utils
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    //todo move to utils
    public Camera getCameraInstance(int currentCameraId) {
        Camera c = null;
        try {
            c = Camera.open(currentCameraId); // attempt to get a Camera
            Camera.Parameters params = c.getParameters();

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
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;

        @SuppressWarnings("deprecation")
        public CameraPreview(Context context) {
            super(context);

        }

        void OnOffFlash(boolean isFlashOn) {

            Camera.Parameters parameters = mCamera.getParameters();

            if (!isFlashOn) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
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

            Camera.Size optimalVideoSize = getOptimalPreviewSize(videosizes, desiredwidth, desiredheight);

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
            if (categoryId.equalsIgnoreCase("7")) {
                mMediaRecorder.setMaxFileSize(cMaxFileSizeInBytesProfile);
            } else {
                mMediaRecorder.setMaxFileSize(cMaxFileSizeInBytes);
            }


            mMediaRecorder.setVideoFrameRate(30);

            sFilepath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO, mContext).toString();

            // Step 4: Set output file
            mMediaRecorder.setOutputFile(sFilepath);

            // Step 5: Set the preview output
            mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());

            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
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
                try {
                    mMediaRecorder.stop(); // stop the recording
                } catch (Exception e) {
                    e.printStackTrace();
                }

                releaseMediaRecorder(); // release the MediaRecorder object

                mCamera.lock(); // take camera access back from MediaRecorder

                releaseCamera();

                isRecording = false;

                Intent intent = new Intent(mContext, ApproveVideoActivity.class);
                intent.putExtra("video_path", sFilepath);
                intent.putExtra("categoryId", categoryId);
                if (subCat != null && subCat.length() > 0) {
                    intent.putExtra("subCat", subCat);
                }
                startActivity(intent);
//                finish();
//                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                onBackPressed();

            } else {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is
                    // prepared,
                    // now you can start recording
                    mMediaRecorder.start();

//                    layoutCameraOption.setVisibility(View.GONE);
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
                if (mCamera != null) {
                    mCamera.stopPreview();

                    Camera.Parameters parameters = mCamera.getParameters();
                    Camera.Size size = getBestPreviewSize(width, height, parameters);

                    if (size != null) {
                        parameters.setPreviewSize(size.width, size.height);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                    }

                    Display display = ((WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE)).getDefaultDisplay();

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

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
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
    public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();

        Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation) {
            case Surface.ROTATION_0:
                			Log.d("mobstar", "ROTATION_0");
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                			Log.d("mobstar","ROTATION_90");
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                			Log.d("mobstar","ROTATION_180");
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                			Log.d("mobstar","ROTATION_270");
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
}
