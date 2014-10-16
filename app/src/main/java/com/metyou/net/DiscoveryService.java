package com.metyou.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.DateTime;
import com.metyou.R;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloud.services.model.UserEncountered;
import com.metyou.cloud.services.model.UsersBatch;
import com.metyou.cloudapi.CloudApi;
import com.metyou.social.SocialProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mihai on 9/11/14.
 */
public class DiscoveryService extends Service {

    private static final long ALARM_INTERVAL = 1000 * 60 * 15 * 2;
    private static final long ALARM_DELAY = 1000 * 30;
    public final static String DISCOVER = "DISCOVER";

    private static final String TAG = "DiscoveryService";
    private String serviceName;
    private String serviceType;
    private NsdManager mNsdManager;
    private int port;
    private NsdManager.RegistrationListener mRegistrationListener;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private Handler handler;
    private Runnable discoverRunnable;
    private CloudApi cloudApi;

    @Override
    public void onCreate() {
        super.onCreate();
        cloudApi = CloudApi.getCloudApi(null);

        Log.d(TAG, "Service Created");
        handler = new Handler(Looper.getMainLooper());
        serviceName = getApplicationContext().getString(R.string.instance_name);
        serviceType = getApplicationContext().getString(R.string.service_type);
        port = getResources().getInteger(R.integer.presence_port);
        mNsdManager = (NsdManager) getApplicationContext().getSystemService(Context.NSD_SERVICE);
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                Log.d("Local Net Service", "registration succeded: " + serviceInfo.getServiceName());
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

        discoverRunnable = new Runnable() {
            @Override
            public void run() {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            }
        };

        registerService();
        setServiceDiscoveryAlarm(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }
        if (intent.getAction() != null && intent.getAction().equals(DISCOVER)) {
            discoverNetUsers();
            Log.d(TAG, "discover peers");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mNsdManager.unregisterService(mRegistrationListener);
        cancelServiceDiscoveryAlarm(this);
        Log.d(TAG, "Service Stopped");
        handler.removeCallbacks(discoverRunnable);
        super.onDestroy();
    }

    public void registerService() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        SocialProvider.init(getApplicationContext());
        serviceInfo.setServiceName(serviceName + SocialProvider.getId());
        serviceInfo.setServiceType(serviceType);
        serviceInfo.setPort(port);
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }


    public void setServiceDiscoveryAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), PendingIntent.FLAG_NO_CREATE);

        if (alarmIntent == null) {
            alarmIntent = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + ALARM_DELAY, ALARM_INTERVAL, alarmIntent);
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

    private void discoverNetUsers() {
        final List<UserEncountered> userEncounteredList = new ArrayList<UserEncountered>();

        mDiscoveryListener = new NsdManager.DiscoveryListener() {

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d("Net Service Discovery", "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d("Net Service Discovery", "Service discovery success " + service);
                if (!service.getServiceType().equals(serviceType)) {
                    Log.d("Net Service Discovery", "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(serviceName + SocialProvider.getId())) {
                    Log.d("Net Service Discovery", "Same machine: " + serviceName);
                } else if (service.getServiceName().contains(serviceName)) {
                    Log.d("Net Service Discovery", "discovered: " + service.getServiceName());
                    UserEncountered userEncountered = new UserEncountered();
                    userEncountered.setKey(serviceToKey(service.getServiceName()));
                    userEncounteredList.add(userEncountered);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                Log.e("Net Service Discovery", "service lost" + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i("Net Service Discovery", "Discovery stopped: " + serviceType);
                UsersBatch usersBatch = new UsersBatch();
                usersBatch.setUsers(userEncounteredList);
                usersBatch.setKey(SocialProvider.getId());
                cloudApi.insertEncounteredUsers(usersBatch, null);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("Net Service Discovery", "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e("Net Service Discovery", "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };

        mNsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        handler.postDelayed(discoverRunnable, 10000);
    }

    public Long serviceToKey(String serviceName) {
        return Long.parseLong(serviceName.substring(6));
    }
}
