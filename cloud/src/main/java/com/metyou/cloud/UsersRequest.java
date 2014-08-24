package com.metyou.cloud;

import java.util.Date;

/**
 * Created by mihai on 8/18/14.
 */
public class UsersRequest {
    private Date beginningDate;
    private int count;
    private Long userKey;

    public Date getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(Date beginningDate) {
        this.beginningDate = beginningDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getUserKey() {
        return userKey;
    }

    public void setUserKey(Long userKey) {
        this.userKey = userKey;
    }
}
