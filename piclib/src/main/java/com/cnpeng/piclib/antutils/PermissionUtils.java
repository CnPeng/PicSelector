package com.cnpeng.piclib.antutils;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by yxj on 16/11/23.
 * shouldShowRequestPermissionRationale第一次请求权限是false，选中了不再提醒后也是false,不是第一次，并且没选中不再提醒，是true
 */

public class PermissionUtils {
    private static final String PREFS_FILE_NAME             = "preference_permission";
    private static final String PREFS_FIRST_TIME_KEY        = "is_app_launched_first_time";
    public static final  int    CODE_RECORD_AUDIO           = 0;
    public static final  int    CODE_GET_ACCOUNTS           = 1;
    public static final  int    CODE_READ_PHONE_STATE       = 2;
    public static final  int    CODE_CALL_PHONE             = 3;
    public static final  int    CODE_CAMERA                 = 4;
    public static final  int    CODE_ACCESS_FINE_LOCATION   = 5;
    public static final  int    CODE_ACCESS_COARSE_LOCATION = 6;
    public static final  int    CODE_READ_EXTERNAL_STORAGE  = 7;
    public static final  int    CODE_WRITE_EXTERNAL_STORAGE = 8;
    public static final  int    CODE_MULTI_PERMISSION       = 100;

    public static final String PERMISSION_RECORD_AUDIO           = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_GET_ACCOUNTS           = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_PHONE_STATE       = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CALL_PHONE             = Manifest.permission.CALL_PHONE;
    public static final String PERMISSION_CAMERA                 = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION   = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE  = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private static final String[] requestPermissions = {PERMISSION_RECORD_AUDIO, PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_PHONE_STATE, PERMISSION_CALL_PHONE, PERMISSION_CAMERA, PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_READ_EXTERNAL_STORAGE, PERMISSION_WRITE_EXTERNAL_STORAGE};

    private static final String[] requestPermissionsStr = {"在设置-应用-校园集结号中开启录音权限,以正常使用聊天等功能", "在设置-应用-校园集结号中开启用户权限," +
            "以正常使用校园集结号功能", "在设置-应用-校园集结号中开启读取手机状态权限,以正常使用校园集结号功能", "在设置-应用-校园集结号中开启打电话状态权限,以正常使用校园集结号功能",
            "在设置-应用-校园集结号中开启相机状态权限,以正常使用聊天、扫描二维码等功能", "在设置-应用-校园集结号中开启位置权限,以正常使用附近同学功能", "在设置-应用-校园集结号中开启位置权限," +
            "以正常使用附近同学功能", "在设置-应用-校园集结号中开启存储空间权限,以正常使用校园集结号功能", "在设置-应用-校园集结号中开启存储空间权限,以正常使用校园集结号功能"};

    // preference utility methods
    private static boolean getApplicationLaunchedFirstTime(Activity activity, int requestCode) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREFS_FIRST_TIME_KEY + requestCode, true);
    }

    private static void setApplicationLaunchedFirstTime(Activity activity, int requestCode) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREFS_FIRST_TIME_KEY + requestCode, false);
        editor.commit();
    }


    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    public static boolean lackStoragePermissions(Activity activity) {//判断是不是有内存权限
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        return ActivityCompat.checkSelfPermission(activity, PERMISSION_WRITE_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED;
    }

    public static boolean lackRecordAudioPermissions(Activity activity) {//判断是不是有内存权限
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        return ActivityCompat.checkSelfPermission(activity, PERMISSION_RECORD_AUDIO) != PackageManager
                .PERMISSION_GRANTED;
    }

    public static boolean lackCameraPermissions(Activity activity) {//判断是不是有内存权限
        if (Build.VERSION.SDK_INT < 23) {
            return false;
        }
        return ActivityCompat.checkSelfPermission(activity, PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED;
    }


    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        requestPermission(activity, requestCode, permissionGrant, false);
    }

    /**
     * Requests permission.
     *
     * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is
     *                    PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission(final Activity activity, final int requestCode,
                                         PermissionGrant permissionGrant, final boolean finish) {
        if (activity == null) {
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            return;
        }

        final String requestPermission = requestPermissions[requestCode];

        //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown 
        // exception code: 1 msg null)，
        if (Build.VERSION.SDK_INT < 23) {
            permissionGrant.onPermissionGranted(requestCode);
            return;
        }

        int checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                shouldShowRationale(activity, requestCode, requestPermission);
            } else {
                if (getApplicationLaunchedFirstTime(activity, requestCode)) {
                    //第一次获取权限，显示dialog
                    setApplicationLaunchedFirstTime(activity, requestCode);
                    shouldShowRationale(activity, requestCode, requestPermission);
                } else {
                    new AlertDialog.Builder(activity).setCancelable(false).setTitle("权限申请").setMessage
                            (requestPermissionsStr[requestCode]).setPositiveButton("去设置", new DialogInterface
                            .OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSettingActivity(activity);
                            if (finish) {
                                activity.finish();
                            }
                        }
                    }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (finish) {
                                activity.finish();
                            }
                        }
                    }).create().show();
                }
            }
        } else {
            permissionGrant.onPermissionGranted(requestCode);
        }
    }

    /**
     * Requests permission.
     *
     * @param requestCode request code, e.g. if you need request CAMERA permission,parameters is
     *                    PermissionUtils.CODE_CAMERA
     */
    public static void requestPermission2(final Activity activity, final int requestCode,
                                          PermissionGrant permissionGrant) {
        requestPermission2(activity, requestCode, permissionGrant, false);
    }

    public static void requestPermission2(final Activity activity, final int requestCode,
                                          PermissionGrant permissionGrant, final boolean finish) {
        if (activity == null) {
            return;
        }

        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            return;
        }

        final String requestPermission = requestPermissions[requestCode];

        //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown
        // exception code: 1 msg null)，
        if (Build.VERSION.SDK_INT < 23) {
            permissionGrant.onPermissionGranted(requestCode);
            return;
        }

        int checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                shouldShowRationale(activity, requestCode, requestPermission);
            } else {
                //                if (getApplicationLaunchedFirstTime(activity, requestCode)) {
                //                    //第一次获取权限，显示dialog
                //                    setApplicationLaunchedFirstTime(activity, requestCode);
                //                    shouldShowRationale(activity, requestCode, requestPermission);
                //                } else {
                new AlertDialog.Builder(activity)
                        .setCancelable(false)
                        .setTitle("权限申请")
                        .setMessage(requestPermissionsStr[requestCode])
                        .setPositiveButton("去设置", (dialog, which) -> {
                            openSettingActivity(activity);
                            if (finish) {
                                activity.finish();
                            }
                        })
                        .setNegativeButton("取消", (dialog, which) -> {
                            if (finish) {
                                activity.finish();
                            }
                        }).create().show();
                //                }
            }
        } else {
            permissionGrant.onPermissionGranted(requestCode);
        }
    }

    private static void shouldShowRationale(final Activity activity, final int requestCode,
                                            final String requestPermission) {
        ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
    }

    /**
     * @param requestCode Need consistent with requestPermission
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode,
                                                @NonNull String[] permissions,
                                                @NonNull int[] grantResults,
                                                PermissionGrant permissionGrant) {
        requestPermissionsResult(activity, requestCode, permissions, grantResults, permissionGrant, false);
    }

    public static void requestPermissionsResult(final Activity activity, final int requestCode,
                                                @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant,
                                                final boolean finish) {
        if (activity == null) {
            return;
        }
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Toast.makeText(activity, "illegal requestCode:" + requestCode, Toast.LENGTH_SHORT).show();
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGrant.onPermissionGranted(requestCode);
        } else {
            new AlertDialog.Builder(activity)
                    .setCancelable(false)
                    .setTitle("权限申请").setMessage(requestPermissionsStr[requestCode])
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openSettingActivity(activity);
                            if (finish) {
                                activity.finish();
                            }
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (finish) {
                                activity.finish();
                            }
                        }
                    })
                    .create()
                    .show();
        }
    }

    private static void openSettingActivity(final Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }


    /**
     * 判断摄像头是否可用 * 主要针对6.0 之前的版本，现在主要是依靠try...catch... 报错信息，感觉不太好， * 以后有更好的方法的话可适当替换 * * @return
     */
    public static boolean permissiontoolbefore23() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5 做的。MX5 通过Camera.open() 拿到的Camera
            // 对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }
        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;
    }
}
