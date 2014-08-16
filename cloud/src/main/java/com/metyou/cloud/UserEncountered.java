package com.metyou.cloud;

import java.util.Date;

/**
 * Created by mihai on 8/14/14.
 */
public class UserEncountered {
    private String userId;
    private Date timeEncountered;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimeEncountered() {
        return timeEncountered;
    }

    public void setTimeEncountered(Date timeEncountered) {
        this.timeEncountered = timeEncountered;
    }
}
