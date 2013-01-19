package org.zepan.elasticsearch.river.pinboard;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 19.01.13
 * Time: 15:21
 * To change this template use File | Settings | File Templates.
 */
public class PinboardRiverModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(River.class).to(PinboardRiver.class).asEagerSingleton();
    }

}
