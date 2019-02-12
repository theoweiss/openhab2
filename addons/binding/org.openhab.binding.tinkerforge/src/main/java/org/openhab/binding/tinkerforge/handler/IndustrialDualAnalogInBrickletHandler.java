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
import org.m1theo.tinkerforge.client.Device;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.IndustrialDualAnalogInDeviceConfig;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.IndustrialDualAnalogInBricklet;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.ChannelId;
import org.m1theo.tinkerforge.client.types.*;

import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.Voltage0Channel;
import org.m1theo.tinkerforge.client.devices.industrialdualanalogIn.Voltage1Channel;

import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.library.unit.MetricPrefix;
import org.eclipse.smarthome.core.library.unit.*;
import org.m1theo.tinkerforge.client.Notifier;
import org.m1theo.tinkerforge.client.CallbackListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The {@link IndustrialDualAnalogInBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class IndustrialDualAnalogInBrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

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
        String configUid = config.getUid();
        if (configUid != null) {
            uid = configUid;
            Bridge bridge = getBridge();
            ThingStatus bridgeStatus = (bridge == null) ? null : bridge.getStatus();
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

private void enable(){
    logger.debug("executing enable");
    Bridge bridge = getBridge();
    ThingStatus bridgeStatus = (bridge == null) ? null : bridge.getStatus();
    BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
    if (brickdBridgeHandler != null) {
        brickdBridgeHandler.registerCallbackListener(this);
        if (bridgeStatus == ThingStatus.ONLINE) {
            Device<?,?> deviceIn = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (deviceIn != null) {
              if (deviceIn.getDeviceType() == DeviceType.industrialdualanalogIn){
                device = (IndustrialDualAnalogInBricklet) deviceIn;
                device.setDeviceConfig(config);
                device.enable();
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
            
            
            if (notifier.getChannelId().equals(ChannelId.voltage0.name())) {
                
                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()), MetricPrefix.MILLI(SmartHomeUnits.VOLT)));
                    
                    return;
                }
                
            }
            
            
            
            if (notifier.getChannelId().equals(ChannelId.voltage1.name())) {
                
                if (newValue instanceof DecimalValue) {
                    logger.debug("new value {}", newValue);
                    updateState(notifier.getChannelId(), new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()), MetricPrefix.MILLI(SmartHomeUnits.VOLT)));
                    
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
                    updateState(ChannelId.voltage0.name(), new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()), MetricPrefix.MILLI(SmartHomeUnits.VOLT)));
                    
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
                    updateState(ChannelId.voltage1.name(), new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()), MetricPrefix.MILLI(SmartHomeUnits.VOLT)));
                    
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
        brickdBridgeHandler.unregisterCallbackListener(this);
    }
    if (device != null) {
        device.disable();
    }

    enabled = false;
}

}