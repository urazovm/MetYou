package com.metyou.social;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
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
    public static final String USER_ID = "USER_ID";
    private static final String PREFERENCES_FILE = "PREFS";
    private static final String FB_EMAIL = "FB_EMAIL";

    private static String facebookId;
    private static String email;
    private static String id;

    public static String getId(Context context) {
        if (id == null) {
            SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
            id = preferences.getString(USER_ID, "-1");
        }
        return id;
    }



    public static void deletePreferences(Context context) {
        SharedPreferences.Editor preferences = context.getSharedPreferences(
                PREFERENCES_FILE,
                Context.MODE_PRIVATE).edit();
        preferences.remove(FB_USER_ID);
        preferences.remove(USER_ID);
        preferences.remove(FB_EMAIL);
        preferences.commit();
    }

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
                    if (response.getError() == null) {
                        storeUserInfo(user, response, listener);
                        listener.onUserInfoRequestCompleted(user, response);
                    } else {
                        FacebookRequestError error = response.getError();
                        Toast.makeText(listener.getActivity(), error.getErrorMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
            request.executeAsync();
        }
    }

    private static void storeUserInfo(GraphUser user, Response response, SocialProviderListener listener) {
        String userEmail = (String)user.getProperty("email");

        Log.d(TAG, user.getInnerJSONObject().toString());
        Log.d(TAG, userEmail);
        Log.d(TAG, user.getFirstName());
        Activity activity = listener.getActivity();
        SharedPreferences.Editor preferences = activity.getSharedPreferences(
                PREFERENCES_FILE,
                Context.MODE_PRIVATE).edit();
        preferences.putString(FB_USER_ID, user.getId());
        preferences.putString(FB_EMAIL, userEmail);
        preferences.commit();
        email = userEmail;
    }


    public static String getFacebookId() {
        return facebookId;
    }

    public static void readPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        facebookId = preferences.getString(FB_USER_ID, "-1");
        id = preferences.getString(USER_ID, "-1");
        email = preferences.getString(FB_EMAIL, "-1");
        Log.d(TAG, "prefs " + preferences.getAll().toString());
    }

    public static SocialIdentity getSocialIdentity() {
        SocialIdentity socialIdentity = new SocialIdentity();
        socialIdentity.setProvider(facebookId);
        socialIdentity.setEmail(email);
        socialIdentity.setProvider(FACEBOOK);
        return socialIdentity;
    }
}
