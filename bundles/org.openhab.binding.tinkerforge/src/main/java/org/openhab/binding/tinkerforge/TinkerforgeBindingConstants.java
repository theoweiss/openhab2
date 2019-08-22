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

    public static final String BINDING_ID = "tinkerforge";
    public static final String DEVICE_UID_PARAM = "uid";

    // List of all Thing Type UIDs
    public static final ThingTypeUID THING_TYPE_BRICKD = new ThingTypeUID(BINDING_ID, "brickd");

    public static final ThingTypeUID THING_TYPE_OUTDOORWEATHER = new ThingTypeUID(BINDING_ID, "outdoorweather");

    public static final ThingTypeUID THING_TYPE_TEMPERATURE = new ThingTypeUID(BINDING_ID, "temperature");

    public static final ThingTypeUID THING_TYPE_TEMPERATUREV2 = new ThingTypeUID(BINDING_ID, "temperatureV2");

    public static final ThingTypeUID THING_TYPE_TEMPERATUREIR = new ThingTypeUID(BINDING_ID, "temperatureir");

    public static final ThingTypeUID THING_TYPE_TEMPERATUREIRV2 = new ThingTypeUID(BINDING_ID, "temperatureirv2");

    public static final ThingTypeUID THING_TYPE_LOADCELL = new ThingTypeUID(BINDING_ID, "loadcell");

    public static final ThingTypeUID THING_TYPE_LOADCELLV2 = new ThingTypeUID(BINDING_ID, "loadcellv2");

    public static final ThingTypeUID THING_TYPE_SOUNDINTENSITY = new ThingTypeUID(BINDING_ID, "soundintensity");

    public static final ThingTypeUID THING_TYPE_SOUNDPRESSURELEVEL = new ThingTypeUID(BINDING_ID, "soundpressurelevel");

    public static final ThingTypeUID THING_TYPE_AMBIENTLIGHT = new ThingTypeUID(BINDING_ID, "ambientlight");

    public static final ThingTypeUID THING_TYPE_AMBIENTLIGHTV2 = new ThingTypeUID(BINDING_ID, "ambientlightV2");

    public static final ThingTypeUID THING_TYPE_AMBIENTLIGHTV3 = new ThingTypeUID(BINDING_ID, "ambientlightV3");

    public static final ThingTypeUID THING_TYPE_INDUSTRIALDUALANALOGIN = new ThingTypeUID(BINDING_ID,
            "industrialdualanalogIn");

    public static final ThingTypeUID THING_TYPE_INDUSTRIALDUALANALOGINV2 = new ThingTypeUID(BINDING_ID,
            "industrialdualanalogInV2");

    public static final ThingTypeUID THING_TYPE_PTC = new ThingTypeUID(BINDING_ID, "ptc");

    public static final ThingTypeUID THING_TYPE_PTCV2 = new ThingTypeUID(BINDING_ID, "ptcV2");

    public static final ThingTypeUID THING_TYPE_BAROMETER = new ThingTypeUID(BINDING_ID, "barometer");

    public static final ThingTypeUID THING_TYPE_BAROMETERV2 = new ThingTypeUID(BINDING_ID, "barometerV2");

    public static final ThingTypeUID THING_TYPE_DISTANCEIR = new ThingTypeUID(BINDING_ID, "distanceIR");

    public static final ThingTypeUID THING_TYPE_DISTANCEIRV2 = new ThingTypeUID(BINDING_ID, "distanceIRV2");

    public static final ThingTypeUID THING_TYPE_UVLIGHT = new ThingTypeUID(BINDING_ID, "uvlight");

    public static final ThingTypeUID THING_TYPE_UVLIGHTV2 = new ThingTypeUID(BINDING_ID, "uvlightv2");

    public static final ThingTypeUID THING_TYPE_PARTICULATEMATTER = new ThingTypeUID(BINDING_ID, "particulatematter");

    public static final ThingTypeUID THING_TYPE_HUMIDITY = new ThingTypeUID(BINDING_ID, "humidity");

    public static final ThingTypeUID THING_TYPE_HUMIDITYV2 = new ThingTypeUID(BINDING_ID, "humidityV2");

    public static final ThingTypeUID THING_TYPE_MOTIONDETECTOR = new ThingTypeUID(BINDING_ID, "motiondetector");

    public static final ThingTypeUID THING_TYPE_MOTIONDETECTORV2 = new ThingTypeUID(BINDING_ID, "motiondetectorV2");

    public static final ThingTypeUID THING_TYPE_AIRQUALITY = new ThingTypeUID(BINDING_ID, "airquality");

    public static final ThingTypeUID THING_TYPE_REALTIMECLOCK = new ThingTypeUID(BINDING_ID, "realtimeclock");

    public static final ThingTypeUID THING_TYPE_REALTIMECLOCKV2 = new ThingTypeUID(BINDING_ID, "realtimeclockV2");

    public static final ThingTypeUID THING_TYPE_ROTARYENCODER = new ThingTypeUID(BINDING_ID, "rotaryencoder");

    public static final ThingTypeUID THING_TYPE_MULTITOUCH = new ThingTypeUID(BINDING_ID, "multitouch");

    public static final ThingTypeUID THING_TYPE_VOLTAGECURRENT = new ThingTypeUID(BINDING_ID, "voltagecurrent");

    public static final ThingTypeUID THING_TYPE_VOLTAGECURRENTV2 = new ThingTypeUID(BINDING_ID, "voltagecurrentV2");

    public static final ThingTypeUID THING_TYPE_DISTANCEUS = new ThingTypeUID(BINDING_ID, "distanceus");

    public static final ThingTypeUID THING_TYPE_DUALRELAY = new ThingTypeUID(BINDING_ID, "dualrelay");

    public static final ThingTypeUID THING_TYPE_INDUSTRIALDUALRELAY = new ThingTypeUID(BINDING_ID,
            "industrialdualrelay");

    public static final ThingTypeUID THING_TYPE_INDUSTRIALQUADRELAYV2 = new ThingTypeUID(BINDING_ID,
            "industrialquadrelayV2");

    public static final ThingTypeUID THING_TYPE_INDUSTRIALQUADRELAY = new ThingTypeUID(BINDING_ID,
            "industrialquadrelay");

    public static final ThingTypeUID THING_TYPE_SOLIDSTATERELAY = new ThingTypeUID(BINDING_ID, "solidstaterelay");

    public static final ThingTypeUID THING_TYPE_SOLIDSTATERELAYV2 = new ThingTypeUID(BINDING_ID, "solidstaterelayV2");

    public static final ThingTypeUID THING_TYPE_NFCRFID = new ThingTypeUID(BINDING_ID, "nfcrfid");

    public static final ThingTypeUID THING_TYPE_NFC = new ThingTypeUID(BINDING_ID, "nfc");

    public static final ThingTypeUID THING_TYPE_IO16 = new ThingTypeUID(BINDING_ID, "io16");

    public static final ThingTypeUID THING_TYPE_IO4 = new ThingTypeUID(BINDING_ID, "io4");

    public static final ThingTypeUID THING_TYPE_IO16V2 = new ThingTypeUID(BINDING_ID, "io16v2");

    public static final ThingTypeUID THING_TYPE_IO4V2 = new ThingTypeUID(BINDING_ID, "io4v2");

    public static final ThingTypeUID THING_TYPE_LCD128X64 = new ThingTypeUID(BINDING_ID, "lcd128x64");

    public static final ThingTypeUID THING_TYPE_LCD20X4 = new ThingTypeUID(BINDING_ID, "lcd20x4");

    public static final ThingTypeUID THING_TYPE_OLED128X64V2 = new ThingTypeUID(BINDING_ID, "oled128x64v2");

    public static final ThingTypeUID THING_TYPE_OLED128X64 = new ThingTypeUID(BINDING_ID, "oled128x64");

    public static final ThingTypeUID THING_TYPE_OLED64X48 = new ThingTypeUID(BINDING_ID, "oled64x48");

    private static final ThingTypeUID[] SUPPORTED_THING_TYPES_UIDS_ARRAY = new ThingTypeUID[] { THING_TYPE_BRICKD,

            THING_TYPE_OUTDOORWEATHER,

            THING_TYPE_TEMPERATURE,

            THING_TYPE_TEMPERATUREV2,

            THING_TYPE_TEMPERATUREIR,

            THING_TYPE_TEMPERATUREIRV2,

            THING_TYPE_LOADCELL,

            THING_TYPE_LOADCELLV2,

            THING_TYPE_SOUNDINTENSITY,

            THING_TYPE_SOUNDPRESSURELEVEL,

            THING_TYPE_AMBIENTLIGHT,

            THING_TYPE_AMBIENTLIGHTV2,

            THING_TYPE_AMBIENTLIGHTV3,

            THING_TYPE_INDUSTRIALDUALANALOGIN,

            THING_TYPE_INDUSTRIALDUALANALOGINV2,

            THING_TYPE_PTC,

            THING_TYPE_PTCV2,

            THING_TYPE_BAROMETER,

            THING_TYPE_BAROMETERV2,

            THING_TYPE_DISTANCEIR,

            THING_TYPE_DISTANCEIRV2,

            THING_TYPE_UVLIGHT,

            THING_TYPE_UVLIGHTV2,

            THING_TYPE_PARTICULATEMATTER,

            THING_TYPE_HUMIDITY,

            THING_TYPE_HUMIDITYV2,

            THING_TYPE_MOTIONDETECTOR,

            THING_TYPE_MOTIONDETECTORV2,

            THING_TYPE_AIRQUALITY,

            THING_TYPE_REALTIMECLOCK,

            THING_TYPE_REALTIMECLOCKV2,

            THING_TYPE_ROTARYENCODER,

            THING_TYPE_MULTITOUCH,

            THING_TYPE_VOLTAGECURRENT,

            THING_TYPE_VOLTAGECURRENTV2,

            THING_TYPE_DISTANCEUS,

            THING_TYPE_DUALRELAY,

            THING_TYPE_INDUSTRIALDUALRELAY,

            THING_TYPE_INDUSTRIALQUADRELAYV2,

            THING_TYPE_INDUSTRIALQUADRELAY,

            THING_TYPE_SOLIDSTATERELAY,

            THING_TYPE_SOLIDSTATERELAYV2,

            THING_TYPE_NFCRFID,

            THING_TYPE_NFC,

            THING_TYPE_IO16,

            THING_TYPE_IO4,

            THING_TYPE_IO16V2,

            THING_TYPE_IO4V2,

            THING_TYPE_LCD128X64,

            THING_TYPE_LCD20X4,

            THING_TYPE_OLED128X64V2,

            THING_TYPE_OLED128X64,

            THING_TYPE_OLED64X48 };
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = new HashSet<>(
            Arrays.asList(SUPPORTED_THING_TYPES_UIDS_ARRAY));

}