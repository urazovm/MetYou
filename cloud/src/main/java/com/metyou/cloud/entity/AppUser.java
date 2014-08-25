package com.metyou.cloud.entity;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;


/**
 * Created by mihai on 8/23/14.
 */

@Entity
public class AppUser {

    @Id
    public Long id;

    public String firstName;

}
