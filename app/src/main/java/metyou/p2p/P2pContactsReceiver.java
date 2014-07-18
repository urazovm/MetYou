package metyou.p2p;

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

import acs.metyou.R;
import metyou.MetYou;

public class P2pContactsReceiver extends BroadcastReceiver {
    private final WifiP2pManager mManager;
    private final Channel mChannel;
    private MetYou activity;
    private PeerListListener peerListListener;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;

    public P2pContactsReceiver(WifiP2pManager manager, Channel channel, MetYou activity) {
        super();
        this.mManager = manager;
        this.activity = activity;
        this.mChannel = channel;
        peerListListener = new PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {
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
