package com.strings.layne.lleasypicture;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * CheckPermission
 * Created by laynehuang on 2016/12/6.
 */

public class CheckPermission {
    private final Context context;

    /*
        构造器
     */
    public CheckPermission(Context context) {
        this.context = context.getApplicationContext();
    }

    /*
        检查权限时,判断系统的权限集合
     */
    public boolean permissionSet(String... permissions) {
        for (String permission : permissions) {
            if (isLackPermission(permission)) {
                return true;
            }
        }
        return false;
    }
    /*
        检查系统权限，判断当前是否缺少权限
     */

    private boolean isLackPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }
}
