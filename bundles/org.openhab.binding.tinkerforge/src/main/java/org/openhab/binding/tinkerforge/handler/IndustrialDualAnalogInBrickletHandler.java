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
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.ChannelId;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.IndustrialDualAnalogInBricklet;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.IndustrialDualAnalogInDeviceConfig;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.Voltage0Channel;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.Voltage0ChannelConfig;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.Voltage1Channel;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.Voltage1ChannelConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link IndustrialDualAnalogInBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class IndustrialDualAnalogInBrickletHandler extends BaseThingHandler
        implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(IndustrialDualAnalogInBrickletHandler.class);
    private @Nullable IndustrialDualAnalogInDeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable IndustrialDualAnalogInBricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    public IndustrialDualAnalogInBrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(IndustrialDualAnalogInDeviceConfig.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.industrialdualanalogIn) {
                        IndustrialDualAnalogInBricklet device = (IndustrialDualAnalogInBricklet) deviceIn;
                        device.setDeviceConfig(config);

                        Channel voltage0Channel = thing.getChannel("voltage0");
                        if (voltage0Channel != null) {
                            Channel currChannel = voltage0Channel;

                            Voltage0ChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(Voltage0ChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.voltage0.name());
                            if (tfChannel instanceof Voltage0Channel) {
                                ((Voltage0Channel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel voltage1Channel = thing.getChannel("voltage1");
                        if (voltage1Channel != null) {
                            Channel currChannel = voltage1Channel;

                            Voltage1ChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(Voltage1ChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.voltage1.name());
                            if (tfChannel instanceof Voltage1Channel) {
                                ((Voltage1Channel) tfChannel).setConfig(channelConfig);
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

            if (notifier.getChannelId().equals(ChannelId.voltage0.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.VOLT)));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.voltage1.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.VOLT)));

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

                case "voltage0":
                    getvoltage0();
                    break;

                case "voltage1":
                    getvoltage1();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("voltage0")) {
            getvoltage0();
        }

        if (isLinked("voltage1")) {
            getvoltage1();
        }

    }

    private void getvoltage0() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IndustrialDualAnalogInBricklet device2 = (IndustrialDualAnalogInBricklet) device;
                Voltage0Channel channel = (Voltage0Channel) device2.getChannel("voltage0");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.voltage0.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.VOLT)));

                    return;
                }

            }
        }
    }

    private void getvoltage1() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IndustrialDualAnalogInBricklet device2 = (IndustrialDualAnalogInBricklet) device;
                Voltage1Channel channel = (Voltage1Channel) device2.getChannel("voltage1");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.voltage1.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SmartHomeUnits.VOLT)));

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