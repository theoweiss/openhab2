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
import org.m1theo.tinkerforge.client.devices.solidstaterelay.RelayConfig;
import org.m1theo.tinkerforge.client.devices.solidstaterelay.SolidStateRelayBricklet;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.solidstaterelay.ChannelId;
import org.m1theo.tinkerforge.client.types.*;

import org.m1theo.tinkerforge.client.devices.solidstaterelay.RelayChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openhab.binding.tinkerforge.internal.CommandConverter;
import org.m1theo.tinkerforge.client.ActuatorChannel;


/**
 * The {@link SolidStateRelayBrickletHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class SolidStateRelayBrickletHandler extends BaseThingHandler implements DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(SolidStateRelayBrickletHandler.class);
    private @Nullable RelayConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable SolidStateRelayBricklet device;
    private @Nullable String uid;

    public SolidStateRelayBrickletHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        switch (channelUID.getId()) {
        
          
            case "relay":
                
                if(command instanceof OnOffType) {
                  ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid, channelUID.getId());
                  channel.setValue(CommandConverter.convert(command));
                }
                
                //TODO do something
                break;
          
        
            default:
                break;
        }

    }

    @Override
    public void initialize() {
        config = getConfigAs(RelayConfig.class);
        String configUid = config.getUid();
        if (configUid != null) {
            uid = configUid;
            Bridge bridge = getBridge();
            ThingStatus bridgeStatus = (bridge == null) ? null : bridge.getStatus();
            BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
            if (brickdBridgeHandler != null) {
                brickdBridgeHandler.registerDeviceStatusListener(this);
                
                if (bridgeStatus == ThingStatus.ONLINE) {
                    Device<?,?> deviceIn = brickdBridgeHandler.getBrickd().getDevice(uid);
                    if (deviceIn != null) {
                      if (deviceIn.getDeviceType() == DeviceType.solidstaterelay){
                        device = (SolidStateRelayBricklet) deviceIn;
                        device.setDeviceConfig(config);
                        device.enable();
                        updateStatus(ThingStatus.ONLINE);

                      } else {
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR);
                      }
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
            }
        }
        return bridgeHandler;
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

    @Override
    public void channelLinked(ChannelUID channelUID) {
        switch (channelUID.getId()) {


          case "relay":
              getrelay();
              break;

          default:
            break;
        }
    }




    private void getrelay() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                SolidStateRelayBricklet device2 = (SolidStateRelayBricklet) device;
                RelayChannel channel = (RelayChannel) device2.getChannel("relay");
                Object newValue = channel.getValue();
                
                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.relay.name(), value);
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
        
    }
    if (device != null) {
        device.disable();
    }

}

}