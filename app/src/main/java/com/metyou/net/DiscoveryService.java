package com.metyou.net;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.IBinder;
import android.util.Log;

import com.metyou.R;
import com.metyou.social.SocialProvider;

/**
 * Created by mihai on 9/11/14.
 */
public class DiscoveryService extends Service {

    private static final String TAG = "DiscoveryService";
    private String serviceName;
    private String serviceType;
    private NsdManager mNsdManager;
    private int port;
    private NsdManager.RegistrationListener mRegistrationListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service Created");
        serviceName = getApplicationContext().getString(R.string.instance_name);
        serviceType = getApplicationContext().getString(R.string.service_type);
        port = getResources().getInteger(R.integer.presence_port);

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
        registerService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        mNsdManager.unregisterService(mRegistrationListener);
        Log.d(TAG, "Service Stopped");
        super.onDestroy();
    }

    public void registerService() {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        SocialProvider.init(getApplicationContext());
        serviceInfo.setServiceName(serviceName + SocialProvider.getId());
        serviceInfo.setServiceType(serviceType);
        serviceInfo.setPort(port);
        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

}
