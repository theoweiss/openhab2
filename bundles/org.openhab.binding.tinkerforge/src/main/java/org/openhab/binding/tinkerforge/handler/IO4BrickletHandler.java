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
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.StateDescription;
import org.m1theo.tinkerforge.client.ActuatorChannel;
import org.m1theo.tinkerforge.client.CallbackListener;
import org.m1theo.tinkerforge.client.Device;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.m1theo.tinkerforge.client.Notifier;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.io4.ChannelId;
import org.m1theo.tinkerforge.client.devices.io4.Gpio0Channel;
import org.m1theo.tinkerforge.client.devices.io4.Gpio1Channel;
import org.m1theo.tinkerforge.client.devices.io4.Gpio2Channel;
import org.m1theo.tinkerforge.client.devices.io4.Gpio3Channel;
import org.m1theo.tinkerforge.client.devices.io4.GpioChannelConfig;
import org.m1theo.tinkerforge.client.devices.io4.IO4Bricklet;
import org.m1theo.tinkerforge.client.devices.io4.IO4DeviceConfig;
import org.m1theo.tinkerforge.client.types.HighLowValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.openhab.binding.tinkerforge.internal.CommandConverter;
import org.openhab.binding.tinkerforge.internal.TFDynamicStateDescriptionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link IO4BrickletHandler} is responsible for handling commands, which
 * are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class IO4BrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(IO4BrickletHandler.class);
    private @Nullable IO4DeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable IO4Bricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    @NonNullByDefault({})
    private TFDynamicStateDescriptionProvider dynamicStateDescriptionProvider;

    public IO4BrickletHandler(Thing thing, TFDynamicStateDescriptionProvider dynamicStateDescriptionProvider) {
        super(thing);
        this.dynamicStateDescriptionProvider = dynamicStateDescriptionProvider;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        switch (channelUID.getId()) {

            case "gpio0":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio1":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio2":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio3":

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
        config = getConfigAs(IO4DeviceConfig.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.io4) {
                        IO4Bricklet device = (IO4Bricklet) deviceIn;
                        device.setDeviceConfig(config);

                        Channel gpio0Channel = thing.getChannel("gpio0");
                        if (gpio0Channel != null) {
                            Channel currChannel = gpio0Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio0.name());
                            if (tfChannel instanceof Gpio0Channel) {
                                ((Gpio0Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio1Channel = thing.getChannel("gpio1");
                        if (gpio1Channel != null) {
                            Channel currChannel = gpio1Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio1.name());
                            if (tfChannel instanceof Gpio1Channel) {
                                ((Gpio1Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio2Channel = thing.getChannel("gpio2");
                        if (gpio2Channel != null) {
                            Channel currChannel = gpio2Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio2.name());
                            if (tfChannel instanceof Gpio2Channel) {
                                ((Gpio2Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio3Channel = thing.getChannel("gpio3");
                        if (gpio3Channel != null) {
                            Channel currChannel = gpio3Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio3.name());
                            if (tfChannel instanceof Gpio3Channel) {
                                ((Gpio3Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
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

            if (notifier.getChannelId().equals(ChannelId.gpio0.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio1.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio2.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio3.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

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

                case "gpio0":
                    getgpio0();
                    break;

                case "gpio1":
                    getgpio1();
                    break;

                case "gpio2":
                    getgpio2();
                    break;

                case "gpio3":
                    getgpio3();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("gpio0")) {
            getgpio0();
        }

        if (isLinked("gpio1")) {
            getgpio1();
        }

        if (isLinked("gpio2")) {
            getgpio2();
        }

        if (isLinked("gpio3")) {
            getgpio3();
        }

    }

    private void getgpio0() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO4Bricklet device2 = (IO4Bricklet) device;
                Gpio0Channel channel = (Gpio0Channel) device2.getChannel("gpio0");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio0.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio1() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO4Bricklet device2 = (IO4Bricklet) device;
                Gpio1Channel channel = (Gpio1Channel) device2.getChannel("gpio1");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio1.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio2() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO4Bricklet device2 = (IO4Bricklet) device;
                Gpio2Channel channel = (Gpio2Channel) device2.getChannel("gpio2");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio2.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio3() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO4Bricklet device2 = (IO4Bricklet) device;
                Gpio3Channel channel = (Gpio3Channel) device2.getChannel("gpio3");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio3.name(), value);
                    return;
                }

            }
        }
    }

    @Override
    public void dispose() {

        dynamicStateDescriptionProvider.removeDescriptionsForThing(thing.getUID());
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