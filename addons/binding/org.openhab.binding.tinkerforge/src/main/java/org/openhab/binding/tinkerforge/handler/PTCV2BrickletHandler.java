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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.CallbackListener;
import org.m1theo.tinkerforge.client.Device;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.m1theo.tinkerforge.client.Notifier;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.ptcV2.ChannelId;
import org.m1theo.tinkerforge.client.devices.ptcV2.PTCV2Bricklet;
import org.m1theo.tinkerforge.client.devices.ptcV2.PTCV2DeviceConfig;
import org.m1theo.tinkerforge.client.devices.ptcV2.ResistanceChannel;
import org.m1theo.tinkerforge.client.devices.ptcV2.ResistanceChannelConfig;
import org.m1theo.tinkerforge.client.devices.ptcV2.TemperatureChannel;
import org.m1theo.tinkerforge.client.devices.ptcV2.TemperatureChannelConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PTCV2BrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class PTCV2BrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(PTCV2BrickletHandler.class);
    private @Nullable PTCV2DeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable PTCV2Bricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    public PTCV2BrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(PTCV2DeviceConfig.class);
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
            brickdBridgeHandler.registerCallbackListener(this, uid);
            if (bridgeStatus == ThingStatus.ONLINE) {
                Device<?, ?> deviceIn = brickdBridgeHandler.getBrickd().getDevice(uid);
                if (deviceIn != null) {
                    if (deviceIn.getDeviceType() == DeviceType.ptcV2) {
                        PTCV2Bricklet device = (PTCV2Bricklet) deviceIn;
                        device.setDeviceConfig(config);

                        Channel temperatureChannel = thing.getChannel("temperature");
                        if (temperatureChannel != null) {
                            Channel currChannel = temperatureChannel;

                            TemperatureChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(TemperatureChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.temperature.name());
                            if (tfChannel instanceof TemperatureChannel) {
                                ((TemperatureChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel resistanceChannel = thing.getChannel("resistance");
                        if (resistanceChannel != null) {
                            Channel currChannel = resistanceChannel;

                            ResistanceChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(ResistanceChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.resistance.name());
                            if (tfChannel instanceof ResistanceChannel) {
                                ((ResistanceChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

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
    public void notify(@Nullable Notifier notifier, @Nullable TinkerforgeValue lastValue,
            @Nullable TinkerforgeValue newValue) {
        if (notifier == null) {
            return;
        }
        if (!notifier.getDeviceId().equals(uid)) {
            return;
        }
        if (notifier.getExternalDeviceId() != null) {
            // TODO
        } else {

            if (notifier.getChannelId().equals(ChannelId.temperature.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.resistance.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }

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

                case "temperature":
                    gettemperature();
                    break;

                case "resistance":
                    getresistance();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("temperature")) {
            gettemperature();
        }

        if (isLinked("resistance")) {
            getresistance();
        }

    }

    private void gettemperature() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                PTCV2Bricklet device2 = (PTCV2Bricklet) device;
                TemperatureChannel channel = (TemperatureChannel) device2.getChannel("temperature");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.temperature.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

                    return;
                }

            }
        }
    }

    private void getresistance() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                PTCV2Bricklet device2 = (PTCV2Bricklet) device;
                ResistanceChannel channel = (ResistanceChannel) device2.getChannel("resistance");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.resistance.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
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
            brickdBridgeHandler.unregisterCallbackListener(this, uid);
        }
        if (device != null) {
            device.disable();
        }

        enabled = false;
    }

}