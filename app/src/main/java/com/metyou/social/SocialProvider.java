package com.metyou.social;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

/**
 * Created by mihai on 7/22/14.
 */
public abstract class SocialProvider {
    public static final int NONE = 0;
    public static final int FACEBOOK = 1;

    private static final String TAG = "SocialProvider";
    public static final String USER_ID = "USER_ID";
    private static final String PREFERENCES_FILE = "PREFS";

    private static String id;

    public interface SocialProviderListener {
        public void onUserInfoRequestCompleted(GraphUser user, Response response);
    }

    public static SocialProvider getProvider(int provider) {
        if (provider == FACEBOOK) {
            return new FacebookProvider();
        } else {
            return null;
        }
    }

    public static int currentProvider() {
        if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
            return SocialProvider.FACEBOOK;
        }
        return SocialProvider.NONE;
    }

    public static void fetchUserInfo(final SocialProviderListener listener) {
        if (Session.getActiveSession() != null && Session.getActiveSession().isOpened()) {
            Request request = Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    id = user.getId();
                    listener.onUserInfoRequestCompleted(user, response);
                }
            });
            request.executeAsync();
        }
    }


    public static String getId() {
        return id;
    }

    public static void readPreferences(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        id = preferences.getString(USER_ID, "-1");
        Log.d(TAG, "prefs " + preferences.getAll().toString());
    }
}
