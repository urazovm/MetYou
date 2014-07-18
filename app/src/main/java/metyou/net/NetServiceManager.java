package metyou.net;

import android.content.Context;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import acs.metyou.R;
import metyou.MetYou;

/**
 * Created by mihai on 7/19/14.
 */
public class NetServiceManager {
    private MetYou activity;
    private RegistrationListener mRegistrationListener;
    private String serviceName;
    private String serviceType;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;

    public NetServiceManager(MetYou activity) {
        this.activity = activity;
        serviceName = activity.getString(R.string.instanceName);
        serviceType = activity.getString(R.string.serviceType);

    }

    public void registerService(int port) {
        NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(serviceName);
        serviceInfo.setServiceType(serviceType);
        mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                serviceName = NsdServiceInfo.getServiceName();
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
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                Log.d("Net Service Discovery", "Service discovery success" + service);
                if (!service.getServiceType().equals(serviceType)) {
                    Log.d("Net Service Discovery", "Unknown Service Type: " + service.getServiceType());
                } else if (service.getServiceName().equals(serviceName)) {
                    Log.d("Net Service Discovery", "Same machine: " + serviceName);
                } else if (service.getServiceName().contains(serviceName)){
                    //mNsdManager.resolveService(service, mResolveListener);
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
    }

    public void discoverServices() {
        mNsdManager.discoverServices(
                serviceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }
}
