package com.metyou.cloud.pojos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mihai on 8/14/14.
 */
public class UsersBatch {
    private boolean reachedEnd;
    private List<UserEncountered> users = new ArrayList<UserEncountered>();
    private Long key;
    public String cursorStart;
    public String cursorTop;

    public List<UserEncountered> getUsers() {
        return users;
    }

    public void addUser(UserEncountered user) {
        users.add(user);
    }

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
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
