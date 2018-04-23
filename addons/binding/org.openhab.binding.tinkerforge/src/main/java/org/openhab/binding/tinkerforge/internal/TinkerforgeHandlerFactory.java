/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.tinkerforge.internal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.openhab.binding.tinkerforge.handler.BrickdBridgeHandler;
import org.osgi.framework.ServiceRegistration;
import org.openhab.binding.tinkerforge.internal.discovery.TinkerforgeDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openhab.binding.tinkerforge.handler.OutdoorWeatherBrickletHandler;

import org.openhab.binding.tinkerforge.handler.TemperatureBrickletHandler;

import org.openhab.binding.tinkerforge.handler.LoadCellBrickletHandler;

import org.openhab.binding.tinkerforge.handler.AmbientLightV2BrickletHandler;

import org.openhab.binding.tinkerforge.handler.HumidityV2BrickletHandler;

import org.openhab.binding.tinkerforge.handler.MotionDetectorV2BrickletHandler;

import org.openhab.binding.tinkerforge.handler.RealTimeClockBrickletHandler;

import org.openhab.binding.tinkerforge.handler.RotaryEncoderBrickletHandler;

import org.openhab.binding.tinkerforge.handler.VoltageCurrentBrickletHandler;

import org.openhab.binding.tinkerforge.handler.DistanceUSBrickletHandler;


/**
 * The {@link TinkerforgeHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.tinkerforge")
@NonNullByDefault
public class TinkerforgeHandlerFactory extends BaseThingHandlerFactory {

    private final Logger logger = LoggerFactory.getLogger(TinkerforgeHandlerFactory.class);
    private final Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    public @Nullable Thing createThing(ThingTypeUID thingTypeUID, Configuration configuration,
            @Nullable ThingUID thingUID, @Nullable ThingUID bridgeUID) {
        if (thingTypeUID.equals(THING_TYPE_BRICKD)) {
            return super.createThing(thingTypeUID, configuration, thingUID, null);
        } else {
            ThingUID deviceUID = getDeviceUID(thingTypeUID, thingUID, configuration, bridgeUID);
            return super.createThing(thingTypeUID, configuration, deviceUID, bridgeUID);
        }
    }

    private ThingUID getDeviceUID(ThingTypeUID thingTypeUID, @Nullable ThingUID thingUID, Configuration configuration,
            @Nullable ThingUID bridgeUID) {
        if (thingUID != null) {
            return thingUID;
        } else {
            String uid = (String) configuration.get(DEVICE_UID_PARAM);
            if (bridgeUID != null) {
                return new ThingUID(thingTypeUID, uid, bridgeUID.getId());
            } else {
                return new ThingUID(thingTypeUID, uid, (String[]) null);
            }
        }
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        if (thingTypeUID.equals(THING_TYPE_BRICKD)) {
             BrickdBridgeHandler handler = new BrickdBridgeHandler((Bridge) thing);
             registerDeviceDiscoveryService(handler);
             return handler;
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_OUTDOORWEATHER)) {
            return new OutdoorWeatherBrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_TEMPERATURE)) {
            return new TemperatureBrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_LOADCELL)) {
            return new LoadCellBrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_AMBIENTLIGHTV2)) {
            return new AmbientLightV2BrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_HUMIDITYV2)) {
            return new HumidityV2BrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_MOTIONDETECTORV2)) {
            return new MotionDetectorV2BrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_REALTIMECLOCK)) {
            return new RealTimeClockBrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_ROTARYENCODER)) {
            return new RotaryEncoderBrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_VOLTAGECURRENT)) {
            return new VoltageCurrentBrickletHandler(thing);
        }
    
    
        if (thingTypeUID.equals(THING_TYPE_DISTANCEUS)) {
            return new DistanceUSBrickletHandler(thing);
        }
    
        return null;
    }

    private synchronized void registerDeviceDiscoveryService(BrickdBridgeHandler bridgeHandler) {
        logger.debug("registering tinkerforge discovery");
        TinkerforgeDiscoveryService discoveryService = new TinkerforgeDiscoveryService(bridgeHandler);
        discoveryService.activate();
        this.discoveryServiceRegs.put(bridgeHandler.getThing().getUID(), bundleContext
                .registerService(DiscoveryService.class.getName(), discoveryService, new Hashtable<String, Object>()));
    }

    @Override
    protected synchronized void removeHandler(ThingHandler thingHandler) {
        if (thingHandler instanceof BrickdBridgeHandler) {
            ServiceRegistration<?> serviceReg = this.discoveryServiceRegs.get(thingHandler.getThing().getUID());
            if (serviceReg != null) {
                // remove discovery service, if bridge handler is removed
                TinkerforgeDiscoveryService service = (TinkerforgeDiscoveryService) bundleContext
                        .getService(serviceReg.getReference());
                if (service != null) {
                    service.deactivate();
                }
                serviceReg.unregister();
                discoveryServiceRegs.remove(thingHandler.getThing().getUID());
            }
        }
    }

}