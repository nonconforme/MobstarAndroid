package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
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
import com.mobstar.home.split.ffmpeg.AfterDoneBackground;
import com.mobstar.home.split.ffmpeg.FFCommandCreator;
import com.mobstar.home.split.ffmpeg.FFTaskBackground;
import com.mobstar.home.split.ffmpeg.TranscdingBackground;
import com.mobstar.home.split.position_variants.PositionVariant;
import com.mobstar.upload.ApproveVideoActivity;
import com.mobstar.utils.Constant;
import com.mobstar.utils.Utility;
import com.mobstar.utils.receiver.HeadsetPlugReceiver;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by vasia on 06.08.15.
 */
public class RecordSplitVideoFragment extends Fragment implements HeadsetPlugReceiver.OnHeadsetPlugListener, View.OnClickListener {
    private static final String LOG_TAG = RecordSplitVideoFragment.class.getName();
    private static final int BUTTON_CLICK_DELAY = 1000; //ms
    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private FrameLayout flVideoPreviewContainer;
    private FrameLayout flVerticalLeft, flVerticalRight;
    private LinearLayout llParalelVideoPosition;
    private ImageView btnRecord;
    private int currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private boolean isFrontCameraAvailable = false;
    private boolean isRecording = false;
    private MediaRecorder mMediaRecorder;
    private TextView textRecordSecond;
    private int currentCount = 15;
    private CountDownTimer recordTimer;
    private String sFilepath;
    private int desiredWidth = 480;
    private int desiredHeight = 720;
    private List<Camera.Size> supportedVideoSizes;
    private Camera.Size optimalVideoSize;
    private final int cMaxRecordDurationInMs = 30099;
    private final long cMaxFileSizeInBytes = 8000000;
    private final long cMaxFileSizeInBytesProfile = 52428800; //50MB
    private String categoryId="0";
    private TextureView textureView;
    private String sVideoPathBack;
    private MediaPlayer mediaPlayer;
    private String readyFilePath;
    private SplitActivity splitActivity;
    private String cameraRotationOutPath;
    private String backRotation ;
    private ImageView ivVideoPreview;
    private Bitmap imageVideoPreview;
    private PositionVariant positionVariant;
    private FrameLayout flCameraPreviewContaner;
    private HeadsetPlugReceiver headsetPlugReceiver;
    private boolean onHeadsetConnect;

    private int surfaceWidth;
    private int surfaceHeight;
    private Rect cropRect;

    private long buttonDelay;
    private boolean isRecordStopped = false;
    private boolean isPrepareRecord = false;


    public static RecordSplitVideoFragment newInstance(final PositionVariant _positionVariant, final Bitmap _videoPreview){
        final RecordSplitVideoFragment recordSplitVideoFragment = new RecordSplitVideoFragment();
        final Bundle args = new Bundle();
        args.putSerializable(Constant.POSITION_VARIANT, _positionVariant);
        args.putParcelable(Constant.IMAGE, _videoPreview);
        recordSplitVideoFragment.setArguments(args);
        return recordSplitVideoFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        splitActivity =(SplitActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentArgs();
    }

    private void getFragmentArgs(){
        final Bundle args = getArguments();
        if (args != null){
            imageVideoPreview = args.getParcelable(Constant.IMAGE);
            positionVariant = (PositionVariant) args.getSerializable(Constant.POSITION_VARIANT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View inflatedView = inflater.inflate(R.layout.fragment_record_video_split, container, false);
        registerHeadsetReceiver();
        mediaPlayer = new MediaPlayer();
        findView(inflatedView);
        createRecordPreview();
        combinationRecordViews();
        initializeView();
//        initializeCamera();
        recordTimer = new CountDownTimer(19000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub

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
        return inflatedView;
    }

    private void initializeCamera() {
        if (flCameraPreviewContaner.getHeight() == 0) {
            initializeCameraPostDelay();
            return;
        }
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
            mCameraPreview = new CameraPreview(getActivity());
            mCameraPreview.setCamera(mCamera);
            flCameraPreviewContaner.removeAllViews();
            final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.gravity = Gravity.CENTER;
            mCameraPreview.setLayoutParams(layoutParams);
            flCameraPreviewContaner.addView(mCameraPreview);
            setPreviewSize();
        }
    }

    private void initializeView() {
        ivVideoPreview.setImageBitmap(imageVideoPreview);
        textRecordSecond.setVisibility(View.GONE);
        textRecordSecond.setText(currentCount + "");
        btnRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRecord:
//                if (resolveClick()) {
                    if (textureView.getSurfaceTexture() == null || flCameraPreviewContaner.getHeight() == 0)
                        return;
                    Surface surface = new Surface(textureView.getSurfaceTexture());
                    startRecord(surface);
//                }
                break;
        }
    }

    private boolean resolveClick(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - buttonDelay >= BUTTON_CLICK_DELAY) {
            return true;
        }
        buttonDelay = currentTime;
        return false;
    }

    private void findView(View inflatedView) {
        btnRecord = (ImageView) inflatedView.findViewById(R.id.btnRecord);
        textRecordSecond = (TextView) inflatedView.findViewById(R.id.textRecordSecond);
        btnRecord = (ImageView) inflatedView.findViewById(R.id.btnRecord);
        flVerticalLeft = (FrameLayout) inflatedView.findViewById(R.id.verticalLeft);
        flVerticalRight = (FrameLayout) inflatedView.findViewById(R.id.verticalRight);
        llParalelVideoPosition = (LinearLayout) inflatedView.findViewById(R.id.llParalelVideoPosition);
    }

    private void createRecordPreview(){
        flCameraPreviewContaner = new FrameLayout(splitActivity);

        flVideoPreviewContainer = new FrameLayout(splitActivity);
        ivVideoPreview = new ImageView(splitActivity);
        ivVideoPreview.setAdjustViewBounds(true);
        ivVideoPreview.setScaleType(ImageView.ScaleType.FIT_XY);
        textureView = new TextureView(splitActivity);
        FrameLayout.LayoutParams mathParentParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mathParentParams.gravity = Gravity.CENTER;
        FrameLayout.LayoutParams wrapContentParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapContentParams.gravity = Gravity.CENTER;
        flVideoPreviewContainer.addView(ivVideoPreview, mathParentParams);
        flVideoPreviewContainer.addView(textureView, wrapContentParams);
    }


    private void startRecord(final Surface surface) {
        if (isRecording) {
            mCameraPreview.stopRecord();
            return;
        }
        if (isPrepareRecord)
            return;
//        new Thread() {
//            public void run() {
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                    }
                    sVideoPathBack = splitActivity.getVideoFilePath();
                    File file = new File(sVideoPathBack);
                    Log.d(LOG_TAG,"sVideoPathBack="+sVideoPathBack);
                    if (file.exists()) {
                        mediaPlayer.setDataSource(sVideoPathBack);
                        mediaPlayer.setSurface(surface);
                        mediaPlayer.setScreenOnWhilePlaying(true);
                        mediaPlayer.prepareAsync();
                        // Play video when the media source is ready for
                        // playback.
                        isPrepareRecord = true;
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mediaPlayer) {
                                mCameraPreview.startRecord();
                            }
                        });
                    }
                } catch (Exception e) {
                    isPrepareRecord = false;
                    e.printStackTrace();
                }
//            }
//        }.start();

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
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            params.set("orientation", "portrait");
            params.setRotation(90);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            supportedVideoSizes = params.getSupportedVideoSizes();

            c.setParameters(params); // instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private void removeTempFile(){
        final String[] tempFileList = {backRotation, cameraRotationOutPath, sFilepath};
        for (String aTempList : tempFileList) {
            if (aTempList == null)
                continue;
            final File tempFile = new File(aTempList);
            tempFile.delete();
        }
    }

    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;

        @SuppressWarnings("deprecation")
        public CameraPreview(Context context) {
            super(context);

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

            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            if (supportedVideoSizes != null) {
                desiredWidth = mCameraPreview.getWidth();
                desiredHeight = mCameraPreview.getHeight();
                Log.d("tagSize", "setRecordSize = " + "width = " + optimalVideoSize.width + " height = " + optimalVideoSize.height);
                calculateCropRect(optimalVideoSize);
            }
            mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

             mMediaRecorder.setVideoSize(optimalVideoSize.width,
                     optimalVideoSize.height);

            mMediaRecorder.setVideoEncodingBitRate(1280000);
            mMediaRecorder.setMaxDuration(cMaxRecordDurationInMs);
            if (categoryId.equalsIgnoreCase("7")) {
                mMediaRecorder.setMaxFileSize(cMaxFileSizeInBytesProfile);
            } else {
                mMediaRecorder.setMaxFileSize(cMaxFileSizeInBytes);
            }
            mMediaRecorder.setVideoFrameRate(30);
            sFilepath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO, splitActivity).toString();

            // Step 4: Set output file
            mMediaRecorder.setOutputFile(sFilepath);

            // Step 5: Set the preview output
            mMediaRecorder.setPreviewDisplay(mHolder.getSurface());
            if (currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mMediaRecorder.setOrientationHint(90);
                				Log.d(LOG_TAG, "setorientation 90");
            } else {
                mMediaRecorder.setOrientationHint(270);
                				Log.d(LOG_TAG, "setorientation 270");
            }

            // Step 6: Prepare configured MediaRecorder
            try {
                mMediaRecorder.prepare();
            } catch (IllegalStateException e) {
                releaseMediaRecorder();
                return false;
            } catch (IOException e) {
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        private void calculateCropRect(final Camera.Size optimalVideoSize){
            cropRect = new Rect();
            switch (positionVariant){
                case ORIGIN_RIGHT:
                case ORIGIN_LEFT:
                    cropRect.left = 0;
                    cropRect.right = optimalVideoSize.width;
                    int invisibleCameraFrameVertical = ((surfaceWidth - flCameraPreviewContaner.getWidth()) / 2) * optimalVideoSize.height / surfaceWidth;
                    cropRect.top = invisibleCameraFrameVertical;
                    cropRect.bottom = optimalVideoSize.height - invisibleCameraFrameVertical;
                    break;
                default:
                    cropRect.top = 0;
                    cropRect.bottom = optimalVideoSize.height;
                    int invisibleCameraFrameHorizontal = ((surfaceHeight - flCameraPreviewContaner.getHeight()) / 2) * optimalVideoSize.width / surfaceHeight;
                    cropRect.left = invisibleCameraFrameHorizontal;
                    cropRect.right = optimalVideoSize.width - invisibleCameraFrameHorizontal;
                    break;
            }
        }

        private void startRecord(){
            if (isRecording)
                return;
            if (prepareVideoRecorder()) {

                isRecording = true;
                ivVideoPreview.setVisibility(GONE);
                try {
                    mMediaRecorder.start();
                }
                catch (RuntimeException e){
                    e.printStackTrace();
                    isPrepareRecord = false;
                    isRecording = false;
                    return;
                }

//                    layoutCameraOption.setVisibility(View.GONE);
                textRecordSecond.setVisibility(View.VISIBLE);

                recordTimer.start();
                mediaPlayer.start();

            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                // inform user
            }
        }

        private void stopRecord(){
            if (isRecordStopped)
                return;
            isRecordStopped = true;
            // stop recording and release camera
            try {
                mMediaRecorder.stop();
                mediaPlayer.stop();// stop the recording
            } catch (Exception e) {
                e.printStackTrace();
            }
//            isRecording = false;
            releaseMediaRecorder(); // release the MediaRecorder object
            mCamera.lock(); // take camera access back from MediaRecorder
            releaseCamera();

            startCropAndRotationCameraVideo();
        }

//        private void recordVideo() {
//
//            if (isRecording) {
//                // stop recording and release camera
//                try {
//                    mMediaRecorder.stop();
//                    mediaPlayer.stop();// stop the recording
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                isRecording = false;
//                releaseMediaRecorder(); // release the MediaRecorder object
//                mCamera.lock(); // take camera access back from MediaRecorder
//                releaseCamera();
//
//                startCropAndRotationCameraVideo();
//
//            } else {
//                // initialize video camera
//                if (prepareVideoRecorder()) {
//                    // Camera is available and unlocked, MediaRecorder is
//                    // prepared,
//                    // now you can start recording
//                    isRecording = true;
//                    ivVideoPreview.setVisibility(GONE);
//                    try {
//                        mMediaRecorder.start();
//                    }
//                    catch (RuntimeException e){
//                        e.printStackTrace();
//                        isRecording = false;
//                        return;
//                    }
//
////                    layoutCameraOption.setVisibility(View.GONE);
//                    textRecordSecond.setVisibility(View.VISIBLE);
//
//                    recordTimer.start();
//                    mediaPlayer.start();
//
//                } else {
//                    // prepare didn't work, release the camera
//                    releaseMediaRecorder();
//                    // inform user
//                }
//            }
//        }

        void setCamera(Camera camera) {
            mCamera = camera;
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
//            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                setCameraDisplayOrientation((Activity) splitActivity, currentCameraId, mCamera);
            } catch (Exception e) {
                e.printStackTrace();
                //				Log.d(Constant.TAG, "Error setting camera preview: " + e.getMessage());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your
            // activity.
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            surfaceWidth = width;
            surfaceHeight = height;

            try {
                if (mCamera != null) {
                    mCamera.stopPreview();

                    Camera.Parameters parameters = mCamera.getParameters();

                    Log.d("tagSize", "surfaceChangeSize = " + "width = " + optimalVideoSize.width + " height = " + optimalVideoSize.height);
                    if (optimalVideoSize != null) {
                        parameters.setPreviewSize(optimalVideoSize.width, optimalVideoSize.height);
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                        mCamera.setParameters(parameters);
                        mCamera.startPreview();
                    }

                    Display display = ((WindowManager) getActivity().getSystemService(getActivity().WINDOW_SERVICE)).getDefaultDisplay();

                    if (display.getRotation() == Surface.ROTATION_270) {
                        parameters.setPreviewSize(width, height);
                    } else {

                    }
                    setCameraDisplayOrientation((Activity) splitActivity, currentCameraId, mCamera);

                    mCamera.setParameters(parameters);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e) {
                e.printStackTrace();
                //				Log.d(Constant.TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }

    private void startCropAndRotationCameraVideo(){
        cameraRotationOutPath = Utility.getTemporaryMediaFile(splitActivity, "cameraRotationOutPath").toString();
        if (cameraRotationOutPath == null && sFilepath != null)
            return;
        backRotation = sVideoPathBack;
        final String complexCommand = FFCommandCreator.getCropAndRotationComplexCommand(sFilepath, cameraRotationOutPath, cropRect, positionVariant);

        new FFTaskBackground(getActivity(), complexCommand, getString(R.string.rotation_title), new AfterDoneBackground() {
            @Override
            public void onAfterDone() {
              startMergeVideo();
            }

            @Override
            public void onCancel() {
                removeTempFile();
                splitActivity.finish();
            }
        }).runTranscoding();
    }

    private void startMergeVideo(){
        Log.d(LOG_TAG, "start join video");
        readyFilePath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO, splitActivity).toString();
        if (readyFilePath == null)
            return;
        new TranscdingBackground(getActivity(), cameraRotationOutPath, backRotation, readyFilePath, onHeadsetConnect, positionVariant, new AfterDoneBackground() {
                    @Override
                    public void onAfterDone() {
                        Log.d(LOG_TAG, "compleat readyFilePath");
                        removeTempFile();
                        startApproveActivity(readyFilePath);
                    }

                    @Override
                    public void onCancel() {
                        removeTempFile();
                        splitActivity.finish();
                    }
                }).runTranscoding();
    }

    private void startApproveActivity(String file) {
        Intent intent = new Intent(splitActivity, ApproveVideoActivity.class);
        intent.putExtra("video_path", file);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra(Constant.ENTRY, splitActivity.getEntry());
        intent.putExtra(ApproveVideoActivity.APPROVE_SPLIT_VIDEO, true);
        startActivity(intent);
        splitActivity.finish();
        splitActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size result = null;
        for (Camera.Size size : sizes) {

            switch (positionVariant){
                case ORIGIN_TOP:
                case ORIGIN_BOTTOM:
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
                    break;
                default:
                    if (size.height <= height) {
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
                    break;
            }
//
//            if (size.width <= width && size.height <= height) {
//                if (result == null) {
//                    result = size;
//                } else {
//                    int resultArea = result.width * result.height;
//                    int newArea = size.width * size.height;
//                    if (newArea > resultArea) {
//                        result = size;
//                    }
//                }
//            }
        }
        return (result);
    }

    void setPreviewSize() {
        int width = flCameraPreviewContaner.getWidth();
        int height = flCameraPreviewContaner.getHeight();

        optimalVideoSize = getOptimalPreviewSize(supportedVideoSizes, width, height);
        Log.d("tagSize", "setPreviewSize = " + "width = " + optimalVideoSize.width + " height = " + optimalVideoSize.height);
        int previewWidth = width;
        int previewHeight = height;
        switch (positionVariant){
            case ORIGIN_RIGHT:
            case ORIGIN_LEFT:
                previewWidth = (optimalVideoSize.height * height) / optimalVideoSize.width;
                break;
            default:
                previewHeight = (optimalVideoSize.width * width) / optimalVideoSize.height;
                break;
        }

        mCameraPreview.getLayoutParams().height = previewHeight;
        mCameraPreview.getLayoutParams().width = previewWidth;

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
                			Log.d(LOG_TAG, "ROTATION_0");
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                			Log.d(LOG_TAG,"ROTATION_90");
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                			Log.d(LOG_TAG,"ROTATION_180");
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                			Log.d(LOG_TAG,"ROTATION_270");
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

    @Override
    public void onStop() {
        super.onStop();
        releaseMediaRecorder();
        releaseCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        initializeCamera();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterHeadsetReceiver();
    }

    private void registerHeadsetReceiver(){
        headsetPlugReceiver = new HeadsetPlugReceiver();
        headsetPlugReceiver.setOnHeadsetPlugListener(this);
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(HeadsetPlugReceiver.HEADSET_PLUG_ACTION);
        splitActivity.registerReceiver(headsetPlugReceiver, intentFilter);
    }

    private void unregisterHeadsetReceiver(){
        if (headsetPlugReceiver == null)
            return;
        splitActivity.unregisterReceiver(headsetPlugReceiver);
        headsetPlugReceiver = null;
    }

    @Override
    public void onHeadsetConnect(boolean connectedMicrophone, String headsetName) {
        onHeadsetConnect = true;
    }

    @Override
    public void onHeadsetDisconnect() {
        onHeadsetConnect = false;
    }

    private void combinationRecordViews(){
        switch (positionVariant){
            case ORIGIN_LEFT:
                onOriginLeft();
                break;
            case ORIGIN_RIGHT:
                onOriginRight();
                break;
            case ORIGIN_RIGHT_TOP:
                onOriginRightTop();
                break;
            case ORIGIN_FULLSCREEN:
                onOriginFullscreen();
                break;
            case ORIGIN_TOP:
                onOriginTop();
                break;
            case ORIGIN_BOTTOM:
                onOriginBottom();
                break;
        }
    }

    private void onOriginLeft(){
        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flVerticalLeft.addView(flVideoPreviewContainer, layoutParams);
        flVerticalRight.addView(flCameraPreviewContaner, layoutParams);

    }

    private void onOriginRight(){

        final FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flVerticalLeft.addView(flCameraPreviewContaner, layoutParams);
        flVerticalRight.addView(flVideoPreviewContainer, layoutParams);
    }

    private void onOriginRightTop(){
        final FrameLayout.LayoutParams layoutCameraParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final int videoPreviewSize = getDisplayWidht() / 2;
        final FrameLayout.LayoutParams layoutVideoParams = new FrameLayout.LayoutParams(videoPreviewSize, videoPreviewSize);
        layoutVideoParams.gravity = Gravity.RIGHT;
        flVerticalRight.setVisibility(View.GONE);
        flVerticalLeft.addView(flCameraPreviewContaner, layoutCameraParams);
        flVerticalLeft.addView(flVideoPreviewContainer, layoutVideoParams);
    }

    private void onOriginFullscreen(){
        final int cameraPreviewSize = getDisplayWidht() / 2;
        final FrameLayout.LayoutParams layoutCameraParams = new FrameLayout.LayoutParams(cameraPreviewSize,cameraPreviewSize);
        layoutCameraParams.gravity = Gravity.RIGHT;
        final FrameLayout.LayoutParams layoutVideoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        flVerticalRight.setVisibility(View.GONE);
        flVerticalLeft.addView(flVideoPreviewContainer, layoutVideoParams);
        flVerticalLeft.addView(flCameraPreviewContaner, layoutCameraParams);
    }

    private void onOriginBottom(){
        llParalelVideoPosition.setOrientation(LinearLayout.VERTICAL);
        onOriginRight();
    }

    private void onOriginTop(){
        llParalelVideoPosition.setOrientation(LinearLayout.VERTICAL);
        onOriginLeft();
    }

    private int getDisplayWidht(){
        final Display display = getActivity().getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
