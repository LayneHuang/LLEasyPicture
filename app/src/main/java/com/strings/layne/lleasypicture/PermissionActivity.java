package com.strings.layne.lleasypicture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by laynehuang on 2016/12/6.
 * 用户权限获取页面，权限处理
 */
public class PermissionActivity extends AppCompatActivity {

    public static final int PERMISSION_GRANTED = 0;                                                // tag the permission
    public static final int PERMISSION_DENIED = 1;                                                 // permission is not enough
    private static final int PERMISSION_REQUEST_CODE = 0;                                          // 系统授权管理页面的结果参数
    private static final String EXTRA_PERMISSION = "com.example.laynehuang.photomanager.activity";
    private static final String PACKAGE_URL_SCHEME = "package:";                                    // 权限方案
    private CheckPermission checkPermission;                                                       // 检查权限类的权限检测器
    private boolean isrequestCheck;                                                                 // 判断是否需要系统权限检测，防止和系统提示框重叠

    /*
        Activity public Start Interface
     */
    public static void startActivityForResult(Activity activity, int requestCode, String... permissions) {
        Intent intent = new Intent(activity, PermissionActivity.class);
        intent.putExtra(EXTRA_PERMISSION, permissions);
        ActivityCompat.startActivityForResult(activity, intent, requestCode, null);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        if (getIntent() == null || !getIntent().hasExtra(EXTRA_PERMISSION)) {
            throw new RuntimeException("当前Activity需要使用静态的StartActivityForResult方法启动");
        }
        checkPermission = new CheckPermission(this);
        isrequestCheck = true;
    }

    /*
        check the authorization after first one
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isrequestCheck) {
            String[] permissions = getPermissions();
            if (checkPermission.permissionSet(permissions)) {
                requestPermissions(permissions);
            } else {
                allPermissionGranted();                                                             //get all the permission
            }
        } else {
            isrequestCheck = true;
        }
    }

    /*
        get all permission
     */
    private void allPermissionGranted() {
        setResult(PERMISSION_GRANTED);
        finish();
    }

    /*
        request permission to campatible Version
     */
    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    /*
        return the paramters
     */
    private String[] getPermissions() {
        return getIntent().getStringArrayExtra(EXTRA_PERMISSION);
    }

    /*
        use for permission management
        if had all permission , directly access in
        if denieg , lack of permission , dialog will hint
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode && hasAllPermissionGranted(grantResults)) {
            isrequestCheck = true;
            allPermissionGranted();
        } else {
            isrequestCheck = false;
            showMissingPermissionDialog();
        }
    }

    //显示对话框提示用户缺少权限
    private void showMissingPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setTitle(R.string.help);//提示帮助
        builder.setMessage(R.string.string_help_text);

        //如果是拒绝授权，则退出应用
        //退出
        builder.setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(PERMISSION_DENIED);//权限不足
                finish();
            }
        });
        //打开设置，让用户选择打开权限
        builder.setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();//打开设置
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    //获取全部权限
    private boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    //打开系统应用设置(ACTION_APPLICATION_DETAILS_SETTINGS:系统设置权限)
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

}
