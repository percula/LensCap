package com.ownzordage.chrx.lenscap;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

public class WidgetIntentReceiver extends BroadcastReceiver {

    private static int clickCount = 0;

    LensCapDeviceAdmin lensCapDeviceAdmin;
    ComponentName mDeviceAdminSample;
    DevicePolicyManager mDPM;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("com.example.chrx.lenscap.TOGGLE")){
            getDeviceAdmin(context);
            updateWidgetPictureAndButtonListener(context);
        }
        if(intent.getAction().equals("com.example.chrx.lenscap.UPDATE")){
            getDeviceAdmin(context);
            updateWidgetPicture(context);
        }
    }


    private void updateWidgetPictureAndButtonListener(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lens_cap_widget);
        remoteViews.setImageViewResource(R.id.widget_image, getImageToSet(context));

        //REMEMBER TO ALWAYS REFRESH YOUR BUTTON CLICK LISTENERS!!!
        remoteViews.setOnClickPendingIntent(R.id.widget_image, LensCapWidget.buildButtonPendingIntent(context));

        LensCapWidget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }

    private void updateWidgetPicture(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.lens_cap_widget);
        if (isActiveAdmin()) {
            if (mDPM.getCameraDisabled(mDeviceAdminSample)) {
                remoteViews.setImageViewResource(R.id.widget_image, R.drawable.lenscap);
            } else {
                remoteViews.setImageViewResource(R.id.widget_image, R.drawable.lens);
            }
        }
        LensCapWidget.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
    }

    private int getImageToSet(Context context) {
        int imageID;
        clickCount++;
        if (clickCount % 2 == 0) {
            imageID = R.drawable.lens;
            LensCapActivator.toggleLensCap(context);
        }
        else {
            imageID = R.drawable.lenscap;
            LensCapActivator.toggleLensCap(context);
            Toast.makeText(context, R.string.lens_cap_status_on, Toast.LENGTH_SHORT).show();
        }
        return imageID;
    }

    private void getDeviceAdmin(Context context) {
        mDPM = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(context, mDeviceAdminReceiver.class);
        lensCapDeviceAdmin = new LensCapDeviceAdmin(mDPM, mDeviceAdminSample);
    }


    public boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }


}
