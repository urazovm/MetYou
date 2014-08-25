package com.metyou.cloud.entity;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

/**
 * Created by mihai on 8/24/14.
 */

@Entity
public class EncounterEvent {

    @Id
    public Long id;

    @Index
    public Date date;

    @Load
    @Index
    @Parent
    public Ref<EncounterInfo> encounterInfoRef;

    public EncounterEvent() {

    }

    public EncounterEvent(EncounterInfo info, Date date) {
        this.date = date;
        this.encounterInfoRef = Ref.create(info);
    }
}
