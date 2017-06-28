package com.ownzordage.chrx.lenscap;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Creates a simple toggle widget
 */
public class LensCapWidget extends AppWidgetProvider {
    public static String ACTION_WIDGET_TOGGLE = "com.ownzordage.chrx.lenscap.TOGGLE";

    // Got help for widget from http://www.androidauthority.com/create-simple-android-widget-608975/
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        LensCapActivator.Status cameraStatus = LensCapActivator.getStatus(context);

        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.lens_cap_widget);

            // Add the data to the RemoteViews
            switch (cameraStatus) {
                case CAMERA_DISABLED:
                    setImage(context, R.drawable.lenscap, views, R.id.widget_image);
                    break;
                case CAMERA_ENABLED:
                    setImage(context, R.drawable.lens, views, R.id.widget_image);
                    break;
                default:
                    setImage(context, R.drawable.lens, views, R.id.widget_image);
                    break;
            }

            // Register an onClickListener
            Intent clickIntent = new Intent(context, LensCapWidget.class);
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

    private void setImage(Context context, @DrawableRes int drawableId, RemoteViews remoteViews, @IdRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            remoteViews.setImageViewResource(id, drawableId);
        } else {
            //noinspection RestrictedApi
            Drawable d = AppCompatDrawableManager.get().getDrawable(context, drawableId);
            Bitmap b = Bitmap.createBitmap(d.getIntrinsicWidth(),
                    d.getIntrinsicHeight(),
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            d.setBounds(0, 0, c.getWidth(), c.getHeight());
            d.draw(c);
            remoteViews.setImageViewBitmap(id, b);
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
        ComponentName lensCapWidgetComponent = new ComponentName(context, LensCapWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = manager.getAppWidgetIds(lensCapWidgetComponent);
        onUpdate(context,manager,appWidgetIds);
    }

}
