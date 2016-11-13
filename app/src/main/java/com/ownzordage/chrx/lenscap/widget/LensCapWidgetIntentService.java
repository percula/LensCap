package com.ownzordage.chrx.lenscap.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ownzordage.chrx.lenscap.LensCapActivator;
import com.ownzordage.chrx.lenscap.R;

/**
 * Created by peter on 11/13/16.
 */

public class LensCapWidgetIntentService extends IntentService {


    public LensCapWidgetIntentService() {
        super("LensCapWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                LensCapWidgetProvider.class));

        // Toggle the lens cap
        LensCapActivator.toggleLensCap(this);

        // Get data
        LensCapActivator.Status cameraStatus = LensCapActivator.getStatus(this);

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            // Get the widget layout
            int layoutId = R.layout.lens_cap_widget;

            // Get the remoteviews object
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            switch (cameraStatus) {
                case CAMERA_DISABLED:
                    views.setImageViewResource(R.id.widget_image,R.id.lensCapOnButton);
                    break;
                case CAMERA_ENABLED:
                    views.setImageViewResource(R.id.widget_image,R.id.lensCapOffButton);
                    break;
                default:
                    views.setImageViewResource(R.id.widget_image,R.id.lensCapOffButton);
                    break;
            }

            // Register an onClickListener
            Intent clickIntent = new Intent();
            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    getApplicationContext(), 0, clickIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
