package com.ownzordage.chrx.lenscap;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by peter on 11/12/16.
 */

public class LensCapActivator {

    public enum Status {
        CAMERA_DISABLED, CAMERA_ENABLED, DEVICE_ADMIN_DISABLED
    }

    public static void toggleLensCap(Context context) {
        DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdminSample = new ComponentName(context, mDeviceAdminReceiver.class);
        String status;

        Status cameraStatus = getStatus(context);
        switch (cameraStatus) {
            case CAMERA_DISABLED:
                mDPM.setCameraDisabled(mDeviceAdminSample, false);
                status = context.getResources().getString(R.string.lens_cap_status_off);
                break;
            case CAMERA_ENABLED:
                mDPM.setCameraDisabled(mDeviceAdminSample, true);
                status = context.getResources().getString(R.string.lens_cap_status_on);
                break;
            default:
                // If no device administrator, send the user straight to the settings page with a help toast
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.DeviceAdminSettings"));
                context.startActivity(intent);
                status = context.getResources().getString(R.string.error_no_device_admin);
                break;
        }
        Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
    }

    public static Status getStatus(Context context) {
        DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName mDeviceAdminSample = new ComponentName(context, mDeviceAdminReceiver.class);
        if (mDPM.isAdminActive(mDeviceAdminSample)) {
            // If the camera is disabled and the method is told to re-enable it
            if (mDPM.getCameraDisabled(mDeviceAdminSample)) {
                return Status.CAMERA_DISABLED;
            } else {
                return Status.CAMERA_ENABLED;
            }
        } else {
            return Status.DEVICE_ADMIN_DISABLED;
        }
    }
}
