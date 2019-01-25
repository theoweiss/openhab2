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
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.library.types.*;
import org.eclipse.smarthome.core.thing.*;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.config.BaseDeviceConfig;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;

import org.m1theo.tinkerforge.client.devices.voltagecurrent.ChannelId;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.library.unit.MetricPrefix;
import org.eclipse.smarthome.core.library.unit.*;
import org.m1theo.tinkerforge.client.Notifier;
import org.m1theo.tinkerforge.client.CallbackListener;
import org.m1theo.tinkerforge.client.types.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The {@link VoltageCurrentBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class VoltageCurrentBrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(VoltageCurrentBrickletHandler.class);
    private @Nullable BaseDeviceConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable String uid;

    public VoltageCurrentBrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

    }

    @Override
    public void initialize() {
        config = getConfigAs(BaseDeviceConfig.class);
        String configUid = config.getUid();
        if (configUid != null) {
            uid = configUid;
            Bridge bridge = getBridge();
            ThingStatus bridgeStatus = (bridge == null) ? null : bridge.getStatus();
            BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
            if (brickdBridgeHandler != null) {
                brickdBridgeHandler.registerDeviceStatusListener(this);
                if (bridgeStatus == ThingStatus.ONLINE) {
                    if (brickdBridgeHandler.getBrickd().getDevice(uid) != null) {
                        updateStatus(ThingStatus.ONLINE);
                    } else {
                        updateStatus(ThingStatus.OFFLINE);
                    }
                } else {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
                }
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
                bridgeHandler.registerCallbackListener(this);
            }
        }
        return bridgeHandler;
    }


    @Override
    public void notify(@Nullable Notifier notifier, @Nullable TinkerforgeValue lastValue, @Nullable TinkerforgeValue
    newValue) {
        if (notifier == null) {
            return;
        }
        if (!notifier.getDeviceId().equals(uid)) {
            return;
        }
        if (notifier.getExternalDeviceId() != null) {
            // TODO
        } else {
            notifier.getChannelId();
            
            
            if (notifier.getChannelId().equals(ChannelId.voltage.name())) {
                
                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()), MetricPrefix.MILLI(SmartHomeUnits.VOLT)));
                    
                    return;
                }
                
            }
            
            
            
            if (notifier.getChannelId().equals(ChannelId.current.name())) {
                
                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()), MetricPrefix.MILLI(SmartHomeUnits.AMPERE)));
                    
                    return;
                }
                
            }
            
            
            
            if (notifier.getChannelId().equals(ChannelId.power.name())) {
                
                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()), MetricPrefix.MILLI(SmartHomeUnits.WATT)));
                    
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
                updateStatus(ThingStatus.ONLINE);
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.GONE);
            }
        }
    }

}