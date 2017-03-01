package com.ownzordage.chrx.lenscap;

import android.app.DialogFragment;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import static com.ownzordage.chrx.lenscap.LensCapActivator.disableDeviceAdmin;

public class MainActivity extends AppCompatActivity {
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

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
                LensCapActivator.toggleLensCap(mContext); //true
                updateUI();
            }
        });

        // Start Analytics tracking
//        ((MyApplication) getApplication()).startTracking();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            menu.findItem(R.id.action_qs_youtube).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_disable_device_admin:
                disableDeviceAdmin(mContext);
                return true;
            case R.id.action_uninstall:
                if (LensCapActivator.getStatus(mContext) != LensCapActivator.Status.DEVICE_ADMIN_DISABLED) {
                    disableDeviceAdmin(mContext);
                } else {
                    Uri packageUri = Uri.parse("package:com.ownzordage.chrx.lenscap");
                    Intent uninstallIntent =
                            new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
                    startActivity(uninstallIntent);
                }
                return true;
            case R.id.action_qs_youtube:
                watchYoutubeVideo("ZdsKdM-IMiQ");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
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

        LensCapActivator.Status cameraStatus = LensCapActivator.getStatus(mContext);

        switch (cameraStatus) {
            case CAMERA_DISABLED:
                setAdminButton.setText(R.string.enable_device_admin_button_enabled);
                setAdminButton.setEnabled(false);
                imageButton.setEnabled(true);

                lensCapOffButton.setEnabled(true);
                lensCapOnButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_on));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_on2));

                imageButton.setImageResource(R.drawable.lenscap);
                break;
            case CAMERA_ENABLED:
                setAdminButton.setText(R.string.enable_device_admin_button_enabled);
                setAdminButton.setEnabled(false);
                imageButton.setEnabled(true);

                lensCapOnButton.setEnabled(true);
                lensCapOffButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_off));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_off2));

                imageButton.setImageResource(R.drawable.lens);
                break;
            default:
                setAdminButton.setText(R.string.enable_device_admin_button);
                setAdminButton.setEnabled(true);

                lensCapOnButton.setEnabled(false);
                lensCapOffButton.setEnabled(false);

                imageButton.setEnabled(false);
                imageButton.setImageResource(R.drawable.lens);
                break;
        }

        updateWidget();
    }

    private void updateWidget() {
        // Register an onClickListener
        Log.v("updateWidget", "START");
        Intent intent = new Intent(this, LensCapWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}