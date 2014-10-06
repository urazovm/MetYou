package com.metyou.cloudapi;

import android.support.annotation.Nullable;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.metyou.cloud.services.Services;
import com.metyou.cloud.services.model.SocialIdentity;
import com.metyou.cloud.services.model.UsersBatch;
import com.metyou.cloud.services.model.UsersRequest;

import java.io.IOException;
import java.util.Date;

/**
 * Created by mihai on 8/7/14.
 */
public class CloudApi {
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    public static final String WEB_CLIENT_ID = "122110790498-g3ejcl7k5boqtm5f5gaaieheb44brvbl.apps.googleusercontent.com";
    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
    private Services services;
    private static CloudApi cloudApi;

    private CloudApi(GoogleAccountCredential credential) {
        services = getApiServiceHandle(credential);
    }

    public static CloudApi getCloudApi(GoogleAccountCredential credential) {
        if (cloudApi == null) {
            cloudApi = new CloudApi(credential);
        }
        return cloudApi;
    }

    private Services getApiServiceHandle(@Nullable GoogleAccountCredential credential) {
        Services.Builder services = new Services.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential);
        services.setRootUrl("http://192.168.0.107:8080/_ah/api/");
        return services.build();
    }

    public void registerUser(SocialIdentity socialIdentity, RegisterTask.RegisterTaskCallback callback) {
        RegisterTask registerTask = new RegisterTask(services, socialIdentity, callback);
        registerTask.execute();
    }

    public void insertEncounteredUsers(UsersBatch users, StoreNetUsers.StoreUsersCallback callback) {
        StoreNetUsers storeTask = new StoreNetUsers(services, users, callback);
        storeTask.execute();
    }

    public void getUsers(UsersRequest ur, GetUsersTask.RequestType type, GetUsersTask.GetUsersCallback callback) {
        GetUsersTask getUsersTask = new GetUsersTask(services, ur, type, callback);
        getUsersTask.execute();
    }

}
