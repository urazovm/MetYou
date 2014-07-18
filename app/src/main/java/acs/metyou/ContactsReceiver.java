package acs.metyou;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ContactsReceiver extends BroadcastReceiver {
    private final WifiP2pManager mManager;
    private final Channel mChannel;
    private MetYou activity;
    private PeerListListener peerListListener;
    private List<String> peerList;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

    public ContactsReceiver(WifiP2pManager manager, Channel channel, MetYou activity) {
        super();
        this.mManager = manager;
        this.activity = activity;
        this.mChannel = channel;
        peerList = new ArrayList();
        listView = (ListView) activity.findViewById(R.id.peer_list);
        arrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, peerList);
        listView.setAdapter(arrayAdapter);

        peerListListener = new PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {

                peerList.clear();
                for (WifiP2pDevice dev:peers.getDeviceList()) {
                    peerList.add(dev.deviceAddress);
                }
                arrayAdapter.notifyDataSetChanged();
            }
        };
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if(mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        }
    }
}
