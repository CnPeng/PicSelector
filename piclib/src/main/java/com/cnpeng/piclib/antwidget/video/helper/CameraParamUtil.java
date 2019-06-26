package com.cnpeng.piclib.antwidget.video.helper;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 445263848@qq.com.
 */

public class CameraParamUtil {
    private static final String TAG = "CameraParamUtil";
    private static CameraParamUtil cameraParamUtil = null;
    private CameraSizeComparator sizeComparator = new CameraSizeComparator();

    private CameraParamUtil() {

    }

    public static CameraParamUtil getInstance() {
        if (cameraParamUtil == null) {
            cameraParamUtil = new CameraParamUtil();
            return cameraParamUtil;
        } else {
            return cameraParamUtil;
        }
    }

    public static List<Size> getSupportedVideoSizes(Camera.Parameters parameters) {
        if (parameters.getSupportedVideoSizes() != null) {
            return parameters.getSupportedVideoSizes();
        } else {
            // Video sizes may be null, which indicates that all the supported
            // preview sizes are supported for video recording.
            return parameters.getSupportedPreviewSizes();
        }
    }

    public static Camera.Size getCurrentScreenSize(int width, List<Camera.Size> sizeList, int n) {
        if (sizeList != null && sizeList.size() > 0) {
            int screenWidth = width * n;
            int[] arry = new int[sizeList.size()];
            int temp = 0;
            for (Size size : sizeList) {
                arry[temp++] = Math.abs(size.width - screenWidth);
            }
            temp = 0;
            int index = 0;
            for (int i = 0; i < arry.length; i++) {
                if (i == 0) {
                    temp = arry[i];
                    index = 0;
                } else {
                    if (arry[i] < temp) {
                        index = i;
                        temp = arry[i];
                    }
                }
            }
            return sizeList.get(index);
        }
        return null;
    }

    public Size getPreviewSize(List<Size> list, int th, float rate) {
        Collections.sort(list, sizeComparator);
        int i = 0;
        for (Size s : list) {
            if ((s.width > th) && equalRate(s, rate)) {
                Log.i(TAG, "MakeSure Preview :w = " + s.width + " h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            return getBestSize(list, rate);
        } else {
            return list.get(i);
        }
    }

    public Size getPictureSize(List<Size> list, int th, float rate) {
        Collections.sort(list, sizeComparator);

        int i = 0;
        for (Size s : list) {
            if ((s.width > th) && equalRate(s, rate)) {
                Log.i(TAG, "MakeSure Picture :w = " + s.width + " h = " + s.height);
                break;
            }
            i++;
        }
        if (i == list.size()) {
            return getBestSize(list, rate);
        } else {
            return list.get(i);
        }
    }

    public Size getBestSize(List<Size> list, float rate) {
        float previewDisparity = 100;
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            Size cur = list.get(i);
            float prop = (float) cur.width / (float) cur.height;
            if (Math.abs(rate - prop) < previewDisparity) {
                previewDisparity = Math.abs(rate - prop);
                index = i;
            }
        }
        return list.get(index);
    }

    public boolean equalRate(Size s, float rate) {
        float r = (float) (s.width) / (float) (s.height);
        if (Math.abs(r - rate) <= 0.2) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isSupportedFocusMode(List<String> focusList, String focusMode) {
        for (int i = 0; i < focusList.size(); i++) {
            if (focusMode.equals(focusList.get(i))) {
                Log.i(TAG, "FocusMode supported " + focusMode);
                return true;
            }
        }
        Log.i(TAG, "FocusMode not supported " + focusMode);
        return false;
    }

    public boolean isSupportedPictureFormats(List<Integer> supportedPictureFormats, int jpeg) {
        for (int i = 0; i < supportedPictureFormats.size(); i++) {
            if (jpeg == supportedPictureFormats.get(i)) {
                Log.i(TAG, "Formats supported " + jpeg);
                return true;
            }
        }
        Log.i(TAG, "Formats not supported " + jpeg);
        return false;
    }

    public int setCameraDisplayOrientation(Activity activity, int cameraId) {
        Camera.CameraInfo info = new Camera.CameraInfo();
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
            default:
                break;
        }

        Log.e(TAG, "info.orientation==" + info.orientation);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees) % 360;
        }
        Log.e(TAG, "setCameraDisplayOrientation==" + result);
        return result;
    }

    public class CameraSizeComparator implements Comparator<Size> {
        public int compare(Size lhs, Size rhs) {
            if (lhs.width == rhs.width) {
                return 0;
            } else if (lhs.width > rhs.width) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
