package com.ownzordage.chrx.lenscap;

import android.app.DialogFragment;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    LensCapDeviceAdmin lensCapDeviceAdmin;
    ComponentName mDeviceAdminSample;
    DevicePolicyManager mDPM;
    WidgetIntentReceiver widgetIntentReceiver;
    Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mDPM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDeviceAdminSample = new ComponentName(mContext, mDeviceAdminReceiver.class);

        widgetIntentReceiver = new WidgetIntentReceiver();
        lensCapDeviceAdmin = new LensCapDeviceAdmin(mDPM, mDeviceAdminSample);

        updateUI();

        Button setAdminButton = (Button) findViewById(R.id.enable_device_admin);
        Button lensCapOnButton = (Button) findViewById(R.id.lensCapOnButton);
        Button lensCapOffButton = (Button) findViewById(R.id.lensCapOffButton);
        ImageButton imageButton = (ImageButton) findViewById(R.id.mainToggleButton);

        setAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new DeviceAdminDialog();
                newFragment.show(getFragmentManager(), "deviceAdmin");
            }
        });

        lensCapOnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LensCapActivator.toggleLensCap(mContext); //false
                updateUI();
            }
        });

        lensCapOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LensCapActivator.toggleLensCap(mContext); //true
                updateUI();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Implement a click
            }
        });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        Button setAdminButton = (Button) findViewById(R.id.enable_device_admin);
        Button lensCapOnButton = (Button) findViewById(R.id.lensCapOnButton);
        Button lensCapOffButton = (Button) findViewById(R.id.lensCapOffButton);
        ImageButton imageButton = (ImageButton) findViewById(R.id.mainToggleButton);
        TextView lensCapStatus = (TextView) findViewById(R.id.lensCapStatus);
        TextView lensCapStatus2 = (TextView) findViewById(R.id.lensCapStatus2);

        if (isActiveAdmin()) {
            setAdminButton.setText(R.string.enable_device_admin_button_enabled);
            setAdminButton.setEnabled(false);

            imageButton.setClickable(true);
            if (mDPM.getCameraDisabled(mDeviceAdminSample)) {
                lensCapOffButton.setEnabled(true);
                lensCapOnButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_on));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_on2));

                imageButton.setImageResource(R.drawable.lenscap);
            } else {
                lensCapOnButton.setEnabled(true);
                lensCapOffButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_off));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_off2));

                imageButton.setImageResource(R.drawable.lens);
            }
        } else {
            setAdminButton.setText(R.string.enable_device_admin_button);
            setAdminButton.setEnabled(true);

            lensCapOnButton.setEnabled(false);
            lensCapOffButton.setEnabled(false);

            imageButton.setEnabled(false);
            imageButton.setImageResource(R.drawable.lens);
        }

        updateWidget();
    }

    private void updateWidget() {
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.lens_cap_widget);

        if (isActiveAdmin()) {
            if (mDPM.getCameraDisabled(mDeviceAdminSample)) {
                remoteViews.setImageViewResource(R.id.widget_image, R.drawable.lenscap);
            } else {
                remoteViews.setImageViewResource(R.id.widget_image, R.drawable.lens);
            }
            LensCapWidget.pushWidgetUpdate(this.getApplicationContext(), remoteViews);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private boolean isActiveAdmin() {
        return mDPM.isAdminActive(mDeviceAdminSample);
    }

}