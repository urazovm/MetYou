package com.metyou.cloudapi;

import android.os.AsyncTask;
import android.util.Log;

import com.metyou.cloud.services.Services;
import com.metyou.cloud.services.model.CloudResponse;
import com.metyou.cloud.services.model.SocialIdentity;

import java.io.IOException;

public class RegisterTask extends AsyncTask<Void, Void, CloudResponse> {

    private static final String TAG = "RegisterTask";
    private final RegisterTaskCallback callback;
    private Services services;
    private SocialIdentity socialIdentity;

    public interface RegisterTaskCallback {
        public void onUserRegistered(CloudResponse response);
    }

    public RegisterTask (Services services, SocialIdentity socialIdentity, RegisterTaskCallback callback) {
        this.services = services;
        this.socialIdentity = socialIdentity;
        this.callback = callback;
    }

    @Override
    protected CloudResponse doInBackground(Void... params) {
        try {
            Services.ServicesOperations.Register registerCommand = services.services().register(socialIdentity);
            CloudResponse response = registerCommand.execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(CloudResponse response) {
        if (response != null) {
            Log.d(TAG, "id: " + response.getId());
        }
        callback.onUserRegistered(response);
    }
}
