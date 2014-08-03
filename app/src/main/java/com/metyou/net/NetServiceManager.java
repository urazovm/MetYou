package com.metyou.net;

import android.app.Activity;
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

import java.net.InetAddress;

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

        NetworkInfo.State state = activeNetInfo.getState();

        if (activeNetInfo.isConnected()) {
            Log.d("Connection", "Connected");
            registerServiceInBackground(context);
            setServiceDiscoveryAlarm(context);
        }  else if (activeNetInfo.isConnectedOrConnecting()) {
            Log.d("Connection", "Connected or Connecting");
        } else {
            cancelServiceDiscoveryAlarm(context);
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
        mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);
        discoveryStarted = false;

        initializeService(activity);
        initializeDiscoveryListener();
        initializeResolveListener();
        initializeUiHandler();
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

    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d("Net Service Discovery", "Service discovery started");
                discoveryStarted = true;
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d("Net Service Discovery", "Service discovery success " + service);
                if (!service.getServiceType().equals(serviceType)) {
                    Log.d("Net Service Discovery", "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(serviceName + SocialProvider.getId())) {
                    Log.d("Net Service Discovery", "Same machine: " + serviceName);
                } else if (service.getServiceName().contains(serviceName)){
                    //mNsdManager.resolveService(service, mResolveListener);
                    Log.d(TAG, "discovered: " + service.getServiceName());

                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e("Net Service Discovery", "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i("Net Service Discovery", "Discovery stopped: " + serviceType);
                discoveryStarted = false;
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("Net Service Discovery", "Discovery failed: Error code:" + errorCode);
                discoveryStarted = false;
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("Net Service Discovery", "Discovery failed: Error code:" + errorCode);
                discoveryStarted = false;
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                serviceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e("Net Service Discovery", "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo mServiceInfo) {
                Log.e("Net Service Discovery", "Resolve Succeeded. " + mServiceInfo);

                if (mServiceInfo.getServiceName().equals(serviceName + SocialProvider.getId())) {
                    Log.d("Net Service Discovery", "Same IP.");
                    return;
                }

                String localServiceName = mServiceInfo.getServiceName().substring(serviceName.length());
                Message serviceResolvedMessage = uiHandler.obtainMessage(0, localServiceName);

                serviceResolvedMessage.sendToTarget();
                service = mServiceInfo;
            }
        };
    }

    public boolean wifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (activeNetInfo == null)
            return false;
        return activeNetInfo.isConnected();
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
        } else {
            Log.d(TAG, "Alarm already set!");
        }
    }

    public void cancelServiceDiscoveryAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0));
    }
}
