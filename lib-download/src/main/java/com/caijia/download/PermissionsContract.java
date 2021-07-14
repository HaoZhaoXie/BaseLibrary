package com.caijia.download;

import android.app.Activity;

public interface PermissionsContract {
    /**
     * 请求权限
     *
     * @param permissions 权限组
     * @param requestCode 请求code
     * @param listener    监听器
     */
    void doRequestPermissions(Activity context, String[] permissions, int requestCode, PermissionListener listener);

    /**
     * 权限申请监听
     * Created by April丶 on 2017/3/15.
     */
    interface PermissionListener {

        /**
         * 权限准许
         *
         * @param requestCode 请求码
         */
        void onPermissionsGranted(int requestCode);

        /**
         * 权限拒绝
         *
         * @param requestCode 请求码
         */
        void onPermissionsDenied(int requestCode);

    }
}
