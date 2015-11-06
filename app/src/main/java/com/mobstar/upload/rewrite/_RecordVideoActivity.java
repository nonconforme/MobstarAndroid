package com.mobstar.upload.rewrite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobstar.R;
import com.mobstar.home.split.ffmpeg.AfterDoneBackground;
import com.mobstar.home.split.ffmpeg.FFCommandCreator;
import com.mobstar.home.split.ffmpeg.FFTaskBackground;
import com.mobstar.upload.ApproveVideoActivity;
import com.mobstar.utils.CameraUtility;
import com.mobstar.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Created by Alexandr on 22.10.2015.
 */
public class _RecordVideoActivity extends Activity implements SensorEventListener {

    public static final int C_MAX_RECORD_DURATION_IN_MS = 30099;
    public static final long C_MAX_FILE_SIZE_IN_BYTES = 8000000;
    public static final long C_MAX_FILE_SIZE_IN_BYTES_PROFILE = 52428800; //50MB
    private static final String LOG_TAG = _RecordVideoActivity.class.getName();

    private Context mContext;
    private Camera mCamera;
//    private CameraPreview mPreview;
    private TextView tvFlash;
    private LinearLayout btnFlash;
    private ImageView btnRecord, ivFlash, btnChangeCamera;
    private boolean isFlashOn = false;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean isFrontCameraAvailable = false;
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    private LinearLayout layoutCameraOption;
    private TextView textRecordSecond;
    private int currentCount = 15;
    private CountDownTimer recordTimer;
    private String sFilepath;
    private int desiredwidth = 480;
    private int desiredheight = 720;
    private List<Camera.Size> videosizes;
    private final int cMaxRecordDurationInMs = 30099;
    private final long cMaxFileSizeInBytes = 8000000;
    private final long cMaxFileSizeInBytesProfile = 52428800; //50MB
    private String categoryId,subCat;
    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    float[] mGravity;
    float[] mGeomagnetic;

    private List<Camera.Size> supportedVideoSizes;
    private CameraPreview mCameraPreview;
    private FrameLayout flCameraPreviewContaner;
    private Camera.Size optimalVideoSize;
    private boolean isRecordStopped = false;
    private boolean isPrepareRecord = false;
    private int mOrientetionCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.screenBrightness = 1.0f;
        this.getWindow().setAttributes(lp);
        setContentView(R.layout.activity_record_video);
        mContext = _RecordVideoActivity.this;
        getArgs();
        initControls();
        Utility.SendDataToGA("RecordVideo Screen", _RecordVideoActivity.this);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        initializeCamera();

    }
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        releaseMediaRecorder();
        flCameraPreviewContaner.removeView(mCameraPreview);
        mCameraPreview.getmHolder().removeCallback(mCameraPreview);
        CameraUtility.releaseCamera(mCamera);
    }


    private void getArgs(){
        final Bundle args = getIntent().getExtras();
        if(args != null) {
            if(args.containsKey("categoryId")) {
                categoryId = args.getString("categoryId");
                subCat = args.getString("subCat");
            }
        }
    }

    private void initControls() {

        layoutCameraOption = (LinearLayout) findViewById(R.id.layoutCameraOption);
        layoutCameraOption.setVisibility(View.VISIBLE);

        textRecordSecond = (TextView) findViewById(R.id.textRecordSecond);
        textRecordSecond.setVisibility(View.GONE);
        flCameraPreviewContaner = (FrameLayout) findViewById(R.id.camera_preview);

        textRecordSecond.setText(currentCount + "");

        recordTimer = new CountDownTimer(19000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                // Log.v(Constant.TAG, "currentCount " + currentCount);

                if (currentCount == 1) {

                    currentCount = 0;

                    mCameraPreview.stopRecord();
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


        btnRecord = (ImageView) findViewById(R.id.btnRecord);
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isRecording)
                    mCameraPreview.stopRecord();
                else {
                    if (!isPrepareRecord)
                        if(mGravity != null && mGeomagnetic != null) {
                            float angle = getDirection();
                            Log.d(LOG_TAG, angle + "");
                            mOrientetionCamera = CameraUtility.getOrientation(angle);
                            mCameraPreview.setOrientation(mOrientetionCamera);
                        }
                        mCameraPreview.startRecord();
                }

            }
        });

        btnChangeCamera = (ImageView) findViewById(R.id.btnChangeCamera);
        btnChangeCamera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFrontCameraAvailable) {
                    onCameraChange();
                }
            }
        });
        tvFlash = (TextView) findViewById(R.id.tvFlash);
        btnFlash = (LinearLayout) findViewById(R.id.btnFlash);
        ivFlash = (ImageView) findViewById(R.id.ivFlash);
        btnFlash.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

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
        });
//        if (CameraUtility.checkCameraHardware(mContext)) {
//
//            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
//            for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
//                Camera.getCameraInfo(i, cameraInfo);
//                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                    isFrontCameraAvailable = true;
//                }
//            }
//
//            if (isFrontCameraAvailable) {
//                currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
//            }
//
//            // Create an instance of Camera
//            mCamera = CameraUtility.getCameraInstance(currentCameraId);
//
//            // Create our Preview view and set it as the content of our
//            // activity.
//            mPreview = new CameraPreview(this);
//            mPreview.setCamera(mCamera);
//
//        }
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
    private void enableRecordButtonPostDelay(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btnRecord.setEnabled(true);
            }
        }, 750);

    }

    private void initializeCamera() {
        if (flCameraPreviewContaner.getMeasuredHeight() == 0) {
            initializeCameraPostDelay();
            return;
        }


        if (CameraUtility.checkCameraHardware(mContext)) {

            final Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
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
            mCamera = CameraUtility.getCameraInstance(currentCameraId);
            supportedVideoSizes = mCamera.getParameters().getSupportedVideoSizes();
            // Create our Preview view and set it as the content of our
            // activity.
            mCameraPreview = new CameraPreview(mContext);
            mCameraPreview.setCamera(mCamera);
//            flCameraPreviewContaner.removeAllViews();
            final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            mCameraPreview.setLayoutParams(layoutParams);

            flCameraPreviewContaner.addView(mCameraPreview);
//            setPreviewSize();
            btnRecord.setEnabled(true);
        }
    }

    private void onCameraChange() {
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


            mCamera = CameraUtility.getCameraInstance(currentCameraId);

            mCameraPreview.setCamera(mCamera);

            mCamera.setPreviewDisplay(mCameraPreview.mHolder);
            mCamera.startPreview();
            CameraUtility.setCameraDisplayOrientation((Activity) mContext, currentCameraId, mCamera);



        } catch (Exception e) {
            e.printStackTrace();
            // ignore: tried to stop a non-existent preview
        }
    }


    private float getDirection()
    {

        float[] temp = new float[9];
        float[] R = new float[9];
        //Load rotation matrix into R
        SensorManager.getRotationMatrix(temp, null,
                mGravity, mGeomagnetic);

        //Remap to camera's point-of-view
        SensorManager.remapCoordinateSystem(temp,
                SensorManager.AXIS_X,
                SensorManager.AXIS_Z, R);

        //Return the orientation values
        float[] values = new float[3];
        SensorManager.getOrientation(R, values);

        //Convert to degrees
        for (int i=0; i < values.length; i++) {
            Double degrees = (values[i] * 180) / Math.PI;
            values[i] = degrees.floatValue();
        }

        return values[2];

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch(event.sensor.getType()) {

            case Sensor.TYPE_ACCELEROMETER:
                mGravity = event.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = event.values.clone();
                break;
            default:
                return;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private int orientation;

        @SuppressWarnings("deprecation")
        public CameraPreview(Context context) {
            super(context);

        }

        public void setOrientation(int orientation){
            this.orientation = orientation;

        }

        public SurfaceHolder getmHolder() {
            return mHolder;
        }

        private void onOffFlash(boolean isFlashOn) {

            final Camera.Parameters parameters = mCamera.getParameters();
            if (!isFlashOn) {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                mCamera.setParameters(parameters);
            } else {
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(parameters);
            }
        }

        private boolean prepareVideoRecorder() {
            if (mCamera == null)
                return false;
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
//            if (supportedVideoSizes != null) {
//                Log.d("tagSize", "setRecordSize = " + "width = " + optimalVideoSize.width + " height = " + optimalVideoSize.height);
////                calculateCropRect(optimalVideoSize);
//            }
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
//
//            mMediaRecorder.setVideoSize(optimalVideoSize.width,
//                    optimalVideoSize.height);



            // // Step 3: Set a CamcorderProfile (requires API Level 8 or
//            // higher)
//            final CamcorderProfile profile = CamcorderProfile.get(currentCameraId, CamcorderProfile.QUALITY_HIGH);
//            if (supportedVideoSizes != null) {
//                optimalVideoSize = getOptimalPreviewSize(supportedVideoSizes, desiredheight, desiredwidth);
//                profile.videoFrameWidth = optimalVideoSize.width;
//                profile.videoFrameHeight = optimalVideoSize.height;
//            }
//            mMediaRecorder.setProfile(profile);

            // Log.v(Constant.TAG, "optimalVideoSize width " +
            // optimalVideoSize.width + " height " + optimalVideoSize.height);
             mMediaRecorder.setVideoSize(optimalVideoSize.width,
             optimalVideoSize.height);

            mMediaRecorder.setVideoEncodingBitRate(1280000);
            mMediaRecorder.setMaxDuration(C_MAX_RECORD_DURATION_IN_MS);
            if(categoryId.equalsIgnoreCase("7")){
                mMediaRecorder.setMaxFileSize(C_MAX_FILE_SIZE_IN_BYTES_PROFILE);
            }
            else{
                mMediaRecorder.setMaxFileSize(C_MAX_FILE_SIZE_IN_BYTES);
            }


            mMediaRecorder.setVideoFrameRate(30);

            sFilepath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO,mContext).toString();

            // Step 4: Set output file
            mMediaRecorder.setOutputFile(sFilepath);

            // Step 5: Set the preview output
            mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());



            setOrientationHint(currentCameraId, orientation, mMediaRecorder);



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

        private void setOrientationHint(int currentCameraId, int orientation, MediaRecorder mMediaRecorder) {
            int resultHint=270;
            switch (orientation){
                case CameraUtility.ORIENTATION_UP:
                    Log.d(LOG_TAG, "orientation = ORIENTATION_UP");
                    resultHint = (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)?90:270;
                    break;
                case CameraUtility.ORIENTATION_RIGHT:
                    Log.d(LOG_TAG, "orientation = ORIENTATION_RIGHT");
                    resultHint = 180;
                    break;
                case CameraUtility.ORIENTATION_LEFT:
                    Log.d(LOG_TAG, "orientation = ORIENTATION_LEFT");
                    resultHint = 0;
                    break;
                case CameraUtility.ORIENTATION_DOWN:
                    Log.d(LOG_TAG, "orientation = ORIENTATION_DOWN");
                    resultHint = (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)?270:90;
                    break;
            }
            Log.d(LOG_TAG, "resultHint = "+resultHint);
            mMediaRecorder.setOrientationHint(resultHint);

        }

        private void startRecord(){
            if (isRecording)
                return;
            if (prepareVideoRecorder()) {

                isRecording = true;
                try {
                    mMediaRecorder.start();
                    isPrepareRecord = true;
                }
                catch (RuntimeException e){
                    e.printStackTrace();
                    isPrepareRecord = false;
                    isRecording = false;
                    return;
                }

                textRecordSecond.setVisibility(View.VISIBLE);

                recordTimer.start();
                enableRecordButtonPostDelay();

            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }

        private void stopRecord(){
            if (isRecordStopped)
                return;
            btnRecord.setEnabled(false);
            isRecordStopped = true;
            // stop recording and release camera
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            isRecording = false;
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock(); // take camera access back from MediaRecorder
            CameraUtility.releaseCamera(mCamera);
            cropNewVideo();
        }

//
        private void setCamera(Camera camera) {
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
                CameraUtility.setCameraDisplayOrientation((Activity) mContext, currentCameraId, mCamera);
            } catch (Exception e) {
                //				Log.d(Constant.TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your
            // activity.
            releaseMediaRecorder();
            CameraUtility.releaseCamera(mCamera);
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            desiredwidth = width;
            desiredheight = height;
            if (supportedVideoSizes != null) {
                optimalVideoSize = getOptimalPreviewSize(supportedVideoSizes, desiredheight, desiredwidth);
            }

            // stop preview before making changes
            try {
                if (mCamera != null) {
                    mCamera.stopPreview();

                    if (optimalVideoSize!=null) {
                        Camera.Parameters parameters = mCamera.getParameters();
                        parameters.setPreviewSize(optimalVideoSize.width, optimalVideoSize.height);
                        mCamera.setParameters(parameters);
                    }
                    CameraUtility.setCameraDisplayOrientation((Activity) mContext, currentCameraId, mCamera);


                    mCamera.startPreview();
                }

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    mCamera.setPreviewDisplay(mHolder);
                    mCamera.startPreview();

                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }


        }
    }

    private void cropNewVideo() {
        final File file = Utility.getTemporaryMediaFile(this, "cropNewVideo");
        if (file == null)
            return;
        final String fileOutPath = file.toString();
        final Rect rect = getCropRect();
        final String complexCommand = FFCommandCreator.getCropAndRotationNewVideoCommand(sFilepath, fileOutPath, rect, mOrientetionCamera);
        startCropTask(complexCommand, fileOutPath);

    }

    private Rect getCropRect() {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(sFilepath);
        String sVideoHeight = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        String sVideoWidth = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);

        int iVideoHeight = Integer.parseInt(sVideoHeight);
        int iVideoWidth = Integer.parseInt(sVideoWidth);
        Rect originRect = new Rect();
        if (iVideoHeight>iVideoWidth) {
            originRect.set(0, (iVideoHeight / 2) - (iVideoWidth / 2), iVideoWidth, (iVideoHeight / 2) + (iVideoWidth / 2));
        } else {
            originRect.set((iVideoWidth / 2) - (iVideoHeight / 2), 0, (iVideoWidth / 2) + (iVideoHeight / 2), iVideoHeight);
        }
        return originRect;
    }

    private void startCropTask(final String _stringCommand, final String _fileOutPath){
        new FFTaskBackground(this, _stringCommand, getString(R.string.crop_title), new AfterDoneBackground() {
            @Override
            public void onAfterDone() {
                if (mOrientetionCamera == CameraUtility.ORIENTATION_RIGHT) {
                    Utility.removeFile(sFilepath);
                    sFilepath = _fileOutPath;
                    final File file = Utility.getTemporaryMediaFile(mContext, "cropNewVideo2");
                    if (file == null)
                        return;
                    final String fileOutPath = file.toString();
                    final Rect rect = new Rect(0, 0, 306, 306);
                    final String complexCommand = FFCommandCreator.getCropAndRotationNewVideoCommand(sFilepath, fileOutPath, rect, mOrientetionCamera);
                    mOrientetionCamera = 0;
                    startCropTask(complexCommand, fileOutPath);
                } else {
                    Utility.removeFile(sFilepath);
                    sFilepath = _fileOutPath;
                    startApproveVideoActivity();
                }
            }

            @Override
            public void onCancel() {
                Utility.removeFile(sFilepath);
                Utility.removeFile(_fileOutPath);
                finish();
            }
        }).runTranscoding();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void startApproveVideoActivity(){
        final Intent intent = new Intent(mContext, ApproveVideoActivity.class);
        intent.putExtra("video_path", sFilepath);
        intent.putExtra("categoryId", categoryId);
        if(subCat != null && subCat.length() > 0){
            intent.putExtra("subCat", subCat);
        }
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
}
