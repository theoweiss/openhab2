/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.tinkerforge.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.ActuatorChannel;
import org.m1theo.tinkerforge.client.Device;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.dualrelay.ChannelId;
import org.m1theo.tinkerforge.client.devices.dualrelay.DualRelayBricklet;
import org.m1theo.tinkerforge.client.devices.dualrelay.DualRelayConfig;
import org.m1theo.tinkerforge.client.devices.dualrelay.Relay1Channel;
import org.m1theo.tinkerforge.client.devices.dualrelay.Relay2Channel;
import org.m1theo.tinkerforge.client.types.OnOffValue;
import org.openhab.binding.tinkerforge.internal.CommandConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link DualRelayBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class DualRelayBrickletHandler extends BaseThingHandler implements DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(DualRelayBrickletHandler.class);
    private @Nullable DualRelayConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable DualRelayBricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    public DualRelayBrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        switch (channelUID.getId()) {

            case "relay1":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "relay2":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            default:
                break;
        }

    }

    @Override
    public void initialize() {
        config = getConfigAs(DualRelayConfig.class);
        String uid = config.getUid();
        if (uid != null) {
            this.uid = uid;
            BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
            if (brickdBridgeHandler != null) {
                brickdBridgeHandler.registerDeviceStatusListener(this);
                enable();
            } else {
                updateStatus(ThingStatus.OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "uid is missing in configuration");
        }
    }

    private synchronized @Nullable BrickdBridgeHandler getBrickdBridgeHandler() {
        if (bridgeHandler == null) {
            Bridge bridge = getBridge();
            if (bridge == null) {
                return null;
            }
            ThingHandler handler = bridge.getHandler();
            if (handler instanceof BrickdBridgeHandler) {
                bridgeHandler = (BrickdBridgeHandler) handler;
            }
        }
        return bridgeHandler;
    }

    private void enable() {
        logger.debug("executing enable");
        Bridge bridge = getBridge();
        ThingStatus bridgeStatus = (bridge == null) ? null : bridge.getStatus();
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {

            if (bridgeStatus == ThingStatus.ONLINE) {
                Device<?, ?> deviceIn = brickdBridgeHandler.getBrickd().getDevice(uid);
                if (deviceIn != null) {
                    if (deviceIn.getDeviceType() == DeviceType.dualrelay) {
                        DualRelayBricklet device = (DualRelayBricklet) deviceIn;
                        device.setDeviceConfig(config);

                        device.enable();
                        this.device = device;
                        enabled = true;
                        updateStatus(ThingStatus.ONLINE);
                        updateChannelStates();

                    } else {
                        logger.error("configuration error");
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
                    }
                } else {
                    logger.error("deviceIn is null");
                    updateStatus(ThingStatus.OFFLINE);
                }
            } else {
                logger.error("bridge is offline");
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            logger.error("brickdBridgeHandler is null");
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    @Override
    public void deviceChanged(@Nullable DeviceChangeType changeType, @Nullable DeviceInfo info) {
        if (changeType == null || info == null) {
            logger.debug("device changed but devicechangtype or deviceinfo are null");
            return;
        }

        if (info.getUid().equals(uid)) {
            if (changeType == DeviceChangeType.ADD) {
                logger.debug("{} added", uid);
                enable();
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.GONE);
            }
        }
    }

    @Override
    public void channelLinked(ChannelUID channelUID) {
        if (enabled) {
            switch (channelUID.getId()) {

                case "relay1":
                    getrelay1();
                    break;

                case "relay2":
                    getrelay2();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("relay1")) {
            getrelay1();
        }

        if (isLinked("relay2")) {
            getrelay2();
        }

    }

    private void getrelay1() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                DualRelayBricklet device2 = (DualRelayBricklet) device;
                Relay1Channel channel = (Relay1Channel) device2.getChannel("relay1");
                Object newValue = channel.getValue();

                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.relay1.name(), value);
                    return;
                }

            }
        }
    }

    private void getrelay2() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                DualRelayBricklet device2 = (DualRelayBricklet) device;
                Relay2Channel channel = (Relay2Channel) device2.getChannel("relay2");
                Object newValue = channel.getValue();

                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.relay2.name(), value);
                    return;
                }

            }
        }
    }

    @Override
    public void dispose() {

        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            brickdBridgeHandler.unregisterDeviceStatusListener(this);

        }
        if (device != null) {
            device.disable();
        }

        enabled = false;
    }

}