package com.metyou.cloudapi;

import android.os.AsyncTask;

import com.google.api.client.util.DateTime;
import com.metyou.cloud.services.Services;
import com.metyou.cloud.services.model.UserEncountered;
import com.metyou.cloud.services.model.UsersBatch;
import com.metyou.cloud.services.model.UsersRequest;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by mihai on 8/18/14.
 */
public class GetUsersTask extends AsyncTask<Void, Void, UsersBatch> {

    private final Services services;
    private final GetUsersCallback callback;
    private final UsersRequest request;

    public interface GetUsersCallback {
        public void onUserLoaded(UsersBatch usersBatch);
    }
    
    public GetUsersTask(Services services,
                        UsersRequest ur,
                        GetUsersCallback callback) {
        this.services = services;
        this.callback = callback;
        this.request = ur;
    }
    
    @Override
    protected UsersBatch doInBackground(Void... params) {
        try {
            Services.ServicesOperations.GetUsers getUsers = services.services().getUsers(request);
            return getUsers.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(UsersBatch usersBatch) {
        callback.onUserLoaded(usersBatch);
    }
}
