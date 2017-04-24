package com.strings.layne.lleasypicture;


import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * LoginActivity
 * usage : deal with all of the permission
 * Created by laynehuang on 2017/4/24.
 */

public class LoginActivity extends AppCompatActivity {

    /*
        配置权限
     */
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_CODE = 0;
    private CheckPermission checkPermission;


    static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WAKE_LOCK,              // 屏幕贴近唤醒
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        checkPermission = new CheckPermission(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission.permissionSet(PERMISSION)) {
            startPermissionActivity();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*
        进入权限设置页面
     */
    private void startPermissionActivity() {
        PermissionActivity.startActivityForResult(this, REQUEST_CODE, PERMISSION);
    }
}
