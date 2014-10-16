package com.metyou.cloud.pojos;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by mihai on 8/18/14.
 */
public class UsersRequest {
    private Date beginningDate;
    private int count;
    private Long userKey;
    private int offset;

    public int year, month, day; //used by explorer
    public String cursorStart;
    public String cursorTop;

    public Date getBeginningDate() {
        if (beginningDate == null) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            beginningDate = c.getTime();
        }
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

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
