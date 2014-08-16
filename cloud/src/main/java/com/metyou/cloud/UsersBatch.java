package com.metyou.cloud;

import com.google.appengine.api.datastore.Key;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihai on 8/14/14.
 */
public class UsersBatch {
    private List<UserEncountered> users;
    private String key;

    public List<UserEncountered> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserEncountered> users) {
        this.users = users;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
