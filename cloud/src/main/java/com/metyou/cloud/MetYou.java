package com.metyou.cloud;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import sun.rmi.runtime.Log;

/**
 * Created by mihai on 8/7/14.
 */

@Api(name = "services",
     version = "v1",
     namespace = @ApiNamespace(ownerDomain = "cloud.metyou.com", ownerName = "cloud.metyou.com", packagePath=""),
     scopes = {Constants.EMAIL_SCOPE},
     clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID, Constants.IOS_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID},
     audiences = {Constants.ANDROID_AUDIENCE}
)

public class MetYou {

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @ApiMethod(
            name = "services.register",
            httpMethod = ApiMethod.HttpMethod.GET)
    public Response registerUser() {
        /*if (user == null) {
            return new Response("hei");
        }*/
        return new Response("0");
    }
}