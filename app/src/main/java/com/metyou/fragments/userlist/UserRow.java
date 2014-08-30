package com.metyou.fragments.userlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.metyou.cloud.services.model.UserEncountered;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by mihai on 8/20/14.
 */
public class UserRow implements ListRow {
    private static final String TAG = "USER_ROW";
    private String socialId;
    private Long key;
    private String firstName;

    public UserRow(UserEncountered userEncountered) {
        this.firstName = userEncountered.getFirstName();
        this.socialId = userEncountered.getSocialId();
        this.key = userEncountered.getKey();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSocialId() {
        return socialId;
    }
}
