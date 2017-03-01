package com.ownzordage.chrx.lenscap;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;


@TargetApi(Build.VERSION_CODES.N)
public class LensCapTileService extends TileService {
    private boolean mIsCameraActive;
    private BroadcastReceiver QsReceiver;

    public LensCapTileService() {
    }

    @Override
    public void onClick() {
        LensCapActivator.toggleLensCap(this);
        updateTile();

//        // Get Google Analytics tracker
//        Tracker tracker = ((MyApplication) this.getApplicationContext()).getTracker();
//
//        // Set screen name.
//        tracker.setScreenName("Quick Settings Tile");
//
//        // Send a screen view.
//        tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }

    @Override
    public void onStartListening() {
        updateTile();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        final IntentFilter QsIntentFilter = new IntentFilter();
        QsIntentFilter.addAction(LensCapWidget.ACTION_WIDGET_TOGGLE);

        QsReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                updateTile();
            }
        };
        // Registers the receiver so that your service will listen for
        // broadcasts
        this.registerReceiver(QsReceiver, QsIntentFilter);
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();

//        // Get Google Analytics tracker
//        Tracker tracker = ((MyApplication) this.getApplicationContext()).getTracker();
//
//        // Send a screen view.
//        tracker.send(new HitBuilders.EventBuilder()
//            .setCategory("Quick Settings Tile")
//            .setAction("Added Tile")
//            .setLabel("Added Quick Settings Tile")
//            .build());
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();

//        // Get Google Analytics tracker
//        Tracker tracker = ((MyApplication) this.getApplicationContext()).getTracker();
//
//        // Send a screen view.
//        tracker.send(new HitBuilders.EventBuilder()
//                .setCategory("Quick Settings Tile")
//                .setAction("Removed Tile")
//                .setLabel("Removed Quick Settings Tile")
//                .build());
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(QsReceiver);
        super.onDestroy();
    }

    public void updateTile() {
        Tile tile = getQsTile();

        if (LensCapActivator.getStatus(this) == LensCapActivator.Status.CAMERA_DISABLED) {
            mIsCameraActive = false;
        } else {
            mIsCameraActive = true;
        }

        if (mIsCameraActive) {
            tile.setIcon(Icon.createWithResource(this, R.drawable.qs_tile_enabled));
            tile.setLabel(getString(R.string.qs_tile_title));
            tile.setState(Tile.STATE_ACTIVE);
            tile.setContentDescription(getString(R.string.qs_tile_description));
        } else {
            tile.setIcon(Icon.createWithResource(this, R.drawable.qs_tile_disabled));
            tile.setLabel(getString(R.string.qs_tile_enable));
            tile.setState(Tile.STATE_INACTIVE);
            tile.setContentDescription(getString(R.string.qs_tile_description));
        }
        tile.updateTile();
    }


}
