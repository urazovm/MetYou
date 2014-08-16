package com.metyou.cloudapi;

import android.os.AsyncTask;

import com.metyou.cloud.services.Services;
import com.metyou.cloud.services.model.CloudResponse;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloud.services.model.UsersBatch;

import java.io.IOException;

/**
 * Created by mihai on 8/15/14.
 */
public class StoreNetUsers extends AsyncTask<Void, Void, Void> {

    private final Services services;
    private final UsersBatch users;
    private final StoreUsersCallback callback;

    public interface StoreUsersCallback {
        public void onUsersStored();
    }

    public StoreNetUsers (Services services, UsersBatch users, StoreUsersCallback callback) {
        this.services = services;
        this.users = users;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            Services.ServicesOperations.InsertEncounteredUsers insertCmd = services.services().insertEncounteredUsers(users);
            insertCmd.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
