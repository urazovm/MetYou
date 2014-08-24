package com.metyou.cloud.pojos;

import com.metyou.cloud.entity.EncounterEvent;

import java.util.List;

/**
 * Created by mihai on 8/14/14.
 */
public class UsersBatch {
    private boolean reachedEnd;
    private List<UserEncountered> users;
    private String key;

    public List<UserEncountered> getUsers() {
        return users;
    }

    public void setUsers(List<UserEncountered> users) {
        this.users = users;
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
}
