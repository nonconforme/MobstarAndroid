package com.mobstar.home.split;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.hardware.Camera.Size;
import android.graphics.Matrix;
import android.graphics.RectF;
import com.mobstar.R;
import com.mobstar.home.split.ffmpeg.AfterDoneBackground;
import com.mobstar.home.split.ffmpeg.FFCommandCreator;
import com.mobstar.home.split.ffmpeg.RotationBackground;
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
    private LinearLayout layoutCameraOption;
    private TextView textRecordSecond;
    private int currentCount = 15;
    private CountDownTimer recordTimer;
    private String sFilepath;
    private int desiredWidth = 480;
    private int desiredHeight = 720;
    private List<Camera.Size> videoSizes;
    private final int cMaxRecordDurationInMs = 30099;
    private final long cMaxFileSizeInBytes = 8000000;
    private final long cMaxFileSizeInBytesProfile = 52428800; //50MB
    private String categoryId="7";
    private String subCat;
    private TextureView textureView;
    private String sVideoPathBack;
    private MediaPlayer mediaPlayer;
    private String readyFilePath;
    private SplitActivity splitActivity;
    private String camersRotation ;
    private String backRotation ;
    private ImageView ivVideoPreview;
    private Bitmap imageVideoPreview;
    private PositionVariant positionVariant;
    private FrameLayout flCameraPreviewContaner;
    private HeadsetPlugReceiver headsetPlugReceiver;
    private boolean onHeadsetConnect;

    private int w;
    private int h;

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
        Bundle extras =savedInstanceState;
        if (extras != null) {
//            sVideoPathBack = extras.getString("video_path");

            if(extras.containsKey("categoryId")) {
                categoryId=extras.getString("categoryId");
                subCat=extras.getString("subCat");
            }
        }
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

                    mCameraPreview.RecordVideo();
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
            flCameraPreviewContaner.addView(mCameraPreview);
//            setPreviewSize(true);

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
                Surface surface = new Surface(textureView.getSurfaceTexture());
                startRecord(surface);
                break;
        }
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

        new Thread() {

            public void run() {

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
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(final MediaPlayer mediaPlayer) {

                                mCameraPreview.RecordVideo();

                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

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

            videoSizes = params.getSupportedVideoSizes();

            c.setParameters(params); // instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    private void removeTempFile(){
        final String[] tempFileList = {backRotation, camersRotation, sFilepath};
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


            // // Step 3: Set a CamcorderProfile (requires API Level 8 or
            // higher)
            CamcorderProfile profile = CamcorderProfile.get(currentCameraId, CamcorderProfile.QUALITY_HIGH);
            if (videoSizes != null) {
                Camera.Size optimalVideoSize = getOptimalPreviewSize(videoSizes, desiredWidth, desiredHeight);
                profile.videoFrameWidth = optimalVideoSize.width;
                profile.videoFrameHeight = optimalVideoSize.height;
            }
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

            sFilepath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO, splitActivity).toString();

            // Step 4: Set output file
            mMediaRecorder.setOutputFile(sFilepath);

            // Step 5: Set the preview output
            mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());

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
                    mMediaRecorder.stop();
                    mediaPlayer.stop();// stop the recording
                } catch (Exception e) {
                    e.printStackTrace();
                }

                releaseMediaRecorder(); // release the MediaRecorder object

                mCamera.lock(); // take camera access back from MediaRecorder

                releaseCamera();

                isRecording = false;
                camersRotation = Utility.getTemporaryMediaFile(splitActivity, "camersRotation").toString();
//                backRotation = Utility.getTemporaryMediaFile(mContext, "backRotation").toString();
                backRotation=sVideoPathBack;
               new RotationBackground(getActivity()
                       , sFilepath, camersRotation, 2, FFCommandCreator.getOutputVideoSizeString(positionVariant), new AfterDoneBackground() {
                   @Override
                   public void onAfterDone() {
                       Log.d(LOG_TAG, "start join video");
                       readyFilePath = Utility.getOutputMediaFile(Utility.MEDIA_TYPE_VIDEO, splitActivity).toString();
                       new TranscdingBackground(
                               getActivity()
                               , camersRotation,
                               backRotation,
                               readyFilePath,
                               onHeadsetConnect,
                               positionVariant,
                               new AfterDoneBackground() {
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

                   @Override
                   public void onCancel() {
                       removeTempFile();
                       splitActivity.finish();
                   }
               }).runTranscoding();


            } else {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is
                    // prepared,
                    // now you can start recording
                    ivVideoPreview.setVisibility(GONE);
                    mMediaRecorder.start();

//                    layoutCameraOption.setVisibility(View.GONE);
                    textRecordSecond.setVisibility(View.VISIBLE);

                    recordTimer.start();
                    mediaPlayer.start();

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
                setCameraDisplayOrientation((Activity) splitActivity, currentCameraId, mCamera);
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
                //				Log.d(Constant.TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }


    private void startApproveActivity(String file) {
        Intent intent = new Intent(splitActivity, ApproveVideoActivity.class);
        intent.putExtra("video_path", file);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra(Constant.ENTRY, splitActivity.getEntry());
        intent.putExtra(ApproveVideoActivity.APPROVE_SPLIT_VIDEO, true);
        if (subCat != null && subCat.length() > 0) {
            intent.putExtra("subCat", subCat);
        }
        startActivity(intent);
        splitActivity.finish();
        splitActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

    }

    private void onOriginFullscreen(){

    }

    private void onOriginTop(){
        llParalelVideoPosition.setOrientation(LinearLayout.VERTICAL);
        onOriginLeft();
    }

    private void onOriginBottom(){
        llParalelVideoPosition.setOrientation(LinearLayout.VERTICAL);
        onOriginRight();
    }
}
