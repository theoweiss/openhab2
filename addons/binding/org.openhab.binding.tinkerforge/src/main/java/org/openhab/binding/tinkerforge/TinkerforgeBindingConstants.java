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
package org.openhab.binding.tinkerforge;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link TinkerforgeBindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
 @NonNullByDefault
public class TinkerforgeBindingConstants {

    private static final String BINDING_ID = "tinkerforge";
    public static final String DEVICE_UID = "uid";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BRICKD = new ThingTypeUID(BINDING_ID, "brickd");
    
    
    public static final ThingTypeUID THING_TYPE_OUTDOORWEATHER = new ThingTypeUID(BINDING_ID, "outdoorweather");
    
    
    public static final ThingTypeUID THING_TYPE_TEMPERATURE = new ThingTypeUID(BINDING_ID, "temperature");
    
    
    public static final ThingTypeUID THING_TYPE_LOADCELL = new ThingTypeUID(BINDING_ID, "loadcell");
    
    
    public static final ThingTypeUID THING_TYPE_AMBIENTLIGHTV2 = new ThingTypeUID(BINDING_ID, "ambientlightV2");
    
    
    public static final ThingTypeUID THING_TYPE_HUMIDITYV2 = new ThingTypeUID(BINDING_ID, "humidityV2");
    
    
    public static final ThingTypeUID THING_TYPE_MOTIONDETECTORV2 = new ThingTypeUID(BINDING_ID, "motiondetectorV2");
    
    
    public static final ThingTypeUID THING_TYPE_REALTIMECLOCK = new ThingTypeUID(BINDING_ID, "realtimeclock");
    
    private static final ThingTypeUID[] SUPPORTED_THING_TYPES_UIDS_ARRAY = new ThingTypeUID[] {
      THING_TYPE_BRICKD,
    
    
      THING_TYPE_OUTDOORWEATHER, 
    
      THING_TYPE_TEMPERATURE, 
    
      THING_TYPE_LOADCELL, 
    
      THING_TYPE_AMBIENTLIGHTV2, 
    
      THING_TYPE_HUMIDITYV2, 
    
      THING_TYPE_MOTIONDETECTORV2, 
    
      THING_TYPE_REALTIMECLOCK
    };
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>(Arrays.asList(SUPPORTED_THING_TYPES_UIDS_ARRAY));

}