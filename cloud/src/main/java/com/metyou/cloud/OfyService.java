package com.metyou.cloud;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.metyou.cloud.entity.AppUser;
import com.metyou.cloud.entity.SocialIdentity;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {

    static {
        ObjectifyService.setFactory(new OfyFactory());
    }

    public static Ofy ofy() {
        return (Ofy)ObjectifyService.ofy();
    }

    public static OfyFactory factory() {
        return (OfyFactory)ObjectifyService.factory();
    }
}
