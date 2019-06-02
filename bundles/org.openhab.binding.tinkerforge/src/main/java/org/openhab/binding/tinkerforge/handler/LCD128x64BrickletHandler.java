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

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.Channel;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.CommonTriggerEvents;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerService;
import org.eclipse.smarthome.core.types.Command;
import org.m1theo.tinkerforge.client.ActuatorChannel;
import org.m1theo.tinkerforge.client.CallbackListener;
import org.m1theo.tinkerforge.client.Device;
import org.m1theo.tinkerforge.client.DeviceAdminListener;
import org.m1theo.tinkerforge.client.DeviceChangeType;
import org.m1theo.tinkerforge.client.DeviceInfo;
import org.m1theo.tinkerforge.client.Notifier;
import org.m1theo.tinkerforge.client.devices.DeviceType;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button0Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button10Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button11Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button1Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button2Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button3Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button4Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button5Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button6Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button7Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button8Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Button9Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.ButtonChannelConfig;
import org.m1theo.tinkerforge.client.devices.lcd128x64.ChannelId;
import org.m1theo.tinkerforge.client.devices.lcd128x64.DisplayChannel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.LCD128x64Bricklet;
import org.m1theo.tinkerforge.client.devices.lcd128x64.LCD128x64Config;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Slider0Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Slider1Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Slider2Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Slider3Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Slider4Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Slider5Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.SliderChannelConfig;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab0Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab1Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab2Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab3Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab4Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab5Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab6Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab7Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab8Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.Tab9Channel;
import org.m1theo.tinkerforge.client.devices.lcd128x64.TabChannelConfig;
import org.m1theo.tinkerforge.client.types.DecimalValue;
import org.m1theo.tinkerforge.client.types.HighLowValue;
import org.m1theo.tinkerforge.client.types.StringValue;
import org.m1theo.tinkerforge.client.types.TinkerforgeValue;
import org.openhab.binding.tinkerforge.action.LCD128x64BrickletActions;
import org.openhab.binding.tinkerforge.internal.CommandConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link LCD128x64BrickletHandler} is responsible for handling commands,
 * which are sent to one of the channels.
 *
 * @author Theo Weiss <theo@m1theo.org> - Initial contribution
 */
@NonNullByDefault

public class LCD128x64BrickletHandler extends BaseThingHandler implements CallbackListener, DeviceAdminListener {

	private final Logger logger = LoggerFactory.getLogger(LCD128x64BrickletHandler.class);
	private @Nullable LCD128x64Config config;
	private @Nullable BrickdBridgeHandler bridgeHandler;
	private @Nullable LCD128x64Bricklet device;
	private @Nullable String uid;
	private boolean enabled = false;

	public LCD128x64BrickletHandler(Thing thing) {
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
		config = getConfigAs(LCD128x64Config.class);
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
					if (deviceIn.getDeviceType() == DeviceType.lcd128x64) {
						LCD128x64Bricklet device = (LCD128x64Bricklet) deviceIn;
						device.setDeviceConfig(config);

						Channel button0Channel = thing.getChannel("button0");
						if (button0Channel != null) {
							Channel currChannel = button0Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button0.name());
							if (tfChannel instanceof Button0Channel) {
								((Button0Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button1Channel = thing.getChannel("button1");
						if (button1Channel != null) {
							Channel currChannel = button1Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button1.name());
							if (tfChannel instanceof Button1Channel) {
								((Button1Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button2Channel = thing.getChannel("button2");
						if (button2Channel != null) {
							Channel currChannel = button2Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button2.name());
							if (tfChannel instanceof Button2Channel) {
								((Button2Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button3Channel = thing.getChannel("button3");
						if (button3Channel != null) {
							Channel currChannel = button3Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button3.name());
							if (tfChannel instanceof Button3Channel) {
								((Button3Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button4Channel = thing.getChannel("button4");
						if (button4Channel != null) {
							Channel currChannel = button4Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button4.name());
							if (tfChannel instanceof Button4Channel) {
								((Button4Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button5Channel = thing.getChannel("button5");
						if (button5Channel != null) {
							Channel currChannel = button5Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button5.name());
							if (tfChannel instanceof Button5Channel) {
								((Button5Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button6Channel = thing.getChannel("button6");
						if (button6Channel != null) {
							Channel currChannel = button6Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button6.name());
							if (tfChannel instanceof Button6Channel) {
								((Button6Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button7Channel = thing.getChannel("button7");
						if (button7Channel != null) {
							Channel currChannel = button7Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button7.name());
							if (tfChannel instanceof Button7Channel) {
								((Button7Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button8Channel = thing.getChannel("button8");
						if (button8Channel != null) {
							Channel currChannel = button8Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button8.name());
							if (tfChannel instanceof Button8Channel) {
								((Button8Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button9Channel = thing.getChannel("button9");
						if (button9Channel != null) {
							Channel currChannel = button9Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button9.name());
							if (tfChannel instanceof Button9Channel) {
								((Button9Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button10Channel = thing.getChannel("button10");
						if (button10Channel != null) {
							Channel currChannel = button10Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button10.name());
							if (tfChannel instanceof Button10Channel) {
								((Button10Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel button11Channel = thing.getChannel("button11");
						if (button11Channel != null) {
							Channel currChannel = button11Channel;

							ButtonChannelConfig channelConfig = currChannel.getConfiguration()
									.as(ButtonChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.button11.name());
							if (tfChannel instanceof Button11Channel) {
								((Button11Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel slider0Channel = thing.getChannel("slider0");
						if (slider0Channel != null) {
							Channel currChannel = slider0Channel;

							SliderChannelConfig channelConfig = currChannel.getConfiguration()
									.as(SliderChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.slider0.name());
							if (tfChannel instanceof Slider0Channel) {
								((Slider0Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel slider1Channel = thing.getChannel("slider1");
						if (slider1Channel != null) {
							Channel currChannel = slider1Channel;

							SliderChannelConfig channelConfig = currChannel.getConfiguration()
									.as(SliderChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.slider1.name());
							if (tfChannel instanceof Slider1Channel) {
								((Slider1Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel slider2Channel = thing.getChannel("slider2");
						if (slider2Channel != null) {
							Channel currChannel = slider2Channel;

							SliderChannelConfig channelConfig = currChannel.getConfiguration()
									.as(SliderChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.slider2.name());
							if (tfChannel instanceof Slider2Channel) {
								((Slider2Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel slider3Channel = thing.getChannel("slider3");
						if (slider3Channel != null) {
							Channel currChannel = slider3Channel;

							SliderChannelConfig channelConfig = currChannel.getConfiguration()
									.as(SliderChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.slider3.name());
							if (tfChannel instanceof Slider3Channel) {
								((Slider3Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel slider4Channel = thing.getChannel("slider4");
						if (slider4Channel != null) {
							Channel currChannel = slider4Channel;

							SliderChannelConfig channelConfig = currChannel.getConfiguration()
									.as(SliderChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.slider4.name());
							if (tfChannel instanceof Slider4Channel) {
								((Slider4Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel slider5Channel = thing.getChannel("slider5");
						if (slider5Channel != null) {
							Channel currChannel = slider5Channel;

							SliderChannelConfig channelConfig = currChannel.getConfiguration()
									.as(SliderChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.slider5.name());
							if (tfChannel instanceof Slider5Channel) {
								((Slider5Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab0Channel = thing.getChannel("tab0");
						if (tab0Channel != null) {
							Channel currChannel = tab0Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab0.name());
							if (tfChannel instanceof Tab0Channel) {
								((Tab0Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab1Channel = thing.getChannel("tab1");
						if (tab1Channel != null) {
							Channel currChannel = tab1Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab1.name());
							if (tfChannel instanceof Tab1Channel) {
								((Tab1Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab2Channel = thing.getChannel("tab2");
						if (tab2Channel != null) {
							Channel currChannel = tab2Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab2.name());
							if (tfChannel instanceof Tab2Channel) {
								((Tab2Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab3Channel = thing.getChannel("tab3");
						if (tab3Channel != null) {
							Channel currChannel = tab3Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab3.name());
							if (tfChannel instanceof Tab3Channel) {
								((Tab3Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab4Channel = thing.getChannel("tab4");
						if (tab4Channel != null) {
							Channel currChannel = tab4Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab4.name());
							if (tfChannel instanceof Tab4Channel) {
								((Tab4Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab5Channel = thing.getChannel("tab5");
						if (tab5Channel != null) {
							Channel currChannel = tab5Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab5.name());
							if (tfChannel instanceof Tab5Channel) {
								((Tab5Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab6Channel = thing.getChannel("tab6");
						if (tab6Channel != null) {
							Channel currChannel = tab6Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab6.name());
							if (tfChannel instanceof Tab6Channel) {
								((Tab6Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab7Channel = thing.getChannel("tab7");
						if (tab7Channel != null) {
							Channel currChannel = tab7Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab7.name());
							if (tfChannel instanceof Tab7Channel) {
								((Tab7Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab8Channel = thing.getChannel("tab8");
						if (tab8Channel != null) {
							Channel currChannel = tab8Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab8.name());
							if (tfChannel instanceof Tab8Channel) {
								((Tab8Channel) tfChannel).setConfig(channelConfig);
							}

						}

						Channel tab9Channel = thing.getChannel("tab9");
						if (tab9Channel != null) {
							Channel currChannel = tab9Channel;

							TabChannelConfig channelConfig = currChannel.getConfiguration().as(TabChannelConfig.class);
							org.m1theo.tinkerforge.client.Channel<?, ?, ?> tfChannel = device
									.getChannel(ChannelId.tab9.name());
							if (tfChannel instanceof Tab9Channel) {
								((Tab9Channel) tfChannel).setConfig(channelConfig);
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
	public Collection<@NonNull Class<? extends @NonNull ThingHandlerService>> getServices() {
		return Collections.singleton(LCD128x64BrickletActions.class);
	}

	public @Nullable DisplayChannel getDisplayChannel() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
				DisplayChannel channel = (DisplayChannel) device2.getChannel("display");
				return channel;
			}
		}
		return null;
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

			if (notifier.getChannelId().equals(ChannelId.button0.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button1.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button2.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button3.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button4.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button5.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button6.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button7.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button8.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button9.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button10.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.button11.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.slider0.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.slider1.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.slider2.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.slider3.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.slider4.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.slider5.name())) {

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(notifier.getChannelId(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab0.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab1.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab2.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab3.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab4.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab5.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab6.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab7.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab8.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

					return;
				}

			}

			if (notifier.getChannelId().equals(ChannelId.tab9.name())) {

				if (newValue instanceof HighLowValue) {
					logger.debug("new value {}", newValue);

					String value = newValue == HighLowValue.HIGH ? CommonTriggerEvents.PRESSED
							: CommonTriggerEvents.RELEASED;
					triggerChannel(notifier.getChannelId(), value);

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

			case "display":
				getdisplay();
				break;

			case "slider0":
				getslider0();
				break;

			case "slider1":
				getslider1();
				break;

			case "slider2":
				getslider2();
				break;

			case "slider3":
				getslider3();
				break;

			case "slider4":
				getslider4();
				break;

			case "slider5":
				getslider5();
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

		if (isLinked("slider0")) {
			getslider0();
		}

		if (isLinked("slider1")) {
			getslider1();
		}

		if (isLinked("slider2")) {
			getslider2();
		}

		if (isLinked("slider3")) {
			getslider3();
		}

		if (isLinked("slider4")) {
			getslider4();
		}

		if (isLinked("slider5")) {
			getslider5();
		}

	}

	private void getdisplay() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
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

	private void getslider0() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
				Slider0Channel channel = (Slider0Channel) device2.getChannel("slider0");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.slider0.name(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}
		}
	}

	private void getslider1() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
				Slider1Channel channel = (Slider1Channel) device2.getChannel("slider1");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.slider1.name(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}
		}
	}

	private void getslider2() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
				Slider2Channel channel = (Slider2Channel) device2.getChannel("slider2");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.slider2.name(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}
		}
	}

	private void getslider3() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
				Slider3Channel channel = (Slider3Channel) device2.getChannel("slider3");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.slider3.name(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}
		}
	}

	private void getslider4() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
				Slider4Channel channel = (Slider4Channel) device2.getChannel("slider4");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.slider4.name(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
					return;
				}

			}
		}
	}

	private void getslider5() {
		BrickdBridgeHandler brickdBridgeHandler = getBrickdBridgeHandler();
		if (brickdBridgeHandler != null) {
			Device<?, ?> device = brickdBridgeHandler.getBrickd().getDevice(uid);
			if (device != null) {
				LCD128x64Bricklet device2 = (LCD128x64Bricklet) device;
				Slider5Channel channel = (Slider5Channel) device2.getChannel("slider5");
				Object newValue = channel.getValue();

				if (newValue instanceof DecimalValue) {
					logger.debug("new value {}", newValue);
					updateState(ChannelId.slider5.name(), new DecimalType(((DecimalValue) newValue).bigDecimalValue()));
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