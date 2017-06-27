package com.ownzordage.chrx.lenscap;

import android.app.Activity;
import android.app.Application;

import org.solovyev.android.checkout.Billing;

import javax.annotation.Nonnull;

/**
 * Extend Application class to enable IAB
 */

public class MyApplication extends Application {

    private static MyApplication sInstance;

    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
        @Override
        public String getPublicKey() {
            return BuildConfig.PUBLIC_IAB_KEY_1
                    + BuildConfig.PUBLIC_IAB_KEY_2
                    + BuildConfig.PUBLIC_IAB_KEY_3
                    + BuildConfig.PUBLIC_IAB_KEY_4
                    + BuildConfig.PUBLIC_IAB_KEY_5;
        }
    });

    /**
     * Returns an instance of {@link MyApplication} attached to the passed activity.
     */
    public static MyApplication get(Activity activity) {
        return (MyApplication) activity.getApplication();
    }

    @Nonnull
    public Billing getBilling() {
        return mBilling;
    }
}
