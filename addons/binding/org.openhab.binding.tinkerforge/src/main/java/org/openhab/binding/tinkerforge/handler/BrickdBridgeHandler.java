package org.openhab.binding.tinkerforge.handler;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.Brickd;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.config.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrickdBridgeHandler extends BaseBridgeHandler {
    private static final String IP_ADDRESS = "ipAddress";
    private final Logger logger = LoggerFactory.getLogger(BrickdBridgeHandler.class);
    private Brickd brickd;

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
        if (getConfig().get(IP_ADDRESS) != null) {
            Host host = new Host(((String) getConfig().get(IP_ADDRESS)));
            brickd = Brickd.createInstance(host);
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR, "ip address is missing");
        }
    }

    public Brickd getBrickd() {
        return brickd;
    }

    public void registerDeviceStatusListener(DeviceAdminListener deviceAdmin) {
        if (brickd != null) {
            brickd.addDeviceAdminListener(deviceAdmin);
        }
    }

    public void unregisterDeviceStatusListener(DeviceAdminListener deviceAdmin) {
        if (brickd != null) {
            brickd.removeDeviceAdminListener(deviceAdmin);
        }
    }

}
