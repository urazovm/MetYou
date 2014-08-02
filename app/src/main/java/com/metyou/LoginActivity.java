package com.metyou;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.metyou.social.SocialProvider;

public class LoginActivity extends Activity implements SocialProvider.SocialProviderListener {

    private static final String KEY_LAST_PROVIDER = "LAST_PROVIDER";
    public static final String USER_ID = "USER_ID";
    private static final String TAG = "LoginActivity";
    private static final String PREFERENCES_FILE = "PREFS";
    private UiLifecycleHelper uiLifecycleHelper;
    private int lastProvider;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state == SessionState.OPENED) {
            Log.d(TAG, "facebook opened");
            saveProvider(SocialProvider.FACEBOOK);
            SocialProvider.fetchUserInfo(this);
        } else {
            Log.d(TAG, "facebook closed");
            saveProvider(SocialProvider.NONE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (SocialProvider.currentProvider() != SocialProvider.NONE) {
            //already signed in; start main activity
            Log.d(TAG, "already signed in");
            startMainActivity();
        }
        uiLifecycleHelper = new UiLifecycleHelper(this, callback);
        uiLifecycleHelper.onCreate(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiLifecycleHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        uiLifecycleHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiLifecycleHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Save the last social login {@link SocialProvider} used.
     *
     * @param provider the provider (see {@link SocialProvider})
     */

    public void saveProvider(int provider) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putInt(KEY_LAST_PROVIDER, provider);
        editor.commit();
    }

    public void getProvider() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        lastProvider = sharedPreferences.getInt(KEY_LAST_PROVIDER, SocialProvider.NONE);
    }

    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserInfoRequestCompleted(GraphUser user, Response response) {
        SharedPreferences.Editor preferences = getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE).edit();
        preferences.putString(USER_ID, user.getId());
        preferences.commit();
        startMainActivity();
    }
}
