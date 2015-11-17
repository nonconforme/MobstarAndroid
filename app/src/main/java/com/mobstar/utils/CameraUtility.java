package com.mobstar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.view.Surface;

import com.mobstar.home.split.position_variants.PositionVariant;

import java.util.List;

/**
 * Created by lipcha on 06.10.15.
 */
public class CameraUtility {

    public static final int ORIENTETION_NO = -1;
    public static final int ORIENTATION_UP = 1;
    public static final int ORIENTATION_RIGHT = 2;
    public static final int ORIENTATION_LEFT = 3;
    public static final int ORIENTATION_DOWN = 4;

    public static Camera getVideoCameraInstance(int currentCameraId) {
        Camera c = null;
        try {
            c = Camera.open(currentCameraId); // attempt to get a Camera
            Camera.Parameters params = c.getParameters();

            final List<String> flashModes = params.getSupportedFlashModes();

            if (flashModes != null && flashModes.size() > 0) {
                // set the focus mode
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }

            params.set("orientation", "portrait");
            params.setRotation(90);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            c.setParameters(params); // instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            e.printStackTrace();
        }
        return c; // returns null if camera is unavailable
    }

    public static Camera getPhotoCameraInstance(int currentCameraId){
        Camera c = null;
        try {
            c = Camera.open(currentCameraId); // attempt to get a Camera
            Camera.Parameters params = c.getParameters();
            params.set("orientation", "portrait");
            params.setRotation(90);
            c.setParameters(params); // instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height, PositionVariant positionVariant) {
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
        }
        return (result);
    }

    public static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size result = null;
        for (Camera.Size size : sizes) {
            if (size.width <= width || size.height <= height) {
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

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {

        final Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
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

        camera.setDisplayOrientation(result);
    }

    public static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static void releaseCamera(Camera mCamera) {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    public static int getOrientation(float angle) {
        int result=ORIENTETION_NO;
        if (-45<angle && angle < 45) //up
          result = ORIENTATION_UP;
        else if (45<angle && angle < 135) //right
            result = ORIENTATION_RIGHT;
        else if (-45>angle && angle > -135) //left
            result = ORIENTATION_LEFT;
        else if (-135 > angle || angle > 135) //down
            result = ORIENTATION_DOWN;
        return result;
    }
}
