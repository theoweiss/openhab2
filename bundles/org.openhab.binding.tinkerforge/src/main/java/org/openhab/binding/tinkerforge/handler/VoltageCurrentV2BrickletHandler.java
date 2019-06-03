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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.MetricPrefix;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
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
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.ChannelId;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.CurrentChannel;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.CurrentChannelConfig;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.PowerChannel;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.PowerChannelConfig;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.VoltageChannel;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.VoltageChannelConfig;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.VoltageCurrentV2Bricklet;
import org.m1theo.tinkerforge.client.devices.voltagecurrentv2.VoltageCurrentV2Config;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link VoltageCurrentV2BrickletHandler} is responsible for handling
 * commands, which are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class VoltageCurrentV2BrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(VoltageCurrentV2BrickletHandler.class);
    private @Nullable VoltageCurrentV2Config config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable VoltageCurrentV2Bricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    public VoltageCurrentV2BrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(VoltageCurrentV2Config.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.voltagecurrentV2) {
                        VoltageCurrentV2Bricklet device = (VoltageCurrentV2Bricklet) deviceIn;
                        device.setDeviceConfig(config);

                        Channel voltageChannel = thing.getChannel("voltage");
                        if (voltageChannel != null) {
                            Channel currChannel = voltageChannel;

                            VoltageChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(VoltageChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.voltage.name());
                            if (tfChannel instanceof VoltageChannel) {
                                ((VoltageChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel currentChannel = thing.getChannel("current");
                        if (currentChannel != null) {
                            Channel currChannel = currentChannel;

                            CurrentChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(CurrentChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.current.name());
                            if (tfChannel instanceof CurrentChannel) {
                                ((CurrentChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel powerChannel = thing.getChannel("power");
                        if (powerChannel != null) {
                            Channel currChannel = powerChannel;

                            PowerChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PowerChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.power.name());
                            if (tfChannel instanceof PowerChannel) {
                                ((PowerChannel) tfChannel).setConfig(channelConfig);
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

            if (notifier.getChannelId().equals(ChannelId.voltage.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.VOLT)));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.current.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.AMPERE)));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.power.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.WATT)));

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

                case "voltage":
                    getvoltage();
                    break;

                case "current":
                    getcurrent();
                    break;

                case "power":
                    getpower();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("voltage")) {
            getvoltage();
        }

        if (isLinked("current")) {
            getcurrent();
        }

        if (isLinked("power")) {
            getpower();
        }

    }

    private void getvoltage() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                VoltageCurrentV2Bricklet device2 = (VoltageCurrentV2Bricklet) device;
                VoltageChannel channel = (VoltageChannel) device2.getChannel("voltage");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.voltage.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.VOLT)));

                    return;
                }

            }
        }
    }

    private void getcurrent() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                VoltageCurrentV2Bricklet device2 = (VoltageCurrentV2Bricklet) device;
                CurrentChannel channel = (CurrentChannel) device2.getChannel("current");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.current.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.AMPERE)));

                    return;
                }

            }
        }
    }

    private void getpower() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                VoltageCurrentV2Bricklet device2 = (VoltageCurrentV2Bricklet) device;
                PowerChannel channel = (PowerChannel) device2.getChannel("power");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.power.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.WATT)));

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