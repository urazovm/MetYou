package metyou.p2p;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import acs.metyou.R;
import metyou.MetYou;

/**
 * Created by mihai on 7/18/14.
 */
public class P2pServiceManager {
    private final IntentFilter intentFilter = new IntentFilter();
    private final MetYou activity;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private P2pContactsReceiver receiver;
    private WifiP2pDnsSdServiceRequest serviceRequest;
    WifiP2pManager.DnsSdServiceResponseListener servListener;
    private WifiP2pManager.DnsSdTxtRecordListener txtListener;

    public P2pServiceManager(MetYou activity) {
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        this.activity = activity;
        mManager = (WifiP2pManager)activity.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(activity, activity.getMainLooper(), null);
        receiver = new P2pContactsReceiver(mManager, mChannel, activity);
        activity.registerReceiver(receiver, intentFilter);
        initServiceRequest();
    }

    public void initServiceRequest() {
        final String instanceName = activity.getString(R.string.instance_name);
        String serviceType = activity.getString(R.string.service_type);

        txtListener = new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(
                    String fullDomain, Map<String, String> record, WifiP2pDevice device) {
                String localInstanceName = fullDomain.split("\\.")[0];
                if (!instanceName.equals(localInstanceName))
                    return;
                activity.addBuddy(record.get("buddyname"));
                Log.d("onRecord", "fullDomain: " + fullDomain + " record: " + record.toString());
                Toast.makeText(activity, record.get("buddyname"), Toast.LENGTH_LONG).show();
            }
        };

        servListener = new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice resourceType) {

                Toast.makeText(activity, resourceType.deviceName, Toast.LENGTH_LONG).show();
            }
        };
        mManager.setDnsSdResponseListeners(mChannel, servListener, txtListener);
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance(instanceName, serviceType);
        mManager.addServiceRequest(mChannel,
                serviceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("Discovery Request", "success");
                    }

                    @Override
                    public void onFailure(int code) {
                        Log.d("Discovery Request", "failed");
                    }
                });
    }

    public void registerService() {
        Map<String, String> record = new HashMap<String, String>();
        record.put("buddyname", "Mihai");

        WifiP2pDnsSdServiceInfo serviceInfo =
                WifiP2pDnsSdServiceInfo.newInstance("MetYou", activity.getString(R.string.service_type), record);

        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d("Local Service Registration", "success");
            }

            @Override
            public void onFailure(int arg0) {
                Log.d("Local Service Registration", "failed");
            }
        });
    }

    public void discoverPeers() {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(activity, "Discovery Initiated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(activity, "Discovery Failed: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void discoverServices() {
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

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

    public void unregisterReceiver() {
        activity.unregisterReceiver(receiver);
    }
}
