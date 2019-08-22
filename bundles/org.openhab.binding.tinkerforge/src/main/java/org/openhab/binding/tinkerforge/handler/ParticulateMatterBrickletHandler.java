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
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
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
import org.m1theo.tinkerforge.client.devices.particulatematter.ChannelId;
import org.m1theo.tinkerforge.client.devices.particulatematter.LaserChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM03CountChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM03CountChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM05CountChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM05CountChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM100ConcentrationChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM100ConcentrationChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM100CountChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM100CountChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM10ConcentrationChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM10ConcentrationChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM10CountChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM10CountChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM25ConcentrationChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM25ConcentrationChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM25CountChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM25CountChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM50CountChannel;
import org.m1theo.tinkerforge.client.devices.particulatematter.PM50CountChannelConfig;
import org.m1theo.tinkerforge.client.devices.particulatematter.ParticulateMatterBricklet;
import org.m1theo.tinkerforge.client.devices.particulatematter.ParticulateMatterDeviceConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.OnOffValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ParticulateMatterBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class ParticulateMatterBrickletHandler extends BaseThingHandler
        implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(ParticulateMatterBrickletHandler.class);
    private @Nullable ParticulateMatterDeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable ParticulateMatterBricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    public ParticulateMatterBrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(ParticulateMatterDeviceConfig.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.particulatematter) {
                        ParticulateMatterBricklet device = (ParticulateMatterBricklet) deviceIn;
                        device.setDeviceConfig(config);

                        Channel pm10ConcentrationChannel = thing.getChannel("pm10Concentration");
                        if (pm10ConcentrationChannel != null) {
                            Channel currChannel = pm10ConcentrationChannel;

                            PM10ConcentrationChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM10ConcentrationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm10Concentration.name());
                            if (tfChannel instanceof PM10ConcentrationChannel) {
                                ((PM10ConcentrationChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm25ConcentrationChannel = thing.getChannel("pm25Concentration");
                        if (pm25ConcentrationChannel != null) {
                            Channel currChannel = pm25ConcentrationChannel;

                            PM25ConcentrationChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM25ConcentrationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm25Concentration.name());
                            if (tfChannel instanceof PM25ConcentrationChannel) {
                                ((PM25ConcentrationChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm100ConcentrationChannel = thing.getChannel("pm100Concentration");
                        if (pm100ConcentrationChannel != null) {
                            Channel currChannel = pm100ConcentrationChannel;

                            PM100ConcentrationChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM100ConcentrationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm100Concentration.name());
                            if (tfChannel instanceof PM100ConcentrationChannel) {
                                ((PM100ConcentrationChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm03CountChannel = thing.getChannel("pm03Count");
                        if (pm03CountChannel != null) {
                            Channel currChannel = pm03CountChannel;

                            PM03CountChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM03CountChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm03Count.name());
                            if (tfChannel instanceof PM03CountChannel) {
                                ((PM03CountChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm05CountChannel = thing.getChannel("pm05Count");
                        if (pm05CountChannel != null) {
                            Channel currChannel = pm05CountChannel;

                            PM05CountChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM05CountChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm05Count.name());
                            if (tfChannel instanceof PM05CountChannel) {
                                ((PM05CountChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm10CountChannel = thing.getChannel("pm10Count");
                        if (pm10CountChannel != null) {
                            Channel currChannel = pm10CountChannel;

                            PM10CountChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM10CountChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm10Count.name());
                            if (tfChannel instanceof PM10CountChannel) {
                                ((PM10CountChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm25CountChannel = thing.getChannel("pm25Count");
                        if (pm25CountChannel != null) {
                            Channel currChannel = pm25CountChannel;

                            PM25CountChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM25CountChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm25Count.name());
                            if (tfChannel instanceof PM25CountChannel) {
                                ((PM25CountChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm50CountChannel = thing.getChannel("pm50Count");
                        if (pm50CountChannel != null) {
                            Channel currChannel = pm50CountChannel;

                            PM50CountChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM50CountChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm50Count.name());
                            if (tfChannel instanceof PM50CountChannel) {
                                ((PM50CountChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        Channel pm100CountChannel = thing.getChannel("pm100Count");
                        if (pm100CountChannel != null) {
                            Channel currChannel = pm100CountChannel;

                            PM100CountChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(PM100CountChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.pm100Count.name());
                            if (tfChannel instanceof PM100CountChannel) {
                                ((PM100CountChannel) tfChannel).setConfig(channelConfig);
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

            if (notifier.getChannelId().equals(ChannelId.pm10Concentration.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    SmartHomeUnits.MICROGRAM_PER_CUBICMETRE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm25Concentration.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    SmartHomeUnits.MICROGRAM_PER_CUBICMETRE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm100Concentration.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    SmartHomeUnits.MICROGRAM_PER_CUBICMETRE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm03Count.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm05Count.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm10Count.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm25Count.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm50Count.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.pm100Count.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

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

                case "pm10Concentration":
                    getpm10Concentration();
                    break;

                case "pm25Concentration":
                    getpm25Concentration();
                    break;

                case "pm100Concentration":
                    getpm100Concentration();
                    break;

                case "pm03Count":
                    getpm03Count();
                    break;

                case "pm05Count":
                    getpm05Count();
                    break;

                case "pm10Count":
                    getpm10Count();
                    break;

                case "pm25Count":
                    getpm25Count();
                    break;

                case "pm50Count":
                    getpm50Count();
                    break;

                case "pm100Count":
                    getpm100Count();
                    break;

                case "laser":
                    getlaser();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("pm10Concentration")) {
            getpm10Concentration();
        }

        if (isLinked("pm25Concentration")) {
            getpm25Concentration();
        }

        if (isLinked("pm100Concentration")) {
            getpm100Concentration();
        }

        if (isLinked("pm03Count")) {
            getpm03Count();
        }

        if (isLinked("pm05Count")) {
            getpm05Count();
        }

        if (isLinked("pm10Count")) {
            getpm10Count();
        }

        if (isLinked("pm25Count")) {
            getpm25Count();
        }

        if (isLinked("pm50Count")) {
            getpm50Count();
        }

        if (isLinked("pm100Count")) {
            getpm100Count();
        }

        if (isLinked("laser")) {
            getlaser();
        }

    }

    private void getpm10Concentration() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM10ConcentrationChannel channel = (PM10ConcentrationChannel) device2.getChannel("pm10Concentration");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm10Concentration.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    SmartHomeUnits.MICROGRAM_PER_CUBICMETRE));

                    return;
                }

            }
        }
    }

    private void getpm25Concentration() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM25ConcentrationChannel channel = (PM25ConcentrationChannel) device2.getChannel("pm25Concentration");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm25Concentration.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    SmartHomeUnits.MICROGRAM_PER_CUBICMETRE));

                    return;
                }

            }
        }
    }

    private void getpm100Concentration() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM100ConcentrationChannel channel = (PM100ConcentrationChannel) device2
                        .getChannel("pm100Concentration");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm100Concentration.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    SmartHomeUnits.MICROGRAM_PER_CUBICMETRE));

                    return;
                }

            }
        }
    }

    private void getpm03Count() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM03CountChannel channel = (PM03CountChannel) device2.getChannel("pm03Count");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm03Count.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }
        }
    }

    private void getpm05Count() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM05CountChannel channel = (PM05CountChannel) device2.getChannel("pm05Count");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm05Count.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }
        }
    }

    private void getpm10Count() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM10CountChannel channel = (PM10CountChannel) device2.getChannel("pm10Count");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm10Count.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }
        }
    }

    private void getpm25Count() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM25CountChannel channel = (PM25CountChannel) device2.getChannel("pm25Count");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm25Count.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }
        }
    }

    private void getpm50Count() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM50CountChannel channel = (PM50CountChannel) device2.getChannel("pm50Count");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm50Count.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }
        }
    }

    private void getpm100Count() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                PM100CountChannel channel = (PM100CountChannel) device2.getChannel("pm100Count");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.pm100Count.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.ONE));

                    return;
                }

            }
        }
    }

    private void getlaser() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                ParticulateMatterBricklet device2 = (ParticulateMatterBricklet) device;
                LaserChannel channel = (LaserChannel) device2.getChannel("laser");
                Object newValue = channel.getValue();

                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.laser.name(), value);
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