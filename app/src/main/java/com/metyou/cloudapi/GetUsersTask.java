package com.metyou.cloudapi;

import android.os.AsyncTask;

import com.metyou.cloud.services.Services;
import com.metyou.cloud.services.model.UsersBatch;
import com.metyou.cloud.services.model.UsersRequest;

import java.io.IOException;

/**
 * Created by mihai on 8/18/14.
 */
public class GetUsersTask extends AsyncTask<Void, Void, UsersBatch> {

    private final Services services;
    private final GetUsersCallback callback;
    private final UsersRequest request;
    private final RequestType type;

    public enum RequestType {
        EMPTY,
        REFRESH,
        MORE
    }

    public interface GetUsersCallback {
        public void onUsersLoaded(RequestType requestType, UsersBatch usersBatch);
    }
    
    public GetUsersTask(Services services,
                        UsersRequest ur,
                        RequestType type,
                        GetUsersCallback callback) {
        this.services = services;
        this.callback = callback;
        this.request = ur;
        this.type = type;
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
        callback.onUsersLoaded(type, usersBatch);
    }
}
