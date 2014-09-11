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
    private static final long ALARM_INTERVAL = 1000 * 60 * 15 * 2;
    private static final long ALARM_DELAY = 0;
    private MainActivity activity;
    private RegistrationListener mRegistrationListener;
    private String serviceName;
    private String serviceType;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo service;
    private Handler uiHandler;
    private boolean discoveryStarted;
    private boolean isRegistered;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );

        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (activeNetInfo == null)
            return;

        if (activeNetInfo.isConnected()) {
            Log.d("Connection", "Connected - starting service DiscoveryService");
            //registerServiceInBackground(context);
            //setServiceDiscoveryAlarm(context);
            context.startService(new Intent(context, DiscoveryService.class));
        }  else if (activeNetInfo.isConnectedOrConnecting()) {
            Log.d("Connection", "Connected or Connecting");
        } else {
            //cancelServiceDiscoveryAlarm(context);
            context.stopService(new Intent(context, DiscoveryService.class));
            Log.d("Connection", "Not Connected");
        }
    }

    private void registerServiceInBackground(Context context) {
        Intent intent = new Intent(context, DiscoverService.class);
        intent.putExtra(DiscoverService.ACTION, DiscoverService.REGISTER);
        context.startService(intent);
    }

    public interface ServicesListener {
        void onDiscoveredBuddy(String buddyId);
    }

    public NetServiceManager() {
        // mandatory sample constructor for manifest
    }

    public NetServiceManager(MainActivity activity) {
        this.activity = activity;
        discoveryStarted = false;

        initializeService(activity);
        //initializeDiscoveryListener();
        //initializeResolveListener();
        //initializeUiHandler();
        registerService(activity, activity.getResources().getInteger(R.integer.presence_port));
    }

    private void initializeService(Context context) {
        serviceName = context.getString(R.string.instance_name);
        serviceType = context.getString(R.string.service_type);
    }

    public void initializeUiHandler() {
        uiHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
//                activity.onDiscoveredBuddy(msg.obj.toString());
                return true;
            }
        });
    }

    public void registerService(Context activity, int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName + SocialProvider.getId());
        serviceInfo.setServiceType(serviceType);
        serviceInfo.setPort(port);
        if (mNsdManager == null) {
            mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);
        }
        initializeRegistrationListener();
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void unregisterService() {
        mNsdManager.unregisterService(mRegistrationListener);
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                //serviceName = NsdServiceInfo.getServiceName();
                Log.d("Local Net Service", "registration succeded: " + serviceInfo.getServiceName());
                isRegistered = true;
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d("Local Net Service", "registration failed");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                Log.d("Local Net Service", "unregistered");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d("Local Net Service", "unregistration failed - errorCode " + errorCode);
            }
        };
    }

    public void tearDown() {
        if (discoveryStarted) {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
    }

    public void setServiceDiscoveryAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE);

        if (alarmIntent == null) {
            alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, ALARM_DELAY, ALARM_INTERVAL, alarmIntent);
            Log.d(TAG, "Alarm set!");
        } else {
            Log.d(TAG, "Alarm already set!");
        }
    }

    public void cancelServiceDiscoveryAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE);
        if (alarmIntent != null) {
            alarmManager.cancel(alarmIntent);
            alarmIntent.cancel();
            Log.d(TAG, "Alarm canceled!");
        }
    }
}
