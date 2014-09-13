package com.metyou.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mihai on 8/2/14.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "triggered");
        Intent newIntent = new Intent(context, DiscoveryService.class);
        newIntent.setAction(DiscoveryService.DISCOVER);
        context.startService(newIntent);
    }
}
