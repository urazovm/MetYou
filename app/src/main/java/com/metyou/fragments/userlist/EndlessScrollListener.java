package com.metyou.fragments.userlist;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloudapi.CloudApi;
import com.metyou.social.SocialProvider;
import com.metyou.social.User;

import java.util.ArrayList;

/**
 * Created by mihai on 8/16/14.
 */
public class EndlessScrollListener implements OnScrollListener {

    private static final String TAG = "EndlessScroll";
    private final EndlessScrollCallback endlessCallBack;
    private boolean active;
    private boolean loading;
    private Handler mHandler;

    public interface EndlessScrollCallback {
        public void onBottom();
    }

    public EndlessScrollListener(EndlessScrollCallback endlessCallback) {
        this.endlessCallBack = endlessCallback;
        loading = false;
        active = true;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount == 0) {
            return;
        }
        if (active && firstVisibleItem + visibleItemCount >= totalItemCount && !loading) {
            Log.d(TAG, "first: " + firstVisibleItem);
            Log.d(TAG, "visibleItemCount: " + visibleItemCount);
            Log.d(TAG, "totalItemCount: " + totalItemCount);
            onBottomTaskStarted();
            endlessCallBack.onBottom();
        }
    }

    private void onBottomTaskStarted() {
        loading = true;
        Log.d(TAG, "bottom task started");
        //Todo add loader
    }

    public void onBottomTaskFinished() {
        this.loading = false;
        Log.d(TAG, "bottom task finished");
    }

    public void performTaskOnBottom(boolean mode) {
        this.active = mode;
    }
}
