package com.tzpt.cloudlibrary.bean;

import com.tzpt.cloudlibrary.ui.permissions.Permission;

/**
 * Created by Administrator on 2018/1/29.
 */

public class CameraAndStoragePermission {
    public boolean cameraGranted;
    public boolean storageGranted;
    public boolean cameraShouldShowRequestPermissionRationale;
    public boolean storageShouldShowRequestPermissionRationale;

    public CameraAndStoragePermission(Permission camera, Permission storage) {
        cameraGranted = camera.granted;
        storageGranted = storage.granted;

        cameraShouldShowRequestPermissionRationale = camera.shouldShowRequestPermissionRationale;
        storageShouldShowRequestPermissionRationale = storage.shouldShowRequestPermissionRationale;
    }
}
