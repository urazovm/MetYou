package com.metyou.cloud;

import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.cmd.Loader;
import com.googlecode.objectify.impl.ObjectifyImpl;

/**
 * Created by mihai on 8/23/14.
 */
public class Ofy extends ObjectifyImpl<Ofy> {

    public Ofy(OfyFactory fact) {
        super(fact);
    }

//    @Override
//    public Loader load() {
//        return new OfyLoader(this);
//    }
}
