package com.ownzordage.chrx.lenscap;

/**
 * Created by chrx on 4/7/16.
 */

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Sample implementation of a DeviceAdminReceiver.  Your controller must provide one,
 * although you may or may not implement all of the methods shown here.
 *
 * All callbacks are on the UI thread and your implementations should not engage in any
 * blocking operations, including disk I/O.
 */
public class mDeviceAdminReceiver extends DeviceAdminReceiver {
    private final String TAG = "DeviceAdminSample";


    void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void goBack(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == ACTION_DEVICE_ADMIN_DISABLE_REQUESTED) {
            abortBroadcast();
        }
        super.onReceive(context, intent);
    }
    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, context.getString(R.string.admin_receiver_status_enabled));
        goBack(context);
    }
    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        return context.getString(R.string.admin_receiver_status_disable_warning);
    }
    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, context.getString(R.string.admin_receiver_status_disabled));
    }
//    @Override
//    public void onPasswordChanged(Context context, Intent intent) {
//        showToast(context, context.getString(R.string.admin_receiver_status_pw_changed));
//    }
//    @Override
//    public void onPasswordFailed(Context context, Intent intent) {
//        showToast(context, context.getString(R.string.admin_receiver_status_pw_failed));
//    }
//    @Override
//    public void onPasswordSucceeded(Context context, Intent intent) {
//        showToast(context, context.getString(R.string.admin_receiver_status_pw_succeeded));
//    }
//    @Override
//    public void onPasswordExpiring(Context context, Intent intent) {
//        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(
//                Context.DEVICE_POLICY_SERVICE);
//        long expr = dpm.getPasswordExpiration(
//                new ComponentName(context, mDeviceAdminReceiver.class));
//        long delta = expr - System.currentTimeMillis();
//        boolean expired = delta < 0L;
//        String message = context.getString(expired ?
//                R.string.expiration_status_past : R.string.expiration_status_future);
//        showToast(context, message);
//        Log.v(TAG, message);
//    }
}
