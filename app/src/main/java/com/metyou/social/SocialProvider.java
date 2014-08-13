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
import com.metyou.cloud.services.model.SocialIdentity;

/**
 * Created by mihai on 7/22/14.
 */
public abstract class SocialProvider {
    public static final String NONE = "NONE";
    public static final String FACEBOOK = "FACEBOOK";

    private static final String TAG = "SocialProvider";
    public static final String FB_USER_ID = "FB_USER_ID";
    private static final String PREFERENCES_FILE = "PREFS";
    private static final String FB_EMAIL = "FB_EMAIL";

    private static String id;
    private static String email;

    public interface SocialProviderListener {
        public void onUserInfoRequestCompleted(GraphUser user, Response response);
        public Activity getActivity();
    }

    public static SocialProvider getProvider(String provider) {
        if (provider.equals(FACEBOOK)) {
            return new FacebookProvider();
        } else {
            return null;
        }
    }

    public static String currentProvider() {
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
                    storeUserInfo(user, response, listener);
                    listener.onUserInfoRequestCompleted(user, response);
                }
            });
            request.executeAsync();
        }
    }

    private static void storeUserInfo(GraphUser user, Response response, SocialProviderListener listener) {
        String userEmail = (String)user.getProperty("email");
        Log.d(TAG, userEmail);
        Activity activity = listener.getActivity();
        SharedPreferences.Editor preferences = activity.getSharedPreferences(
                PREFERENCES_FILE,
                Context.MODE_PRIVATE).edit();
        preferences.putString(FB_USER_ID, user.getId());
        preferences.putString(FB_EMAIL, userEmail);
        preferences.commit();
        id = user.getId();
        email = userEmail;
    }


    public static String getId() {
        return id;
    }

    public static void readPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        id = preferences.getString(FB_USER_ID, "-1");
        Log.d(TAG, "prefs " + preferences.getAll().toString());
    }

    public static SocialIdentity getSocialIdentity() {
        SocialIdentity socialIdentity = new SocialIdentity();
        socialIdentity.setSocialId(id);
        socialIdentity.setEmail(email);
        socialIdentity.setProvider(FACEBOOK);
        return socialIdentity;
    }
}
