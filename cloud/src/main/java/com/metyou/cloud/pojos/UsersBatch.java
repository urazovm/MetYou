package com.metyou.cloud.pojos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihai on 8/14/14.
 */
public class UsersBatch {
    private boolean reachedEnd;
    private List<UserEncountered> users = new ArrayList<UserEncountered>();
    private String key;

    public List<UserEncountered> getUsers() {
        return users;
    }

    public void addUser(UserEncountered user) {
        users.add(user);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isReachedEnd() {
        return reachedEnd;
    }

    public void setReachedEnd(boolean reachedEnd) {
        this.reachedEnd = reachedEnd;
    }

    public int size() {
        return users.size();
    }
}
