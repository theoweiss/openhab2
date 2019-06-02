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
import org.eclipse.smarthome.core.library.unit.SIUnits;
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
import org.m1theo.tinkerforge.client.devices.airquality.AirPressureChannel;
import org.m1theo.tinkerforge.client.devices.airquality.AirPressureChannelConfig;
import org.m1theo.tinkerforge.client.devices.airquality.AirQualityBricklet;
import org.m1theo.tinkerforge.client.devices.airquality.AirQualityDeviceConfig;
import org.m1theo.tinkerforge.client.devices.airquality.ChannelId;
import org.m1theo.tinkerforge.client.devices.airquality.HumidityChannel;
import org.m1theo.tinkerforge.client.devices.airquality.HumidityChannelConfig;
import org.m1theo.tinkerforge.client.devices.airquality.IAQAccuracyChannel;
import org.m1theo.tinkerforge.client.devices.airquality.IAQAccuracyChannelConfig;
import org.m1theo.tinkerforge.client.devices.airquality.IAQIndexChannel;
import org.m1theo.tinkerforge.client.devices.airquality.IAQIndexChannelConfig;
import org.m1theo.tinkerforge.client.devices.airquality.TemperatureChannel;
import org.m1theo.tinkerforge.client.devices.airquality.TemperatureChannelConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link AirQualityBrickletHandler} is responsible for handling commands,
 * which are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class AirQualityBrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

	private final Logger logger = LoggerFactory.getLogger(AirQualityBrickletHandler.class);
	private @Nullable AirQualityDeviceConfig config;
	private @Nullable BrickdBridgeHandler bridgeHandler;
	private @Nullable AirQualityBricklet device;
	private @Nullable String uid;
	private boolean enabled = false;

	public AirQualityBrickletHandler(Thing thing) {
		super(thing);
	}

	@Override
	public void handleCommand(ChannelUID channelUID, Command command) {

	}

	@Override
	public void initialize() {
		config = getConfigAs(AirQualityDeviceConfig.class);
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
					if (deviceIn.getDeviceType() == DeviceType.airquality) {
						AirQualityBricklet device = (AirQualityBricklet) deviceIn;
						device.setDeviceConfig(config);

						Channel iaqIndexChannel = thing.getChannel("iaqIndex");
						if (iaqIndexChannel != null) {
							Channel currChannel = iaqIndexChannel;

							IAQIndexChannelConfig channelConfig = currChannel.getConfiguration()
									.as(IAQIndexChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.iaqIndex.name());
							if (tfChannel instanceof IAQIndexChannel) {
								((IAQIndexChannel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel iaqAccuracyChannel = thing.getChannel("iaqAccuracy");
						if (iaqAccuracyChannel != null) {
							Channel currChannel = iaqAccuracyChannel;

							IAQAccuracyChannelConfig channelConfig = currChannel.getConfiguration()
									.as(IAQAccuracyChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.iaqAccuracy.name());
							if (tfChannel instanceof IAQAccuracyChannel) {
								((IAQAccuracyChannel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel airpressureChannel = thing.getChannel("airpressure");
						if (airpressureChannel != null) {
							Channel currChannel = airpressureChannel;

							AirPressureChannelConfig channelConfig = currChannel.getConfiguration()
									.as(AirPressureChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.airpressure.name());
							if (tfChannel instanceof AirPressureChannel) {
								((AirPressureChannel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel temperatureChannel = thing.getChannel("temperature");
						if (temperatureChannel != null) {
							Channel currChannel = temperatureChannel;

							TemperatureChannelConfig channelConfig = currChannel.getConfiguration()
									.as(TemperatureChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.temperature.name());
							if (tfChannel instanceof TemperatureChannel) {
								((TemperatureChannel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel humidityChannel = thing.getChannel("humidity");
						if (humidityChannel != null) {
							Channel currChannel = humidityChannel;

							HumidityChannelConfig channelConfig = currChannel.getConfiguration()
									.as(HumidityChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.humidity.name());
							if (tfChannel instanceof HumidityChannel) {
								((HumidityChannel) tfChannel).setConfig(channelConfig);
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

			if (notifier.getChannelId().equals(ChannelId.iaqIndex.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.iaqAccuracy.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.airpressure.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new QuantityType<>(
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.MILLIBAR));

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.temperature.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new QuantityType<>(
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.humidity.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new QuantityType<>(
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.PERCENT));

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

			case "iaqIndex":
				getiaqIndex();
				break;

			case "iaqAccuracy":
				getiaqAccuracy();
				break;

			case "airpressure":
				getairpressure();
				break;

			case "temperature":
				gettemperature();
				break;

			case "humidity":
				gethumidity();
				break;

			default:
				break;
			}
		}
	}

	private void updateChannelStates() {

		if (isLinked("iaqIndex")) {
			getiaqIndex();
		}

		if (isLinked("iaqAccuracy")) {
			getiaqAccuracy();
		}

		if (isLinked("airpressure")) {
			getairpressure();
		}

		if (isLinked("temperature")) {
			gettemperature();
		}

		if (isLinked("humidity")) {
			gethumidity();
		}

	}

	private void getiaqIndex() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				AirQualityBricklet device2 = (AirQualityBricklet) device;
				IAQIndexChannel channel = (IAQIndexChannel) device2.getChannel("iaqIndex");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.iaqIndex.name(),
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}
		}
	}

	private void getiaqAccuracy() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				AirQualityBricklet device2 = (AirQualityBricklet) device;
				IAQAccuracyChannel channel = (IAQAccuracyChannel) device2.getChannel("iaqAccuracy");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.iaqAccuracy.name(),
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}
		}
	}

	private void getairpressure() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				AirQualityBricklet device2 = (AirQualityBricklet) device;
				AirPressureChannel channel = (AirPressureChannel) device2.getChannel("airpressure");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.airpressure.name(), new QuantityType<>(
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.MILLIBAR));

					return;
				}

			}
		}
	}

	private void gettemperature() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				AirQualityBricklet device2 = (AirQualityBricklet) device;
				TemperatureChannel channel = (TemperatureChannel) device2.getChannel("temperature");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.temperature.name(), new QuantityType<>(
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SIUnits.CELSIUS));

					return;
				}

			}
		}
	}

	private void gethumidity() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				AirQualityBricklet device2 = (AirQualityBricklet) device;
				HumidityChannel channel = (HumidityChannel) device2.getChannel("humidity");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.humidity.name(), new QuantityType<>(
							new DecimalType(((DecimalValue) newValue).bigDecimalValue()), SmartHomeUnits.PERCENT));

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