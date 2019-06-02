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
package org.openhab.binding.tinkerforge.internal;

import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.DEVICE_UID_PARAM;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.SUPPORTED_THING_TYPES_UIDS;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_AIRQUALITY;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_AMBIENTLIGHT;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_AMBIENTLIGHTV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_AMBIENTLIGHTV3;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_BAROMETER;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_BAROMETERV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_BRICKD;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_DISTANCEIR;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_DISTANCEIRV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_DISTANCEUS;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_DUALRELAY;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_HUMIDITY;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_HUMIDITYV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_INDUSTRIALDUALANALOGIN;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_INDUSTRIALDUALANALOGINV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_INDUSTRIALDUALRELAY;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_INDUSTRIALQUADRELAY;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_INDUSTRIALQUADRELAYV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_IO16;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_IO16V2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_IO4;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_IO4V2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_LCD128X64;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_LCD20X4;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_LOADCELL;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_LOADCELLV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_MOTIONDETECTOR;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_MOTIONDETECTORV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_MULTITOUCH;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_NFC;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_NFCRFID;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_OLED128X64;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_OLED128X64V2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_OLED64X48;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_OUTDOORWEATHER;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_PTC;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_PTCV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_REALTIMECLOCK;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_REALTIMECLOCKV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_ROTARYENCODER;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_SOLIDSTATERELAY;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_SOLIDSTATERELAYV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_SOUNDINTENSITY;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_SOUNDPRESSURELEVEL;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_TEMPERATURE;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_TEMPERATUREIR;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_TEMPERATUREIRV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_TEMPERATUREV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_UVLIGHT;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_UVLIGHTV2;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_VOLTAGECURRENT;
import static org.openhab.binding.tinkerforge.TinkerforgeBindingConstants.THING_TYPE_VOLTAGECURRENTV2;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.tinkerforge.handler.AirQualityBrickletHandler;
import org.openhab.binding.tinkerforge.handler.AmbientLightBrickletHandler;
import org.openhab.binding.tinkerforge.handler.AmbientLightV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.AmbientLightV3BrickletHandler;
import org.openhab.binding.tinkerforge.handler.BarometerBrickletHandler;
import org.openhab.binding.tinkerforge.handler.BarometerV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.BrickdBridgeHandler;
import org.openhab.binding.tinkerforge.handler.DistanceIRBrickletHandler;
import org.openhab.binding.tinkerforge.handler.DistanceIRV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.DistanceUSBrickletHandler;
import org.openhab.binding.tinkerforge.handler.DualRelayBrickletHandler;
import org.openhab.binding.tinkerforge.handler.HumidityBrickletHandler;
import org.openhab.binding.tinkerforge.handler.HumidityV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.IO16BrickletHandler;
import org.openhab.binding.tinkerforge.handler.IO16V2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.IO4BrickletHandler;
import org.openhab.binding.tinkerforge.handler.IO4V2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.IndustrialDualAnalogInBrickletHandler;
import org.openhab.binding.tinkerforge.handler.IndustrialDualAnalogInV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.IndustrialDualRelayBrickletHandler;
import org.openhab.binding.tinkerforge.handler.IndustrialQuadRelayBrickletHandler;
import org.openhab.binding.tinkerforge.handler.IndustrialQuadRelayBrickletV2Handler;
import org.openhab.binding.tinkerforge.handler.LCD128x64BrickletHandler;
import org.openhab.binding.tinkerforge.handler.LCD20x4BrickletHandler;
import org.openhab.binding.tinkerforge.handler.LoadCellBrickletHandler;
import org.openhab.binding.tinkerforge.handler.LoadCellV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.MotionDetectorBrickletHandler;
import org.openhab.binding.tinkerforge.handler.MotionDetectorV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.MultiTouchBrickletHandler;
import org.openhab.binding.tinkerforge.handler.NFCBrickletHandler;
import org.openhab.binding.tinkerforge.handler.NFCRFIDBrickletHandler;
import org.openhab.binding.tinkerforge.handler.OLED128x64BrickletHandler;
import org.openhab.binding.tinkerforge.handler.OLED128x64V2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.OLED64x48BrickletHandler;
import org.openhab.binding.tinkerforge.handler.OutdoorWeatherBrickletHandler;
import org.openhab.binding.tinkerforge.handler.PTCBrickletHandler;
import org.openhab.binding.tinkerforge.handler.PTCV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.RealTimeClockBrickletHandler;
import org.openhab.binding.tinkerforge.handler.RealTimeClockV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.RotaryEncoderBrickletHandler;
import org.openhab.binding.tinkerforge.handler.SolidStateRelayBrickletHandler;
import org.openhab.binding.tinkerforge.handler.SolidStateRelayBrickletV2Handler;
import org.openhab.binding.tinkerforge.handler.SoundIntensityBrickletHandler;
import org.openhab.binding.tinkerforge.handler.SoundPressureLevelBrickletHandler;
import org.openhab.binding.tinkerforge.handler.TemperatureBrickletHandler;
import org.openhab.binding.tinkerforge.handler.TemperatureIRBrickletHandler;
import org.openhab.binding.tinkerforge.handler.TemperatureIRV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.TemperatureV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.UVLightBrickletHandler;
import org.openhab.binding.tinkerforge.handler.UVLightV2BrickletHandler;
import org.openhab.binding.tinkerforge.handler.VoltageCurrentBrickletHandler;
import org.openhab.binding.tinkerforge.handler.VoltageCurrentV2BrickletHandler;
import org.openhab.binding.tinkerforge.internal.discovery.TinkerforgeDiscoveryService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TinkerforgeHandlerFactory} is responsible for creating things and
 * thing handlers.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.tinkerforge")
@NonNullByDefault
public class TinkerforgeHandlerFactory extends BaseThingHandlerFactory {

	private final Logger logger = LoggerFactory.getLogger(TinkerforgeHandlerFactory.class);
	private final Map<ThingUID, @Nullable ServiceRegistration<?>> discoveryServiceRegs = new HashMap<>();

	@NonNullByDefault({})
	private TFDynamicStateDescriptionProvider dynamicStateDescriptionProvider;

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

		if (thingTypeUID.equals(THING_TYPE_TEMPERATUREV2)) {
			return new TemperatureV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_TEMPERATUREIR)) {
			return new TemperatureIRBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_TEMPERATUREIRV2)) {
			return new TemperatureIRV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_LOADCELL)) {
			return new LoadCellBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_LOADCELLV2)) {
			return new LoadCellV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_SOUNDINTENSITY)) {
			return new SoundIntensityBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_SOUNDPRESSURELEVEL)) {
			return new SoundPressureLevelBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_AMBIENTLIGHT)) {
			return new AmbientLightBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_AMBIENTLIGHTV2)) {
			return new AmbientLightV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_AMBIENTLIGHTV3)) {
			return new AmbientLightV3BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_INDUSTRIALDUALANALOGIN)) {
			return new IndustrialDualAnalogInBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_INDUSTRIALDUALANALOGINV2)) {
			return new IndustrialDualAnalogInV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_PTC)) {
			return new PTCBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_PTCV2)) {
			return new PTCV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_BAROMETER)) {
			return new BarometerBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_BAROMETERV2)) {
			return new BarometerV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_DISTANCEIR)) {
			return new DistanceIRBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_DISTANCEIRV2)) {
			return new DistanceIRV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_UVLIGHT)) {
			return new UVLightBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_UVLIGHTV2)) {
			return new UVLightV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_HUMIDITY)) {
			return new HumidityBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_HUMIDITYV2)) {
			return new HumidityV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_MOTIONDETECTOR)) {
			return new MotionDetectorBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_MOTIONDETECTORV2)) {
			return new MotionDetectorV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_AIRQUALITY)) {
			return new AirQualityBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_REALTIMECLOCK)) {
			return new RealTimeClockBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_REALTIMECLOCKV2)) {
			return new RealTimeClockV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_ROTARYENCODER)) {
			return new RotaryEncoderBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_MULTITOUCH)) {
			return new MultiTouchBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_VOLTAGECURRENT)) {
			return new VoltageCurrentBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_VOLTAGECURRENTV2)) {
			return new VoltageCurrentV2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_DISTANCEUS)) {
			return new DistanceUSBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_DUALRELAY)) {
			return new DualRelayBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_INDUSTRIALDUALRELAY)) {
			return new IndustrialDualRelayBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_INDUSTRIALQUADRELAYV2)) {
			return new IndustrialQuadRelayBrickletV2Handler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_INDUSTRIALQUADRELAY)) {
			return new IndustrialQuadRelayBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_SOLIDSTATERELAY)) {
			return new SolidStateRelayBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_SOLIDSTATERELAYV2)) {
			return new SolidStateRelayBrickletV2Handler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_NFCRFID)) {
			return new NFCRFIDBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_NFC)) {
			return new NFCBrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_IO16)) {

			return new IO16BrickletHandler(thing, dynamicStateDescriptionProvider);

		}

		if (thingTypeUID.equals(THING_TYPE_IO4)) {

			return new IO4BrickletHandler(thing, dynamicStateDescriptionProvider);

		}

		if (thingTypeUID.equals(THING_TYPE_IO16V2)) {

			return new IO16V2BrickletHandler(thing, dynamicStateDescriptionProvider);

		}

		if (thingTypeUID.equals(THING_TYPE_IO4V2)) {

			return new IO4V2BrickletHandler(thing, dynamicStateDescriptionProvider);

		}

		if (thingTypeUID.equals(THING_TYPE_LCD128X64)) {
			return new LCD128x64BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_LCD20X4)) {
			return new LCD20x4BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_OLED128X64V2)) {
			return new OLED128x64V2BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_OLED128X64)) {
			return new OLED128x64BrickletHandler(thing);
		}

		if (thingTypeUID.equals(THING_TYPE_OLED64X48)) {
			return new OLED64x48BrickletHandler(thing);
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

	@Reference
	protected void setDynamicStateDescriptionProvider(TFDynamicStateDescriptionProvider provider) {
		this.dynamicStateDescriptionProvider = provider;
	}

	protected void unsetDynamicStateDescriptionProvider(TFDynamicStateDescriptionProvider provider) {
		this.dynamicStateDescriptionProvider = null;
	}
}