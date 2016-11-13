package com.ownzordage.chrx.lenscap;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;

/**
 * Created by chrx on 4/7/16.
 */
public class LensCapDeviceAdmin {
    DevicePolicyManager mDPM;
    ComponentName mDeviceAdminSample;

    public LensCapDeviceAdmin(DevicePolicyManager devicePolicyManager,ComponentName componentName) {
        mDPM = devicePolicyManager;
        mDeviceAdminSample = componentName;
    }

    /**
     * Helper to determine if we are an active admin
     */


    public void enableCameraDevice() {

    }

    public void disableCameraDevice() {

    }

    public  void toggleCameraDevice() {

    }

}
