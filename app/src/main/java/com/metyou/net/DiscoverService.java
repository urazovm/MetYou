package com.metyou.net;

import android.app.Activity;
import android.app.IntentService;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by mihai on 8/2/14.
 */
public class DiscoverService extends IntentService {

    public final static String ACTION = "ACTION";
    public final static String REGISTER = "REGISTER";
    public final static String DISCOVER = "DISCOVER";
    private static final String TAG = "DiscoverService";
    private NsdManager mNsdManager;
    private String serviceName;
    private String serviceType;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private NsdManager.RegistrationListener mRegistrationListener;
    private Boolean finishRegistering;
    private ArrayList<UserEncountered> usersDiscovered;

    public DiscoverService () {
        super("DiscoverService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        serviceName = getApplicationContext().getString(R.string.instance_name);
        serviceType = getApplicationContext().getString(R.string.service_type);
        if (intent.getStringExtra(ACTION).equals(REGISTER)) {
            registerService(getResources().getInteger(R.integer.presence_port), getApplicationContext());
        } else if (intent.getStringExtra(ACTION).equals(DISCOVER)) {

            usersDiscovered = new ArrayList<UserEncountered>();
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
//                        UserEncountered userEncountered = new UserEncountered();
//                        userEncountered.setUserId(serviceToKey(service.getServiceName()));
//                        userEncountered.setTimeEncountered(new DateTime(new Date()));
//                        usersDiscovered.add(userEncountered);
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

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mNsdManager.stopServiceDiscovery(mDiscoveryListener);

            if (usersDiscovered.isEmpty()){
                return;
            }
            SocialProvider.init((Activity) getApplicationContext());
            SocialIdentity socialIdentity = SocialProvider.getSocialIdentity();

            GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(
                    this, CloudApi.AUDIENCE);
            Log.d(TAG, socialIdentity.getEmail());
            credential.setSelectedAccountName(socialIdentity.getEmail());
            CloudApi cloudApi = CloudApi.getCloudApi(credential);
            UsersBatch usersBatch = new UsersBatch();
            usersBatch.setUsers(usersDiscovered);
            //usersBatch.setKey(SocialProvider.getId(this));
            cloudApi.insertEncounteredUsers(usersBatch, null);
            Log.d(TAG, "after executor");

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

    public void registerService(int port, Context context) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        SocialProvider.init(getApplicationContext());
        serviceInfo.setServiceName(serviceName + SocialProvider.getId());
        serviceInfo.setServiceType(serviceType);
        serviceInfo.setPort(port);
        mNsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);

        initializeRegistrationListener();
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public String serviceToKey(String serviceName) {
        return serviceName.substring(6);
    }
}
