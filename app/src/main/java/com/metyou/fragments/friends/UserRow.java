package com.metyou.fragments.friends;

import com.metyou.cloud.services.model.UserEncountered;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

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

    public String getLastSeenHumanReadable() {
        long diff = new Date().getTime() - lastSeen.getTime();
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        long days = TimeUnit.MILLISECONDS.toDays(diff);
        String humanReadable = "last seen ";


        if (minutes < 1) {
            humanReadable += "just seconds ago";
        } else if (minutes == 1) {
            humanReadable += "about a minute ago";
        } else if(minutes < 60) {
            humanReadable += "about " + minutes + " minutes ago";
        } else if (hours < 24) {
            humanReadable += hours + " hours ago";
        } else if (days == 1) {
            humanReadable += "a day ago";
        } else if (days < 3) {
            humanReadable += days + " days ago";
        } else {
            humanReadable += "on " + new SimpleDateFormat("dd/mm/yy").format(lastSeen);
        }
        return humanReadable;
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
