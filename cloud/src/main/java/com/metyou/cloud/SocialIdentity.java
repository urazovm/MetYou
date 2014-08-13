package com.metyou.cloud;

/**
 * Created by mihai on 8/11/14.
 */
public class SocialIdentity {

    private String provider;
    private String socialId;
    private String email;

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getSocialId() {
        return socialId;
    }

    public String getProvider() {
        return provider;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
