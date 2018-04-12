/**
 * Copyright (c) 2014,2018 by the respective copyright holders.
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.tinkerforge.internal;

import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;
import org.eclipse.smarthome.core.thing.Bridge;
import org.openhab.binding.tinkerforge.handler.BrickdBridgeHandler;

import org.openhab.binding.tinkerforge.handler.OutdoorWeatherBrickletHandler;

import org.openhab.binding.tinkerforge.handler.TemperatureBrickletHandler;

import org.openhab.binding.tinkerforge.handler.LoadCellBrickletHandler;

import org.openhab.binding.tinkerforge.handler.AmbientLightV2BrickletHandler;

import org.openhab.binding.tinkerforge.handler.HumidityV2BrickletHandler;

import org.openhab.binding.tinkerforge.handler.MotionDetectorV2BrickletHandler;

import org.openhab.binding.tinkerforge.handler.RealTimeClockBrickletHandler;


/**
 * The {@link TinkerforgeHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, immediate = true, configurationPid = "binding.tinkerforge")
@NonNullByDefault
public class TinkerforgeHandlerFactory extends BaseThingHandlerFactory {

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();
        if (thingTypeUID.equals(THING_TYPE_BRICKD)) {
            return new BrickdBridgeHandler((Bridge) thing);
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
    
        return null;
    }
}