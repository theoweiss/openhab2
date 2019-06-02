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
package org.openhab.binding.tinkerforge.internal.discovery;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.openhab.binding.tinkerforge.TinkerforgeBindingConstants;
import org.openhab.binding.tinkerforge.handler.BrickdBridgeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link TinkerforgeDiscoveryService} devices which are added by the
 * Tinkerforge enumeration service.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault
public class TinkerforgeDiscoveryService extends AbstractDiscoveryService implements DeviceAdminListener {

	private final Logger logger = LoggerFactory.getLogger(TinkerforgeDiscoveryService.class);

	private static int TIMEOUT = 60;
	private BrickdBridgeHandler brickdBridgeHandler;

	public TinkerforgeDiscoveryService(BrickdBridgeHandler brickdBridgeHandler) {
		super(TIMEOUT);
		this.brickdBridgeHandler = brickdBridgeHandler;
	}

	@Override
	protected void startScan() {
		// not needed
	}

	public void activate() {
		logger.debug("register device status listener");
		brickdBridgeHandler.registerDeviceStatusListener(this);
	}

	@Override
	public void deactivate() {
		// removeOlderResults(new Date().getTime(),
		// brickdBridgeHandler.getThing().getUID());
		brickdBridgeHandler.unregisterDeviceStatusListener(this);
	}

	@Override
	public void deviceChanged(@Nullable DeviceChangeType deviceChangeType, @Nullable DeviceInfo deviceInfo) {
		if (deviceChangeType == null || deviceInfo == null) {
			logger.debug("device changed but devicechangtype or deviceinfo are null");
			return;
		}
		if (deviceChangeType == DeviceChangeType.ADD) {
			logger.debug("device added{} {}", deviceInfo.getDeviceType(), deviceInfo.getUid());
			ThingUID thingUID = getThingUID(deviceInfo);
			ThingTypeUID thingTypeUID = getThingTypeUID(deviceInfo);
			if (thingUID != null) {
				ThingUID bridgeUID = brickdBridgeHandler.getThing().getUID();
				Map<String, Object> properties = new HashMap<>(1);
				properties.put(TinkerforgeBindingConstants.DEVICE_UID_PARAM, deviceInfo.getUid());
				DiscoveryResult discoveryResult = DiscoveryResultBuilder.create(thingUID).withThingType(thingTypeUID)
						.withProperties(properties).withBridge(bridgeUID)
						.withRepresentationProperty(TinkerforgeBindingConstants.DEVICE_UID_PARAM)
						.withLabel(deviceInfo.getDeviceType().getDeviceName() + " " + deviceInfo.getUid()).build();
				thingDiscovered(discoveryResult);
			} else {
				logger.debug("discovered unsupported device {} with uid {}", deviceInfo.getDeviceType().name(),
						deviceInfo.getUid());
			}
		} else if (deviceChangeType == DeviceChangeType.REMOVE) {
			// TODO
		}
	}

	private @Nullable ThingUID getThingUID(DeviceInfo deviceInfo) {
		ThingUID bridgeUID = brickdBridgeHandler.getThing().getUID();
		ThingTypeUID thingTypeUID = getThingTypeUID(deviceInfo);
		if (TinkerforgeBindingConstants.SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)) {
			return new ThingUID(thingTypeUID, bridgeUID, deviceInfo.getUid());
		} else {
			return null;
		}
	}

	private ThingTypeUID getThingTypeUID(DeviceInfo deviceInfo) {
		return new ThingTypeUID(TinkerforgeBindingConstants.BINDING_ID, deviceInfo.getDeviceType().name());
	}
}
