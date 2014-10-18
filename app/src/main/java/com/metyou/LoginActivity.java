package com.metyou;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.FacebookException;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.metyou.cloud.services.model.CloudResponse;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloudapi.CloudApi;
import com.metyou.cloudapi.RegisterTask;
import com.metyou.net.DiscoveryService;
import com.metyou.net.NetServiceManager;
import com.metyou.social.SocialProvider;

import org.json.JSONArray;

import java.io.IOException;
import java.util.Arrays;

public class LoginActivity extends Activity implements
        SocialProvider.SocialProviderListener,
        RegisterTask.RegisterTaskCallback {

    private static final String KEY_LAST_PROVIDER = "LAST_PROVIDER";
    public static final String USER_ID = "USER_ID";
    private static final String TAG = "LoginActivity";
    private static final String PREFERENCES_FILE = "PREFS";
    public static final String LOG_OUT_ACTION = "LOG_OUT";
    private UiLifecycleHelper uiLifecycleHelper;
    private LoginButton loginButton;
    private String lastProvider;
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
            loginButton.setEnabled(false);
            SocialProvider.fetchUserInfo(this);
        } else {
            Log.d(TAG, "facebook closed");
            saveProvider(SocialProvider.NONE);
        }
    }

    private void registerUser() {
        SocialIdentity socialIdentity = SocialProvider.getSocialIdentity();
        CloudApi cloudApi = CloudApi.getCloudApi(null);
        cloudApi.registerUser(socialIdentity, this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        loginButton = (LoginButton) findViewById(R.id.facebook_auth_button);

        if (SocialProvider.currentProvider() != SocialProvider.NONE) {
            if (getIntent().getAction().equals(LOG_OUT_ACTION)) {
                stopService(new Intent(this, DiscoveryService.class));
                logOut();
            } else {
                //already signed in; set social data
                SocialProvider.init(this);
                // check id
                Log.d(TAG, "already signed in");
                Long id = SocialProvider.getId();
                if (id.equals(-1)) {
                    logOut();
                } else {
                    startMainActivity();
                }
            }
        }

        loginButton.setOnErrorListener(new LoginButton.OnErrorListener() {
            @Override
            public void onError(FacebookException error) {
                String errorMessage = error.getMessage();
                if (errorMessage.equals("net::ERR_NAME_NOT_RESOLVED") ||
                    errorMessage.equals("net::ERR_ADDRESS_UNREACHABLE")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("Login Failed");
                    builder.setMessage("Please check your network connectivity or try again later!");
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
                Log.d(TAG, error.getMessage());
            }
        });

        setFacebookPermissions();
        uiLifecycleHelper = new UiLifecycleHelper(this, callback);
        uiLifecycleHelper.onCreate(savedInstanceState);
    }

    private void logOut() {
        if (Session.getActiveSession() != null) {
            Session.getActiveSession().closeAndClearTokenInformation();
        }
        Session.setActiveSession(null);
        SocialProvider.deletePreferences(this);
        setNetworkChangeReceiver(false);
        loginButton.setEnabled(true);
    }

    private void setFacebookPermissions() {
        LoginButton fbLoginButton = (LoginButton) findViewById(R.id.facebook_auth_button);
        fbLoginButton.setReadPermissions("email", "user_photos");
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
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
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
        } else if (id == R.id.list) {
            listAccount();
        }
        return super.onOptionsItemSelected(item);
    }

    private void listAccount() {
        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        for (Account account : accounts) {
            Log.d(TAG, account.toString());
        }
    }

    /**
     * Save the last social login {@link SocialProvider} used.
     *
     * @param provider the provider (see {@link SocialProvider})
     */

    public void saveProvider(String provider) {
        SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
        editor.putString(KEY_LAST_PROVIDER, provider);
        editor.commit();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserInfoRequestCompleted(GraphUser user, Response response) {
        if (response.getError() != null) {
            logOut();
        } else {
            SocialProvider.init(this);
            registerUser();
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void onUserRegistered(CloudResponse response) {
        //store user id
        if (response == null) {
            Toast.makeText(this, "An error occured", Toast.LENGTH_LONG).show();
            logOut();
        } else {
            //store information
            SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE).edit();
            editor.putLong(USER_ID, response.getId());
            editor.commit();
            SocialProvider.init(this);
            setNetworkChangeReceiver(true);
            startMainActivity();
        }
    }

    private void setNetworkChangeReceiver(boolean set) {
        ComponentName componentName = new ComponentName(this, NetServiceManager.class);
        int flag = (set ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
        getPackageManager().setComponentEnabledSetting(componentName, flag, PackageManager.DONT_KILL_APP);
    }
}
