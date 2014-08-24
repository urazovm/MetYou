package com.metyou.cloud;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.googlecode.objectify.ObjectifyFactory;
import com.metyou.cloud.entity.AppUser;
import com.metyou.cloud.entity.Encounter;
import com.metyou.cloud.entity.SocialIdentity;
import com.metyou.cloud.entity.EncounterEvent;

import javax.inject.Inject;

/**
 * Created by mihai on 8/23/14.
 */

@Singleton
public class OfyFactory extends ObjectifyFactory {

    @Inject
    private static Injector injector;


    public OfyFactory() {
        this.register(AppUser.class);
        this.register(SocialIdentity.class);
        this.register(EncounterEvent.class);
        this.register(Encounter.class);
    }

//    @Override
//    public <T> T construct(Class<T> type) {
//        return injector.getInstance(type);
//    }

    @Override
    public Ofy begin() {
        return new Ofy(this);
    }
}
