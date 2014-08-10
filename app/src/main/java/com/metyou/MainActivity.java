package com.metyou;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;


import com.metyou.cloud.services.model.Response;
import com.metyou.cloudapi.CloudApi;
import com.metyou.net.NetServiceManager;
import com.metyou.social.SocialProvider;
import com.metyou.cloud.services.Services;


import java.io.IOException;


public class MainActivity extends FragmentActivity /*,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener */{
    private static final String TAG = "MainActivity";

    private ViewPager viewPager;
    private AppPagerAdapter appPagerAdapter;
    private PagerSlidingTabStrip slidingTabs;
    private NetServiceManager netServiceManager;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;


//    private final static int
//            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
//    private LocationClient mLocationClient;
//    private LocationRequest mLocationRequest;

//    @Override
//    public void onConnected(Bundle bundle) {
//        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
//    }

//    @Override
//    public void onDisconnected() {
//        Toast.makeText(this, "Disconnected. Please re-connect.",
//                Toast.LENGTH_SHORT).show();
//    }

//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        if (connectionResult.hasResolution()) {
//            try {
//                connectionResult.startResolutionForResult(this, 9000);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else {
//            showErrorDialog(connectionResult.getErrorCode());
//        }
//
//    }

//    private void showErrorDialog(int errorCode) {
//
//        // Get the error dialog from Google Play services
//        Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
//                errorCode,
//                this,
//                9000);
//
//        // If Google Play services can provide an error dialog
//        if (errorDialog != null) {
//
//            // Create a new DialogFragment in which to show the error dialog
//            ErrorDialogFragment errorFragment = new ErrorDialogFragment();
//
//            // Set the dialog in the DialogFragment
//            errorFragment.setDialog(errorDialog);
//
//            // Show the error dialog in the DialogFragment
//            errorFragment.show(getSupportFragmentManager(), "MainActivity");
//        }
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
//        ((GoogleMapFragment)mapFragment).add(new LatLng(location.getLatitude(), location.getLongitude()));
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
//    }

//    public static class ErrorDialogFragment extends DialogFragment {
//        private Dialog mDialog;
//
//        public ErrorDialogFragment() {
//            super();
//            mDialog = null;
//        }
//
//        public void setDialog(Dialog dialog) {
//            mDialog = dialog;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            return mDialog;
//        }
//
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        SocialProvider.readPreferences(this);
        if (SocialProvider.currentProvider() != SocialProvider.NONE) {
            Log.d(TAG, "Already signed in! User Id: " + SocialProvider.getId());
        }
        setActionBarTabs();
        netServiceManager = new NetServiceManager(this);
        netServiceManager.setServiceDiscoveryAlarm(this);
    }

    private void setActionBarTabs() {
        appPagerAdapter = new AppPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager)findViewById(R.id.pager);
        viewPager.setAdapter(appPagerAdapter);
        slidingTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        slidingTabs.setViewPager(viewPager);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

            }
        };

        ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                getActionBar().setSelectedNavigationItem(position);
            }
        };

        for (int i = 0; i < appPagerAdapter.getCount(); i++) {
            getActionBar().addTab(getActionBar().newTab()
                    .setText(appPagerAdapter.getPageTitle(i))
                    .setTabListener(tabListener));
        }

        //slidingTabs.setOnPageChangeListener(pageChangeListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.get_resp) {
            getResponse();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getResponse() {
        final AsyncTask<Void, Void, String> getResponse = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                        MainActivity.this, CloudApi.AUDIENCE);
                AccountManager am = AccountManager.get(MainActivity.this);
                for (Account account: am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)) {
                    Log.d(TAG, account.name + " " + account.type);
                }
                credential.setSelectedAccountName(am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)[0].name);

                Services services = CloudApi.getApiServiceHandle(null);
                try {

                    Services.ServicesOperations.Register registerCommand = services.services().register();
                    Response resp = registerCommand.execute();
                    return resp.getId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                if (s != null) {
                    Log.d(TAG, "Response:" + s);
                } else {
                    Log.d(TAG, "Response: none");
                }
            }
        };
        getResponse.execute((Void)null);
    }
}
