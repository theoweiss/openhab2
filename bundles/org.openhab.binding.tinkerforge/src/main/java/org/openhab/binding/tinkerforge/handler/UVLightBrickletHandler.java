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
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.QuantityType;
import org.eclipse.smarthome.core.library.unit.MetricPrefix;
import org.eclipse.smarthome.core.library.unit.SmartHomeUnits;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.CallbackListener;
import org.m1theo.tinkerforge.client.Device;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.m1theo.tinkerforge.client.Notifier;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.uvlight.ChannelId;
import org.m1theo.tinkerforge.client.devices.uvlight.UVLightBricklet;
import org.m1theo.tinkerforge.client.devices.uvlight.UVLightChannel;
import org.m1theo.tinkerforge.client.devices.uvlight.UVLightChannelConfig;
import org.m1theo.tinkerforge.client.devices.uvlight.UVLightDeviceConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link UVLightBrickletHandler} is responsible for handling commands,
 * which are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class UVLightBrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

	private final Logger logger = LoggerFactory.getLogger(UVLightBrickletHandler.class);
	private @Nullable UVLightDeviceConfig config;
	private @Nullable BrickdBridgeHandler bridgeHandler;
	private @Nullable UVLightBricklet device;
	private @Nullable String uid;
	private boolean enabled = false;

	public UVLightBrickletHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {

	}

	@Override
	public void initialize() {
		config = getConfigAs(UVLightDeviceConfig.class);
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
			brickdBridgeHandler.registerCallbackListener(this, uid);
			if (bridgeStatus == ThingStatus.ONLINE) {
				Device<?, ?> deviceIn = brickdBridgeHandler.getBrickd().getDevice(uid);
				if (deviceIn != null) {
					if (deviceIn.getDeviceType() == DeviceType.uvlight) {
						UVLightBricklet device = (UVLightBricklet) deviceIn;
						device.setDeviceConfig(config);

						Channel uvLightChannel = thing.getChannel("uvLight");
						if (uvLightChannel != null) {
							Channel currChannel = uvLightChannel;

							UVLightChannelConfig channelConfig = currChannel.getConfiguration()
									.as(UVLightChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.uvLight.name());
							if (tfChannel instanceof UVLightChannel) {
								((UVLightChannel) tfChannel).setConfig(channelConfig);
							}

						}

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
	public void notify(@Nullable Notifier notifier, @Nullable TinkerforgeValue lastValue,
			@Nullable TinkerforgeValue newValue) {
		if (notifier == null) {
			return;
		}
		if (!notifier.getDeviceId().equals(uid)) {
			return;
		}
		if (notifier.getExternalDeviceId() != null) {
			// TODO
		} else {

			if (notifier.getChannelId().equals(ChannelId.uvLight.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(),
							new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
									MetricPrefix.MILLI(SmartHomeUnits.IRRADIANCE)));

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

			case "uvLight":
				getuvLight();
				break;

			default:
				break;
			}
		}
	}

	private void updateChannelStates() {

		if (isLinked("uvLight")) {
			getuvLight();
		}

	}

	private void getuvLight() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				UVLightBricklet device2 = (UVLightBricklet) device;
				UVLightChannel channel = (UVLightChannel) device2.getChannel("uvLight");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.uvLight.name(),
							new QuantityType<>(new DecimalType(((DecimalValue) newValue).bigDecimalValue()),
									MetricPrefix.MILLI(SmartHomeUnits.IRRADIANCE)));

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
			brickdBridgeHandler.unregisterCallbackListener(this, uid);
		}
		if (device != null) {
			device.disable();
		}

		enabled = false;
	}

}