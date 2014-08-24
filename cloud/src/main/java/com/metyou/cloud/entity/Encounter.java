package com.metyou.cloud.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

/**
 * Created by mihai on 8/24/14.
 */

@Entity
public class Encounter {

    @Id
    Long id;

    Date date;

    public Encounter() {

    }

    public Encounter(Date date) {
        this.date = date;
    }
}
