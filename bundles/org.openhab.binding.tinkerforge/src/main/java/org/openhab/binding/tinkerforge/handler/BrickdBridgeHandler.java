/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tinkerforge.handler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.Brickd;
import org.m1theo.tinkerforge.client.BrickdStatusListener;
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
public class BrickdBridgeHandler extends BaseBridgeHandler implements BrickdStatusListener {
    private final Logger logger = LoggerFactory.getLogger(BrickdBridgeHandler.class);
    private Brickd brickd;
    private AtomicBoolean isConnected = new AtomicBoolean(false);

    private final List<DeviceAdminListener> deviceAdminListeners = new CopyOnWriteArrayList<>();
    // private final List<CallbackListener> callbackListeners = new
    // CopyOnWriteArrayList<>();

    public BrickdBridgeHandler(Bridge bridge) {
        super(bridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // not needed
    }

    @Override
    public void initialize() {
        Host config = getConfigAs(Host.class);
        logger.debug("Initializing brickd bridge handler.");
        if (config.getHost() != null) {
            brickd = Brickd.createInstance(config, false);
            brickd.addBrickdStatusListener(this);
            for (DeviceAdminListener listener : deviceAdminListeners) {
                logger.debug("register deviceadminlistener");
                brickd.addDeviceAdminListener(listener);
            }
            updateStatus(ThingStatus.OFFLINE);
            brickd.connect();
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
        deviceAdminListeners.remove(deviceAdmin);
        if (brickd != null) {
            brickd.removeDeviceAdminListener(deviceAdmin);
        }
    }

    public void registerCallbackListener(CallbackListener listener, String uid) {
        if (brickd != null) {
            brickd.addCallbackListener(listener, uid);
        } else {
            logger.error("listener registration failed, brickd is null. uid {}", uid);
        }
    }

    public void unregisterCallbackListener(CallbackListener listener, String uid) {
        if (brickd != null) {
            brickd.removeCallbackListener(listener, uid);
        } else {
            logger.error("listener removal failed, brickd is null. uid {}", uid);
        }
    }

    @Override
    public void connected(boolean connected) {
        if (connected) {
            isConnected.compareAndSet(false, true);
            updateStatus(ThingStatus.ONLINE);
        } else {
            isConnected.compareAndSet(true, false);
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void dispose() {
        if (isConnected.get()) {
            brickd.disconnect();
        }
        brickd.removeBrickdStatusListener(this);
    }

}
