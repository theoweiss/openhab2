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
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.SIUnits;
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
import org.m1theo.tinkerforge.client.devices.outdoorweather.BatteryLowStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.BatteryLowStationChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.ChannelId;
import org.m1theo.tinkerforge.client.devices.outdoorweather.GustSpeedStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.GustSpeedStationChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.HumiditySensorChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.HumiditySensorChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.HumidityStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.HumidityStationChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.LastChangeSensorChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.LastChangeSensorChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.LastChangeStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.LastChangeStationChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.OutdoorWeatherBricklet;
import org.m1theo.tinkerforge.client.devices.outdoorweather.OutdoorWeatherDeviceConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.RainStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.RainStationChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.TemperatureSensorChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.TemperatureSensorChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.TemperatureStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.TemperatureStationChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.WindDirectionStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.WindDirectionStationChannelConfig;
import org.m1theo.tinkerforge.client.devices.outdoorweather.WindSpeedStationChannel;
import org.m1theo.tinkerforge.client.devices.outdoorweather.WindSpeedStationChannelConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.HighLowValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OutdoorWeatherBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class OutdoorWeatherBrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(OutdoorWeatherBrickletHandler.class);
    private @Nullable OutdoorWeatherDeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable OutdoorWeatherBricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    public OutdoorWeatherBrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(OutdoorWeatherDeviceConfig.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.outdoorweather) {
                        OutdoorWeatherBricklet device = (OutdoorWeatherBricklet) deviceIn;
                        device.setDeviceConfig(config);

                        Channel temperatureStationChannel = thing.getChannel("temperatureStation");
                        if (temperatureStationChannel != null) {

                            TemperatureStationChannelConfig channelConfig = temperatureStationChannel.getConfiguration()
                                    .as(TemperatureStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.temperatureStation.name());
                            if (tfChannel instanceof TemperatureStationChannel) {
                                ((TemperatureStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel humidityStationChannel = thing.getChannel("humidityStation");
                        if (humidityStationChannel != null) {

                            HumidityStationChannelConfig channelConfig = humidityStationChannel.getConfiguration()
                                    .as(HumidityStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.humidityStation.name());
                            if (tfChannel instanceof HumidityStationChannel) {
                                ((HumidityStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel windSpeedStationChannel = thing.getChannel("windSpeedStation");
                        if (windSpeedStationChannel != null) {

                            WindSpeedStationChannelConfig channelConfig = windSpeedStationChannel.getConfiguration()
                                    .as(WindSpeedStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.windSpeedStation.name());
                            if (tfChannel instanceof WindSpeedStationChannel) {
                                ((WindSpeedStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel gustSpeedStationChannel = thing.getChannel("gustSpeedStation");
                        if (gustSpeedStationChannel != null) {

                            GustSpeedStationChannelConfig channelConfig = gustSpeedStationChannel.getConfiguration()
                                    .as(GustSpeedStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gustSpeedStation.name());
                            if (tfChannel instanceof GustSpeedStationChannel) {
                                ((GustSpeedStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel rainStationChannel = thing.getChannel("rainStation");
                        if (rainStationChannel != null) {

                            RainStationChannelConfig channelConfig = rainStationChannel.getConfiguration()
                                    .as(RainStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.rainStation.name());
                            if (tfChannel instanceof RainStationChannel) {
                                ((RainStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel windDirectionStationChannel = thing.getChannel("windDirectionStation");
                        if (windDirectionStationChannel != null) {

                            WindDirectionStationChannelConfig channelConfig = windDirectionStationChannel
                                    .getConfiguration().as(WindDirectionStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.windDirectionStation.name());
                            if (tfChannel instanceof WindDirectionStationChannel) {
                                ((WindDirectionStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel lastChangeStationChannel = thing.getChannel("lastChangeStation");
                        if (lastChangeStationChannel != null) {

                            LastChangeStationChannelConfig channelConfig = lastChangeStationChannel.getConfiguration()
                                    .as(LastChangeStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.lastChangeStation.name());
                            if (tfChannel instanceof LastChangeStationChannel) {
                                ((LastChangeStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel batteryLowStationChannel = thing.getChannel("batteryLowStation");
                        if (batteryLowStationChannel != null) {

                            BatteryLowStationChannelConfig channelConfig = batteryLowStationChannel.getConfiguration()
                                    .as(BatteryLowStationChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.batteryLowStation.name());
                            if (tfChannel instanceof BatteryLowStationChannel) {
                                ((BatteryLowStationChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel temperatureSensorChannel = thing.getChannel("temperatureSensor");
                        if (temperatureSensorChannel != null) {

                            TemperatureSensorChannelConfig channelConfig = temperatureSensorChannel.getConfiguration()
                                    .as(TemperatureSensorChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.temperatureSensor.name());
                            if (tfChannel instanceof TemperatureSensorChannel) {
                                ((TemperatureSensorChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel humiditySensorChannel = thing.getChannel("humiditySensor");
                        if (humiditySensorChannel != null) {

                            HumiditySensorChannelConfig channelConfig = humiditySensorChannel.getConfiguration()
                                    .as(HumiditySensorChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.humiditySensor.name());
                            if (tfChannel instanceof HumiditySensorChannel) {
                                ((HumiditySensorChannel) tfChannel).setConfig(channelConfig);
                            }
                        }

                        Channel lastChangeSensorChannel = thing.getChannel("lastChangeSensor");
                        if (lastChangeSensorChannel != null) {

                            LastChangeSensorChannelConfig channelConfig = lastChangeSensorChannel.getConfiguration()
                                    .as(LastChangeSensorChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.lastChangeSensor.name());
                            if (tfChannel instanceof LastChangeSensorChannel) {
                                ((LastChangeSensorChannel) tfChannel).setConfig(channelConfig);
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

            if (notifier.getChannelId().equals(ChannelId.temperatureStation.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.humidityStation.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.PERCENT));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.windSpeedStation.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gustSpeedStation.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.rainStation.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.windDirectionStation.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.batteryLowStation.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.temperatureSensor.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.humiditySensor.name())) {

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.PERCENT));

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

                case "temperatureStation":
                    gettemperatureStation();
                    break;

                case "humidityStation":
                    gethumidityStation();
                    break;

                case "windSpeedStation":
                    getwindSpeedStation();
                    break;

                case "gustSpeedStation":
                    getgustSpeedStation();
                    break;

                case "rainStation":
                    getrainStation();
                    break;

                case "windDirectionStation":
                    getwindDirectionStation();
                    break;

                case "lastChangeStation":
                    getlastChangeStation();
                    break;

                case "batteryLowStation":
                    getbatteryLowStation();
                    break;

                case "temperatureSensor":
                    gettemperatureSensor();
                    break;

                case "humiditySensor":
                    gethumiditySensor();
                    break;

                case "lastChangeSensor":
                    getlastChangeSensor();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("temperatureStation")) {
            gettemperatureStation();
        }

        if (isLinked("humidityStation")) {
            gethumidityStation();
        }

        if (isLinked("windSpeedStation")) {
            getwindSpeedStation();
        }

        if (isLinked("gustSpeedStation")) {
            getgustSpeedStation();
        }

        if (isLinked("rainStation")) {
            getrainStation();
        }

        if (isLinked("windDirectionStation")) {
            getwindDirectionStation();
        }

        if (isLinked("lastChangeStation")) {
            getlastChangeStation();
        }

        if (isLinked("batteryLowStation")) {
            getbatteryLowStation();
        }

        if (isLinked("temperatureSensor")) {
            gettemperatureSensor();
        }

        if (isLinked("humiditySensor")) {
            gethumiditySensor();
        }

        if (isLinked("lastChangeSensor")) {
            getlastChangeSensor();
        }

    }

    private void gettemperatureStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                TemperatureStationChannel channel = (TemperatureStationChannel) device2
                        .getChannel("temperatureStation");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.temperatureStation.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

                    return;
                }

            }
        }
    }

    private void gethumidityStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                HumidityStationChannel channel = (HumidityStationChannel) device2.getChannel("humidityStation");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.humidityStation.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.PERCENT));

                    return;
                }

            }
        }
    }

    private void getwindSpeedStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                WindSpeedStationChannel channel = (WindSpeedStationChannel) device2.getChannel("windSpeedStation");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.windSpeedStation.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }
        }
    }

    private void getgustSpeedStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                GustSpeedStationChannel channel = (GustSpeedStationChannel) device2.getChannel("gustSpeedStation");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.gustSpeedStation.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }
        }
    }

    private void getrainStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                RainStationChannel channel = (RainStationChannel) device2.getChannel("rainStation");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.rainStation.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }
        }
    }

    private void getwindDirectionStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                WindDirectionStationChannel channel = (WindDirectionStationChannel) device2
                        .getChannel("windDirectionStation");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.windDirectionStation.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }
        }
    }

    private void getlastChangeStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                LastChangeStationChannel channel = (LastChangeStationChannel) device2.getChannel("lastChangeStation");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.lastChangeStation.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }
        }
    }

    private void getbatteryLowStation() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                BatteryLowStationChannel channel = (BatteryLowStationChannel) device2.getChannel("batteryLowStation");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.batteryLowStation.name(), value);
                    return;
                }

            }
        }
    }

    private void gettemperatureSensor() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                TemperatureSensorChannel channel = (TemperatureSensorChannel) device2.getChannel("temperatureSensor");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.temperatureSensor.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

                    return;
                }

            }
        }
    }

    private void gethumiditySensor() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                HumiditySensorChannel channel = (HumiditySensorChannel) device2.getChannel("humiditySensor");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.humiditySensor.name(), new QuantityType<>(
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.PERCENT));

                    return;
                }

            }
        }
    }

    private void getlastChangeSensor() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                OutdoorWeatherBricklet device2 = (OutdoorWeatherBricklet) device;
                LastChangeSensorChannel channel = (LastChangeSensorChannel) device2.getChannel("lastChangeSensor");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.lastChangeSensor.name(),
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