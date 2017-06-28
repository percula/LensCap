package com.ownzordage.chrx.lenscap;

import android.app.DialogFragment;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.Billing;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.solovyev.android.checkout.Sku;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;
import static com.ownzordage.chrx.lenscap.LensCapActivator.disableDeviceAdmin;


/**
 * The primary Activity
 */
public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    Context mContext;

    private ActivityCheckout mCheckout;
    private List<String> mSkus;

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

        // Show Quick Settings promo card or hide it depending on version
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            findViewById(R.id.quick_settings_card).setVisibility(View.VISIBLE);
            findViewById(R.id.quick_settings_show_me).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    watchYoutubeVideo("ZdsKdM-IMiQ");
                }
            });
        } else {
            findViewById(R.id.quick_settings_card).setVisibility(View.GONE);
        }

        final Billing billing = MyApplication.get(this).getBilling();
        mCheckout = Checkout.forActivity(this, billing);
        mCheckout.start();
        mCheckout.createPurchaseFlow(new PurchaseListener());

        mSkus = new ArrayList<>();
        mSkus.add("one");
        mSkus.add("two");
        mSkus.add("three");
        mSkus.add("four");

        refreshInventory();
    }


    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        // your code here

    }

    private void refreshInventory() {
        final Inventory.Request request = Inventory.Request.create();
        request.loadAllPurchases();
        request.loadSkus(ProductTypes.IN_APP, mSkus);
        mCheckout.loadInventory(request, new InventoryCallback());
    }

    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(@NonNull Inventory.Products products) {
            updateIABViews(products);
        }
    }

    private void updateIABViews(Inventory.Products products) {
        final Inventory.Product product = products.get(ProductTypes.IN_APP);
        if (isSupporter(product)) {
            findViewById(R.id.donate_container).setVisibility(GONE);
            findViewById(R.id.donate_thank_you_container).setVisibility(View.VISIBLE);
        } else {
            updateIABButton(R.id.donate_one, product, mSkus.get(0));
            updateIABButton(R.id.donate_two, product, mSkus.get(1));
            updateIABButton(R.id.donate_three, product, mSkus.get(2));
            updateIABButton(R.id.donate_four, product, mSkus.get(3));
        }
    }

    private boolean isSupporter(Inventory.Product product) {
        for (String sku : mSkus) {
            if (product.isPurchased(sku)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCheckout.onActivityResult(requestCode, resultCode, data);
    }

    public void updateIABButton(int viewId, Inventory.Product product, String sku) {
        Button IABButton = (Button) findViewById(viewId);
        if (IABButton != null && product != null) {
            Sku thisSku = product.getSku(sku);
            if (thisSku != null) {
                IABButton.setText(thisSku.price);
            }
            if (product.isPurchased(sku)) {
                IABButton.setEnabled(false);
            }
        }
    }

    public void inAppPurchaseClick(View view) {
        int id = view.getId();
        switch(id) {
            case (R.id.donate_one):
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, mSkus.get(0), null, mCheckout.getPurchaseFlow());
                    }
                });
                break;
            case (R.id.donate_two):
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, mSkus.get(1), null, mCheckout.getPurchaseFlow());
                    }
                });
                break;
            case (R.id.donate_three):
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, mSkus.get(2), null, mCheckout.getPurchaseFlow());
                    }
                });
                break;
            case (R.id.donate_four):
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, mSkus.get(3), null, mCheckout.getPurchaseFlow());
                    }
                });
                break;
        }
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
                setAdminButton.setVisibility(GONE);
                imageButton.setEnabled(true);

                lensCapOffButton.setEnabled(true);
                lensCapOnButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_on));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_on2));

                imageButton.setImageResource(R.drawable.lenscap);
                break;
            case CAMERA_ENABLED:
                setAdminButton.setVisibility(GONE);
                imageButton.setEnabled(true);

                lensCapOnButton.setEnabled(true);
                lensCapOffButton.setEnabled(false);

                lensCapStatus.setText(getText(R.string.lens_cap_status_off));
                lensCapStatus2.setText(getText(R.string.lens_cap_status_off2));

                imageButton.setImageResource(R.drawable.lens);
                break;
            default:
                setAdminButton.setVisibility(View.VISIBLE);

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

}