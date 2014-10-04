package com.metyou.fragments.friends;

import android.os.Handler;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

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
