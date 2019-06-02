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
package org.openhab.binding.tinkerforge.handler;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.ActuatorChannel;
import org.m1theo.tinkerforge.client.Device;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.oled128x64v2.ChannelId;
import org.m1theo.tinkerforge.client.devices.oled128x64v2.DisplayChannel;
import org.m1theo.tinkerforge.client.devices.oled128x64v2.OLED128x64V2Bricklet;
import org.m1theo.tinkerforge.client.devices.oled128x64v2.OLED128x64V2Config;
import org.m1theo.tinkerforge.client.types.StringValue;
import org.openhab.binding.tinkerforge.internal.CommandConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link OLED128x64V2BrickletHandler} is responsible for handling commands,
 * which are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class OLED128x64V2BrickletHandler extends BaseThingHandler implements DeviceAdminListener {

	private final Logger logger = LoggerFactory.getLogger(OLED128x64V2BrickletHandler.class);
	private @Nullable OLED128x64V2Config config;
	private @Nullable BrickdBridgeHandler bridgeHandler;
	private @Nullable OLED128x64V2Bricklet device;
	private @Nullable String uid;
	private boolean enabled = false;

	public OLED128x64V2BrickletHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {

		switch (channelUID.getId()) {

		case "display":

			if (command instanceof StringType) {
				ActuatorChannel channel = (ActuatorChannel) bridgeHandler.getBrickd().getChannel(uid,
						channelUID.getId());
				channel.setValue(CommandConverter.convert(command));
			}

			// TODO do something
			break;

		default:
			break;
		}

	}

	@Override
	public void initialize() {
		config = getConfigAs(OLED128x64V2Config.class);
		String uid = config.getUid();
		if (uid != null) {
			this.uid = uid;
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

	private void enable() {
		logger.debug("executing enable");
		Bridge bridge = getBridge();
		ThingStatus bridgeStatus = (bridge == null) ? null : bridge.getStatus();
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {

			if (bridgeStatus == ThingStatus.ONLINE) {
				Device<?, ?> deviceIn = brickdBridgeHandler.getBrickd().getDevice(uid);
				if (deviceIn != null) {
					if (deviceIn.getDeviceType() == DeviceType.oled128x64v2) {
						OLED128x64V2Bricklet device = (OLED128x64V2Bricklet) deviceIn;
						device.setDeviceConfig(config);

						device.enable();
						this.device = device;
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

			case "display":
				getdisplay();
				break;

			default:
				break;
			}
		}
	}

	private void updateChannelStates() {

		if (isLinked("display")) {
			getdisplay();
		}

	}

	private void getdisplay() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				OLED128x64V2Bricklet device2 = (OLED128x64V2Bricklet) device;
				DisplayChannel channel = (DisplayChannel) device2.getChannel("display");
				Object newValue = channel.getValue();

				if (newValue instanceof StringValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.display.name(), new StringType(newValue.toString()));
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

		enabled = false;
	}

}