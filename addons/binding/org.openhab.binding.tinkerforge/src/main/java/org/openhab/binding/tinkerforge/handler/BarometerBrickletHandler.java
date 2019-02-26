/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.tinkerforge.handler;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.MetricPrefix;
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
import org.m1theo.tinkerforge.client.devices.barometer.AirPressureChannel;
import org.m1theo.tinkerforge.client.devices.barometer.AirPressureChannelConfig;
import org.m1theo.tinkerforge.client.devices.barometer.AltitudeChannel;
import org.m1theo.tinkerforge.client.devices.barometer.AltitudeChannelConfig;
import org.m1theo.tinkerforge.client.devices.barometer.BarometerBricklet;
import org.m1theo.tinkerforge.client.devices.barometer.BarometerDeviceConfig;
import org.m1theo.tinkerforge.client.devices.barometer.ChannelId;
import org.m1theo.tinkerforge.client.devices.barometer.TemperatureChannel;
import org.m1theo.tinkerforge.client.devices.barometer.TemperatureChannelConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link BarometerBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class BarometerBrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(BarometerBrickletHandler.class);
    private @Nullable BarometerDeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable BarometerBricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    private @Nullable ScheduledFuture<?> pollingJob;

    public BarometerBrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(BarometerDeviceConfig.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.barometer) {
                        BarometerBricklet device = (BarometerBricklet) deviceIn;
                        device.setDeviceConfig(config);

                        Channel airpressureChannel = thing.getChannel("airpressure");
                        if (airpressureChannel != null) {
                            Channel currChannel = airpressureChannel;

                            AirPressureChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(AirPressureChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.airpressure.name());
                            if (tfChannel instanceof AirPressureChannel) {
                                ((AirPressureChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

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

                        Channel altitudeChannel = thing.getChannel("altitude");
                        if (altitudeChannel != null) {
                            Channel currChannel = altitudeChannel;

                            AltitudeChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(AltitudeChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.altitude.name());
                            if (tfChannel instanceof AltitudeChannel) {
                                ((AltitudeChannel) tfChannel).setConfig(channelConfig);
                            }

                        }

                        device.enable();
                        this.device = device;
                        enabled = true;
                        updateStatus(ThingStatus.ONLINE);
                        updateChannelStates();
                        pollChannels();
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

            if (notifier.getChannelId().equals(ChannelId.airpressure.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.MILLIBAR));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.altitude.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SIUnits.METRE)));

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

                case "airpressure":
                    getairpressure();
                    break;

                case "temperature":
                    gettemperature();
                    break;

                case "altitude":
                    getaltitude();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("airpressure")) {
            getairpressure();
        }

        if (isLinked("temperature")) {
            gettemperature();
        }

        if (isLinked("altitude")) {
            getaltitude();
        }

    }

    private void getairpressure() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                BarometerBricklet device2 = (BarometerBricklet) device;
                AirPressureChannel channel = (AirPressureChannel) device2.getChannel("airpressure");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.airpressure.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.MILLIBAR));

                    return;
                }

            }
        }
    }

    private void gettemperature() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                BarometerBricklet device2 = (BarometerBricklet) device;
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

    private void getaltitude() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                BarometerBricklet device2 = (BarometerBricklet) device;
                AltitudeChannel channel = (AltitudeChannel) device2.getChannel("altitude");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.altitude.name(),
                            new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
                                    MetricPrefix.MILLI(SIUnits.METRE)));

                    return;
                }

            }
        }
    }

    private void pollChannels() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                gettemperature();

            }
        };
        pollingJob = scheduler.scheduleAtFixedRate(runnable, 0, 30, TimeUnit.SECONDS);
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

        if (pollingJob != null) {
            pollingJob.cancel(true);
        }
        enabled = false;
    }

}