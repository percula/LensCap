package com.ownzordage.chrx.lenscap;

import android.app.DialogFragment;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ownzordage.chrx.lenscap.LensCapActivator.disableDeviceAdmin;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    Context mContext;
    IInAppBillingService mService;
    ArrayList<InAppPurchaseItem> inAppPurchaseItems = new ArrayList<>();

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

        ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);

                getInAppPurchases();
            }
        };

        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

    }

    private void getInAppPurchases() {
        ArrayList<String> skuList = new ArrayList<String> ();
        skuList.add("one");
        skuList.add("two");
        skuList.add("three");
        skuList.add("four");
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList("ITEM_ID_LIST", skuList);

        GetSKUsTask getSkusTask = new GetSKUsTask();
        getSkusTask.execute(querySkus);
    }


    private class GetSKUsTask extends AsyncTask<Bundle, Void, ArrayList<InAppPurchaseItem>> {
        protected ArrayList<InAppPurchaseItem> doInBackground(Bundle ... input) {
            Bundle querySkus = input[0];
            ArrayList<InAppPurchaseItem> inAppPurchaseItems = new ArrayList<>();

            try {
                Bundle skuDetails = mService.getSkuDetails(3, getPackageName(), "inapp", querySkus);
                int response = skuDetails.getInt("RESPONSE_CODE");
                if (response == 0) {
                    ArrayList<String> responseList = skuDetails.getStringArrayList("DETAILS_LIST");

                    if (responseList != null && responseList.size() > 0) {
                        for (String thisResponse : responseList) {
                            JSONObject object = new JSONObject(thisResponse);
                            String sku = object.getString("productId");
                            String price = object.getString("price");
                            inAppPurchaseItems.add(new InAppPurchaseItem(sku, price));
                            Toast.makeText(mContext, sku + price, Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            } catch (RemoteException|JSONException e) {
                Log.e(LOG_TAG, "Error getting skus", e);
            }

            return inAppPurchaseItems;
        }

        protected void onPostExecute(ArrayList<InAppPurchaseItem> results) {
            inAppPurchaseItems.clear();
            inAppPurchaseItems.addAll(results);
        }
    }

    public void inAppPurchaseClick(View view) {
        int id = view.getId();
        if (inAppPurchaseItems.size() < 4) {
            Toast.makeText(mContext, "In app purchases not loaded properly", Toast.LENGTH_SHORT).show();
            return;
        }
        switch(id) {
            case (R.id.donate_one):
                buyInAppPurchase(inAppPurchaseItems.get(0));
                break;
            case (R.id.donate_two):
                buyInAppPurchase(inAppPurchaseItems.get(1));
                break;
            case (R.id.donate_three):
                buyInAppPurchase(inAppPurchaseItems.get(2));
                break;
            case (R.id.donate_four):
                buyInAppPurchase(inAppPurchaseItems.get(3));
                break;
        }
    }

    private void buyInAppPurchase(InAppPurchaseItem inAppPurchaseItem) {
        try {
            Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(), inAppPurchaseItem.getSku(), "inapp", "");
            if (buyIntentBundle.getInt("RESPONSE_CODE") == 0) {
                PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
                startIntentSenderForResult(pendingIntent.getIntentSender(), 1001, new Intent(),
                        Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
            }
        } catch (RemoteException|IntentSender.SendIntentException e) {
            Log.e(LOG_TAG, "Error completing in app purchase", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
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