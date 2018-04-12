package org.openhab.binding.tinkerforge.handler;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrickdBridgeHandler extends BaseBridgeHandler {
    private static final String HOST = "host";
    private final Logger logger = LoggerFactory.getLogger(BrickdBridgeHandler.class);

    public BrickdBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // not needed
    }

    @Override
    public void initialize() {
        logger.debug("Initializing hue bridge handler.");
        if (getConfig().get(HOST) != null) {
            // TODO initialize brickd

        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR, "ip address is missing");
        }
    }

}
