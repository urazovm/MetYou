package com.metyou.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.metyou.MainActivity;
import com.metyou.R;
import com.metyou.social.SocialProvider;

public class NetServiceManager extends BroadcastReceiver {
    private static final String TAG = "NetServiceManager";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (activeNetInfo == null)
            return;

        if (activeNetInfo.isConnected()) {
            Log.d("Connection", "Connected - starting service DiscoveryService");
            context.startService(new Intent(context, DiscoveryService.class));
        }  else if (activeNetInfo.isConnectedOrConnecting()) {
            Log.d("Connection", "Connected or Connecting");
        } else {
            context.stopService(new Intent(context, DiscoveryService.class));
            Log.d("Connection", "Not Connected");
        }
    }

    public NetServiceManager() {
        // mandatory sample constructor for manifest
    }
}
