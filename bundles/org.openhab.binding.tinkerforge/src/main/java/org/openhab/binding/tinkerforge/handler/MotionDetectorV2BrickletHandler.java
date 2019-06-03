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
import org.eclipse.smarthome.core.thing.Bridge;
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
import org.m1theo.tinkerforge.client.devices.motiondetectorV2.BottomLedChannel;
import org.m1theo.tinkerforge.client.devices.motiondetectorV2.ChannelId;
import org.m1theo.tinkerforge.client.devices.motiondetectorV2.MotionDetectedChannel;
import org.m1theo.tinkerforge.client.devices.motiondetectorV2.MotionDetectorV2Bricklet;
import org.m1theo.tinkerforge.client.devices.motiondetectorV2.MotionDetectorV2DeviceConfig;
import org.m1theo.tinkerforge.client.devices.motiondetectorV2.TopLeftLedChannel;
import org.m1theo.tinkerforge.client.devices.motiondetectorV2.TopRightLedChannel;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.HighLowValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link MotionDetectorV2BrickletHandler} is responsible for handling
 * commands, which are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class MotionDetectorV2BrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(MotionDetectorV2BrickletHandler.class);
    private @Nullable MotionDetectorV2DeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable MotionDetectorV2Bricklet device;
    private @Nullable String uid;
    private boolean enabled = false;

    public MotionDetectorV2BrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        switch (channelUID.getId()) {

            case "topLeftled":

                // TODO do something
                break;

            case "topRightled":

                // TODO do something
                break;

            case "bottomled":

                // TODO do something
                break;

            default:
                break;
        }

    }

    @Override
    public void initialize() {
        config = getConfigAs(MotionDetectorV2DeviceConfig.class);
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
                    if (deviceIn.getDeviceType() == DeviceType.motiondetectorV2) {
                        MotionDetectorV2Bricklet device = (MotionDetectorV2Bricklet) deviceIn;
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

            if (notifier.getChannelId().equals(ChannelId.motiondetected.name())) {

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

                case "motiondetected":
                    getmotiondetected();
                    break;

                case "topLeftled":
                    gettopLeftled();
                    break;

                case "topRightled":
                    gettopRightled();
                    break;

                case "bottomled":
                    getbottomled();
                    break;

                default:
                    break;
            }
        }
    }

    private void updateChannelStates() {

        if (isLinked("motiondetected")) {
            getmotiondetected();
        }

        if (isLinked("topLeftled")) {
            gettopLeftled();
        }

        if (isLinked("topRightled")) {
            gettopRightled();
        }

        if (isLinked("bottomled")) {
            getbottomled();
        }

    }

    private void getmotiondetected() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                MotionDetectorV2Bricklet device2 = (MotionDetectorV2Bricklet) device;
                MotionDetectedChannel channel = (MotionDetectedChannel) device2.getChannel("motiondetected");
                Object newValue = channel.getValue();

                if (newValue instanceof HighLowValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == HighLowValue.HIGH ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.motiondetected.name(), value);
                    return;
                }

            }
        }
    }

    private void gettopLeftled() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                MotionDetectorV2Bricklet device2 = (MotionDetectorV2Bricklet) device;
                TopLeftLedChannel channel = (TopLeftLedChannel) device2.getChannel("topLeftled");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.topLeftled.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }
        }
    }

    private void gettopRightled() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                MotionDetectorV2Bricklet device2 = (MotionDetectorV2Bricklet) device;
                TopRightLedChannel channel = (TopRightLedChannel) device2.getChannel("topRightled");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.topRightled.name(),
                            new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
                    return;
                }

            }
        }
    }

    private void getbottomled() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                MotionDetectorV2Bricklet device2 = (MotionDetectorV2Bricklet) device;
                BottomLedChannel channel = (BottomLedChannel) device2.getChannel("bottomled");
                Object newValue = channel.getValue();

                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(ChannelId.bottomled.name(),
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