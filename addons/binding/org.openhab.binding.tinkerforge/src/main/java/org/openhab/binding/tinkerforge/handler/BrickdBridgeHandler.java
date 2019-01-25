/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.tinkerforge.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.Brickd;
import org.m1theo.tinkerforge.client.CallbackListener;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.config.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BrickdBridgeHandler} is the handler for the Tinkerforge brickd.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
public class BrickdBridgeHandler extends BaseBridgeHandler {
    private static final String IP_ADDRESS = "ipAddress";
    private final Logger logger = LoggerFactory.getLogger(BrickdBridgeHandler.class);
    private Brickd brickd;
    private final List<DeviceAdminListener> deviceAdminListeners = new CopyOnWriteArrayList<>();
    // private final List<CallbackListener> callbackListeners = new CopyOnWriteArrayList<>();

    public BrickdBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // not needed
    }

    @Override
    public void initialize() {
        logger.debug("Initializing brickd bridge handler.");
        if (getConfig().get(IP_ADDRESS) != null) {
            Host host = new Host(((String) getConfig().get(IP_ADDRESS)));
            brickd = Brickd.createInstance(host);
            for (DeviceAdminListener listener : deviceAdminListeners) {
                logger.debug("register deviceadminlistener");
                brickd.addDeviceAdminListener(listener);
            }
            // for (CallbackListener listener : callbackListeners) {
            // logger.debug("add callbackListener");
            // brickd.addCallbackListener(listener);
            // }
            brickd.connect();
            updateStatus(ThingStatus.ONLINE);
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR, "ip address is missing");
        }
    }

    public Brickd getBrickd() {
        return brickd;
    }

    public void registerDeviceStatusListener(DeviceAdminListener deviceAdmin) {
        deviceAdminListeners.add(deviceAdmin);
        if (brickd != null) {
            brickd.addDeviceAdminListener(deviceAdmin);
        }
    }

    public void unregisterDeviceStatusListener(DeviceAdminListener deviceAdmin) {
        if (brickd != null) {
            brickd.removeDeviceAdminListener(deviceAdmin);
        }
    }

    public void registerCallbackListener(CallbackListener listener) {
        // callbackListeners.add(listener);
        brickd.addCallbackListener(listener);
        // TODO get current status of the device and call the listener for initial values
    }

    public void unregisterCallbackListener(CallbackListener listener) {
        if (brickd != null) {
            brickd.removeCallbackListener(listener);
        }
    }
}
