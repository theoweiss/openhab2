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
import org.m1theo.tinkerforge.client.devices.io16v2.ChannelId;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio0Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio10Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio11Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio12Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio13Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio14Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio15Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio1Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio2Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio3Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio4Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio5Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio6Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio7Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio8Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.Gpio9Channel;
import org.m1theo.tinkerforge.client.devices.io16v2.GpioChannelConfig;
import org.m1theo.tinkerforge.client.devices.io16v2.IO16V2Bricklet;
import org.m1theo.tinkerforge.client.devices.io16v2.IO16V2DeviceConfig;
import org.m1theo.tinkerforge.client.types.HighLowValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.openhab.binding.tinkerforge.internal.CommandConverter;
import org.openhab.binding.tinkerforge.internal.TFDynamicStateDescriptionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link IO16V2BrickletHandler} is responsible for handling commands, which
 * are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class IO16V2BrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(IO16V2BrickletHandler.class);
    private @Nullable IO16V2DeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable IO16V2Bricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    @NonNullByDefault({})
    private TFDynamicStateDescriptionProvider dynamicStateDescriptionProvider;

    public IO16V2BrickletHandler(Thing thing, TFDynamicStateDescriptionProvider dynamicStateDescriptionProvider) {
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

            case "gpio4":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio5":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio6":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio7":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio8":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio9":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio10":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio11":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio12":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio13":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio14":

                if (command instanceof OnOffType) {
                    ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
                            channelUID.getId());
                    channel.setValue(CommandConverter.convert(command));
                }

                // TODO do something
                break;

            case "gpio15":

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
        config = getConfigAs(IO16V2DeviceConfig.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.io16v2) {
                        IO16V2Bricklet device = (IO16V2Bricklet) deviceIn;
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

                        Channel gpio4Channel = thing.getChannel("gpio4");
                        if (gpio4Channel != null) {
                            Channel currChannel = gpio4Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio4.name());
                            if (tfChannel instanceof Gpio4Channel) {
                                ((Gpio4Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio5Channel = thing.getChannel("gpio5");
                        if (gpio5Channel != null) {
                            Channel currChannel = gpio5Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio5.name());
                            if (tfChannel instanceof Gpio5Channel) {
                                ((Gpio5Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio6Channel = thing.getChannel("gpio6");
                        if (gpio6Channel != null) {
                            Channel currChannel = gpio6Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio6.name());
                            if (tfChannel instanceof Gpio6Channel) {
                                ((Gpio6Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio7Channel = thing.getChannel("gpio7");
                        if (gpio7Channel != null) {
                            Channel currChannel = gpio7Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio7.name());
                            if (tfChannel instanceof Gpio7Channel) {
                                ((Gpio7Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio8Channel = thing.getChannel("gpio8");
                        if (gpio8Channel != null) {
                            Channel currChannel = gpio8Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio8.name());
                            if (tfChannel instanceof Gpio8Channel) {
                                ((Gpio8Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio9Channel = thing.getChannel("gpio9");
                        if (gpio9Channel != null) {
                            Channel currChannel = gpio9Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio9.name());
                            if (tfChannel instanceof Gpio9Channel) {
                                ((Gpio9Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio10Channel = thing.getChannel("gpio10");
                        if (gpio10Channel != null) {
                            Channel currChannel = gpio10Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio10.name());
                            if (tfChannel instanceof Gpio10Channel) {
                                ((Gpio10Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio11Channel = thing.getChannel("gpio11");
                        if (gpio11Channel != null) {
                            Channel currChannel = gpio11Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio11.name());
                            if (tfChannel instanceof Gpio11Channel) {
                                ((Gpio11Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio12Channel = thing.getChannel("gpio12");
                        if (gpio12Channel != null) {
                            Channel currChannel = gpio12Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio12.name());
                            if (tfChannel instanceof Gpio12Channel) {
                                ((Gpio12Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio13Channel = thing.getChannel("gpio13");
                        if (gpio13Channel != null) {
                            Channel currChannel = gpio13Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio13.name());
                            if (tfChannel instanceof Gpio13Channel) {
                                ((Gpio13Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio14Channel = thing.getChannel("gpio14");
                        if (gpio14Channel != null) {
                            Channel currChannel = gpio14Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio14.name());
                            if (tfChannel instanceof Gpio14Channel) {
                                ((Gpio14Channel) tfChannel).setConfig(channelConfig);
                            }
                            if (dynamicStateDescriptionProvider != null) {
                                logger.debug("mode {}", channelConfig.getMode());
                                boolean readOnly = channelConfig.getMode().equals("input") ? true : false;
                                dynamicStateDescriptionProvider.setDescription(currChannel.getUID(),
                                        new StateDescription(null, null, null, null, readOnly, null));
                            }
                        }

                        Channel gpio15Channel = thing.getChannel("gpio15");
                        if (gpio15Channel != null) {
                            Channel currChannel = gpio15Channel;

                            GpioChannelConfig channelConfig = currChannel.getConfiguration()
                                    .as(GpioChannelConfig.class);
                            org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
                                    .getChannel(ChannelId.gpio15.name());
                            if (tfChannel instanceof Gpio15Channel) {
                                ((Gpio15Channel) tfChannel).setConfig(channelConfig);
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

            if (notifier.getChannelId().equals(ChannelId.gpio4.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio5.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio6.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio7.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio8.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio9.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio10.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio11.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio12.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio13.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio14.name())) {

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);

                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(notifier.getChannelId(), value);

                    return;
                }

            }

            if (notifier.getChannelId().equals(ChannelId.gpio15.name())) {

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

                case "gpio4":
                    getgpio4();
                    break;

                case "gpio5":
                    getgpio5();
                    break;

                case "gpio6":
                    getgpio6();
                    break;

                case "gpio7":
                    getgpio7();
                    break;

                case "gpio8":
                    getgpio8();
                    break;

                case "gpio9":
                    getgpio9();
                    break;

                case "gpio10":
                    getgpio10();
                    break;

                case "gpio11":
                    getgpio11();
                    break;

                case "gpio12":
                    getgpio12();
                    break;

                case "gpio13":
                    getgpio13();
                    break;

                case "gpio14":
                    getgpio14();
                    break;

                case "gpio15":
                    getgpio15();
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

        if (isLinked("gpio4")) {
            getgpio4();
        }

        if (isLinked("gpio5")) {
            getgpio5();
        }

        if (isLinked("gpio6")) {
            getgpio6();
        }

        if (isLinked("gpio7")) {
            getgpio7();
        }

        if (isLinked("gpio8")) {
            getgpio8();
        }

        if (isLinked("gpio9")) {
            getgpio9();
        }

        if (isLinked("gpio10")) {
            getgpio10();
        }

        if (isLinked("gpio11")) {
            getgpio11();
        }

        if (isLinked("gpio12")) {
            getgpio12();
        }

        if (isLinked("gpio13")) {
            getgpio13();
        }

        if (isLinked("gpio14")) {
            getgpio14();
        }

        if (isLinked("gpio15")) {
            getgpio15();
        }

    }

    private void getgpio0() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
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
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
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
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
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
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
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

    private void getgpio4() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio4Channel channel = (Gpio4Channel) device2.getChannel("gpio4");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio4.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio5() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio5Channel channel = (Gpio5Channel) device2.getChannel("gpio5");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio5.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio6() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio6Channel channel = (Gpio6Channel) device2.getChannel("gpio6");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio6.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio7() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio7Channel channel = (Gpio7Channel) device2.getChannel("gpio7");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio7.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio8() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio8Channel channel = (Gpio8Channel) device2.getChannel("gpio8");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio8.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio9() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio9Channel channel = (Gpio9Channel) device2.getChannel("gpio9");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio9.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio10() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio10Channel channel = (Gpio10Channel) device2.getChannel("gpio10");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio10.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio11() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio11Channel channel = (Gpio11Channel) device2.getChannel("gpio11");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio11.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio12() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio12Channel channel = (Gpio12Channel) device2.getChannel("gpio12");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio12.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio13() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio13Channel channel = (Gpio13Channel) device2.getChannel("gpio13");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio13.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio14() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio14Channel channel = (Gpio14Channel) device2.getChannel("gpio14");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio14.name(), value);
                    return;
                }

            }
        }
    }

    private void getgpio15() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IO16V2Bricklet device2 = (IO16V2Bricklet) device;
                Gpio15Channel channel = (Gpio15Channel) device2.getChannel("gpio15");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.gpio15.name(), value);
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