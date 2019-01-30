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
import org.m1theo.tinkerforge.client.devices.industrialquadrelayV2.QuadRelayConfig;
import org.m1theo.tinkerforge.client.devices.industrialquadrelayV2.IndustrialQuadRelayBrickletV2;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.industrialquadrelayV2.ChannelId;
import org.m1theo.tinkerforge.client.types.*;

import org.m1theo.tinkerforge.client.devices.industrialquadrelayV2.Relay0Channel;
import org.m1theo.tinkerforge.client.devices.industrialquadrelayV2.Relay1Channel;
import org.m1theo.tinkerforge.client.devices.industrialquadrelayV2.Relay2Channel;
import org.m1theo.tinkerforge.client.devices.industrialquadrelayV2.Relay3Channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openhab.binding.tinkerforge.internal.CommandConverter;
import org.m1theo.tinkerforge.client.ActuatorChannel;


/**
 * The {@link IndustrialQuadRelayBrickletV2Handler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class IndustrialQuadRelayBrickletV2Handler extends BaseThingHandler implements DeviceAdminListener {

    private final Logger logger = LoggerFactory.getLogger(IndustrialQuadRelayBrickletV2Handler.class);
    private @Nullable QuadRelayConfig config;
    private @Nullable BrickdBridgeHandler bridgeHandler;
    private @Nullable String uid;

    public IndustrialQuadRelayBrickletV2Handler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        switch (channelUID.getId()) {
        
          
            case "relay0":
                
                if(command instanceof OnOffType) {
                  ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid, channelUID.getId());
                  channel.setValue(CommandConverter.convert(command));
                }
                
                //TODO do something
                break;
          
        
          
            case "relay1":
                
                if(command instanceof OnOffType) {
                  ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid, channelUID.getId());
                  channel.setValue(CommandConverter.convert(command));
                }
                
                //TODO do something
                break;
          
        
          
            case "relay2":
                
                if(command instanceof OnOffType) {
                  ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid, channelUID.getId());
                  channel.setValue(CommandConverter.convert(command));
                }
                
                //TODO do something
                break;
          
        
          
            case "relay3":
                
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
        config = getConfigAs(QuadRelayConfig.class);
        String configUid = config.getUid();
        if (configUid != null) {
            uid = configUid;
            Bridge bridge = getBridge();
            ThingStatus bridgeStatus = (bridge == null) ? null : bridge.getStatus();
            BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
            if (brickdBridgeHandler != null) {
                brickdBridgeHandler.registerDeviceStatusListener(this);
                if (bridgeStatus == ThingStatus.ONLINE) {
                    Device<?,?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
                    if (device != null) {
                      if (device.getDeviceType() == DeviceType.industrialquadrelayV2){
                        IndustrialQuadRelayBrickletV2 device2 = (IndustrialQuadRelayBrickletV2) device;
                        device2.setDeviceConfig(config);
                        device2.enable();
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


          case "relay0":
              getrelay0();
              break;


          case "relay1":
              getrelay1();
              break;


          case "relay2":
              getrelay2();
              break;


          case "relay3":
              getrelay3();
              break;

          default:
            break;
        }
    }



    private void getrelay0() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IndustrialQuadRelayBrickletV2 device2 = (IndustrialQuadRelayBrickletV2) device;
                Relay0Channel channel = (Relay0Channel) device2.getChannel("relay0");
                Object newValue = channel.getValue();
                
                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.relay0.name(), value);
                    return;
                }
                
            }
        }
    }



    private void getrelay1() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IndustrialQuadRelayBrickletV2 device2 = (IndustrialQuadRelayBrickletV2) device;
                Relay1Channel channel = (Relay1Channel) device2.getChannel("relay1");
                Object newValue = channel.getValue();
                
                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.relay1.name(), value);
                    return;
                }
                
            }
        }
    }



    private void getrelay2() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IndustrialQuadRelayBrickletV2 device2 = (IndustrialQuadRelayBrickletV2) device;
                Relay2Channel channel = (Relay2Channel) device2.getChannel("relay2");
                Object newValue = channel.getValue();
                
                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.relay2.name(), value);
                    return;
                }
                
            }
        }
    }



    private void getrelay3() {
        BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
        if (brickdBridgeHandler != null) {
            Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
            if (device != null) {
                IndustrialQuadRelayBrickletV2 device2 = (IndustrialQuadRelayBrickletV2) device;
                Relay3Channel channel = (Relay3Channel) device2.getChannel("relay3");
                Object newValue = channel.getValue();
                
                if (newValue instanceof OnOffValue) {
                    logger.debug("new value {}", newValue);
                    OnOffType value = newValue == OnOffValue.ON ? OnOffType.ON : OnOffType.OFF;
                    updateState(ChannelId.relay3.name(), value);
                    return;
                }
                
            }
        }
    }



}