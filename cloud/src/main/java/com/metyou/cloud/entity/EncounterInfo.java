package com.metyou.cloud.entity;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by mihai on 8/24/14.
 */

@Entity
public class EncounterInfo {

    @Id
    Long id;

    @Load
    @Index
    private ArrayList<Ref<AppUser>> users = new ArrayList<Ref<AppUser>>();

    @Index
    public Date lastSeen;

    public EncounterInfo() {
    }


    public EncounterInfo(AppUser appUser1, AppUser appUser2) {
        users.add(Ref.create(appUser1));
        users.add(Ref.create(appUser2));
    }

    public Long getOtherUserId(Long id) {
        if (users.get(0).get().id.equals(id)) {
            return users.get(1).get().id;
        }
        return users.get(0).get().id;
    }

    public AppUser getOtherAppUser(Long id) {
        if (users.get(0).get().id.equals(id)) {
            return users.get(1).get();
        }
        return users.get(0).get();
    }
}
