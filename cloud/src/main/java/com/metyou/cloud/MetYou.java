package com.metyou.cloud;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.users.User;
import com.google.appengine.api.datastore.KeyFactory;

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
            httpMethod = ApiMethod.HttpMethod.POST)
    public CloudResponse registerUser(SocialIdentity socialIdentity, User user) {
        if (user == null) {
            return null;
        }
        //check user existence
        Query.Filter emailFilter = new Query.FilterPredicate(
                "email",
                Query.FilterOperator.EQUAL,
                socialIdentity.getEmail());
        Query.Filter providerFilter = new Query.FilterPredicate(
                "socialProvider",
                Query.FilterOperator.EQUAL,
                socialIdentity.getProvider()
        );

        Query.Filter identityFilter = Query.CompositeFilterOperator.and(emailFilter, providerFilter);
        Query query = new Query("SocialIdentity").setFilter(identityFilter);
        PreparedQuery preparedQuery = datastore.prepare(query);
        Entity identity = preparedQuery.asSingleEntity();
        if (identity != null) {
            //already stored in the datastore
            CloudResponse response = new CloudResponse();
            Key userKey = identity.getParent();
            response.setId(KeyFactory.keyToString(userKey));
            return response;
        } else {
            //must be stored in the datastore
            Transaction tx = datastore.beginTransaction();
            Entity newUser = new Entity("User");
            datastore.put(newUser);
            Entity newIdentity = new Entity("SocialIdentity", newUser.getKey());
            newIdentity.setProperty("email", socialIdentity.getEmail());
            newIdentity.setProperty("socialId", socialIdentity.getSocialId());
            newIdentity.setProperty("socialProvider", socialIdentity.getProvider());
            datastore.put(newIdentity);
            tx.commit();
            CloudResponse response = new CloudResponse();
            response.setId(KeyFactory.keyToString(newUser.getKey()));
            return response;
        }
    }
}