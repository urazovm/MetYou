package com.metyou.cloud.entity;


import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

/**
 * Created by mihai on 8/23/14.
 */

@Entity
public class SocialIdentity {

    @Id Long id;
    private String email;

    @Index
    private String provider;

    @Index
    private String providerId;
    private String firstName;
    private String lastName;

    @Load
    @Index
    private Ref<AppUser> user;

    public SocialIdentity() {
    }

    public SocialIdentity(String providerId, String email, String provider) {
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
    }


    public String getEmail() {
        return email;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public AppUser getUser() {
        return user.get();
    }

    public void setUser(AppUser user) {
        this.user = Ref.create(user);
    }
}
