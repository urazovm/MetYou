package com.metyou.cloud.pojos;

import com.google.appengine.api.users.User;

import java.util.Date;

/**
 * Created by mihai on 8/24/14.
 */
public class UserEncountered {
    public String socialId;
    public String firstName;
    public Date date;


    public UserEncountered(String firstName, Date date) {
        this.firstName = firstName;
        this.date = date;
    }

}
