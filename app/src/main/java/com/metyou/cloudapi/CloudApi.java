package com.metyou.cloudapi;

import android.support.annotation.Nullable;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.metyou.cloud.services.Services;

/**
 * Created by mihai on 8/7/14.
 */
public class CloudApi {
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    public static final String WEB_CLIENT_ID = "122110790498-g3ejcl7k5boqtm5f5gaaieheb44brvbl.apps.googleusercontent.com";
    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;


    public static Services getApiServiceHandle(@Nullable GoogleAccountCredential credential) {
        Services.Builder services = new Services.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential);
        services.setRootUrl("http://192.168.1.105:8080/_ah/api/");
        return services.build();
    }


}
