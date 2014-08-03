package com.metyou.net;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.metyou.R;
import com.metyou.social.SocialProvider;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by mihai on 8/2/14.
 */
public class DiscoverService extends IntentService {

    public final static String ACTION = "ACTION";
    public final static String REGISTER = "REGISTER";
    public final static String DISCOVER = "DISCOVER";
    private NsdManager mNsdManager;
    private String serviceName;
    private String serviceType;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.RegistrationListener mRegistrationListener;
    private Boolean finishRegistering;

    public DiscoverService () {
        super("DiscoverService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        serviceName = getApplicationContext().getString(R.string.instance_name);
        serviceType = getApplicationContext().getString(R.string.service_type);
        if (intent.getStringExtra(ACTION).equals(REGISTER)) {
            registerService(getResources().getInteger(R.integer.presence_port));
        } else if (intent.getStringExtra(ACTION).equals(DISCOVER)) {

            mNsdManager = (NsdManager) getApplicationContext().getSystemService(Context.NSD_SERVICE);
            // Instantiate a new DiscoveryListener
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
                        //mNsdManager.resolveService(service, mResolveListener);
                        Log.d("Net Service Discovery", "discovered: " + service.getServiceName());

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

            ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
            executor.schedule(new Runnable() {
                @Override
                public void run() {
                    mNsdManager.stopServiceDiscovery(mDiscoveryListener);
                }
            }, 5, TimeUnit.SECONDS);
        }
    }

    public void initializeRegistrationListener() {
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
    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        SocialProvider.readPreferences(this);
        serviceInfo.setServiceName(serviceName + SocialProvider.getId());
        serviceInfo.setServiceType(serviceType);
        serviceInfo.setPort(port);
        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        initializeRegistrationListener();
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }
}
