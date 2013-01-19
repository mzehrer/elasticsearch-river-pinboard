package org.zepan.elasticsearch.plugin.river.pinboard;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
import org.zepan.elasticsearch.river.pinboard.PinboardRiverModule;

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 19.01.13
 * Time: 15:28
 * To change this template use File | Settings | File Templates.
 */
public class PinboardRiverPlugin extends AbstractPlugin {

    @Inject
    public PinboardRiverPlugin() {
    }

    @Override
    public String name() {
        return "river-pinboard";
    }

    @Override
    public String description() {
        return "River Pinboard Plugin";
    }

    /**
     * Registers the {@link PinboardRiverModule}
     * @param module the elasticsearch module used to handle rivers
     */
    public void onModule(RiversModule module) {
        module.registerRiver("pinboard", PinboardRiverModule.class);
    }

}
