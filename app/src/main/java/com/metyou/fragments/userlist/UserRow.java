package com.metyou.fragments.userlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.metyou.cloud.services.model.UserEncountered;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

/**
 * Created by mihai on 8/20/14.
 */
public class UserRow implements ListRow {
    private static final String TAG = "USER_ROW";
    private String socialId;
    private Long key;
    private String firstName;
    private Date lastSeen;

    public UserRow(UserEncountered userEncountered) {
        this.firstName = userEncountered.getFirstName();
        this.socialId = userEncountered.getSocialId();
        this.key = userEncountered.getKey();
        this.lastSeen = new Date(userEncountered.getDate().getValue());
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSocialId() {
        return socialId;
    }

    public Long getKey() {
        return key;
    }

    public Date getLastSeen() {
        return lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserRow) {
            //Log.d("compare", ((UserRow)o).getKey() +" : " + key);
            return ((UserRow) o).getKey().equals(key);
        } else {
            return false;
        }
    }
}
