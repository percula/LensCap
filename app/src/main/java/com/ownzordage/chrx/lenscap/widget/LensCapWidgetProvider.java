package com.ownzordage.chrx.lenscap.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.ownzordage.chrx.lenscap.LensCapActivator;
import com.ownzordage.chrx.lenscap.R;

/**
 * Implementation of App Widget functionality.
 */
public class LensCapWidgetProvider extends AppWidgetProvider {
    public static String ACTION_WIDGET_TOGGLE = "com.ownzordage.chrx.lenscap.TOGGLE";

    // Got help for widget from http://www.androidauthority.com/create-simple-android-widget-608975/
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v("onUpdate", "START");
        // Get data
        LensCapActivator.Status cameraStatus = LensCapActivator.getStatus(context);

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            // Get the remoteviews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lens_cap_widget);

            // Add the data to the RemoteViews
            switch (cameraStatus) {
                case CAMERA_DISABLED:
                    views.setImageViewResource(R.id.widget_image,R.drawable.lenscap);
                    break;
                case CAMERA_ENABLED:
                    views.setImageViewResource(R.id.widget_image,R.drawable.lens);
                    break;
                default:
                    views.setImageViewResource(R.id.widget_image,R.drawable.lens);
                    break;
            }

            // Register an onClickListener
            Intent clickIntent = new Intent(context, LensCapWidgetProvider.class);
            clickIntent.setAction(ACTION_WIDGET_TOGGLE);
            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    appWidgetIds);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("onReceive", "START");
        // Only toggle the lens cap when pressed. The widgets automatically UPDATE on system
        // start and finish, so this logic needs to be here if the setting should stay the same
        if (intent.getAction().equals(ACTION_WIDGET_TOGGLE)) {
            // Toggle the lens cap
            LensCapActivator.toggleLensCap(context);
        }

        // Update the graphics (calling Super() did not work to update the widget after
        // changing settings in MainActivity
        ComponentName lensCapWidgetComponent = new ComponentName(context, LensCapWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(lensCapWidgetComponent);
        onUpdate(context,manager,appWidgetIds);
    }
}
