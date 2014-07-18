package acs.metyou;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;


public class MetYou extends Activity {

    private static final int SERVER_PORT = 8000;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel mChannel;
    private boolean isWifiP2pEnabled;
    private ContactsReceiver receiver;
    private WifiP2pManager mManager;
    private HashMap<String, String> buddies;
    private WifiP2pDnsSdServiceRequest serviceRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_contacts);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        buddies = new HashMap<String, String>();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.share_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new ContactsReceiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
        startRegistration();
        mManager.discoverPeers(mChannel, new ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MetYou.this, "Discovery Initiated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MetYou.this, "Discovery Failed: " + reason, Toast.LENGTH_SHORT).show();
            }
        });

        discoverService();
    }

    public void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", "Mihai");
        record.put("available", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);

        mManager.addLocalService(mChannel, serviceInfo, new ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(MetYou.this, "Register successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int arg0) {
                Toast.makeText(MetYou.this, "Register failed", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void discoverService() {
        DnsSdTxtRecordListener txtListener;
        txtListener = new DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map<String, String> record, WifiP2pDevice device) {
                buddies.put(device.deviceAddress, record.get("buddyname"));
                Log.d("onRecord", record.toString());
                Toast.makeText(MetYou.this, record.get("buddyname"), Toast.LENGTH_LONG).show();
            }
        };

        DnsSdServiceResponseListener servListener = new DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                // Update the device name with the human-friendly version from
                // the DnsTxtRecord, assuming one arrived.
                resourceType.deviceName = buddies
                        .containsKey(resourceType.deviceAddress) ? buddies
                        .get(resourceType.deviceAddress) : resourceType.deviceName;

                Toast.makeText(MetYou.this, resourceType.deviceName, Toast.LENGTH_LONG).show();
            }
        };

        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);

        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel,
                serviceRequest,
                new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Discovery Request", "success");
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.d("Discovery Request", "failed");
                    }
                });

        mManager.discoverServices(mChannel, new ActionListener() {

            @Override
            public void onSuccess() {
                Log.d("Discovery", "success");
            }

            @Override
            public void onFailure(int code) {
                Log.d("Discovery", "failed");
            }
        });
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
}
